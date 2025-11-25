package com.urim.cloudpicturebackend.manager.sharding;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.urim.cloudpicturebackend.model.entity.Space;
import com.urim.cloudpicturebackend.model.enums.SpaceLevelEnum;
import com.urim.cloudpicturebackend.model.enums.SpaceTypeEnum;
import com.urim.cloudpicturebackend.service.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.jdbc.core.connection.ShardingSphereConnection;
import org.apache.shardingsphere.infra.metadata.database.rule.ShardingSphereRuleMetaData;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.rule.ShardingRule;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//@Component
@Slf4j
public class DynamicShardingManager {

    @Resource
    private DataSource dataSource;

    @Resource
    private SpaceService spaceService;

    private static final String LOGIC_TABLE_NAME = "picture";

    private static final String DATABASE_NAME = "cloud-picture"; // 配置文件中的数据源名称

    @PostConstruct
    public void initialize() {
        try {
            log.info("初始化动态分表配置...");
            // 延迟初始化，等待 ShardingSphere 完全启动
            Thread.sleep(2000);
            updateShardingTableNodes();
        } catch (Exception e) {
            log.warn("初始化动态分表配置失败，将在后续使用时重试: {}", e.getMessage());
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 获取所有动态表名，包括初始表 picture 和分表 picture_{spaceId}
     */
    private Set<String> fetchAllPictureTableNames() {
        // 为了测试方便，直接对所有团队空间分表（实际上线改为仅对旗舰版生效）
        Set<Long> spaceIds = spaceService.lambdaQuery()
                .eq(Space::getSpaceType, SpaceTypeEnum.TEAM.getValue())
                .list()
                .stream()
                .map(Space::getId)
                .collect(Collectors.toSet());
        Set<String> tableNames = spaceIds.stream()
                .map(spaceId -> LOGIC_TABLE_NAME + "_" + spaceId)
                .collect(Collectors.toSet());
        tableNames.add(LOGIC_TABLE_NAME); // 添加初始逻辑表
        return tableNames;
    }

    /**
     * 更新 ShardingSphere 的 actual-data-nodes 动态表名配置
     */
    private void updateShardingTableNodes() {
        try {
            Set<String> tableNames = fetchAllPictureTableNames();
            // cloud-picture.picture_112321321,cloud-picture.picture_1123213123
            String newActualDataNodes = tableNames.stream()
                    .map(tableName -> DATABASE_NAME + "." + tableName) // 使用数据源名称作为前缀
                    .collect(Collectors.joining(","));
            log.info("动态分表 actual-data-nodes 配置: {}", newActualDataNodes);

            ContextManager contextManager = getContextManager();
            var databases = contextManager.getMetaDataContexts()
                    .getMetaData()
                    .getDatabases();

            // 动态获取数据库名称（如果 DATABASE_NAME 不存在，尝试获取第一个数据库）
            String actualDatabaseName = DATABASE_NAME;
            if (!databases.containsKey(DATABASE_NAME) && !databases.isEmpty()) {
                actualDatabaseName = databases.keySet().iterator().next();
                log.warn("数据库名称 {} 不存在，使用找到的数据库名称: {}", DATABASE_NAME, actualDatabaseName);
            }

            var database = databases.get(actualDatabaseName);
            if (database == null) {
                log.error("无法找到 ShardingSphere 数据库，可用数据库: {}", databases.keySet());
                return;
            }

            ShardingSphereRuleMetaData ruleMetaData = database.getRuleMetaData();

            Optional<ShardingRule> shardingRule = ruleMetaData.findSingleRule(ShardingRule.class);
            if (shardingRule.isPresent()) {
                ShardingRuleConfiguration ruleConfig = (ShardingRuleConfiguration) shardingRule.get().getConfiguration();
                List<ShardingTableRuleConfiguration> updatedRules = ruleConfig.getTables()
                        .stream()
                        .map(oldTableRule -> {
                            if (LOGIC_TABLE_NAME.equals(oldTableRule.getLogicTable())) {
                                ShardingTableRuleConfiguration newTableRuleConfig = new ShardingTableRuleConfiguration(LOGIC_TABLE_NAME, newActualDataNodes);
                                newTableRuleConfig.setDatabaseShardingStrategy(oldTableRule.getDatabaseShardingStrategy());
                                newTableRuleConfig.setTableShardingStrategy(oldTableRule.getTableShardingStrategy());
                                newTableRuleConfig.setKeyGenerateStrategy(oldTableRule.getKeyGenerateStrategy());
                                newTableRuleConfig.setAuditStrategy(oldTableRule.getAuditStrategy());
                                return newTableRuleConfig;
                            }
                            return oldTableRule;
                        })
                        .collect(Collectors.toList());
                ruleConfig.setTables(updatedRules);
                contextManager.alterRuleConfiguration(actualDatabaseName, Collections.singleton(ruleConfig));
                contextManager.reloadDatabase(actualDatabaseName);
                log.info("动态分表规则更新成功！");
            } else {
                log.error("未找到 ShardingSphere 的分片规则配置，动态分表更新失败。");
            }
        } catch (Exception e) {
            log.error("更新 ShardingSphere 分表配置失败", e);
            throw e;
        }
    }

    /**
     * 动态创建空间图片分表
     *
     * @param space
     */
    public void createSpacePictureTable(Space space) {
        // 仅为旗舰版团队空间创建分表
        if (space.getSpaceType() == SpaceTypeEnum.TEAM.getValue() && space.getSpaceLevel() == SpaceLevelEnum.FLAGSHIP.getValue()) {
            Long spaceId = space.getId();
            String tableName = LOGIC_TABLE_NAME + "_" + spaceId;
            // 创建新表
            String createTableSql = "CREATE TABLE " + tableName + " LIKE " + LOGIC_TABLE_NAME;
            try {
                SqlRunner.db().update(createTableSql);
                // 更新分表
                updateShardingTableNodes();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("创建图片空间分表失败，空间 id = {}", space.getId());
            }
        }
    }

    /**
     * 获取 ShardingSphere ContextManager
     */
    private ContextManager getContextManager() {
        try (ShardingSphereConnection connection = dataSource.getConnection().unwrap(ShardingSphereConnection.class)) {
            return connection.getContextManager();
        } catch (SQLException e) {
            throw new RuntimeException("获取 ShardingSphere ContextManager 失败", e);
        }
    }
}
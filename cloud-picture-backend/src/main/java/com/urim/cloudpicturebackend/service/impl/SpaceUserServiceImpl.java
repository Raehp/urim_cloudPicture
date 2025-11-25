package com.urim.cloudpicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urim.cloudpicturebackend.exception.BusinessException;
import com.urim.cloudpicturebackend.exception.ErrorCode;
import com.urim.cloudpicturebackend.exception.ThrowUtils;
import com.urim.cloudpicturebackend.mapper.SpaceUserMapper;
import com.urim.cloudpicturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.urim.cloudpicturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.urim.cloudpicturebackend.model.entity.Space;
import com.urim.cloudpicturebackend.model.entity.SpaceUser;
import com.urim.cloudpicturebackend.model.entity.User;
import com.urim.cloudpicturebackend.model.enums.SpaceRoleEnum;
import com.urim.cloudpicturebackend.model.vo.SpaceUserVo;
import com.urim.cloudpicturebackend.model.vo.SpaceVo;
import com.urim.cloudpicturebackend.model.vo.UserVo;
import com.urim.cloudpicturebackend.service.SpaceService;
import com.urim.cloudpicturebackend.service.SpaceUserService;
import com.urim.cloudpicturebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
        implements SpaceUserService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private SpaceService spaceService;

    /**
     * 添加空间成员
     *
     * @param spaceUserAddRequest
     * @return
     */
    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        // 参数校验
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
        // 判断当前用户是否存在
        Long userId = spaceUser.getUserId();
        SpaceUser user = this.getById(userId);
        ThrowUtils.throwIf(user != null, ErrorCode.NOT_FOUND_ERROR, "用户已存在");
        validSpaceUser(spaceUser, true);

        // 数据库操作
        boolean result = this.save(spaceUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return spaceUser.getId();
    }

    /**
     * 验证空间用户数据的有效性
     *
     * @param spaceUser 空间用户对象
     * @param add       是否为新增操作 true-新增 false-更新
     */
    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        // 创建时，空间 id 和用户 id 必填
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
            User user = userService.getById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        // 校验空间角色
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间角色不存在");
        }
    }


    /**
     * 根据SpaceUser对象获取封装的SpaceUserVo对象
     *
     * @param spaceUser 空间用户关联对象
     * @param request   HTTP请求对象
     * @return 封装的空间用户VO对象
     */
    @Override
    public SpaceUserVo getSpaceUserVo(SpaceUser spaceUser, HttpServletRequest request) {
        // 对象转封装类
        SpaceUserVo spaceUserVO = SpaceUserVo.objToVo(spaceUser);
        // 关联查询用户信息
        Long userId = spaceUser.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVo userVO = userService.getUserVo(user);
            spaceUserVO.setUser(userVO);
        }
        // 关联查询空间信息
        Long spaceId = spaceUser.getSpaceId();
        if (spaceId != null && spaceId > 0) {
            Space space = spaceService.getById(spaceId);
            SpaceVo spaceVo = spaceService.getSpaceVO(space, request);
            spaceUserVO.setSpace(spaceVo);
        }
        return spaceUserVO;
    }

    /**
     * 将 SpaceUser 列表转换为 SpaceUserVo 列表，并填充每个 SpaceUserVo 关联的用户信息和空间信息。
     *
     * @param spaceUserList SpaceUser 对象列表，用于转换和数据填充
     * @return 转换并填充完成的 SpaceUserVo 对象列表；如果输入为空，则返回空列表
     */
    @Override
    public List<SpaceUserVo> getSpaceUserVoList(List<SpaceUser> spaceUserList) {
        // 判断输入列表是否为空
        if (CollUtil.isEmpty(spaceUserList)) {
            return Collections.emptyList();
        }
        // 对象列表 => 封装对象列表
        List<SpaceUserVo> spaceUserVOList = spaceUserList.stream().map(SpaceUserVo::objToVo).collect(Collectors.toList());
        // 1. 收集需要关联查询的用户 ID 和空间 ID
        Set<Long> userIdSet = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        Set<Long> spaceIdSet = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        // 2. 批量查询用户和空间
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        Map<Long, List<Space>> spaceIdSpaceListMap = spaceService.listByIds(spaceIdSet).stream()
                .collect(Collectors.groupingBy(Space::getId));
        // 3. 填充 SpaceUserVo 的用户和空间信息
        spaceUserVOList.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            // 填充用户信息
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceUserVO.setUser(userService.getUserVo(user));
            // 填充空间信息
            Space space = null;
            if (spaceIdSpaceListMap.containsKey(spaceId)) {
                space = spaceIdSpaceListMap.get(spaceId).get(0);
            }
            spaceUserVO.setSpace(SpaceVo.objToVo(space));
        });
        return spaceUserVOList;
    }

    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest 查询对象
     * @return 查询对象
     */
    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
        return queryWrapper;
    }
}

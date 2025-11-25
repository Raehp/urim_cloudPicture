package com.urim.cloudpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.urim.cloudpicturebackend.model.dto.space.SpaceAddRequest;
import com.urim.cloudpicturebackend.model.dto.space.SpaceQueryRequest;
import com.urim.cloudpicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.urim.cloudpicturebackend.model.entity.User;
import com.urim.cloudpicturebackend.model.vo.SpaceVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author zhl20
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-10-25 14:54:52
*/
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 校验空间
     *
     * @param space
     * @param add 判断是新增还是修改
     */
    void validSpace(Space space,boolean add);

    /**
     * 获取单个空间封装类
     * @param space
     * @param request
     * @return
     */
    SpaceVo getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 分页获取空间封装类
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVo> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 分页查询空间
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间级别填充空间信息
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 检查空间权限
     *
     * @param loginUser
     * @param space
     */
    void checkSpaceAuth(User loginUser, Space space);
}

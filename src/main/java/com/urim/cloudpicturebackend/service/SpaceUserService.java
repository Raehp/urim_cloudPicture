package com.urim.cloudpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.urim.cloudpicturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.urim.cloudpicturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.urim.cloudpicturebackend.model.entity.SpaceUser;
import com.urim.cloudpicturebackend.model.vo.SpaceUserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhl20
 * @description 针对表【space_user(空间)】的数据库操作Service
 * @createDate 2025-10-25 14:54:52
 */
public interface SpaceUserService extends IService<SpaceUser> {
    /**
     * 创建空间成员
     *
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验空间成员
     *
     * @param spaceUser
     * @param add       是否为创建时检验
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取空间成员包装类（单条）
     *
     * @param spaceUser
     * @param request
     * @return
     */
    SpaceUserVo getSpaceUserVo(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员包装类（列表）
     *
     * @param spaceUserList
     * @return
     */
    List<SpaceUserVo> getSpaceUserVoList(List<SpaceUser> spaceUserList);


    /**
     * 获取查询对象
     *
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
}

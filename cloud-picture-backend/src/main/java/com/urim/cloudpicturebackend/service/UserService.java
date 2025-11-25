package com.urim.cloudpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.urim.cloudpicturebackend.model.dto.user.UserAddRequest;
import com.urim.cloudpicturebackend.model.dto.user.UserQueryRequest;
import com.urim.cloudpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.urim.cloudpicturebackend.model.vo.LoginUserVo;
import com.urim.cloudpicturebackend.model.vo.UserVo;
import org.apache.ibatis.annotations.Update;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author zhl20
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-10-04 15:43:56
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    public long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 获取加密密码
     *
     * @param userPassword
     * @return
     */
    String getEcrptyPassword(String userPassword);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登录请求
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获得脱敏后的登录用户信息
     * @param user
     * @return
     */
    LoginUserVo getLoginUserVo(User user);

    /**
     * 获得脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVo getUserVo(User user);

    /**
     * 获得脱敏后的用户信息列表
     *
     * @param userList
     * @return
     */
    List<UserVo> getUserVoList(List<User> userList);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 获取用查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 判断用户是否为管理员
     */
    boolean isAdmin(User user);
}

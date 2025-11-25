package com.urim.cloudpicturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.urim.cloudpicturebackend.annotation.AuthCheck;
import com.urim.cloudpicturebackend.common.BaseResponse;
import com.urim.cloudpicturebackend.common.DeleteRequest;
import com.urim.cloudpicturebackend.common.ResultUtils;
import com.urim.cloudpicturebackend.constants.UserConstant;
import com.urim.cloudpicturebackend.exception.BusinessException;
import com.urim.cloudpicturebackend.exception.ErrorCode;
import com.urim.cloudpicturebackend.exception.ThrowUtils;
import com.urim.cloudpicturebackend.model.dto.user.*;
import com.urim.cloudpicturebackend.model.entity.User;
import com.urim.cloudpicturebackend.model.vo.LoginUserVo;
import com.urim.cloudpicturebackend.model.vo.UserVo;
import com.urim.cloudpicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验参数
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVo loginUserVo = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVo);
    }

    /**
     * 获取当前用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
        // 获取当前用户
        User loginUserVo = userService.getLoginUser(request);
        // 返回脱敏后的信息
        return ResultUtils.success(userService.getLoginUserVo(loginUserVo));
    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        Boolean res = userService.userLogout(request);
        return ResultUtils.success(res);
    }

    /**
     * 添加用户
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtils.copyProperties(userAddRequest,user);
        // 填充默认值
        final String defaultPassword = "12345678";
        String ecrptyPassword = userService.getEcrptyPassword(defaultPassword);
        user.setUserPassword(ecrptyPassword);
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        // 插入数据库
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"插入数据库失败");
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据id获取用户（仅限管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVo> getUserVoById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVo(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 或者使用 remove 方法配合查询条件
        boolean res = userService.removeById(deleteRequest.getId());

        return ResultUtils.success(res);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);
        boolean res = userService.updateById(user);
        if (!res) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"更新失败");
        }
        return ResultUtils.success(res);
    }

    /**
     * 分页获取用户封装列表(仅限管理员)
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVo>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前页
        long current = userQueryRequest.getCurrent();
        // 获取每页记录数
        long pageSize = userQueryRequest.getPageSize();

        // 获取分页查询结果
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 将获取到的结果进行脱敏
        // 1. 重新封装分页查询结果到UserVo中
        Page<UserVo> userVoPage = new Page<>(current, pageSize,userPage.getTotal());
        // 2. 将获取到的结果进行脱敏
        List<UserVo> userVoList = userService.getUserVoList(userPage.getRecords());
        // 3. 将脱敏后的结果封装返回
        userVoPage.setRecords(userVoList);
        return ResultUtils.success(userVoPage);
    }
}

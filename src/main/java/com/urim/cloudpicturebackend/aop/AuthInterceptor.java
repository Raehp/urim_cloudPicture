package com.urim.cloudpicturebackend.aop;

import com.urim.cloudpicturebackend.annotation.AuthCheck;
import com.urim.cloudpicturebackend.exception.BusinessException;
import com.urim.cloudpicturebackend.exception.ErrorCode;
import com.urim.cloudpicturebackend.model.entity.User;
import com.urim.cloudpicturebackend.model.enums.UserRoleEnum;
import com.urim.cloudpicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取添加了AuthCheck注解的方法执行需要什么权限？
        String mustRole = authCheck.mustRole();
        // 获取当前用户的信息
        // 1. 获取当前的请求信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 2. 获取当前用户
        User loginUser = userService.getLoginUser(request);
        // 将执行该方法必须要有的权限转化为枚举类
        UserRoleEnum mustRoleEnum = UserRoleEnum.getByValue(mustRole);
        // 如果当前不需要权限，直接放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        // 接下来的操作都需要权限
        // 将用户的权限转换为枚举类
        UserRoleEnum userRoleEnum = UserRoleEnum.getByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 该操作需要管理员权限，但是用户没有管理员权限
        if (mustRoleEnum.getValue().equals(UserRoleEnum.ADMIN.getValue()) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 放行
        return joinPoint.proceed();
    }
}

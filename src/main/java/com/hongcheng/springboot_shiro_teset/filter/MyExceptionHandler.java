package com.hongcheng.springboot_shiro_teset.filter;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

/**
 * 	对controller的一个切面增强，可以只针对部分controller
 * 	这个用不用都无所谓
 * */
@ControllerAdvice(basePackages = "com.hongcheng.springboot_shiro_teset.controller")
@Slf4j
public class MyExceptionHandler {

	/**
	 * 	统一异常处理
	 * */
    @ExceptionHandler()
    @ResponseBody
    public String ErrorHandler(AuthorizationException e) {
        log.error("没有通过权限验证！", e);
        return "没有通过权限验证！";
    }
}
package com.hongcheng.springboot_shiro_teset.controller;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongcheng.springboot_shiro_teset.bean.User;

@RestController
public class LoginController {

    @RequestMapping("/login")
    public String login(User user) {
    	
        //添加用户认证信息
        Subject subject = SecurityUtils.getSubject();
        /**
         * 	如果是账号密码类型，可以创建一个UsernamePasswordToken。	
         * 	如果使用Token令牌，可以使用EasyTypeToken这个类，相对于UsernamePasswordToken，只是把密码视为空串，username视为token。
         * 	如果用EasyTypeToken，记得要自己去解析token，然后获取相应的用户信息
         * */
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                user.getUserName(),
                user.getPassword()
        );
        try {
            //进行验证，这里可以捕获异常，然后返回对应信息
            subject.login(usernamePasswordToken);
            // 判断有没有角色
            subject.checkRole("admin");
            // 判断有没有权限
            subject.checkPermissions("query", "add");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return "账号或密码错误！";
        } catch (AuthorizationException e) {
            e.printStackTrace();
            return "没有权限";
        }
        return "login success";
    }
     //注解验角色和权限
    @RequiresRoles("admin")
    @RequiresPermissions("add")
    @RequestMapping("/index")
    public String index(HttpSession httpsession) {
    	Session session = SecurityUtils.getSubject().getSession();
    	System.err.println(session);
    	System.err.println(httpsession);
    	
    	/***
    	 * 	shiro的session和request和response，其实就是HttpServletSession、HttpServletRequest、HttpServletResponse的子类
    	 * 	org.apache.shiro.web.session.mgt.ServletContainerSessionManager.getSession(SessionKey)可以看到这个结论
    	 * */
    	httpsession.setAttribute("http", "httpsession测试");
    	Object attribute = session.getAttribute("http");
    	System.err.println(attribute);
        return "index!";
    }
}

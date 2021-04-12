package com.hongcheng.springboot_shiro_teset.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.hongcheng.springboot_shiro_teset.bean.Permission;
import com.hongcheng.springboot_shiro_teset.bean.Role;
import com.hongcheng.springboot_shiro_teset.bean.User;
import com.hongcheng.springboot_shiro_teset.service.LoginService;

public class CustomRealm extends AuthorizingRealm {

	/**
	 * 	这里可以用@Autowired，因为CustomRealm是在ShiroConfig里面通过@Bean交由spring进行管理的
	 * */
	@Autowired
    private LoginService loginService;
	
	
	/**
	 * 	授权
	 * */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//获取登录用户名
        String name = (String) principals.getPrimaryPrincipal();
        //根据用户名去数据库查询用户信息
        User user = loginService.getUserByName(name);
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (Role role : user.getRoles()) {
            //添加角色
            simpleAuthorizationInfo.addRole(role.getRoleName());
            //添加权限
            for (Permission permission : role.getPermissions()) {
                simpleAuthorizationInfo.addStringPermission(permission.getPermissionName());
            }
        }
        return simpleAuthorizationInfo;
	}

	/***
	 * 	认证，这里只是根据前端获取到的账号密码，让你去别的地方，如数据库，查找他的认证信息		<br>
	 * 	密码的对比并不在这里，SimpleCredentialsMatcher的doCredentialsMatch()方法才是密码对比	<br>
	 * 	如果用户不存在、或者用户被锁定，这里就应该抛出响应的错误<br>
	 * 	<br>
	 * 	<br>
	 * 	如果使用Token令牌，controller处使用了EasyTypeToken这个类，记得自己去解析token，然后获取相应的用户信息
	 * */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		 //加这一步的目的是在Post请求的时候会先进认证，然后在到请求
        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        //获取用户信息
        String name = authenticationToken.getPrincipal().toString();
        User user = loginService.getUserByName(name);
        if (user == null) {
            //这里返回后会报出对应异常
            throw new UnknownAccountException("账号不存在");
        } else {
            //这里验证authenticationToken和simpleAuthenticationInfo的信息
        	ByteSource salt = ByteSource.Util.bytes(name);		// 加密用的盐
        	/**
        	 * 	SimpleAuthenticationInfo这玩意就是你自己保存的用户认证信息，doGetAuthenticationInfo方法主要目的就是通过用户名、或者token去获取这个用户认证信息。
        	 * 	以便在后面给密码匹配器进行验证。
        	 * 	注意两个名词：
        	 * 		principal：用户的主体、当事人，按官方的文档说法就是用户的唯一标识，不管你是账号，还是userId，还是身份证，只要唯一就行。
        	 * 		credentials: 凭证，也就是密码，或者密钥，是可以证明用户身份的东西。
        	 * */
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(name, user.getPassword().toString(), salt, getName());
            return simpleAuthenticationInfo;
        }
	}

}

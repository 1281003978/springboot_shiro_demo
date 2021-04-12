package com.hongcheng.springboot_shiro_teset.shiro;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 	这个是用来多多realm验证的
 * */
public class CustomRealm2 extends AuthorizingRealm {
	/**
	 * 	授权
	 * */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            //添加角色
        simpleAuthorizationInfo.addRole("admin");
        simpleAuthorizationInfo.addStringPermission("add");
        return simpleAuthorizationInfo;
	}

	/***
	 * 	认证
	 * */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		String username = authenticationToken.getPrincipal().toString();
		if(!"123".equals(username)) {
			throw new AccountException("账号找不到");
		}
		char[] password = (char[]) authenticationToken.getCredentials();
		SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username,password,username);
		return simpleAuthenticationInfo;
	}

}

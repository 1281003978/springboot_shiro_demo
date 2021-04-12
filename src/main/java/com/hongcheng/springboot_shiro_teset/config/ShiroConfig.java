package com.hongcheng.springboot_shiro_teset.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.hongcheng.springboot_shiro_teset.dao.MySessionDao;
import com.hongcheng.springboot_shiro_teset.shiro.CustomRealm;
import com.hongcheng.springboot_shiro_teset.shiro.CustomRealm2;

@Configuration
public class ShiroConfig {
	
    /**
     * 	启动代理  <br>
     * 	作用就是让controller中的@RequiresRoles("admin")、@RequiresPermissions("add")这些权限注解生效  <br>
     * 	如果不用注解权限，这里可以删掉  <br>
     * */
    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }
    
    /**
     * 	加入注解的使用，不加入这个注解不生效	<br>
     * 	作用就是让controller中的@RequiresRoles("admin")、@RequiresPermissions("add")这些权限注解生效  <br>
     * 	如果不用注解权限，这里可以删掉  <br>
     * */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    

    /**
     * 	将自己的验证方式加入容器     <br>
     * 	Realm接口是shiro的一个重要接口，主要负责获取认证和授权信息
     * */
    @Bean
    public CustomRealm myShiroRealm() {
        CustomRealm customRealm = new CustomRealm();
        /** 密码比较器，shiro有定义了不少hash类型的密码比较器，我们也可以自己重写个自定义的密码比较器，只需要
         * 继承SimpleCredentialsMatcher这个类，同时重写doCredentialsMatch()这个方法就好了
         * shiro会在org.apache.shiro.realm.AuthenticatingRealm.assertCredentialsMatch(AuthenticationToken, AuthenticationInfo)这个方法里面
      	 * 调用这个密码比较器进行密码的比较
      	 * */
        // 如果不用自定义密码比较，md5CredentialsMatcher可以删掉
        HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher();
        md5CredentialsMatcher.setHashIterations(20);
        md5CredentialsMatcher.setHashAlgorithmName(Md5Hash.ALGORITHM_NAME);
        customRealm.setCredentialsMatcher(md5CredentialsMatcher);
        return customRealm;
    }
    
    
    /**
     * 	Realm可以有对多个，shiro可以根据不同的认证策略，搭配多个Realm进行认证  <br>
     * 	如果不用多realm验证，这里可以删掉 <br>
     * */
    @Bean
    public CustomRealm2 myShiroRealm2() {
        CustomRealm2 customRealm = new CustomRealm2();
        return customRealm;
    }

    /**
     * 	权限管理，配置主要是Realm的管理认证     <br>
     * 	SecurityManager是shiro的一个重要接口，负责管理整个shiro的权限控制      <br>
     * 	所以我们需要给SecurityManager进行个性化配置
     * */
    @Bean
    public SecurityManager securityManager(RedisTemplate<String, Object> redisTemplate) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        /***
         * 	多realm验证
         * */
        List<Realm> realmList = new LinkedList<Realm>();
        realmList.add(myShiroRealm());
        realmList.add(myShiroRealm2());
        securityManager.setRealms(realmList);
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        /**
         * 	认证策略不管选择哪种，都会全部执行完，然后判断是否通过认证
         * */
        authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        authenticator.setRealms(realmList);
        securityManager.setAuthenticator(authenticator);
        /**
         * 	session管理
         * 	sessionManager如果不需要自定义，可以忽略，使用默认的
         * */
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdUrlRewritingEnabled(true);		// 允许sessionId进行url重写
        /**
         * 	shiro的session是会定期进行删除的	
         * 	详情可以看这里：org.apache.shiro.session.mgt.AbstractValidatingSessionManager.enableSessionValidation()
         * 	org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler.enableSessionValidation()
         * */
        sessionManager.setGlobalSessionTimeout(10000);				// session超时时长
        sessionManager.setDeleteInvalidSessions(true);				// 删除失效session
        sessionManager.setSessionValidationSchedulerEnabled(true);	// 允许定期删除
        sessionManager.setSessionValidationInterval(15000);			// session定期删除时间
        /**
         * 	session保存进redis里
         * 	sessionDao如果不需要，可以删掉
         * */
        MySessionDao sessionDao = new MySessionDao(redisTemplate);
        sessionDao.setSessionIdGenerator(new JavaUuidSessionIdGenerator());		// sessionId生成器
        sessionManager.setSessionDAO(sessionDao);
        
        securityManager.setSessionManager(sessionManager);
        
        return securityManager;
    }

    /**
     * 	Filter工厂，设置对应的过滤条件和跳转条件。shiro的权限控制，就是通过filter来控制的
详情看org.apache.shiro.web.filter.mgt.DefaultFilter

Filter Name			Class
anon				org.apache.shiro.web.filter.authc.AnonymousFilter
authc				org.apache.shiro.web.filter.authc.FormAuthenticationFilter
authcBasic			org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
authcBearer			org.apache.shiro.web.filter.authc.BearerHttpAuthenticationFilter
logout				org.apache.shiro.web.filter.authc.LogoutFilter
noSessionCreation	org.apache.shiro.web.filter.session.NoSessionCreationFilter
perms				org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
port				org.apache.shiro.web.filter.authz.PortFilter
rest				org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter
roles				org.apache.shiro.web.filter.authz.RolesAuthorizationFilter
ssl					org.apache.shiro.web.filter.authz.SslFilter
user				org.apache.shiro.web.filter.authc.UserFilter
     * */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
    	/**
    	 * 	请求资源的过滤
    	 * 	这一块是必须的，不能去掉
    	 * */
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> map = new HashMap<>();
        //登出
        map.put("/logout", "logout");
        // 对login这个地址匿名访问
        map.put("/login*", "anon");
        map.put("/error*", "anon");
        //对所有用户认证
        map.put("/**", "authc");
        //登录
        shiroFilterFactoryBean.setLoginUrl("/login.html");
        //首页
        shiroFilterFactoryBean.setSuccessUrl("/index.html");
        //错误页面，认证不通过跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/error.html");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }
}

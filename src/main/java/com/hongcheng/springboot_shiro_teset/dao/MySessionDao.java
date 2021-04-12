package com.hongcheng.springboot_shiro_teset.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 	对session的一个持久化处理。<br>
 * 	将默认的ecache缓存换成了redis，可以作为一个分布式session共享实现
 * */
public class MySessionDao extends EnterpriseCacheSessionDAO{

	
	private RedisTemplate<String, Object> redisTemplate;
	
	
	/**
	 * 	这里需要通过传值，将redisTemplate传入。	<br>
	 * 	用@autowried注入是会不行的，因为MySessionDao   <br>
	 * 	是由我们自己new的，所以spring不会处理他的属性，另外，这里用applicationContent的getBean()  <br>
	 * 	也是会出现问题的，MySessionDao是在shiroConfig中的一个bean中创建的，他的创建顺序可能先于    <br>
	 * 	applicationContent的注入。
	 * */
	public MySessionDao (RedisTemplate<String, Object> redisTemplate) {
		setCacheManager(null);
		this.redisTemplate = redisTemplate;
	}
	
	/**
	 * 	从redis里面取
	 * */
	protected Session doReadSession(Serializable sessionId) {
    	System.err.println("获取Session" + sessionId);
		Object object = redisTemplate.opsForHash().get("shiro-session", sessionId.toString());
		return object == null?null:(Session)object; 
    }

	/**
	 * 	更新redis
	 * */
    protected void doUpdate(Session session) {
    	System.err.println("更新Session" + session.getId());
    	Serializable sessionId = session.getId();
    	redisTemplate.opsForHash().put("shiro-session", sessionId.toString(),session);
    }

    /**
     * 	删除redis
     * */
    protected void doDelete(Session session) {
    	System.err.println("删除Session" + session.getId());
    	Serializable sessionId = session.getId();
    	redisTemplate.opsForHash().delete("shiro-session", sessionId.toString());
    }
    
    
    /**
     * 	获取全部session，判断是否过期，如果过期，删除	<br>
     * 	默认实现：org.apache.shiro.session.mgt.eis.CachingSessionDAO.getActiveSessions()	<br>
     * 	默认实现是从CacheManager得缓存里面取的，但是我们这里的没有设置CacheManager，所以一直为空	<br>
     * 	导致redis中已经过期了的session一直没有被清理，所以这里我们要重写
     * */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Session> getActiveSessions() {
    	List values = redisTemplate.opsForHash().values("shiro-session");
		return values;
    }
	
}

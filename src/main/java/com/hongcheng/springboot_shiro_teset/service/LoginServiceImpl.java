package com.hongcheng.springboot_shiro_teset.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;

import com.hongcheng.springboot_shiro_teset.bean.Permission;
import com.hongcheng.springboot_shiro_teset.bean.Role;
import com.hongcheng.springboot_shiro_teset.bean.User;

@Service
public class LoginServiceImpl implements LoginService {
	@Override
    public User getUserByName(String getMapByName) {
        //模拟数据库查询，正常情况此处是从数据库或者缓存查询。
        return getMapByName(getMapByName);
    }

    /**
     * 模拟数据库查询
     * @param userName
     * @return
     */
    private User getMapByName(String userName){
        //共添加两个用户，两个用户都是admin一个角色，
        //chc有query和add权限，zhangsan只有一个query权限
        Set<Permission> permissionsSet = new HashSet<>();
        permissionsSet.add(new Permission("1","query"));
        permissionsSet.add(new Permission("2","add"));
        
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role("1","admin",permissionsSet));
        
        User user = new User("1","chc",encrypt("123456","chc"),roleSet);
        Map<String ,User> map = new HashMap<>();
        map.put(user.getUserName(), user);

        
        
        Set<Permission> permissionsSet1 = new HashSet<>();
        permissionsSet1.add(new Permission("3","query"));
        
        
        Set<Role> roleSet1 = new HashSet<>();
        roleSet1.add(new Role("2","user",permissionsSet1));
        
        
        User user1 = new User("2","zhangsan",encrypt("123456","zhangsan"),roleSet1);
        map.put(user1.getUserName(), user1);
        
        
        return map.get(userName);
    }
    
    
    /***
     * 	模拟加密
     * */
    private String encrypt(String password,String name) {
		String algorithmName = Md5Hash.ALGORITHM_NAME;	// 加密算法
		String credentials = password;						// 密码
		String salt = name;									// 加密盐值
		int hashIterations = 20;							// 加密次数
		SimpleHash simpleHash = new SimpleHash(algorithmName, credentials, salt, hashIterations);
		return simpleHash.toString();
	}
	
    
}

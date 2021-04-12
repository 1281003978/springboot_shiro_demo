package com.hongcheng.springboot_shiro_teset.bean;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 	用户
 * */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class User {
    private String id;
    private String userName;
    private String password;
    /**
     * 用户对应的角色集合
     */
    private Set<Role> roles;
}
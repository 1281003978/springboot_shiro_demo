package com.hongcheng.springboot_shiro_teset.bean;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 	角色
 * */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Role {

    private String id;
    private String roleName;
    /**
     * 角色对应权限集合
     */
    private Set<Permission> permissions;
}
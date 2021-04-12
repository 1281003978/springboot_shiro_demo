package com.hongcheng.springboot_shiro_teset.bean;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 	权限，shiro的权限，其实也只是一种资源的标识符，你拥有这个权限，就说明你有这个标识符，那你就可以访问这个资源
 * */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Permission {
	private String id;
    private String permissionName;
}

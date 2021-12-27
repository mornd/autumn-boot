package com.mornd.system.entity.vo;

import com.mornd.system.entity.po.SysPermission;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @author mornd
 * @dateTime 2021/11/9 - 9:41
 */
@Data
public class SysPermissionVO extends PageInfoResult {
    private String id;
    //父id
    private String parentId;
    //菜单标题
    private String title;
    //前端路由对象name值
    private String name;
    //前端路由对象path值
    private String path;
    //前端路由对象component组件路径
    private String component;
    //权限编码
    private String code;
    //图标
    private String icon;
    //排序
    private Double sort;
    //是否保持激活(0：否 1：是)
    private Integer keepAlive;
    //是否要求权限
    private Integer requireAuth;
    //是否启用(1：启用0：禁用)
    private Integer enabled;
    //是否隐藏菜单(0：隐藏，1：显示)
    private Integer hidden;
    //菜单类型(0:一级菜单;1:子菜单;2:按钮权限)
    private Integer menuType;
    //是否路由菜单: 0:不是  1:是（默认值1）
    private Integer isRoute;
    //创建时间
    private Date gmtCreate;
    //创建人
    private String createBy;
    //修改时间
    private Date gmtModified;
    //修改人
    private String modifiedBy;

    //子菜单
    private Set<SysPermission> children;

    //用于响应前端 判断是否存在子集
    private Boolean hasChildren;
}

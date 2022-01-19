package com.mornd.system.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.validation.UpdateValidGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mornd
 * @dateTime 2021/8/10 - 10:20
 * 系统用户
 */
@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true) //callSuper = true 会连同父类的属性加在一起生成equals(Object o)和hashCode()方法
@ApiModel("系统用户")
public class SysUser extends BaseEntity implements UserDetails {

    //@TableId(type = IdType.ASSIGN_UUID) ASSIGN_UUID =>a794d53804816017978dba76b05b19a1
    @TableId(type = IdType.ASSIGN_ID) // ASSIGN_ID =>1425004256210038785
    @NotBlank(message = "ID不能为空",groups = {UpdateValidGroup.class})
    private String id;

    @NotBlank(message = "登录名不能为空")
    @ApiModelProperty("用户登录名称")
    private String loginName;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String  password;

    @ApiModelProperty("真实名称")
    private String realName;

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("出生日期")
    private Date birthday;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("账号状态 0：禁用，1：正常")
    private Integer status;

    /*@ApiModelProperty("账号是否锁定 0：锁定 1：正常")
    private Integer accountLocked;*/

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("邮箱")
    private String email;

    //角色集合
    @TableField(exist = false)
    Set<SysRole> roles;

    //菜单权限集合
    @TableField(exist = false)
    Set<SysPermission> permissions;

    /**
     * 设置角色、权限数据
     * @return
     */
    @Override
    //注解作用：序列化时忽略该方法 用于将该对象存入redis中 不加该注解，redis反序列化时会报错
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(ObjectUtils.isEmpty(this.roles)) {
            return null;
        }
        List<SimpleGrantedAuthority> authorities = this.roles.stream().map(role ->
                new SimpleGrantedAuthority(SecurityConst.ROLE_PREFIX + role.getCode()))
                .collect(Collectors.toList());
        if(!ObjectUtils.isEmpty(authorities)){
            this.permissions.forEach(item -> {
                if(!StringUtils.isBlank(item.getCode())){
                    authorities.add(new SimpleGrantedAuthority(item.getCode()));
                }
            });
        }
        return authorities;
    }

    /**
     * 用户名
     * @return
     */
    @Override
    //加注解代表该'username'属性不需要序列化
    @JsonIgnore
    public String getUsername() {
        return this.loginName;
    }

    /**
     * 账号是否未过期
     * @return
     */
    @Override
    //加注解代表该'accountNonExpired'属性不需要序列化
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账号是否未被锁定
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 证书是否未过期
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账号是否启用
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return EnableState.ENABLE.getCode().equals(this.status);
    }
}

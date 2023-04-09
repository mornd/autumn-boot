package com.mornd.system.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mornd.system.constant.EntityConst;
import com.mornd.system.constant.SecurityConst;
import com.mornd.system.entity.po.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:37
 * security 用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser implements UserDetails, Serializable {
    private static final long serialVersionUID = 42L;

    public AuthUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    private SysUser sysUser;

    /**
     * ip
     */
    private String ip;
    /**
     * 操作地址
     */
    private String address;
    /**
     * 登录时间
     */
    private Date loginTime;
    /**
     * 操作系统
     */
    private String os;
    /**
     * 浏览器
     */
    private String browser;

    @Override
    @JsonIgnore // 注解作用：序列化时忽略该方法 用于将该对象存入redis中 不加该注解，redis反序列化时会报错
    //@JsonDeserialize(using = AuthorityDeserializer.class)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 获取角色的编码值，添加至权限集合 authorities 中
        List<SimpleGrantedAuthority> roleAuthorities = new ArrayList<>();
        if(sysUser.getRoles() != null) {
            // 添加角色的编码值，并添加前缀ROLE_
            roleAuthorities.addAll(this.sysUser.getRoles().stream()
                    .map(code -> new SimpleGrantedAuthority(SecurityConst.ROLE_PREFIX + code))
                    .collect(Collectors.toList()));
        }
        if(sysUser.getPermissions() != null) {
            // 添加菜单权限的编码值
            roleAuthorities.addAll(this.sysUser.getPermissions().stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }
        return roleAuthorities;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return sysUser.getLoginName();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return sysUser.getPassword();
    }

    /**
     * 帐户是否未过期
     * @return
     */
    @Override
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
        return EntityConst.ENABLED.equals(sysUser.getStatus());
    }
}

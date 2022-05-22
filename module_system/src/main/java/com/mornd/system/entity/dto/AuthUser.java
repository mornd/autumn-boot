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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
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
    
    private SysUser sysUser;

    @Override
    @JsonIgnore // 注解作用：序列化时忽略该方法 用于将该对象存入redis中 不加该注解，redis反序列化时会报错
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(ObjectUtils.isEmpty(sysUser.getRoles())) {
            return null;
        }
        // 获取角色的编码值，添加至权限集合 authorities 中
        List<SimpleGrantedAuthority> roleAuthorities = sysUser.getRoles().stream()
                .filter(r -> StringUtils.hasText(r.getCode()))
                .map(r -> new SimpleGrantedAuthority(SecurityConst.ROLE_PREFIX + r.getCode()))
                .collect(Collectors.toList());
        
        // 获取菜单权限的编码值，添加至权限集合 authorities 中
        List<SimpleGrantedAuthority> perAuthorities = sysUser.getPermissions().stream()
                .filter(p -> StringUtils.hasText(p.getCode()))
                .map(p -> new SimpleGrantedAuthority(p.getCode()))
                .collect(Collectors.toList());
        // 合并
        roleAuthorities.addAll(perAuthorities);
        
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

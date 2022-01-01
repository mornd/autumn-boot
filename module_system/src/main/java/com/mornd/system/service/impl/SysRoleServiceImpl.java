package com.mornd.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.entity.po.SysRole;
import com.mornd.system.entity.po.base.BaseEntity;
import com.mornd.system.mapper.SysRoleMapper;
import com.mornd.system.service.SysRoleService;
import com.mornd.system.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author mornd
 * @dateTime 2021/8/10 - 16:28
 */
@Service
@Transactional
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    private Integer enabled = BaseEntity.EnableState.ENABLE.getCode();
    private Integer disabled = BaseEntity.EnableState.DISABLE.getCode();
    
    @Override
    public Set<SysRole> findByUserId(String userId, Integer enabled) {
        return baseMapper.findByUserId(userId, enabled);
    }

    /**
     * 工具方法：获取当前用户的所有可用权限
     * @return
     */
    public Set<SysRole> getCurrentRoles() {
        return  findByUserId( SecurityUtil.getLoginUserId(), enabled);
    }

    /**
     * 工具方法：获取当前用户的角色id集合
     * @return
     */
    public List<String> getCurrentRoleIds() {
        Set<SysRole> currentRoles = getCurrentRoles();
        if(!ObjectUtils.isEmpty(currentRoles)) {
            List<String> ids = new ArrayList<>();
            currentRoles.forEach(i -> ids.add(i.getId()));
            return ids;
        }
        return null;
    }
}

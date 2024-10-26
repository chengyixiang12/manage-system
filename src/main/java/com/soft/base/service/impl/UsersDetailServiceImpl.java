package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.soft.base.entity.SysUser;
import com.soft.base.exception.ServiceException;
import com.soft.base.mapper.SysRoleMapper;
import com.soft.base.mapper.SysUsersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.soft.base.constants.BaseConstant.*;

/**
* @author cyq
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-09-30 15:49:52
*/
@Service
@Slf4j
public class UsersDetailServiceImpl implements UserDetailsService{

    private final SysUsersMapper sysUsersMapper;

    private final SysRoleMapper sysRoleMapper;

    @Autowired
    public UsersDetailServiceImpl(SysUsersMapper sysUsersMapper, SysRoleMapper sysRoleMapper) {
        this.sysUsersMapper = sysUsersMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUsersMapper.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, username));
        if (sysUser == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("用户“" + username + "”不存在");
        } else if (DEL_FLAG_UNEXIST.equals(sysUser.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException("用户“" + username + "”已删除");
        } else if (ENABLED_FALSE.equals(sysUser.getEnabled())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("用户“" + username + "”已停用");
        }
        // 角色集合
        List<String> roleCodes = sysRoleMapper.getRoleCodeByUserId(sysUser.getId());

        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                sysUser.getEnabled(),
                sysUser.getAccountNonExpired(),
                sysUser.getCredentialsNonExpired(),
                sysUser.getAccountNonLocked(),
                roleCodes.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }
}





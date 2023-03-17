package com.zhang.faslbadmin.admin.model.bo;

import com.zhang.faslbadmin.admin.model.po.FasUserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  19:26
 */
public class AdminUserDetails implements UserDetails {

    /**
     * 后台用户
     */
    private final FasUserAccount user;

    public AdminUserDetails(FasUserAccount fasUser) {
        this.user = fasUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getState().equals(0);
    }
}

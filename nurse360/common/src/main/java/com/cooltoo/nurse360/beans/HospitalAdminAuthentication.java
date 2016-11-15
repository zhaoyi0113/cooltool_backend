package com.cooltoo.nurse360.beans;

import com.cooltoo.nurse360.constants.AdminRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/15.
 */
public class HospitalAdminAuthentication implements Authentication {

    HospitalAdminBean admin;

    public HospitalAdminAuthentication(HospitalAdminBean bean) {
        this.admin = bean;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Object tmpRoles = admin.getProperty(HospitalAdminBean.ROLE);
        if (null==tmpRoles) {
            return authorities;
        }

        List<AdminRole> roles = (List<AdminRole>) tmpRoles;
        for (AdminRole tmp : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+tmp.name()));
        }
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return admin.getPassword();
    }

    @Override
    public Object getDetails() {
        return this;
    }

    @Override
    public Object getPrincipal() {
        return admin.getName();
    }

    @Override
    public boolean isAuthenticated() {
        return admin.isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        admin.setAuthenticated(isAuthenticated);
    }

    @Override
    public String getName() {
        return admin.getName();
    }
}

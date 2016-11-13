package com.cooltoo.nurse360.filters.hospital;

import com.cooltoo.nurse360.entities.HospitalAdminEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaoyi0113 on 13/11/2016.
 */
public class HospitalUserAuthentication implements Authentication{

    private HospitalAdminEntity entity;
    private boolean authenticated = true;

    public HospitalUserAuthentication(HospitalAdminEntity entity) {
        this.entity = entity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //TODO: read role from database
        Set<GrantedAuthority> authorites = new HashSet<>();
        authorites.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authorites;
    }

    @Override
    public Object getCredentials() {
        return entity.getPassword();
    }

    @Override
    public Object getDetails() {
        return entity;
    }

    @Override
    public Object getPrincipal() {
        return entity.getName();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return entity.getName();
    }
}

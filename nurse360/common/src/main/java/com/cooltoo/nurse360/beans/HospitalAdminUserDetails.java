package com.cooltoo.nurse360.beans;

import com.cooltoo.beans.AdminUserBean;
import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.nurse360.constants.AdminRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Created by zhaolisong on 2016/11/25.
 */
public class HospitalAdminUserDetails implements UserDetails {

    public static final String HOSPITAL_ID = "HOSPITAL_ID";
    public static final String DEPARTMENT_ID = "DEPARTMENT_ID";

    private boolean isAuthenticated = true;
    private Object userBean; /* nursego_admin_user, cooltoo_nurse Bean*/
    private Map<String, Object> properties = new HashMap<>();

    //====================================================================
    //            getter and setter
    //====================================================================
    public long getId() {
        if (userBean instanceof AdminUserBean) {
            return ((AdminUserBean) userBean).getId();
        }
        else if (userBean instanceof NurseBean) {
            return ((NurseBean)userBean).getId();
        }
        return 0;
    }

    public Object getUserBean() {
        return userBean;
    }

    public void setUserBean(Object userBean) {
        this.userBean = userBean;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    //====================================================================
    //            is Administrator, NurseManager, Nurse
    //====================================================================
    public boolean isAdmin() {
        return userBean instanceof AdminUserBean;
    }

    public boolean isNurseManager() {
        if (userBean instanceof NurseBean) {
            UserAuthority authority = ((NurseBean) userBean).getAuthority();
            NurseExtensionBean extension = (NurseExtensionBean) ((NurseBean) userBean).getProperty(NurseBean.INFO_EXTENSION);

            return UserAuthority.AGREE_ALL.equals(authority)
                    && (null!=extension && YesNoEnum.YES.equals(extension.getIsManager()));
        }
        return false;
    }

    public boolean isNurse() {
        if (userBean instanceof NurseBean) {
            UserAuthority authority = ((NurseBean) userBean).getAuthority();
            NurseExtensionBean extension = (NurseExtensionBean) ((NurseBean) userBean).getProperty(NurseBean.INFO_EXTENSION);

            return UserAuthority.AGREE_ALL.equals(authority)
                    && (null!=extension && !YesNoEnum.YES.equals(extension.getIsManager()));
        }
        return false;
    }

    public AdminUserType getAdminType() {
        if (isAdmin()) {
            return AdminUserType.ADMINISTRATOR;
        }
        else if (isNurseManager()) {
            return AdminUserType.MANAGER;
        }
        else if (isNurse()) {
            return AdminUserType.NORMAL;
        }
        return null;
    }

    public List<AdminRole> getAdminRole() {
        List<AdminRole> roles = new ArrayList<>();
        if (isAdmin()) {
            roles.add(AdminRole.ADMIN);
            roles.add(AdminRole.MANAGER);
            roles.add(AdminRole.NURSE);
        } else if (isNurseManager()) {
            roles.add(AdminRole.MANAGER);
            roles.add(AdminRole.NURSE);
        } else if (isNurse()) {
            roles.add(AdminRole.NURSE);
        }
        return roles;
    }

    //====================================================================
    //            UserDetails interface
    //====================================================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<AdminRole> roles = getAdminRole();
        for (AdminRole tmp : roles) {
            authorities.add(new SimpleGrantedAuthority(tmp.name()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        if (userBean instanceof AdminUserBean) {
            return ((AdminUserBean) userBean).getPassword();
        }
        else if (userBean instanceof NurseBean) {
            return ((NurseBean)userBean).getPassword();
        }
        return "UNKNOWN";
    }

    @Override
    public String getUsername() {
        if (userBean instanceof AdminUserBean) {
            return ((AdminUserBean) userBean).getUserName();
        }
        else if (userBean instanceof NurseBean) {
            return ((NurseBean)userBean).getMobile();
        }
        return "UNKNOWN";
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
        if (userBean instanceof AdminUserBean) {
            return true;
        }
        else if (userBean instanceof NurseBean) {
            UserAuthority authority = ((NurseBean) userBean).getAuthority();
            RegisterFrom registerFrom = ((NurseBean) userBean).getRegisterFrom();

            return UserAuthority.AGREE_ALL.equals(authority) && RegisterFrom.GO2NURSE.equals(registerFrom);
        }
        return false;
    }

    // ==========================================================================
    //  Authentication
    // ==========================================================================

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }
}

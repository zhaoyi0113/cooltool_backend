package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.hospital.service.HospitalAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Created by zhaoyi0113 on 13/11/2016.
 */
@Component
public class HospitalUserDetailService implements UserDetailsService {

    @Autowired
    private HospitalAdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HospitalAdminBean one = adminService.getAdminUserWithoutInfo(username, CommonStatus.ENABLED);
        if (one == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return one;
    }
}

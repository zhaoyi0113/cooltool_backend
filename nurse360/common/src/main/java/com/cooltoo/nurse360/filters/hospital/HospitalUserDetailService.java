package com.cooltoo.nurse360.filters.hospital;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.entities.HospitalAdminEntity;
import com.cooltoo.nurse360.repository.HospitalAdminRepository;
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
    private HospitalAdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HospitalAdminEntity entity = adminRepository.findFirstByNameAndStatus(username, CommonStatus.ENABLED);
        if (entity == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return entity;
    }
}

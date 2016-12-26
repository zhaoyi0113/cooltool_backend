package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.beans.AdminUserBean;
import com.cooltoo.beans.NurseAuthorizationBean;
import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.AdminUserType;
import com.cooltoo.converter.NurseBeanConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.services.AdminUserService;
import com.cooltoo.services.CommonNurseAuthorizationService;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.services.CommonNurseService;
import com.cooltoo.services.NurseExtensionService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhaoyi0113 on 13/11/2016.
 */
@Component
public class HospitalUserDetailService implements UserDetailsService {

    @Autowired private AdminUserService adminUserService;
    @Autowired private CommonNurseService nurseService;
    @Autowired private NurseBeanConverter nurseBeanConverter;
    @Autowired private NurseExtensionService nurseExtensionService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelationService;
    @Autowired private CommonNurseAuthorizationService nurseAuthorizationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUserBean admin = adminUserService.getUser(username);
        List<NurseEntity> nurseEntity = nurseService.getNurseByMobile(username);
        NurseBean nurseBean = VerifyUtil.isListEmpty(nurseEntity) ? null : nurseBeanConverter.convert(nurseEntity.get(0));

        return getUserDetail(username, admin, nurseBean);
    }

    public HospitalAdminUserDetails getUser(AdminUserType adminUserType, long adminUserId) {
        HospitalAdminUserDetails userDetails = null;
        if (AdminUserType.ADMINISTRATOR.equals(adminUserType)) {
            AdminUserBean admin = adminUserService.getUser(adminUserId);
            userDetails = new HospitalAdminUserDetails();
            userDetails.setUserBean(admin);
            return userDetails;
        }
        else if (AdminUserType.MANAGER.equals(adminUserType) || AdminUserType.NORMAL.equals(adminUserType)) {
            NurseEntity nurseEntity = nurseService.getNurseById(adminUserId);
            NurseBean nurseBean = null==nurseEntity ? null : nurseBeanConverter.convert(nurseEntity);
            userDetails = new HospitalAdminUserDetails();

            NurseExtensionBean extension = nurseExtensionService.getExtensionByNurseId(nurseBean.getId());
            NurseHospitalRelationBean nurseHospital = nurseHospitalRelationService.getRelationWithoutOtherInfoByNurseId(nurseBean.getId());
            NurseAuthorizationBean authorization = nurseAuthorizationService.getAuthorizationByNurseId(nurseBean.getId());

            nurseBean.setProperty(NurseBean.INFO_EXTENSION, extension);
            nurseBean.setProperty(NurseBean.AUTHORIZATION, authorization);
            userDetails.setUserBean(nurseBean);
            if (null!=nurseHospital) {
                userDetails.setProperty(HospitalAdminUserDetails.HOSPITAL_ID, nurseHospital.getHospitalId());
                userDetails.setProperty(HospitalAdminUserDetails.DEPARTMENT_ID, nurseHospital.getDepartmentId());
            }
            userDetails.setUserBean(nurseBean);

            return userDetails;
        }
        return userDetails;
    }

    private HospitalAdminUserDetails getUserDetail(String userName, AdminUserBean admin, NurseBean nurseBean) {
        if (null==admin && null==nurseBean) {
            throw new UsernameNotFoundException(userName + " not found");
        }

        HospitalAdminUserDetails userDetails = null;
        if (null!=nurseBean) {
            userDetails = new HospitalAdminUserDetails();

            NurseExtensionBean extension = nurseExtensionService.getExtensionByNurseId(nurseBean.getId());
            NurseHospitalRelationBean nurseHospital = nurseHospitalRelationService.getRelationWithoutOtherInfoByNurseId(nurseBean.getId());
            NurseAuthorizationBean authorization = nurseAuthorizationService.getAuthorizationByNurseId(nurseBean.getId());

            nurseBean.setProperty(NurseBean.INFO_EXTENSION, extension);
            nurseBean.setProperty(NurseBean.AUTHORIZATION, authorization);
            if (null!=nurseHospital) {
                userDetails.setProperty(HospitalAdminUserDetails.HOSPITAL_ID, nurseHospital.getHospitalId());
                userDetails.setProperty(HospitalAdminUserDetails.DEPARTMENT_ID, nurseHospital.getDepartmentId());
            }
            userDetails.setUserBean(nurseBean);

            return userDetails;
        }
        else if (null!=admin) {
            userDetails = new HospitalAdminUserDetails();
            userDetails.setUserBean(admin);
            return userDetails;
        }

        return userDetails;
    }
}

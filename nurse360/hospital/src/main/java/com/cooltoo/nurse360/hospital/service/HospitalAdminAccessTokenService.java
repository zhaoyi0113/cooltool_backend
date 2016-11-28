package com.cooltoo.nurse360.hospital.service;

import com.cooltoo.beans.AdminUserTokenAccessBean;
import com.cooltoo.beans.NurseTokenAccessBean;
import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.services.AdminUserService;
import com.cooltoo.services.AdminUserTokenAccessService;
import com.cooltoo.services.CommonNurseService;
import com.cooltoo.services.NurseTokenAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by zhaolisong on 2016/11/10.
 */
@Service("HospitalAdminAccessTokenService")
public class HospitalAdminAccessTokenService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalAdminAccessTokenService.class);

    @Autowired private NurseTokenAccessService nurseAccessTokenService;
    @Autowired private AdminUserTokenAccessService adminUserTokenAccessService;
    @Autowired private AdminUserService adminUserService;
    @Autowired private CommonNurseService nurseService;

    @Transactional
    public String addToken(AdminUserType adminType, String nameOrMobile, String password) {
        String token = null;
        if (AdminUserType.ADMINISTRATOR.equals(adminType)) {
            AdminUserTokenAccessBean adminToken = this.adminUserTokenAccessService.addToken(nameOrMobile, password);
            token = adminToken.getToken();
        }
        else if (AdminUserType.MANAGER.equals(adminType) || AdminUserType.NORMAL.equals(adminType)) {
            NurseTokenAccessBean adminToken = this.nurseAccessTokenService.addToken(nameOrMobile/* is mobile number */, password);
            token = adminToken.getToken();
        }
        return token;
    }

    @Transactional
    public void setTokenDisable(AdminUserType adminType, String token) {
        if (AdminUserType.ADMINISTRATOR.equals(adminType)) {
            adminUserTokenAccessService.setTokenDisable(token);
        }
        else if (AdminUserType.MANAGER.equals(adminType) || AdminUserType.NORMAL.equals(adminType)) {
            nurseAccessTokenService.setTokenDisable(token);
        }
        return;
    }

    public boolean isTokenEnable(AdminUserType adminType, String token) {
        if (AdminUserType.ADMINISTRATOR.equals(adminType)) {
            return adminUserTokenAccessService.isTokenEnable(token);
        }
        else if (AdminUserType.MANAGER.equals(adminType) || AdminUserType.NORMAL.equals(adminType)) {
            return nurseAccessTokenService.isTokenEnable(token);
        }
        return false;
    }

    public Long getUserIdByToken(AdminUserType adminUserType, String token) {
        // get admin by token
        if (AdminUserType.ADMINISTRATOR.equals(adminUserType)) {
            AdminUserTokenAccessBean tokenAccess = adminUserTokenAccessService.getToken(token);
            if (null==tokenAccess) {
                return null;
            }
            if (!CommonStatus.ENABLED.equals(tokenAccess.getStatus())) {
                return null;
            }
            if (!adminUserService.existUser(tokenAccess.getUserId())) {
                return null;
            }
            return tokenAccess.getUserId();
        }
        // get nurse manager and nurse
        else if (AdminUserType.MANAGER.equals(adminUserType) || AdminUserType.NORMAL.equals(adminUserType)) {
            NurseTokenAccessBean tokenAccess = nurseAccessTokenService.getToken(token);
            if (null==tokenAccess) {
                return null;
            }
            if (!CommonStatus.ENABLED.equals(tokenAccess.getStatus())) {
                return null;
            }
            if (!nurseService.existNurse(tokenAccess.getUserId())) {
                return null;
            }
            return tokenAccess.getUserId();
        }
        return null;
    }
}

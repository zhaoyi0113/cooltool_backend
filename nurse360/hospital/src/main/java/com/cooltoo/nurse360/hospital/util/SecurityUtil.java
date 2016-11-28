package com.cooltoo.nurse360.hospital.util;

import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.util.VerifyUtil;
import org.springframework.security.core.Authentication;

/**
 * Created by zhaolisong on 2016/11/28.
 */
public class SecurityUtil {

    public static SecurityUtil newInstance() {
        return new SecurityUtil();
    }

    public HospitalAdminUserDetails getUserDetails(Authentication authentication) {
        if (authentication instanceof HospitalAdminAuthentication) {
            HospitalAdminAuthentication adminAuthentication = (HospitalAdminAuthentication) authentication;
            if (adminAuthentication.getDetails() instanceof HospitalAdminUserDetails) {
                return (HospitalAdminUserDetails) adminAuthentication.getDetails();
            }
        }
        return null;
    }

    public Integer[] getHospitalDepartment(String strHospitalId, String strDepartmentId, HospitalAdminUserDetails userDetails) {
        Integer hospitalId   = 0;
        Integer departmentId = 0;
        if (userDetails instanceof HospitalAdminUserDetails) {
            if (userDetails.isAdmin()) {
                hospitalId   = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
                departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
            } else {
                hospitalId   = (Integer) userDetails.getProperty(HospitalAdminUserDetails.HOSPITAL_ID);
                departmentId = (Integer) userDetails.getProperty(HospitalAdminUserDetails.DEPARTMENT_ID);
                hospitalId   = null != hospitalId ? hospitalId : 0;
                departmentId = null != departmentId ? departmentId : 0;
            }
        }
        return new Integer[]{hospitalId, departmentId};
    }

    public Long[] getHospitalDepartmentLongId(String strHospitalId, String strDepartmentId, HospitalAdminUserDetails userDetails) {
        Long hospitalId   = 0L;
        Long departmentId = 0L;
        if (userDetails instanceof HospitalAdminUserDetails) {
            if (userDetails.isAdmin()) {
                hospitalId   = VerifyUtil.isIds(strHospitalId)   ? VerifyUtil.parseLongIds(strHospitalId).get(0) : null;
                departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseLongIds(strDepartmentId).get(0) : null;
            } else {
                Integer iHospitalId   = (Integer) userDetails.getProperty(HospitalAdminUserDetails.HOSPITAL_ID);
                Integer iDepartmentId = (Integer) userDetails.getProperty(HospitalAdminUserDetails.DEPARTMENT_ID);
                hospitalId   = null != iHospitalId   ? new Long(iHospitalId)   : 0;
                departmentId = null != iDepartmentId ? new Long(iDepartmentId) : 0;
            }
        }
        return new Long[]{hospitalId, departmentId};
    }
}

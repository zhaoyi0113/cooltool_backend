package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.ViewVendorPatientRelationBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.WhoDenyPatient;
import com.cooltoo.go2nurse.repository.ViewVendorPatientRelationRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhaolisong on 2016/11/24.
 */
@Service("ViewVendorPatientRelationService")
public class ViewVendorPatientRelationService {

    private static final Logger logger = LoggerFactory.getLogger(ViewVendorPatientRelationService.class);
    private static final String PROP_FORBIDDEN = "forbidden";
    private static final String PROP_ORDER_NUMBER   = "order_number";
    private static final String PROP_HOSPITAL   = "hospital";
    private static final String PROP_DEPARTMENT = "department";

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ViewVendorPatientRelationRepository repository;
    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;

    @Autowired private Go2NurseUtility utility;
    @Autowired private DenyPatientService denyPatientService;


    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countVendorsPatientByCondition(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, String userOrPatientName) {
        if (VerifyUtil.isStringEmpty(userOrPatientName)) { userOrPatientName = null; }
        else { userOrPatientName = userOrPatientName.replace('%', '\0').trim()+"%"; }
        List<Object[]> set = repository.findByConditions(vendorType, vendorId, vendorDepartId, userOrPatientName);
        long count = VerifyUtil.isListEmpty(set) ? 0 : set.size();
        logger.info("count vendor's patient by vendorId={} vendorDepartId={} vendorType={} userOrPatientName={}, count is {}",
                vendorId, vendorDepartId, vendorType, userOrPatientName, count);
        return count;
    }

    public List<ViewVendorPatientRelationBean> getVendorsPatientByCondition(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, String userOrPatientName, int pageIndex, int sizePerPage) {
        logger.info("get vendor's patient by vendorId={} vendorDepartId={} vendorType={} userOrPatientName={} at page={} sizePerPage={}",
                vendorId, vendorDepartId, vendorType, userOrPatientName, pageIndex, sizePerPage);
        if (VerifyUtil.isStringEmpty(userOrPatientName)) { userOrPatientName = null; }
        else { userOrPatientName = userOrPatientName.replace('%', '\0').trim()+"%"; }
        List<ViewVendorPatientRelationBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<Object[]> resultSet = repository.findByConditions(vendorType, vendorId, vendorDepartId, userOrPatientName, request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
            fillPropertyHospitalDepartment(
                    beans,
                    null!=vendorId       ? vendorId.intValue()       : 0,
                    null!=vendorDepartId ? vendorDepartId.intValue() : 0);
            fillPropertyOrderNumber(
                    beans,
                    vendorType,
                    null!=vendorId       ? vendorId       : 0,
                    null!=vendorDepartId ? vendorDepartId : 0);
        }

        logger.warn("visit record count={}", beans.size());
        return beans;
    }

    public List<String> getVendorsByPatient(Long userId, Long patientId) {
        logger.info("get vendors by userId={} patientId={}", userId, patientId);
        if (null==userId && null==patientId) {
            return new ArrayList<>();
        }
        List<Object[]> set = repository.findVendorsByPatient(userId, patientId);
        // vendorTypeOrdinal_vendorId_departId
        List<String> keys = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(set)) {
            for (Object[] tmp : set) {
                if (null==tmp || tmp.length==0) { continue; }
                StringBuilder tmpStr = new StringBuilder();
                tmpStr.append(tmp[0]).append("_").append(tmp[1]).append("_").append(tmp[2]);
                String key = tmpStr.toString();
                if (!keys.contains(key)) {
                    keys.add(key);
                }
            }
        }

        logger.warn("view record count={}", keys.size());
        return keys;
    }

    //===============================================================
    //             get ----  nurse/manager using
    //===============================================================

    public long countHospitalPatientByCondition(Long hospitalId, Long departmentId, String userOrPatientName) {
        if (VerifyUtil.isStringEmpty(userOrPatientName)) { userOrPatientName = null; }
        else { userOrPatientName = userOrPatientName.replace('%', '\0').trim()+"%"; }
        List<Object[]> set = repository.findByConditions(ServiceVendorType.HOSPITAL, hospitalId, departmentId, userOrPatientName);
        long count = VerifyUtil.isListEmpty(set) ? 0 : set.size();
        logger.info("count hospital's patient by hospitalId={} departmentId={} userOrPatientName={}, count is {}",
                hospitalId, departmentId, userOrPatientName, count);
        return count;
    }

    public List<ViewVendorPatientRelationBean> getHospitalPatientByCondition(Long hospitalId, Long departmentId, String userOrPatientName, int pageIndex, int sizePerPage) {
        logger.info("get hospital's patient by hospitalId={} departmentId={} userOrPatientName={} at page={} sizePerPage={}",
                hospitalId, departmentId, userOrPatientName, pageIndex, sizePerPage);
        if (VerifyUtil.isStringEmpty(userOrPatientName)) { userOrPatientName = null; }
        else { userOrPatientName = userOrPatientName.replace('%', '\0').trim()+"%"; }
        List<ViewVendorPatientRelationBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<Object[]> resultSet = repository.findByConditions(ServiceVendorType.HOSPITAL, hospitalId, departmentId, userOrPatientName, request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        fillPropertyHospitalDepartment(beans,
                null!=hospitalId   ? hospitalId.intValue()   : 0,
                null!=departmentId ? departmentId.intValue() : 0);
        fillPropertyOrderNumber(beans,
                ServiceVendorType.HOSPITAL,
                null!=hospitalId   ? hospitalId   : 0,
                null!=departmentId ? departmentId : 0);

        logger.warn("view record count={}", beans.size());
        return beans;
    }


    public void setForbiddenFlag(List<ViewVendorPatientRelationBean> vendorPatientRelations,
                                  Long nurseId,
                                  ServiceVendorType vendorType, Long vendorId, Long departId
    ) {
        List<Long> usersIdNurseDenied  = new ArrayList<>();
        List<Long> usersIdVendorDenied = new ArrayList<>();
        if (null!=nurseId) {
            usersIdNurseDenied = denyPatientService.getDeniedUserId(WhoDenyPatient.NURSE, nurseId, null, null, null);
        }
        if (null!=vendorType && null!=vendorId) {
            usersIdVendorDenied = denyPatientService.getDeniedUserId(WhoDenyPatient.VENDOR, null, vendorType, vendorId, departId);
        }

        for (ViewVendorPatientRelationBean tmp : vendorPatientRelations) {
            boolean isForbidden = usersIdVendorDenied.contains(tmp.getUserId()) || usersIdNurseDenied.contains(tmp.getUserId());
            tmp.setProperties(ViewVendorPatientRelationService.PROP_FORBIDDEN, isForbidden);
        }
    }

    private List<ViewVendorPatientRelationBean> entitiesToBeans(Iterable<Object[]> entities) {
        List<ViewVendorPatientRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        Iterator<Object[]> iterator = null;
        try { iterator = entities.iterator(); }
        catch (Exception ex) {};
        if (null==iterator) {
            return beans;
        }

        while (iterator.hasNext()) {
            Object[] tmp = iterator.next();
            if (null==tmp || tmp.length==0
                    || !(tmp[0] instanceof Long)
                    || !(tmp[1] instanceof Long)) {
                continue;
            }
            ViewVendorPatientRelationBean bean = new ViewVendorPatientRelationBean();
            bean.setUserId((Long)tmp[0]);
            bean.setPatientId((Long)tmp[1]);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<ViewVendorPatientRelationBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        for (ViewVendorPatientRelationBean tmp : beans) {
            if (!userIds.contains(tmp.getUserId())) {
                userIds.add(tmp.getUserId());
            }
            if (!patientIds.contains(tmp.getPatientId())) {
                patientIds.add(tmp.getPatientId());
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);

        // fill properties
        for (ViewVendorPatientRelationBean tmp : beans) {
            UserBean user = userIdToBean.get(tmp.getUserId());
            tmp.setUser(user);
            PatientBean patient = patientIdToBean.get(tmp.getPatientId());
            tmp.setPatient(patient);
        }
    }

    private void fillPropertyHospitalDepartment(List<ViewVendorPatientRelationBean> beans, Integer hospitalId, Integer departmentId) {
        HospitalBean hospital = hospitalService.getHospital(hospitalId);
        HospitalDepartmentBean department = departmentService.getById(departmentId, utility.getHttpPrefixForNurseGo());

        // fill properties
        for (ViewVendorPatientRelationBean tmp : beans) {
            tmp.setProperties(PROP_HOSPITAL, hospital);
            tmp.setProperties(PROP_DEPARTMENT, department);
        }
    }

    private void fillPropertyOrderNumber(List<ViewVendorPatientRelationBean> beans, ServiceVendorType vendorType, Long vendorId, Long departId) {
        // calculate order number
        List<Object[]> orders = repository.findRecordByConditions(vendorType, vendorId, departId, null, "order");
        if (VerifyUtil.isListEmpty(orders)) {
            return;
        }
        Map<String, Long> patientOrderNumber = new HashMap<>();
        for (Object[] tmp : orders) {
            Object userId    = tmp[0];
            Object patientId = tmp[1];
            String key = userId+"_"+patientId;
            Long size = patientOrderNumber.get(key);
            if (null==size) {
                patientOrderNumber.put(key, 1L);
            }
            else {
                patientOrderNumber.put(key, size+1);
            }
        }

        // fill properties
        for (ViewVendorPatientRelationBean tmp : beans) {
            String key = tmp.getUserId()+"_"+tmp.getPatientId();
            Long size = patientOrderNumber.get(key);
            tmp.setProperties(PROP_ORDER_NUMBER, null==size ? 0L : size);
        }

    }
}

package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.ViewVendorPatientRelationBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.repository.ViewVendorPatientRelationRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/24.
 */
@Service("ViewVendorPatientRelationService")
public class ViewVendorPatientRelationService {

    private static final Logger logger = LoggerFactory.getLogger(ViewVendorPatientRelationService.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ViewVendorPatientRelationRepository repository;
    @Autowired private UserService userService;
    @Autowired private PatientService patientService;



    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countVendorsPatientByCondition(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId) {
        List<Object[]> set = repository.findByConditions(vendorType, vendorId, vendorDepartId);
        long count = VerifyUtil.isListEmpty(set) ? 0 : set.size();
        logger.info("count vendor's patient by vendorId={} vendorDepartId={} vendorType={} contentLike={}, count is {}",
                vendorId, vendorDepartId, vendorType, count);
        return count;
    }

    public List<ViewVendorPatientRelationBean> getVendorsPatientByCondition(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, int pageIndex, int sizePerPage) {
        logger.info("get vendor's patient by vendorId={} vendorDepartId={} vendorType={} at page={} sizePerPage={}",
                vendorId, vendorDepartId, vendorType, pageIndex, sizePerPage);
        List<ViewVendorPatientRelationBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<Object[]> resultSet = repository.findByConditions(vendorType, vendorId, vendorDepartId, request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);

        logger.warn("visit record count={}", beans.size());
        return beans;
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

}

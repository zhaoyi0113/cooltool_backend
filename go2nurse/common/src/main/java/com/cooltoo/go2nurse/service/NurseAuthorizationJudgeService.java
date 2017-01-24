package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseAuthorizationBean;
import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 21/12/2016.
 */
@Service("NurseAuthorizationJudgeService")
public class NurseAuthorizationJudgeService {

    private static final Logger logger = LoggerFactory.getLogger(NurseAuthorizationJudgeService.class);

    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private ServiceOrderService orderService;
    @Autowired private ViewVendorPatientRelationService vendorPatientRelationService;

    public List<NurseBean> canNurseAnswerConsultation(List<NurseBean> nurses, Long userId) {
        List<NurseBean> nurseCanAnswer = new ArrayList<>();

        if (VerifyUtil.isListEmpty(nurses) || null==userId) {
            return nurseCanAnswer;
        }

        // nurse authorization
        PersonalAuthorization nurseAuth = new PersonalAuthorization();
        List<String> vendorsKey = vendorPatientRelationService.getVendorsByPatient(userId, null);

        for (NurseBean tmp : nurses) {
            nurseAuth.setNurse(tmp);

            NurseExtensionBean nurseExtensionInfo = (NurseExtensionBean) tmp.getProperty(NurseBean.INFO_EXTENSION);
            // is nurse denied by administrator
            if (null==nurseExtensionInfo || !YesNoEnum.YES.equals(nurseExtensionInfo.getAnswerNursingQuestion())) {
                continue;
            }
            // is nurse a expert
            if (null!=nurseExtensionInfo && YesNoEnum.YES.equals(nurseExtensionInfo.getIsExpert())) {
                nurseCanAnswer.add(tmp);
                continue;
            }

            boolean isPatientBelongToDepartment = vendorsKey.contains(nurseAuth.getKey());
            if (isPatientBelongToDepartment) {
                if (UserAuthority.DENY_ALL.equals(nurseAuth.getAuthority().getAuthConsultationHeadNurse())) {
                    continue;
                }
                if (CommonStatus.ENABLED.equals(nurseAuth.getAuthority().getDenyAllAuthHeadNurse())) {
                    continue;
                }
            }

            nurseCanAnswer.add(tmp);
        }

        return nurseCanAnswer;
    }

    public boolean canNurseAnswerConsultation(Long nurseId, Long userId) {
        if (null==nurseId || null==userId) {
            return false;
        }
        NurseBean nurse = nurseService.getNurseById(nurseId);

        // nurse authorization
        PersonalAuthorization nurseAuth = new PersonalAuthorization();
        nurseAuth.setNurse(nurse);

        // is nurse denied by administrator
        NurseExtensionBean nurseExtensionInfo = (NurseExtensionBean) nurse.getProperty(NurseBean.INFO_EXTENSION);
        if (null==nurseExtensionInfo || !YesNoEnum.YES.equals(nurseExtensionInfo.getAnswerNursingQuestion())) {
            throw new BadRequestException(ErrorCode.NURSE_AUTH_DENIED_BY_ADMIN);
        }

        // is nurse a expert
        if (null!=nurseExtensionInfo && YesNoEnum.YES.equals(nurseExtensionInfo.getIsExpert())) {
            return true;
        }

        List<String> vendorsKey = vendorPatientRelationService.getVendorsByPatient(userId, null);
        boolean isPatientBelongToDepartment = vendorsKey.contains(nurseAuth.getKey());
        if (isPatientBelongToDepartment) {
            if (UserAuthority.DENY_ALL.equals(nurseAuth.getAuthority().getAuthConsultationHeadNurse())) {
                throw new BadRequestException(ErrorCode.NURSE_AUTH_DENIED_BY_HEAD_NURSE);
            }
            if (CommonStatus.ENABLED.equals(nurseAuth.getAuthority().getDenyAllAuthHeadNurse())) {
                throw new BadRequestException(ErrorCode.NURSE_AUTH_DENIED_BY_HEAD_NURSE);
            }
        }

        return true;
    }

    public boolean canNurseFetchOrder(Long nurseId, Long orderId) {
        if (null==nurseId || null==orderId) {
            return false;
        }
        NurseBean nurse = nurseService.getNurseById(nurseId);
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
        ServiceOrderBean order = orders.get(0);

        // nurse authorization
        PersonalAuthorization nurseAuth = new PersonalAuthorization();
        nurseAuth.setNurse(nurse);

        // is nurse denied by administrator
        if (UserAuthority.DENY_ALL.equals(nurseAuth.getAuthority().getAuthOrderAdmin())) {
            throw new BadRequestException(ErrorCode.NURSE_AUTH_DENIED_BY_ADMIN);
        }

        // the vendor that order belong to
        PersonalAuthorization ordersVendor = new PersonalAuthorization();
        ordersVendor.setVendorType(order.getVendorType());
        ordersVendor.setVendorId(order.getVendorId());
        ordersVendor.setDepartId(order.getVendorDepartId());

        boolean isOrderBelongToDepartment = nurseAuth.getKey().equals(ordersVendor.getKey());
        // order belong to the department nurse in
        if (isOrderBelongToDepartment) {
            // is this nurse the employee of this department
            NurseHospitalRelationBean nurseHospitalRelation = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.INFO_EXTENSION);
            if (null==nurseHospitalRelation || !YesNoEnum.YES.equals(nurseHospitalRelation.getApproval())) {
                throw new BadRequestException(ErrorCode.NURSE_NOT_BELONG_TO_DEPARTMENT);
            }
            // is patient belong to department nurse in
            List<String> vendorsKey = vendorPatientRelationService.getVendorsByPatient(order.getUserId(), null);
            boolean isPatientBelongToDepartment = vendorsKey.contains(nurseAuth.getKey());
            if (isPatientBelongToDepartment) {
                if (UserAuthority.DENY_ALL.equals(nurseAuth.getAuthority().getAuthOrderHeadNurse())) {
                    throw new BadRequestException(ErrorCode.NURSE_AUTH_DENIED_BY_HEAD_NURSE);
                }
                if (CommonStatus.ENABLED.equals(nurseAuth.getAuthority().getDenyAllAuthHeadNurse())) {
                    throw new BadRequestException(ErrorCode.NURSE_AUTH_DENIED_BY_HEAD_NURSE);
                }
            }
        }

        return true;
    }

    public List<NurseBean> canNurseOfDepartFetchOrder(List<NurseBean> nurses) {
        if (VerifyUtil.isListEmpty(nurses)) {
            return nurses;
        }

        List<NurseBean> nurseFetchOrder = new ArrayList<>();
        PersonalAuthorization nurseAuth = new PersonalAuthorization();
        for (NurseBean tmp : nurses) {
            nurseAuth.setNurse(tmp);

            // is nurse denied by administrator
            if (UserAuthority.DENY_ALL.equals(nurseAuth.getAuthority().getAuthOrderAdmin())) {
                continue;
            }

            NurseHospitalRelationBean nurseHospitalRelation = (NurseHospitalRelationBean) tmp.getProperty(NurseBean.HOSPITAL_DEPARTMENT);
            // this nurse is not an employee of this department
            if (null==nurseHospitalRelation || !YesNoEnum.YES.equals(nurseHospitalRelation.getApproval())) {
                continue;
            }
            // is patient belong to department nurse in
            if (UserAuthority.DENY_ALL.equals(nurseAuth.getAuthority().getAuthOrderHeadNurse())) {
                continue;
            }
            if (CommonStatus.ENABLED.equals(nurseAuth.getAuthority().getDenyAllAuthHeadNurse())) {
                continue;
            }
            nurseFetchOrder.add(tmp);
        }

        return nurseFetchOrder;
    }

    public boolean couldNursePublishNotification(long nurseId) {
        if(nurseService.existsNurse(nurseId)){
            NurseBean nurse = nurseService.getNurseById(nurseId);
            NurseExtensionBean extension = (NurseExtensionBean) nurse.getProperty(NurseBean.INFO_EXTENSION);
            // is head nurse
            if (null!=extension && YesNoEnum.YES.equals(extension.getIsManager())) {
                return true;
            }
            NurseHospitalRelationBean nurseHospitalRelation = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);
            // this nurse is not an employee of this department
            if (null==nurseHospitalRelation || !YesNoEnum.YES.equals(nurseHospitalRelation.getApproval())) {
                return false;
            }
            PersonalAuthorization nurseAuth = new PersonalAuthorization();
            nurseAuth.setNurse(nurse);
            if (CommonStatus.ENABLED.equals(nurseAuth.getAuthority().getDenyAllAuthHeadNurse())) {
                return false;
            }
            // has authorization of publishing notification
            NurseAuthorizationBean authorization = (NurseAuthorizationBean) nurse.getProperty(NurseBean.AUTHORIZATION);
            if (UserAuthority.AGREE_ALL.equals(authorization.getAuthNotificationHeadNurse())) {
                return true;
            }
        }
        return false;
    }


    private static class PersonalAuthorization {
        private ServiceVendorType vendorType;
        private long vendorId;
        private long departId;
        private String vendorKey;

        private NurseAuthorizationBean authority;

        public void setVendorType(ServiceVendorType vendorType) {
            this.vendorType = vendorType;
            updateKey();
        }

        public void setVendorId(long vendorId) {
            this.vendorId = vendorId;
            updateKey();
        }

        public void setDepartId(long departId) {
            this.departId = departId;
            updateKey();
        }

        public String getKey() {
            return vendorKey;
        }

        private void updateKey() {
            StringBuilder msg = new StringBuilder();
            msg.append(null==vendorType ? null : vendorType.name());
            msg.append("_").append(vendorId);
            msg.append("_").append(departId);
            vendorKey = msg.toString();
        }

        public NurseAuthorizationBean getAuthority() {
            return authority;
        }
        public void setAuthority(NurseAuthorizationBean authority) {
            this.authority = authority;
        }

        public void setNurse(NurseBean nurse) {
            NurseHospitalRelationBean nurseHospitalRelation = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);
            if (null!=nurseHospitalRelation) {
                vendorType = ServiceVendorType.HOSPITAL;
                vendorId = nurseHospitalRelation.getHospitalId();
                departId = nurseHospitalRelation.getDepartmentId();
            }
            NurseAuthorizationBean nurseAuthorization = (NurseAuthorizationBean) nurse.getProperty(NurseBean.AUTHORIZATION);
            if (null!=nurseAuthorization) {
                authority = nurseAuthorization;
            }
            updateKey();
        }
    }
}

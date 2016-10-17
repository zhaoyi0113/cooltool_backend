package com.cooltoo.go2nurse.openapp;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.converter.WeChatAccountBeanConverter;
import com.cooltoo.go2nurse.entities.WeChatAccountEntity;
import com.cooltoo.go2nurse.repository.WeChatAccountRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.repository.HospitalDepartmentRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Service("WeChatAccountService")
public class WeChatAccountService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatAccountService.class);

    @Autowired private WeChatAccountRepository repository;
    @Autowired private WeChatAccountBeanConverter beanConverter;
    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private Go2NurseUtility utility;

    //===============================================================
    //               getting
    //===============================================================
    public WeChatAccountBean getWeChatAccountById(int accountId) {
        logger.info("get WeChat Account by accountId={}", accountId);
        WeChatAccountEntity entity = repository.findOne(accountId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        WeChatAccountBean bean = beanConverter.convert(entity);
        fillOtherPropertiesOfSingleBean(bean);
        return bean;
    }

    public WeChatAccountBean getWeChatAccountByAppId(String appId) {
        logger.info("get WeChat Account by appId={}", appId);
        List<WeChatAccountEntity> entities = repository.findByAppId(appId);
        if (VerifyUtil.isListEmpty(entities)) {
            return null;
        }
        WeChatAccountBean bean = beanConverter.convert(entities.get(0));
        fillOtherPropertiesOfSingleBean(bean);
        return bean;
    }

    public long countWeChatAccount(String status) {
        logger.info("count WeChat account by status={}", status);
        long result = 0;
        if ("ALL".equalsIgnoreCase(status)) {
            result = repository.count();
        }
        else {
            CommonStatus enumStatus = CommonStatus.parseString(status);
            result = repository.countByStatus(enumStatus);
        }
        logger.info("WeChat account size={}", result);
        return result;
    }

    public List<WeChatAccountBean> getWeChatAccount(String status, int pageIndex, int sizePerPage) {
        logger.info("get WeChat account by status={} pageIndex={} sizePerPage={}", status, pageIndex, sizePerPage);
        PageRequest page = new PageRequest(pageIndex, sizePerPage, Sort.Direction.ASC, "id");
        Page<WeChatAccountEntity> result = null;
        if ("ALL".equalsIgnoreCase(status)) {
            result = repository.findAll(page);
        }
        else {
            CommonStatus enumStatus = CommonStatus.parseString(status);
            result = repository.findByStatus(enumStatus, page);
        }
        List<WeChatAccountBean> beans = entitiesToBeans(result);
        fillOtherProperties(beans);
        logger.info("WeChat account size={}", beans.size());
        return beans;
    }

    private List<WeChatAccountBean> entitiesToBeans(Iterable<WeChatAccountEntity> entities) {
        List<WeChatAccountBean> ret = new ArrayList<>();
        if (null!=entities) {
            for (WeChatAccountEntity tmp : entities) {
                ret.add(beanConverter.convert(tmp));
            }
        }
        return ret;
    }

    private void fillOtherProperties(List<WeChatAccountBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Integer> hospitalIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (WeChatAccountBean tmp : beans) {
            if (!hospitalIds.contains(tmp.getHospitalId())) {
                hospitalIds.add(tmp.getHospitalId());
            }
            if (!departmentIds.contains(tmp.getDepartmentId())) {
                departmentIds.add(tmp.getDepartmentId());
            }
        }

        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalIds);
        Map<Integer, HospitalDepartmentBean> departmentIdToBean = departmentService.getDepartmentIdToBean(departmentIds, utility.getHttpPrefixForNurseGo());
        for (WeChatAccountBean tmp : beans) {
            HospitalBean hospital = hospitalIdToBean.get(tmp.getHospitalId());
            HospitalDepartmentBean department = departmentIdToBean.get(tmp.getDepartmentId());
            tmp.setHospital(hospital);
            tmp.setDepartment(department);
        }
    }

    private void fillOtherPropertiesOfSingleBean(WeChatAccountBean bean) {
        if (null==bean) {
            return;
        }
        HospitalBean hospital = hospitalService.getHospital(bean.getHospitalId());
        HospitalDepartmentBean department = departmentService.getById(bean.getDepartmentId(), utility.getHttpPrefixForNurseGo());
        bean.setHospital(hospital);
        bean.setDepartment(department);
    }


    //===============================================================
    //               deleting
    //===============================================================

    @Transactional
    public WeChatAccountBean deleteWeChatAccountById(int accountId) {
        logger.info("delete WeChat Account by accountId={}", accountId);
        WeChatAccountEntity entity = repository.findOne(accountId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        repository.delete(entity);
        return beanConverter.convert(entity);
    }


    //===============================================================
    //               updating
    //===============================================================

    @Transactional
    public WeChatAccountBean updateWeChatAccount(int accountId, String appSecret, String mchId, String name, String status, Integer hospitalId, Integer departmentId) {
        logger.info("update WeChat account={} with appId={} appSecret={} mchId={} name={} status={}",
                accountId, appSecret, mchId, name, status);
        WeChatAccountEntity entity = repository.findOne(accountId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(appSecret) && !appSecret.trim().equalsIgnoreCase(entity.getAppSecret())) {
            entity.setAppSecret(appSecret.trim());
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(mchId) && !mchId.trim().equalsIgnoreCase(entity.getMchId())) {
            entity.setMchId(mchId.trim());
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(name) && !name.trim().equalsIgnoreCase(entity.getName())) {
            entity.setName(name.trim());
            changed = true;
        }

        if (null!=hospitalId && null!=departmentId) {
            HospitalDepartmentEntity department = departmentRepository.findOne(departmentId);
            if (hospitalId==department.getHospitalId()) {
                entity.setHospitalId(hospitalId);
                entity.setDepartmentId(departmentId);
                changed = true;
            }
        }

        CommonStatus enumStatus = CommonStatus.parseString(status);
        if (null!=enumStatus && !enumStatus.equals(entity.getStatus())) {
            entity.setStatus(enumStatus);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        WeChatAccountBean bean = beanConverter.convert(entity);
        fillOtherPropertiesOfSingleBean(bean);
        logger.info("WeChat Account after updated<==>{}", bean);
        return bean;
    }

    //===============================================================
    //               adding
    //===============================================================

    @Transactional
    public WeChatAccountBean addWeChatAccount(String appId, String appSecret, String mchId, String name, int hospitalId, int departmentId) {
        logger.info("add WeChat account appId={} appSecret={} mchId={} name={}", appId, appSecret, mchId, name);
        if (VerifyUtil.isStringEmpty(appId) || VerifyUtil.isStringEmpty(appSecret)) {
            logger.error("appId or appSecret is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        appId = appId.trim();
        appSecret = appSecret.trim();

        WeChatAccountEntity entity = null;
        List<WeChatAccountEntity> result = repository.findByAppIdAndAppSecret(appId, appSecret);
        if (VerifyUtil.isListEmpty(result)) {
            entity = new WeChatAccountEntity();
            entity.setAppId(appId);
            entity.setAppSecret(appSecret);
            entity.setTimeCreated(new Date());
        }
        else {
            entity = result.get(0);
        }

        if (hospitalId!=0 && departmentId!=0) {
            HospitalDepartmentEntity department = departmentRepository.findOne(departmentId);
            if (null!=department && hospitalId==department.getHospitalId()) {
                entity.setHospitalId(hospitalId);
                entity.setDepartmentId(departmentId);
            }
        }

        entity.setName(!VerifyUtil.isStringEmpty(name) ? name.trim() : null);
        entity.setMchId(!VerifyUtil.isStringEmpty(mchId) ? mchId.trim() : null);
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);


        result = repository.findByAppId(appId);
        for (int i=0; i<result.size(); i++) {
            WeChatAccountEntity tmp = result.get(i);
            if (tmp.getId()==entity.getId()) {
                result.remove(i);
                break;
            }
        }
        repository.delete(result);

        WeChatAccountBean bean = beanConverter.convert(entity);
        logger.info("WeChat Account is {}", bean);
        return bean;
    }

}

package com.cooltoo.nurse360.hospital.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.HospitalAdminAccessUrlBean;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.beans.HospitalManagementUrlBean;
import com.cooltoo.nurse360.converters.HospitalAdminAccessUrlBeanConverter;
import com.cooltoo.nurse360.entities.HospitalAdminAccessUrlEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.repository.HospitalAdminAccessUrlRepository;
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
 * Created by zhaolisong on 2016/11/10.
 */
@Service("HospitalAdminAccessUrlService")
public class HospitalAdminAccessUrlService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalAdminAccessUrlService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private HospitalAdminAccessUrlRepository repository;
    @Autowired private HospitalAdminAccessUrlBeanConverter beanConverter;

    @Autowired private HospitalManagementUrlService managementUrlService;
    @Autowired private HospitalAdminService adminService;

    //=================================================================
    //                 getter for administrator
    //=================================================================
    public long countAdminMngUrl(Long adminId, Long httpUrlId, CommonStatus status) {
        long count = repository.countByConditions(adminId, httpUrlId, status);
        logger.info("count admin management urls by adminId={} httpUrlId={} status={}, count is {}",
                adminId, httpUrlId, status, count);
        return count;
    }

    public List<HospitalAdminAccessUrlBean> getAdminMngUrl(Long adminId, Long httpUrlId, CommonStatus status, int pageIndex, int sizePerPage) {
        logger.info("get admin management urls by adminId={} httpUrlId={} status={} at page={} sizePerPage={}",
                adminId, httpUrlId, status, pageIndex, sizePerPage);
        List<HospitalAdminAccessUrlBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<HospitalAdminAccessUrlEntity> resultSet = repository.findByConditions(adminId, httpUrlId, status, request);
        beans = entitiesToBeans(resultSet);

        logger.warn("admin management urls count={}", beans.size());
        return beans;
    }


    //==================================================================
    //                   getter for admin_user
    //==================================================================
    public boolean hasAdminMngUrl(long adminId, long urlId) {
        logger.info("exist admin management urls by adminId={} urlId={}", adminId, urlId);
        List<HospitalAdminAccessUrlEntity> entities = repository.findByConditions(adminId, urlId, CommonStatus.ENABLED, sort);
        return !VerifyUtil.isListEmpty(entities);
    }

    public List<HospitalAdminAccessUrlBean> getAdminMngUrlByAdminId(long adminId) {
        logger.info("get admin management urls by adminId={}", adminId);
        List<HospitalAdminAccessUrlEntity> entities = repository.findByConditions(adminId, null, CommonStatus.ENABLED, sort);
        List<HospitalAdminAccessUrlBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        return beans;
    }

    public List<HospitalAdminAccessUrlBean> getAdminMngUrlByUrlId(long urlId) {
        logger.info("get admin management urls by urlId={}", urlId);
        List<HospitalAdminAccessUrlEntity> entities = repository.findByConditions(null, urlId, CommonStatus.ENABLED, sort);
        List<HospitalAdminAccessUrlBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        return beans;
    }

    public boolean existsAdminMngUrl(long urlId) {
        return repository.exists(urlId);
    }

    private List<HospitalAdminAccessUrlBean> entitiesToBeans(Iterable<HospitalAdminAccessUrlEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<HospitalAdminAccessUrlBean> beans = new ArrayList<>();
        for(HospitalAdminAccessUrlEntity tmp : entities) {
            HospitalAdminAccessUrlBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<HospitalAdminAccessUrlBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> adminIds = new ArrayList<>();
        List<Long> urlIds = new ArrayList<>();
        for (HospitalAdminAccessUrlBean tmp : beans) {
            if (!adminIds.contains(tmp.getAdminId())) {
                adminIds.add(tmp.getAdminId());
            }
            if (!urlIds.contains(tmp.getUrlId())) {
                urlIds.add(tmp.getUrlId());
            }
        }

        Map<Long, HospitalAdminBean> adminIdToBean = adminService.getAdminUserIdToBean(adminIds);
        Map<Long, HospitalManagementUrlBean> urlIdToBean = managementUrlService.getHospitalMngUrlIdToBean(urlIds);

        // fill properties
        for (HospitalAdminAccessUrlBean tmp : beans) {
            HospitalAdminBean admin = adminIdToBean.get(tmp.getAdminId());
            HospitalManagementUrlBean url = urlIdToBean.get(tmp.getUrlId());
            tmp.setAdmin(admin);
            tmp.setUrl(url);
        }
    }

    //===============================================================
    //             delete
    //===============================================================
    @Transactional
    public long deleteAdminMngUrl(long accessId) {
        logger.info("delete admin management accessId={}", accessId);
        HospitalAdminAccessUrlEntity one = repository.findOne(accessId);
        if (null==one) {
            logger.info("delete nothing");
            return accessId;
        }
        repository.delete(one);
        return accessId;
    }

    @Transactional
    public List<Long> deleteAdminMngUrl(long adminId, long urlId) {
        logger.info("delete admin management adminId={} urlId={}", adminId, urlId);
        List<HospitalAdminAccessUrlEntity> ones = repository.findByConditions(adminId, urlId, null, sort);
        List<Long> deletedIds = new ArrayList<>();
        if (null==ones) {
            logger.info("delete nothing");
            return deletedIds;
        }

        for (HospitalAdminAccessUrlEntity tmp : ones) {
            deletedIds.add(tmp.getId());
        }
        repository.delete(ones);
        return deletedIds;
    }

    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public long updateAdminMngUrl(long accessId, CommonStatus status) {
        logger.info("update admin management accessId={} status={}", accessId, status);

        HospitalAdminAccessUrlEntity one = repository.findOne(accessId);
        if (null==one) {
            logger.info("delete nothing");
            return accessId;
        }

        if (null!=status && !status.equals(one.getStatus())) {
            one.setStatus(status);
        }
        repository.save(one);


        return accessId;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addAdminMngUrl(long adminId, long httpUrlId) {
        logger.info("add admin management urls by adminId={} httpUrlId={}", adminId, httpUrlId);

        if (!adminService.existsAdminUser(adminId)) {
            logger.error("admin not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (!managementUrlService.existsHospitalMngUrl(httpUrlId)) {
            logger.error("httpUrlId not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        HospitalAdminAccessUrlEntity entity = null;
        List<HospitalAdminAccessUrlEntity> entities = repository.findByConditions(adminId, httpUrlId, null, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            entity = new HospitalAdminAccessUrlEntity();
        }
        else {
            entity = entities.get(0);
        }

        entity.setAdminId(adminId);
        entity.setUrlId(httpUrlId);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        entity = repository.save(entity);

        entities = repository.findByConditions(adminId, httpUrlId, null, sort);
        for (int i=0; i<entities.size(); i++) {
            HospitalAdminAccessUrlEntity tmp = entities.get(i);
            if (tmp.getId()==entity.getId()) {
                entities.remove(i);
                break;
            }
        }

        if (!VerifyUtil.isListEmpty(entities)) {
            repository.delete(entities);
        }

        return entity.getId();
    }
}

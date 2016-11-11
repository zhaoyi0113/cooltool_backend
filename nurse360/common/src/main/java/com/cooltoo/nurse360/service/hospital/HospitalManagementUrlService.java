package com.cooltoo.nurse360.service.hospital;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.RequestMethod;
import com.cooltoo.nurse360.beans.HospitalManagementUrlBean;
import com.cooltoo.nurse360.converters.HospitalManagementUrlBeanConverter;
import com.cooltoo.nurse360.entities.HospitalManagementUrlEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.repository.HospitalManagementUrlRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhaolisong on 2016/11/9.
 */
@Service("HospitalManagementUrlService")
public class HospitalManagementUrlService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalManagementUrlService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private HospitalManagementUrlRepository repository;
    @Autowired private HospitalManagementUrlBeanConverter beanConverter;

    //=================================================================
    //                 getter for administrator
    //=================================================================
    public long countHospitalMngUrl(RequestMethod httpType, String httpUrl, CommonStatus status) {
        httpUrl = VerifyUtil.isStringEmpty(httpUrl) ? null : (httpUrl+"%");
        long count = repository.countByConditions(httpType, httpUrl, status);
        logger.info("count hospital management urls by httpType={} httpUrl={} status={}, count is {}",
                httpType, httpUrl, status, count);
        return count;
    }

    public List<HospitalManagementUrlBean> getHospitalMngUrl(RequestMethod httpType, String httpUrl, CommonStatus status, int pageIndex, int sizePerPage) {
        logger.info("get hospital management urls by httpType={} httpUrl={} status={} at page={} sizePerPage={}",
                httpType, httpUrl, status, pageIndex, sizePerPage);
        httpUrl = VerifyUtil.isStringEmpty(httpUrl) ? null : (httpUrl+"%");
        List<HospitalManagementUrlBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<HospitalManagementUrlEntity> resultSet = repository.findByConditions(httpType, httpUrl, status, request);
        beans = entitiesToBeans(resultSet);

        logger.warn("hospital management urls count={}", beans.size());
        return beans;
    }

    public Map<Long, HospitalManagementUrlBean> getHospitalMngUrlIdToBean(List<Long> urlIds) {
        Map<Long, HospitalManagementUrlBean> value = new HashMap<>();
        if (VerifyUtil.isListEmpty(urlIds)) {
            return value;
        }
        List<HospitalManagementUrlEntity> entities = repository.findAll(urlIds);
        List<HospitalManagementUrlBean> beans = entitiesToBeans(entities);

        for (HospitalManagementUrlBean tmp : beans) {
            value.put(tmp.getId(), tmp);
        }

        return value;
    }


    //==================================================================
    //                   getter for admin_user
    //==================================================================

    public HospitalManagementUrlBean getHospitalMngUrl(RequestMethod httpType, String httpUrl) {
        logger.info("get hospital management urls by httpType={} httpUrl={}", httpType, httpUrl);
        List<HospitalManagementUrlEntity> one = repository.findByConditions(httpType, httpUrl, CommonStatus.ENABLED, sort);
        if (VerifyUtil.isListEmpty(one)) {
            return null;
        }
        return beanConverter.convert(one.get(0));
    }

    public HospitalManagementUrlBean getHospitalMngUrl(long urlId) {
        logger.info("get hospital management urls by urlId={}", urlId);
        HospitalManagementUrlEntity one = repository.findOne(urlId);
        if (null==one) {
            return null;
        }
        return beanConverter.convert(one);
    }

    public boolean existsHospitalMngUrl(long urlId) {
        return repository.exists(urlId);
    }

    private List<HospitalManagementUrlBean> entitiesToBeans(Iterable<HospitalManagementUrlEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<HospitalManagementUrlBean> beans = new ArrayList<>();
        for(HospitalManagementUrlEntity tmp : entities) {
            HospitalManagementUrlBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }


    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public long updateHospitalMngUrl(long httpUrlId, YesNoEnum needToken, CommonStatus status) {
        logger.info("update hospital management urls={} status={}", httpUrlId, status);

        HospitalManagementUrlEntity one = repository.findOne(httpUrlId);
        if (null==one) {
            logger.info("delete nothing");
            return httpUrlId;
        }

        if (null!=needToken && !needToken.equals(one.getNeedToken())) {
            one.setNeedToken(needToken);
        }
        if (null!=status && !status.equals(one.getStatus())) {
            one.setStatus(status);
        }
        repository.save(one);


        return httpUrlId;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addHospitalMngUrl(String httpUrl, String introduction, RequestMethod httpType, YesNoEnum needToken) {
        logger.info("add hospital management urls by httpType={} httpUrl={} introduction={} needToken={}",
                httpType, httpUrl, introduction, needToken);

        if (VerifyUtil.isStringEmpty(httpUrl) || null==httpType) {
            logger.error("url is empty, or httpType is null");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        HospitalManagementUrlEntity entity = null;
        List<HospitalManagementUrlEntity> entities = repository.findByConditions(httpType, httpUrl, null, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            entity = new HospitalManagementUrlEntity();
        }
        else {
            entity = entities.get(0);
        }

        introduction = VerifyUtil.isStringEmpty(introduction) ? null : introduction;
        entity.setHttpUrl(httpUrl);
        entity.setHttpType(httpType);
        entity.setIntroduction(introduction);
        entity.setNeedToken(needToken);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        entity = repository.save(entity);

        entities = repository.findByConditions(httpType, httpUrl, null, sort);
        for (int i=0; i<entities.size(); i++) {
            HospitalManagementUrlEntity tmp = entities.get(i);
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

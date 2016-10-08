package com.cooltoo.go2nurse.openapp;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.converter.WeChatAccountBeanConverter;
import com.cooltoo.go2nurse.entities.WeChatAccountEntity;
import com.cooltoo.go2nurse.repository.WeChatAccountRepository;
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

/**
 * Created by zhaolisong on 16/10/8.
 */
@Service("WeChatAccountService")
public class WeChatAccountService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatAccountService.class);

    @Autowired private WeChatAccountRepository repository;
    @Autowired private WeChatAccountBeanConverter beanConverter;

    //===============================================================
    //               getting
    //===============================================================
    public WeChatAccountBean getWeChatAccountById(int accountId) {
        logger.info("get WeChat Account by accountId={}", accountId);
        WeChatAccountEntity entity = repository.findOne(accountId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        return beanConverter.convert(entity);
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
    public WeChatAccountBean updateWeChatAccount(int accountId, String appSecret, String mchId, String name, String status) {
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

        CommonStatus enumStatus = CommonStatus.parseString(status);
        if (null!=enumStatus && !enumStatus.equals(entity.getStatus())) {
            entity.setStatus(enumStatus);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        WeChatAccountBean bean = beanConverter.convert(entity);
        logger.info("WeChat Account after updated<==>{}", bean);
        return bean;
    }

    //===============================================================
    //               adding
    //===============================================================

    @Transactional
    public WeChatAccountBean addWeChatAccount(String appId, String appSecret, String mchId, String name) {
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

        entity.setName(!VerifyUtil.isStringEmpty(name) ? name.trim() : null);
        entity.setMchId(!VerifyUtil.isStringEmpty(mchId) ? mchId.trim() : null);
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);


        result = repository.findByAppIdAndAppSecret(appId, appSecret);
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

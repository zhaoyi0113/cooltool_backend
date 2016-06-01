package com.cooltoo.services;

import com.cooltoo.beans.SensitiveWordBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SensitiveWordType;
import com.cooltoo.converter.SensitiveWordBeanConverter;
import com.cooltoo.entities.SensitiveWordEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.SensitiveWordRepository;
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
 * Created by hp on 2016/5/31.
 */
@Service("SensitiveWordService")
public class SensitiveWordService {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordService.class);
    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));

    @Autowired private SensitiveWordRepository repository;
    @Autowired private SensitiveWordBeanConverter beanConverter;

    //==========================================================
    //                     get
    //==========================================================
    public List<String> getAllType() {
        List<String> allType = SensitiveWordType.getAllType();
        logger.info("get all sensitive word type={}", allType);
        return allType;
    }

    public long countAll() {
        long count = repository.count();
        logger.info("count sensitive word, count={}", count);
        return count;
    }

    public long countWords(String sensitiveWordType, String strStatus) {
        logger.info("count sensitive word by type={} status={}", sensitiveWordType, strStatus);
        long count = 0;
        SensitiveWordType type = SensitiveWordType.parseString(sensitiveWordType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=type && null!=status) {
            count = repository.countByTypeAndStatus(type, status);
        }
        else if (null!=type && null==status) {
            count = repository.countByType(type);
        }
        else if (null==type && null!=status) {
            count = repository.countByStatus(status);
        }
        logger.info("count is {}", count);
        return count;
    }

    public List<SensitiveWordBean> getAll() {
        List<SensitiveWordEntity> resultSet = repository.findAll(sort);
        List<SensitiveWordBean> beans = entitiesToBeans(resultSet);
        logger.info("get all sensitive word, count={}", beans.size());
        return beans;
    }

    public List<SensitiveWordBean> getWords(String sensitiveWordType, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get sensitive word by type={} status={} at page={} {}/page",
                sensitiveWordType, strStatus, pageIndex, sizePerPage);
        Page<SensitiveWordEntity> resultSet = null;
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        SensitiveWordType type = SensitiveWordType.parseString(sensitiveWordType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=type && null!=status) {
            resultSet = repository.findByTypeAndStatus(type, status, page);
        }
        else if (null!=type && null==status) {
            resultSet = repository.findByType(type, page);
        }
        else if (null==type && null!=status) {
            resultSet = repository.findByStatus(status, page);
        }
        List<SensitiveWordBean> beans = entitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<SensitiveWordBean> getWords(String sensitiveWordType, String strStatus) {
        logger.info("get sensitive word by type={} status={}", sensitiveWordType, strStatus);
        List<SensitiveWordEntity> resultSet = new ArrayList<>();
        SensitiveWordType type = SensitiveWordType.parseString(sensitiveWordType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=type && null!=status) {
            resultSet = repository.findByTypeAndStatus(type, status, sort);
        }
        else if (null!=type && null==status) {
            resultSet = repository.findByType(type, sort);
        }
        else if (null==type && null!=status) {
            resultSet = repository.findByStatus(status, sort);
        }
        List<SensitiveWordBean> beans = entitiesToBeans(resultSet);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<SensitiveWordBean> entitiesToBeans(Iterable<SensitiveWordEntity> entities) {
        List<SensitiveWordBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (SensitiveWordEntity entity : entities) {
            beans.add(beanConverter.convert(entity));
        }
        return beans;
    }

    //===================================================================
    //                      add
    //===================================================================
    @Transactional
    public SensitiveWordBean addWord(String word, String sensitiveType) {
        logger.info("add sensitive word={} type={}", word, sensitiveType);
        if (VerifyUtil.isStringEmpty(word)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        SensitiveWordType type = SensitiveWordType.parseString(sensitiveType);
        if (null==type) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        word = word.trim();
        long count = repository.countByWord(word);
        if (count>0) {
            throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
        }
        SensitiveWordEntity entity = new SensitiveWordEntity();
        entity.setWord(word);
        entity.setType(type);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        repository.save(entity);

        SensitiveWordBean bean = beanConverter.convert(entity);
//        modifySensitiveWordCache(bean);

        return bean;
    }

    //===================================================================
    //                     update
    //===================================================================
    @Transactional
    public SensitiveWordBean updateWord(int recordId, String sensitiveType, String strStatus) {
        logger.info("update by id={} to type={} status={}", recordId, sensitiveType, strStatus);
        if (repository.exists(recordId)) {
            SensitiveWordEntity entity = repository.getOne(recordId);
            SensitiveWordType type = SensitiveWordType.parseString(sensitiveType);
            CommonStatus status = CommonStatus.parseString(strStatus);
            boolean changed = false;
            if (null!=type && !type.equals(entity.getType())) {
                entity.setType(type);
                changed = true;
            }
            if (null!=status && !status.equals(entity.getStatus())) {
                entity.setStatus(status);
                changed = true;
            }
            if (changed) {
                repository.save(entity);
//                modifySensitiveWordCache(beanConverter.convert(entity));
            }
            return beanConverter.convert(entity);
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

//    private void modifySensitiveWordCache(SensitiveWordBean newBean) {
//        if (null==newBean){
//            return;
//        }
//        boolean newOne = true;
//        if (allSensitiveWords.contains(newBean)) {
//            for (SensitiveWordBean bean : allSensitiveWords) {
//                if (bean.getId() == newBean.getId()) {
//                    newOne = false;
//                    bean.setStatus(newBean.getStatus());
//                    bean.setType(newBean.getType());
//                    break;
//                }
//            }
//        }
//        else {
//            allSensitiveWords.add(newBean);
//        }
//    }
}

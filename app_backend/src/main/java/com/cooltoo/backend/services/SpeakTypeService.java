package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.converter.SpeakTypeBeanConverter;
import com.cooltoo.backend.entities.SpeakTypeEntity;
import com.cooltoo.backend.repository.SpeakTypeRepository;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/3/28.
 */
@Service("SpeakTypeService")
public class SpeakTypeService {

    private static final Logger logger = LoggerFactory.getLogger(SpeakTypeService.class.getName());

    @Autowired
    private SpeakTypeRepository speakTypeRepository;

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    @Autowired
    private SpeakTypeBeanConverter beanConverter;


    //=============================================================
    //             get
    //=============================================================

    public SpeakTypeBean getSpeakType(int speakTypeId) {
        logger.info("get speak type by id {}", speakTypeId);
        SpeakTypeEntity speakType = speakTypeRepository.findOne(speakTypeId);
        if (null==speakType) {
            logger.error("speak type not exist");
            return new SpeakTypeBean();
        }
        SpeakTypeBean bean = beanConverter.convert(speakType);
        bean.setImageUrl(storageService.getFilePath(bean.getImageId()));
        bean.setDisableImageUrl(storageService.getFilePath(bean.getDisableImageId()));
        return bean;
    }

    public List<SpeakTypeBean> getAllSpeakType() {
        SpeakTypeBean       bean    = null;
        List<Long>          imageIds = new ArrayList<Long>();
        List<SpeakTypeBean> beans   = new ArrayList<SpeakTypeBean>();

        Iterable<SpeakTypeEntity> speakTypes = speakTypeRepository.findAll();
        for (SpeakTypeEntity speakType : speakTypes) {
            bean = beanConverter.convert(speakType);
            if (bean.getImageId()>0) {
                imageIds.add(bean.getImageId());
            }
            if (bean.getDisableImageId()>0) {
                imageIds.add(bean.getDisableImageId());
            }
            beans.add(bean);
        }

        Map<Long, String> idToPath = storageService.getFilePath(imageIds);

        for (SpeakTypeBean tmp : beans) {
            if (tmp.getImageId()>0) {
                tmp.setImageUrl(idToPath.get(tmp.getImageId()));
            }
            if (tmp.getDisableImageId()>0) {
                tmp.setDisableImageUrl(idToPath.get(tmp.getDisableImageId()));
            }
        }
        return beans;
    }

    public SpeakTypeBean getSpeakTypeByType(SpeakType speakType) {
        List<SpeakTypeBean> all = getAllSpeakType();
        for (SpeakTypeBean one : all) {
            if (one.getType().equals(speakType)) {
                return one;
            }
        }
        return null;
    }

    //=============================================================
    //             update
    //=============================================================

    @Transactional
    public SpeakTypeBean updateSpeakType(int id, String name, int factor, InputStream image, InputStream disableImage) {
        SpeakTypeEntity speakType = speakTypeRepository.findOne(id);
        if (null==speakType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name)) {
            speakType.setName(name);
            changed=true;
        }
        if (factor>0) {
            speakType.setFactor(factor);
            changed = true;
        }
        if (null!=image) {
            long fileId = storageService.saveFile(speakType.getImageId(), speakType.getName(), image);
            speakType.setImageId(fileId);
            changed = true;
        }
        if (null!=disableImage) {
            long fileId = storageService.saveFile(speakType.getDisableImageId(), speakType.getName(), disableImage);
            speakType.setDisableImageId(fileId);
            changed=true;
        }

        if (changed) {
            speakType = speakTypeRepository.save(speakType);
        }
        return beanConverter.convert(speakType);
    }
}

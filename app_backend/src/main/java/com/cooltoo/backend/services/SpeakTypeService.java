package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.converter.SpeakTypeBeanConverter;
import com.cooltoo.backend.entities.SpeakTypeEntity;
import com.cooltoo.backend.repository.SpeakTypeRepository;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.OfficialFileStorageService;
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
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;

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
        bean.setImageUrl(officialStorage.getFilePath(bean.getImageId()));
        bean.setDisableImageUrl(officialStorage.getFilePath(bean.getDisableImageId()));
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

        Map<Long, String> idToPath = officialStorage.getFilePath(imageIds);

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

    public List<Integer> getSpeakTypeIdByTypes(List<SpeakType> speakTypes) {
        logger.info("get speak type ids by speak types {}", speakTypes);
        if (VerifyUtil.isListEmpty(speakTypes)) {
            return new ArrayList<>();
        }

        List<Integer> speakTypeIds = speakTypeRepository.findByTypeIn(speakTypes);
        logger.info("get speak type ids={}", speakTypeIds);
        if (null == speakTypeIds) {
            return new ArrayList<>();
        }
        return speakTypeIds;
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
        String imageUrl=null;
        if (null!=image) {
            long fileId = officialStorage.addFile(speakType.getImageId(), speakType.getName(), image);
            if (fileId>0) {
                imageUrl = officialStorage.getFilePath(fileId);
                speakType.setImageId(fileId);
                changed = true;
            }
        }
        String disableImageUrl=null;
        if (null!=disableImage) {
            long fileId = officialStorage.addFile(speakType.getDisableImageId(), speakType.getName(), disableImage);
            if (fileId>0) {
                disableImageUrl = officialStorage.getFilePath(fileId);
                speakType.setDisableImageId(fileId);
                changed = true;
            }
        }

        if (changed) {
            speakType = speakTypeRepository.save(speakType);
        }

        SpeakTypeBean bean = beanConverter.convert(speakType);
        bean.setImageUrl(imageUrl);
        bean.setDisableImageUrl(disableImageUrl);
        return bean;
    }
}

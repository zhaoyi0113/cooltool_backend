package com.cooltoo.services;

import com.cooltoo.beans.PlatformVersionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.PlatformType;
import com.cooltoo.converter.PlatformVersionBeanConverter;
import com.cooltoo.entities.PlatformVersionEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.PlatformVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yzzhao on 5/22/16.
 */
@Service
public class PlatformVersionService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformVersionService.class);

    @Autowired
    private PlatformVersionRepository versionRepository;

    @Autowired
    private PlatformVersionBeanConverter beanConverter;

    @Transactional
    public PlatformVersionBean addPlatformVersion(String platformType, String version, String link) {
        if (whetherExistPlatformVersion(platformType, version)) {
            throw new BadRequestException(ErrorCode.PLATFORM_VERSION_EXISTED);
        }
        disableAllPlatformVersions(platformType);
        PlatformVersionEntity entity = new PlatformVersionEntity();
        return savePlatformVersionEntity(platformType, version, link, CommonStatus.ENABLED, entity);
    }

    @Transactional
    public PlatformVersionBean editPlatformVersion(int id, String platformType, String version, String link) {
        PlatformVersionEntity entity = versionRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.PLATFORM_VERSION_NOT_FOUND);
        }
        return savePlatformVersionEntity(platformType, version, link, entity.getStatus(), entity);
    }

    public List<PlatformVersionBean> getAllPlatformVersions() {
        Iterable<PlatformVersionEntity> all = versionRepository.findAll();
        return getPlatformVersionBeans(all);
    }

    public PlatformVersionBean getPlatformVersion(int id) {
        if (!versionRepository.exists(id)) {
            throw new BadRequestException(ErrorCode.PLATFORM_VERSION_NOT_FOUND);
        }
        return beanConverter.convert(versionRepository.findOne(id));
    }

    public List<PlatformVersionBean> getPlatformVersionBeans(Iterable<PlatformVersionEntity> all) {
        List<PlatformVersionBean> beans = new ArrayList<>();
        for (PlatformVersionEntity entity : all) {
            beans.add(beanConverter.convert(entity));
        }
        return beans;
    }

    @Transactional
    public void setPlatformVersionStatus(int id, String status) {
        PlatformVersionEntity entity = versionRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.PLATFORM_VERSION_NOT_FOUND);
        }
        try {
            CommonStatus cStatus = CommonStatus.valueOf(status);
            if(CommonStatus.ENABLED.equals(cStatus)){
                disableAllPlatformVersions(entity.getPlatformType().name());
            }
            entity.setStatus(cStatus);
            versionRepository.save(entity);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    public List<PlatformVersionBean> getPlatformVersionByType(String type) {
        try {
            PlatformType pType = PlatformType.valueOf(type);
            List<PlatformVersionEntity> entities = versionRepository.findByPlatformType(pType);
            return getPlatformVersionBeans(entities);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    private PlatformVersionBean savePlatformVersionEntity(String platformType, String version, String link, CommonStatus status, PlatformVersionEntity entity) {
        try {
            PlatformType pType = PlatformType.valueOf(platformType);
            entity.setStatus(status);
            entity.setPlatformType(pType);
            entity.setTimeCreated(Calendar.getInstance().getTime());
            entity.setVersion(version);
            entity.setLink(link);
            PlatformVersionEntity saved = versionRepository.save(entity);
            return beanConverter.convert(saved);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    private boolean whetherExistPlatformVersion(String type, String version) {
        try {
            PlatformType ptype = PlatformType.valueOf(type);
            List<PlatformVersionEntity> versions = versionRepository.findByPlatformTypeAndVersion(ptype, version);
            return !versions.isEmpty();

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    public PlatformVersionBean getPlatformLatestVersion(PlatformType type) {
        List<PlatformVersionEntity> platforms = versionRepository.findByPlatformTypeAndStatusOrderByTimeCreatedDesc(type, CommonStatus.ENABLED);
        if (platforms.isEmpty()) {
            throw new BadRequestException(ErrorCode.PLATFORM_VERSION_NOT_FOUND);
        }
        return beanConverter.convert(platforms.get(0));
    }

    private void disableAllPlatformVersions(String platformType) {
        List<PlatformVersionEntity> platformVersions = versionRepository.findByPlatformType(PlatformType.valueOf(platformType));
        for (PlatformVersionEntity entity : platformVersions) {
            if(CommonStatus.ENABLED.equals(entity.getStatus())) {
                entity.setStatus(CommonStatus.DISABLED);
                versionRepository.save(entity);
            }
        }
    }

}

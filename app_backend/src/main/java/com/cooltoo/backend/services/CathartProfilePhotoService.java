package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.CathartProfilePhotoBean;
import com.cooltoo.backend.converter.CathartProfilePhotoBeanConverter;
import com.cooltoo.backend.entities.CathartProfilePhotoEntity;
import com.cooltoo.backend.repository.CathartProfilePhotoRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/4/18.
 */
@Service("CathartProfilePhotoService")
public class CathartProfilePhotoService {

    private static final Logger logger = LoggerFactory.getLogger(CathartProfilePhotoService.class.getName());

    @Autowired private CathartProfilePhotoRepository repository;
    @Autowired private CathartProfilePhotoBeanConverter beanConverter;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    //=======================================================
    //                 get
    //=======================================================

    public List<CathartProfilePhotoBean> getAllEnable(CommonStatus status) {
        logger.info("get all enable {} cathart profile photos");
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.ASC, "enable"),
                new Sort.Order(Sort.Direction.DESC, "timeCreated")
        );
        List<CathartProfilePhotoEntity> resultSet    = repository.findByEnable(status, sort);
        List<CathartProfilePhotoBean>   enablePhotos = entities2Beans(resultSet);
        fillOtherProperties(enablePhotos);
        logger.info("get all enable {} cathart profile photos, size={}", enablePhotos.size());
        return enablePhotos;
    }

    public List<CathartProfilePhotoBean> getAll() {
        logger.info("get all cathart profile photos");
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.DESC, "timeCreated")
        );
        List<CathartProfilePhotoEntity> resultSet = repository.findAll(sort);
        List<CathartProfilePhotoBean>   photos    = entities2Beans(resultSet);
        fillOtherProperties(photos);
        logger.info("get all cathart profile photos, size={}", photos.size());
        return photos;
    }

    public CathartProfilePhotoBean getOne(long recordId) {
        logger.info("get one cathart profile photo by id={}", recordId);
        CathartProfilePhotoEntity reseltSet = repository.findOne(recordId);
        if (null==reseltSet) {
            return null;
        }

        CathartProfilePhotoBean photo    = beanConverter.convert(reseltSet);
        long                    imageId  = photo.getImageId();
        String                  imageUrl = storageService.getFilePath(imageId);
        photo.setImageUrl(imageUrl);
        return photo;
    }

    private List<CathartProfilePhotoBean> entities2Beans(List<CathartProfilePhotoEntity> entities) {
        if (null==entities||entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<CathartProfilePhotoBean> beans = new ArrayList<>();
        for (CathartProfilePhotoEntity tmp : entities) {
            CathartProfilePhotoBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<CathartProfilePhotoBean> profilePhotos) {
        if (null==profilePhotos||profilePhotos.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (CathartProfilePhotoBean tmp : profilePhotos) {
            imageIds.add(tmp.getImageId());
        }

        Map<Long, String> imageId2Url = storageService.getFilePath(imageIds);
        for (CathartProfilePhotoBean tmp : profilePhotos) {
            long imageId = tmp.getImageId();
            String imageUrl = imageId2Url.get(imageId);
            tmp.setImageUrl(imageUrl);
        }
    }

    //=============================================================
    //          add      for administrator use
    //=============================================================

    @Transactional
    public CathartProfilePhotoBean createCathartProfilePhoto(
            String name, String imageName, InputStream image, String strEnable
    ) {
        logger.error("create cathart profile photo name={} status={} imageName={} image={}", name, strEnable, imageName, (null!=image));
        if (null==image) {
            logger.error("image is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        CommonStatus enable = CommonStatus.parseString(strEnable);
        if (null==enable) {
            logger.error("enable is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = "cathart_"+System.nanoTime();
        }
        CathartProfilePhotoEntity entity = new CathartProfilePhotoEntity();
        entity.setName(name);
        entity.setEnable(enable);
        entity.setTimeCreated(new Date());
        long   imageId  = storageService.saveFile(-1, imageName, image);
        String imageUrl = storageService.getFilePath(imageId);
        entity.setImageId(imageId);
        repository.save(entity);

        CathartProfilePhotoBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        return bean;
    }

    //=============================================================
    //          update   for administrator use
    //=============================================================

    @Transactional
    public CathartProfilePhotoBean updateCathartProfilePhoto(
            long recordId, String name, String imageName, InputStream image, String strEnable
    ) {
        logger.error("update cathart profile photo id={} name={} status={} imageName={} image={}", recordId, name, strEnable, imageName, (null!=image));
        CathartProfilePhotoEntity entity = repository.findOne(recordId);
        if (null==entity) {
            logger.error("record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        CommonStatus enable = CommonStatus.parseString(strEnable);
        if (null!=enable) {
            entity.setEnable(enable);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(name) && !name.equals(entity.getName())) {
            entity.setName(name);
            changed = true;
        }
        long   imageId  = 0;
        String imageUrl = null;
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "cathart_" + System.nanoTime();
            }
            imageId  = storageService.saveFile(entity.getImageId(), imageName, image);
            imageUrl = storageService.getFilePath(imageId);
            entity.setImageId(imageId);
            changed = true;
        }
        if (changed) {
            entity.setTimeCreated(new Date());
            repository.save(entity);
        }

        CathartProfilePhotoBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        return bean;
    }

    @Transactional
    public List<CathartProfilePhotoBean> disableByIds(String strIds) {
        logger.info("disable cathart profile photo by ids={}", strIds);
        if (!VerifyUtil.isIds(strIds)) {
            logger.warn("the ids in invalid");
            return new ArrayList<>();
        }

        String[]   strArray  = strIds.split(",");
        List<Long> recordIds = new ArrayList<>();
        for (String tmp : strArray) {
            Long id = Long.parseLong(tmp);
            recordIds.add(id);
        }

        return disableByIds(recordIds);
    }

    @Transactional
    public List<CathartProfilePhotoBean> disableByIds(List<Long> lIds) {
        logger.info("disable cathart profile photo by ids={}", lIds);
        List<CathartProfilePhotoEntity> entities = repository.findAll(lIds);
        if (null==entities||entities.isEmpty()) {
            logger.info("the records not exist");
            return new ArrayList<>();
        }

        for (CathartProfilePhotoEntity tmp : entities) {
            tmp.setEnable(CommonStatus.DISABLED);
        }
        repository.save(entities);

        List<CathartProfilePhotoBean> beans = entities2Beans(entities);
        return beans;
    }

    //=============================================================
    //          delete   for administrator use
    //=============================================================

    @Transactional
    public List<CathartProfilePhotoBean> deleteByIds(String strIds) {
        logger.info("delete cathart profile photo by ids={}", strIds);
        if (!VerifyUtil.isIds(strIds)) {
            logger.warn("the ids in invalid");
            return new ArrayList<>();
        }

        String[]   strArray  = strIds.split(",");
        List<Long> recordIds = new ArrayList<>();
        for (String tmp : strArray) {
            Long id = Long.parseLong(tmp);
            recordIds.add(id);
        }

        return deleteByIds(recordIds);
    }

    @Transactional
    public List<CathartProfilePhotoBean> deleteByIds(List<Long> lIds) {
        logger.info("delete cathart profile photo by ids={}", lIds);
        List<CathartProfilePhotoEntity> entities = repository.findAll(lIds);
        if (null==entities||entities.isEmpty()) {
            logger.info("the records not exist");
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (CathartProfilePhotoEntity tmp : entities) {
            imageIds.add(tmp.getImageId());
        }

        storageService.deleteFiles(imageIds);
        repository.delete(entities);

        List<CathartProfilePhotoBean> beans = entities2Beans(entities);
        return beans;
    }

}

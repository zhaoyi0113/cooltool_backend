package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.ImagesInSpeakBean;
import com.cooltoo.backend.converter.ImagesInSpeakBeanConverter;
import com.cooltoo.backend.entities.ImagesInSpeakEntity;
import com.cooltoo.backend.repository.ImagesInSpeakRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/14.
 */
@Service("ImagesInSpeakService")
public class ImagesInSpeakService {

    private static final Logger logger = LoggerFactory.getLogger(ImagesInSpeakService.class.getName());

    private static final Sort sort = new Sort(
                                            new Sort.Order(Sort.Direction.ASC, "speakId"),
                                            new Sort.Order(Sort.Direction.ASC, "timeCreated")
                                     );

    @Autowired
    private ImagesInSpeakRepository repository;
    @Autowired
    private ImagesInSpeakBeanConverter beanConverter;
    @Autowired
    @Qualifier("UserFileStorageService")
    private UserFileStorageService userStorage;

    //================================================================
    //            get
    //================================================================

    public long countImagesInSpeak(long speakId) {
        logger.info("get image count in speak {}", speakId);
        long count = repository.countBySpeakId(speakId);
        logger.info("get image count {} in speak {}", count, speakId);
        return count;
    }

    public List<ImagesInSpeakBean> getImagesInSpeak(Long speakId) {
        List<Long> speakIds = new ArrayList<Long>();
        speakIds.add(speakId);
        Map<Long, List<ImagesInSpeakBean>> imagesInSpeak = getImagesInSpeak(speakIds);
        return imagesInSpeak.get(speakId);
    }

    public Map<Long, List<ImagesInSpeakBean>> getImagesInSpeak(List<Long> speakIds) {
        if (null==speakIds || speakIds.isEmpty()) {
            return new HashMap<>();
        }

        List<ImagesInSpeakEntity>          resultSet = repository.findBySpeakIdIn(speakIds, sort);
        List<ImagesInSpeakBean>            images    = entitiesToBeans(resultSet);
        fillOtherProperties(images);
        Map<Long, List<ImagesInSpeakBean>> retValue  = listToMap(images);
        return retValue;
    }

    private Map<Long, List<ImagesInSpeakBean>> listToMap(List<ImagesInSpeakBean> imagesInSpeak) {
        if (null==imagesInSpeak || imagesInSpeak.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, List<ImagesInSpeakBean>> speakId2Map = new HashMap<>();
        for (ImagesInSpeakBean bean : imagesInSpeak) {
            List<ImagesInSpeakBean> images = speakId2Map.get(bean.getSpeakId());
            if (null==images) {
                images = new ArrayList<>();
                speakId2Map.put(bean.getSpeakId(), images);
            }
            images.add(bean);
        }

        return speakId2Map;
    }

    private List<ImagesInSpeakBean> entitiesToBeans(List<ImagesInSpeakEntity> entities) {
        if (null==entities || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<ImagesInSpeakBean> beans = new ArrayList<>();
        for (ImagesInSpeakEntity tmp : entities) {
            ImagesInSpeakBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<ImagesInSpeakBean> imagesInSpeak) {
        if (null==imagesInSpeak || imagesInSpeak.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImagesInSpeakBean tmp : imagesInSpeak) {
            if (tmp.getImageId()<=0) {
                continue;
            }
            imageIds.add(tmp.getImageId());
        }

        Map<Long, String> imageId2Path = userStorage.getFilePath(imageIds);
        for (ImagesInSpeakBean tmp : imagesInSpeak) {
            if (tmp.getImageId()<=0) {
                continue;
            }
            String imagePath = imageId2Path.get(tmp.getImageId());
            tmp.setImageUrl(imagePath);
        }
    }

    //====================================================
    //          delete
    //====================================================

    public ImagesInSpeakBean deleteById(long imageInSpeakId) {
        logger.info("delete image in speak by id {}", imageInSpeakId);
        ImagesInSpeakEntity image = repository.findOne(imageInSpeakId);
        if (null==image) {
            logger.warn("image in speak is not exist");
            return new ImagesInSpeakBean();
        }

        userStorage.deleteFile(image.getImageId());
        repository.delete(image);

        return beanConverter.convert(image);
    }

    @Transactional
    public Map<Long, List<ImagesInSpeakBean>> deleteBySpeakIds(List<Long> speakIds) {
        if (null==speakIds || speakIds.isEmpty()) {
            return new HashMap<>();
        }

        List<ImagesInSpeakEntity> images = repository.findBySpeakIdIn(speakIds);
        if (null==images || images.isEmpty()) {
            return new HashMap<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImagesInSpeakEntity tmp : images) {
            imageIds.add(tmp.getImageId());
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        List<ImagesInSpeakBean>            beans = entitiesToBeans(images);
        Map<Long, List<ImagesInSpeakBean>> map   = listToMap(beans);
        return map;
    }

    //====================================================
    //          add
    //====================================================

    @Transactional
    public ImagesInSpeakBean addImage(long speakId, String imageName, InputStream image) {
        logger.info("add image name={} file={} to speak {}", imageName, (null!=image), speakId);

        if (null==image) {
            logger.error("image file is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = "img_speak_" + System.nanoTime();
        }
        long   imageId   = userStorage.addFile(-1, imageName, image);
        String imagePath = userStorage.getFilePath(imageId);

        ImagesInSpeakEntity entity = new ImagesInSpeakEntity();
        entity.setSpeakId(speakId);
        entity.setImageId(imageId);
        entity.setTimeCreated(new Date());
        entity = repository.save(entity);

        ImagesInSpeakBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imagePath);
        return bean;
    }
}

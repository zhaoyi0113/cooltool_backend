package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.entities.ImageInUserConsultationEntity;
import com.cooltoo.go2nurse.repository.ImageInUserConsultationRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hp on 2016/8/28.
 */
@Service("ImageInUserConsultationService")
public class ImageInUserConsultationService {

    private static final Logger logger = LoggerFactory.getLogger(ImageInUserConsultationService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "consultationId"),
            new Sort.Order(Sort.Direction.ASC, "talkId"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private ImageInUserConsultationRepository repository;
    @Autowired private UserGo2NurseFileStorageService userStorage;

    //================================================================
    //            get
    //================================================================
    public long countImage(Long consultationId, Long talkId) {
        long count = repository.countByConsultationIdAndTalkId(consultationId, talkId);
        logger.info("count by consultationId={} talkId={} count={}", consultationId, talkId, count);
        return count;
    }

    public Map<Long, List<String>> getConsultationIdToImagesUrl(List<Long> consultationIds) {
        logger.info("get images in consultations={}", consultationIds);
        Map<Long, List<String>> consultationId2Map = new HashMap<>();
        if (VerifyUtil.isListEmpty(consultationIds)) {
            return consultationId2Map;
        }

        List<ImageInUserConsultationEntity> resultSet = repository.findByConsultationIdInAndTalkId(consultationIds, 0L, sort);
        List<Long> imageIds = new ArrayList<>();
        for (ImageInUserConsultationEntity tmp : resultSet) {
            imageIds.add(tmp.getImageId());
        }
        Map<Long, String> imageIdToUrl = userStorage.getFileUrl(imageIds);

        for (ImageInUserConsultationEntity tmp : resultSet) {
            String url = imageIdToUrl.get(tmp.getImageId());
            if (VerifyUtil.isStringEmpty(url)) {
                continue;
            }
            List<String> imagesUrl = consultationId2Map.get(tmp.getConsultationId());
            if (null==imagesUrl) {
                imagesUrl = new ArrayList<>();
                consultationId2Map.put(tmp.getConsultationId(), imagesUrl);
            }
            imagesUrl.add(url);
        }

        return consultationId2Map;
    }

    public Map<Long, List<String>> getTalkIdToImagesUrl(Long consultationId) {
        if (null==consultationId) {
            return new HashMap<>();
        }

        List<ImageInUserConsultationEntity> resultSet = repository.findByConsultationId(consultationId, sort);
        if (VerifyUtil.isListEmpty(resultSet)) {
            return new HashMap<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInUserConsultationEntity tmp : resultSet) {
            imageIds.add(tmp.getImageId());
        }
        Map<Long, String> imageIdToUrl = userStorage.getFileUrl(imageIds);

        Map<Long, List<String>> talkId2Map = new HashMap<>();
        for (ImageInUserConsultationEntity tmp : resultSet) {
            String url = imageIdToUrl.get(tmp.getImageId());
            if (VerifyUtil.isStringEmpty(url)) {
                continue;
            }
            List<String> imagesUrl = talkId2Map.get(tmp.getTalkId());
            if (null==imagesUrl) {
                imagesUrl = new ArrayList<>();
                talkId2Map.put(tmp.getTalkId(), imagesUrl);
            }
            imagesUrl.add(url);
        }

        return talkId2Map;
    }

    //====================================================
    //                    deleting
    //====================================================

    @Transactional
    public List<Long> deleteByConsultationId(long consultationId) {
        List<ImageInUserConsultationEntity> images = repository.findByConsultationId(consultationId);
        if (null==images || images.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInUserConsultationEntity tmp : images) {
            imageIds.add(tmp.getImageId());
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        return imageIds;
    }

    @Transactional
    public List<Long> deleteByTalkIds(List<Long> talkIds) {
        if (null==talkIds || talkIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<ImageInUserConsultationEntity> images = repository.findByTalkIdIn(talkIds);
        if (null==images || images.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInUserConsultationEntity tmp : images) {
            imageIds.add(tmp.getImageId());
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        return imageIds;
    }

    //====================================================
    //                   adding
    //====================================================
    @Transactional
    public Map<String, String> addImage(long consultationId, long talkId, String imageName, InputStream image) {
        logger.info("add image to consultation={} at talk={} with name={} file={}", consultationId, talkId, imageName, (null!=image));

        if (null==image) {
            logger.error("image file is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = "consultation_image_" + System.nanoTime();
        }
        long imageId = userStorage.addFile(-1, imageName, image);
        String imageUrl = userStorage.getFileURL(imageId);
        talkId = talkId<0 ? 0 : talkId;

        ImageInUserConsultationEntity entity = new ImageInUserConsultationEntity();
        entity.setConsultationId(consultationId);
        entity.setTalkId(talkId);
        entity.setImageId(imageId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        Map<String, String> idAndUrl = new HashMap<>();
        idAndUrl.put("imageUrl", imageUrl);
        idAndUrl.put("id", entity.getId()+"");
        return idAndUrl;
    }
}

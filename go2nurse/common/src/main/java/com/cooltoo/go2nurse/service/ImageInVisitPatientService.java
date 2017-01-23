package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.entities.ImageInVisitPatientEntity;
import com.cooltoo.go2nurse.repository.ImageInVisitPatientRepository;
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
 * Created by zhaolisong on 2016/11/06.
 */
@Service("ImageInVisitPatientService")
public class ImageInVisitPatientService {

    private static final Logger logger = LoggerFactory.getLogger(ImageInVisitPatientService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "nurseVisitPatientId"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private ImageInVisitPatientRepository repository;
    @Autowired private UserGo2NurseFileStorageService userStorage;

    //================================================================
    //            get
    //================================================================
    public long countImage(Long nurseVisitPatientId) {
        long count = repository.countByNurseVisitPatientId(nurseVisitPatientId);
        logger.info("count by nurseVisitPatientId={} count={}", nurseVisitPatientId, count);
        return count;
    }

    public List<String> getNurseVisitPatientImagesUrl(Long nurseVisitPatientId) {
        if (null==nurseVisitPatientId) {
            return new ArrayList<>();
        }

        List<ImageInVisitPatientEntity> resultSet = repository.findByNurseVisitPatientId(nurseVisitPatientId, sort);
        if (VerifyUtil.isListEmpty(resultSet)) {
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInVisitPatientEntity tmp : resultSet) {
            imageIds.add(tmp.getImageId());
        }
        Map<Long, String> imageIdToUrl = userStorage.getFileUrl(imageIds);

        List<String> imageUrl = new ArrayList<>();
        for (ImageInVisitPatientEntity tmp : resultSet) {
            String url = imageIdToUrl.get(tmp.getImageId());
            if (VerifyUtil.isStringEmpty(url)) {
                continue;
            }
            imageUrl.add(url);
        }

        return imageUrl;
    }

    public Map<Long, List<String>> getNurseVisitPatientImagesUrl(List<Long> nurseVisitPatientIds, Map<Long, Map<Long, String>> visitRecordIdToMapOfImageIdToUrl) {
        if (VerifyUtil.isListEmpty(nurseVisitPatientIds)) {
            return new HashMap<>();
        }

        List<ImageInVisitPatientEntity> resultSet = repository.findByNurseVisitPatientIdIn(nurseVisitPatientIds, sort);
        if (VerifyUtil.isListEmpty(resultSet)) {
            return new HashMap<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInVisitPatientEntity tmp : resultSet) {
            imageIds.add(tmp.getImageId());
        }
        Map<Long, String> imageIdToUrl = userStorage.getFileUrl(imageIds);

        Map<Long, List<String>> visitId2Map = new HashMap<>();
        for (ImageInVisitPatientEntity tmp : resultSet) {
            String url = imageIdToUrl.get(tmp.getImageId());
            if (VerifyUtil.isStringEmpty(url)) {
                continue;
            }
            List<String> imagesUrl = visitId2Map.get(tmp.getNurseVisitPatientId());
            if (null==imagesUrl) {
                imagesUrl = new ArrayList<>();
                visitId2Map.put(tmp.getNurseVisitPatientId(), imagesUrl);
            }
            imagesUrl.add(url);

            if (null!=visitRecordIdToMapOfImageIdToUrl) {
                Map<Long, String> recordImageIdToUrl = visitRecordIdToMapOfImageIdToUrl.get(tmp.getNurseVisitPatientId());
                if (null==recordImageIdToUrl) {
                    recordImageIdToUrl = new HashMap<>();
                    visitRecordIdToMapOfImageIdToUrl.put(tmp.getNurseVisitPatientId(), recordImageIdToUrl);
                }
                recordImageIdToUrl.put(tmp.getImageId(), url);
            }
        }

        return visitId2Map;
    }

    public Map<Long, String> getNurseVisitPatientImageIdToUrl(List<Long> imageIds) {
        if (VerifyUtil.isListEmpty(imageIds)) {
            return new HashMap<>();
        }

        Map<Long, String> imageIdToUrl = userStorage.getFileUrl(imageIds);
        return imageIdToUrl;
    }

    public String getNurseVisitPatientImageUrl(long imageId) {
        String imageUrl = userStorage.getFileURL(imageId);
        return imageUrl;
    }
    //====================================================
    //                    deleting
    //====================================================

    @Transactional
    public List<Long> deleteByNurseVisitPatientId(long nurseVisitPatientId) {
        List<ImageInVisitPatientEntity> images = repository.findByNurseVisitPatientId(nurseVisitPatientId);
        if (null==images || images.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInVisitPatientEntity tmp : images) {
            imageIds.add(tmp.getImageId());
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        return imageIds;
    }

    @Transactional
    public List<Long> deleteByNurseVisitPatientIds(List<Long> nurseVisitPatientId) {
        List<ImageInVisitPatientEntity> images = repository.findByNurseVisitPatientIdIn(nurseVisitPatientId, sort);
        if (null==images || images.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInVisitPatientEntity tmp : images) {
            imageIds.add(tmp.getImageId());
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        return imageIds;
    }

    @Transactional
    public List<Long> deleteByNurseVisitPatientId(long nurseVisitPatientId, long imageId) {
        List<ImageInVisitPatientEntity> images = repository.findByNurseVisitPatientIdAndImageId(nurseVisitPatientId, imageId);
        if (null==images || images.isEmpty()) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInVisitPatientEntity tmp : images) {
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
    public Map<String, String> addImage(long nurseVisitPatientId, String imageName, InputStream image) {
        logger.info("add image to nurseVisitPatientId={} with name={} file={}",
                nurseVisitPatientId, imageName, (null!=image));

        if (null==image) {
            logger.error("image file is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = "nurse_visit_patient_image_" + System.nanoTime();
        }
        long imageId = userStorage.addFile(-1, imageName, image);
        String imageUrl = userStorage.getFileURL(imageId);

        ImageInVisitPatientEntity entity = new ImageInVisitPatientEntity();
        entity.setNurseVisitPatientId(nurseVisitPatientId);
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

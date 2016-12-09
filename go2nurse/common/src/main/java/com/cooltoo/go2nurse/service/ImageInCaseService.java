package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.entities.ImageInCaseEntity;
import com.cooltoo.go2nurse.repository.ImageInCaseRepository;
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
 * Created by zhaolisong on 2016/10/23.
 */
@Service("ImageInCaseService")
public class ImageInCaseService {

    private static final Logger logger = LoggerFactory.getLogger(ImageInCaseService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "casebookId"),
            new Sort.Order(Sort.Direction.ASC, "caseId"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private ImageInCaseRepository repository;
    @Autowired private UserGo2NurseFileStorageService userStorage;

    //================================================================
    //            get
    //================================================================
    public boolean existImage(long casebookId, long caseId, long imageId) {
        long count = repository.countByCasebookIdAndCaseIdAndImageId(casebookId, caseId, imageId);
        return count>0;
    }

    public long countImage(Long casebookId, Long caseId) {
        long count = repository.countByCasebookIdAndCaseId(casebookId, caseId);
        logger.info("count by casebookId={} caseId={} count={}", casebookId, caseId, count);
        return count;
    }

    public Map<Long, List<String>> getCaseIdToImagesUrl(Long casebookId) {
        if (null==casebookId) {
            return new HashMap<>();
        }

        List<ImageInCaseEntity> resultSet = repository.findByCasebookId(casebookId, sort);
        if (VerifyUtil.isListEmpty(resultSet)) {
            return new HashMap<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInCaseEntity tmp : resultSet) {
            imageIds.add(tmp.getImageId());
        }
        Map<Long, String> imageIdToUrl = userStorage.getFileUrl(imageIds);

        Map<Long, List<String>> caseId2Map = new HashMap<>();
        for (ImageInCaseEntity tmp : resultSet) {
            String url = imageIdToUrl.get(tmp.getImageId());
            if (VerifyUtil.isStringEmpty(url)) {
                continue;
            }
            List<String> imagesUrl = caseId2Map.get(tmp.getCaseId());
            if (null==imagesUrl) {
                imagesUrl = new ArrayList<>();
                caseId2Map.put(tmp.getCaseId(), imagesUrl);
            }
            imagesUrl.add(url);
        }

        return caseId2Map;
    }

    //====================================================
    //                    deleting
    //====================================================

    @Transactional
    public List<Long> deleteByCasebookId(long consultationId) {
        List<ImageInCaseEntity> images = repository.findByCasebookId(consultationId);
        if (null==images || images.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInCaseEntity tmp : images) {
            imageIds.add(tmp.getImageId());
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        return imageIds;
    }

    @Transactional
    public List<Long> deleteByCaseIds(List<Long> caseIds) {
        if (null==caseIds || caseIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<ImageInCaseEntity> images = repository.findByCaseIdIn(caseIds);
        if (null==images || images.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (ImageInCaseEntity tmp : images) {
            imageIds.add(tmp.getImageId());
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        return imageIds;
    }

    @Transactional
    public List<Long> deleteByImageIds(List<Long> imageIds) {
        if (null==imageIds || imageIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<ImageInCaseEntity> images = repository.findByImageIdIn(imageIds);
        if (null==images || images.isEmpty()) {
            return new ArrayList<>();
        }

        userStorage.deleteFiles(imageIds);
        repository.delete(images);

        return imageIds;
    }

    //====================================================
    //                   adding
    //====================================================
    @Transactional
    public Map<String, String> addImage(long casebookId, long caseId, String imageName, InputStream image) {
        logger.info("add image to casebookId={} at caseId={} with name={} file={}", casebookId, caseId, imageName, (null!=image));

        if (null==image) {
            logger.error("image file is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(imageName)) {
            imageName = "consultation_image_" + System.nanoTime();
        }
        long imageId = userStorage.addFile(-1, imageName, image);
        String imageUrl = userStorage.getFileURL(imageId);
        caseId = caseId<0 ? 0 : caseId;

        ImageInCaseEntity entity = new ImageInCaseEntity();
        entity.setCasebookId(casebookId);
        entity.setCaseId(caseId);
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

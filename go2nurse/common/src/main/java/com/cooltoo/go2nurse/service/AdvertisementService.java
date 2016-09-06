package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.AdvertisementBean;
import com.cooltoo.go2nurse.converter.AdvertisementBeanConverter;
import com.cooltoo.go2nurse.entities.AdvertisementEntity;
import com.cooltoo.go2nurse.repository.AdvertisementRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/9/6.
 */
@Service("AdvertisementService")
public class AdvertisementService {

    private final static Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    private final static Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "orderIndex")
            , new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private AdvertisementRepository repository;
    @Autowired private AdvertisementBeanConverter beanConverter;

    @Autowired private UserGo2NurseFileStorageService userFileStorage;


    //===========================================================
    //                    get
    //===========================================================

    public long countAdvertisementByStatus(String strStatus) {
        logger.info("count all advertisement by status={}", strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = 0;
        if ("ALL".equalsIgnoreCase(strStatus)) {
            count = repository.count();
        }
        else if (null != status) {
            count = repository.countByStatus(status);
        }
        logger.info("count all advertisement, size={}", count);
        return count;
    }

    public List<AdvertisementBean> getAdvertisementByIds(String strAdvertisementIds) {
        List<Long> advertisementIds = VerifyUtil.parseLongIds(strAdvertisementIds);
        return getAdvertisementByIds(advertisementIds);
    }

    public List<AdvertisementBean> getAdvertisementByIds(List<Long> advertisementIds) {
        if (null==advertisementIds || advertisementIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<AdvertisementEntity> resultSet = repository.findAll(advertisementIds);
        List<AdvertisementBean>   activities = entities2Beans(resultSet);
        fillOtherProperties(activities);
        return activities;
    }

    public List<AdvertisementBean> getAdvertisementByStatus(String strStatus) {
        logger.info("get advertisement by status {}", strStatus);

        // get nuser by authority
        List<AdvertisementEntity> resultSet = (List<AdvertisementEntity>) getAdvertisementByStatus(strStatus, sort);

        // parse to bean
        List<AdvertisementBean> beans = entities2Beans(resultSet);
        fillOtherProperties(beans);
        logger.info("get advertisement size={}", beans.size());
        return beans;
    }

    public List<AdvertisementBean> getAdvertisementByStatus(String strStatus, int pageIndex, int number) {
        logger.info("get advertisement by status {} at page {} with number {}", strStatus, pageIndex, number);

        // get nuser by authority
        PageRequest          page          = new PageRequest(pageIndex, number, sort);
        Page<AdvertisementEntity> resultSetPage = (Page<AdvertisementEntity>) getAdvertisementByStatus(strStatus, page);
        // parse to bean
        List<AdvertisementBean> beans = entities2Beans(resultSetPage);
        fillOtherProperties(beans);
        logger.info("get advertisement size={}", beans.size());
        return beans;
    }

    private Iterable<AdvertisementEntity> getAdvertisementByStatus(String strStatus, Object sortOrPage) {
        Iterable<AdvertisementEntity> advertisements = null;
        CommonStatus status = CommonStatus.parseString(strStatus);
        if ("ALL".equalsIgnoreCase(strStatus)) {
            if (sortOrPage instanceof PageRequest) {
                advertisements = repository.findAll((Pageable) sortOrPage);
            }
            else if (sortOrPage instanceof Sort) {
                advertisements = repository.findAll((Sort) sortOrPage);
            }
        }
        else if (null != status) {
            if (sortOrPage instanceof PageRequest) {
                advertisements = repository.findByStatus(status, (Pageable) sortOrPage);
            }
            else if (sortOrPage instanceof Sort) {
                advertisements = repository.findByStatus(status, (Sort) sortOrPage);
            }
        }
        return advertisements;
    }

    private List<AdvertisementBean> entities2Beans(Iterable<AdvertisementEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<AdvertisementBean> retVal = new ArrayList<>();
        for (AdvertisementEntity tmp : entities) {
            AdvertisementBean bean = beanConverter.convert(tmp);
            retVal.add(bean);
        }

        return retVal;
    }

    private void fillOtherProperties(List<AdvertisementBean> activities) {
        if (null==activities || activities.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (AdvertisementBean tmp : activities) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            imageIds.add(tmp.getFrontCover());
        }

        Map<Long, String> imgId2Url = userFileStorage.getFileUrl(imageIds);
        for (AdvertisementBean tmp : activities) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            long   imgId  = tmp.getFrontCover();
            String imgUrl = imgId2Url.get(imgId);
            tmp.setFrontCoverUrl(imgUrl);
        }
    }

    //===========================================================
    //                    create
    //===========================================================

    @Transactional
    public long createAdvertisement(String frontCoverName, InputStream frontCover, String description, String detailsUrl) {
        logger.info("create an advertisement by frontCoverName={} frontCover={} description={}, detailsUrl={}",
                frontCoverName, frontCover!=null, description, detailsUrl);
        if (null==frontCover) {
            logger.error("the frontCover is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        AdvertisementEntity entity = new AdvertisementEntity();
        long frontCoverId = userFileStorage.addFile(-1, frontCoverName, frontCover);
        if (frontCoverId<0) {
            logger.error("add frontCover is failed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setFrontCover(frontCoverId);
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description.trim());
        }
        if (!VerifyUtil.isStringEmpty(detailsUrl)) {
            entity.setDetailsUrl(detailsUrl.trim());
        }
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setOrderIndex(0);
        entity = repository.save(entity);
        entity.setOrderIndex(Integer.valueOf(entity.getId()+""));
        entity = repository.save(entity);
        logger.info("create an advertisement id={}", entity.getId());
        return entity.getId();
    }

    //===========================================================
    //                    update
    //===========================================================

    @Transactional
    public AdvertisementBean updateAdvertisement(long advertisementId, String description,
                                                String detailsUrl, String strStatus,
                                                 String frontCoverName, InputStream frontCover
    ) {
        logger.info("update advertisement {} by description={}, imageName={} image={}, enrollUrl={}",
                advertisementId, description, frontCoverName, frontCover!=null, detailsUrl);

        AdvertisementEntity entity = repository.findOne(advertisementId);
        if (null==entity) {
            logger.error("the advertisement is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;

        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description.trim());
            changed = true;
        }

        long imageId = 0;
        String imageUrl = null;
        if (null!=frontCover) {
            if (VerifyUtil.isStringEmpty(frontCoverName)) {
                frontCoverName = "frontCover"+System.currentTimeMillis();
            }
            imageId = userFileStorage.addFile(entity.getFrontCover(), frontCoverName, frontCover);
            imageUrl = userFileStorage.getFilePath(imageId);
            entity.setFrontCover(imageId);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(detailsUrl)) {
            entity.setDetailsUrl(detailsUrl);
            changed = true;
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity.setTime(new Date());
            entity = repository.save(entity);
        }

        AdvertisementBean bean = beanConverter.convert(entity);
        bean.setFrontCoverUrl(imageUrl);
        return bean;
    }

    //=============================================================
    //          delete   for administrator use
    //=============================================================

    @Transactional
    public String deleteByIds(String strIds) {
        logger.info("delete advertisement by ids={}", strIds);
        if (!VerifyUtil.isIds(strIds)) {
            logger.warn("the ids in invalid");
            return "";
        }

        List<Long> recordIds = VerifyUtil.parseLongIds(strIds);
        recordIds = deleteByIds(recordIds);
        return VerifyUtil.numList2String(recordIds);
    }

    @Transactional
    public List<Long> deleteByIds(List<Long> lIds) {
        logger.info("delete advertisement by ids={}", lIds);
        if (null==lIds || lIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<AdvertisementEntity> resultSet = repository.findAll(lIds);

        //
        // clean front cover
        //
        List<Long> frontCoverIds = new ArrayList<>();
        for (AdvertisementEntity tmp : resultSet) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            frontCoverIds.add(tmp.getFrontCover());
        }
        userFileStorage.deleteFiles(frontCoverIds);

        //
        // remove the advertisements
        //
        repository.delete(resultSet);

        return lIds;
    }
}

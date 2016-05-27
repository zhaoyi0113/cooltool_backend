package com.cooltoo.services;

import com.cooltoo.beans.ActivityBean;
import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.converter.ActivityBeanConverter;
import com.cooltoo.entities.ActivityEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.ActivityRepository;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.services.file.TemporaryFileStorageService;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.NumberUtil;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by hp on 2016/4/20.
 */
@Service("ActivityService")
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class.getName());

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.DESC, "createTime")
            );

    @Autowired private ActivityRepository repository;
    @Autowired private ActivityBeanConverter beanConverter;
    @Autowired private TemporaryFileStorageService tempStorage;
    @Autowired private OfficialFileStorageService officialStorage;

    //===========================================================
    //                    get
    //===========================================================

    public long countActivityByStatus(String strStatus) {
        logger.info("count all activity by status={}", strStatus);
        long count = 0;
        if ("ALL".equalsIgnoreCase(strStatus)) {
            count = repository.count();
        }
        else {
            ActivityStatus status = ActivityStatus.parseString(strStatus);
            if (null != status) {
                count = repository.countByStatus(status);
            }
        }
        logger.info("count all activity, size={}", count);
        return count;
    }

    public List<ActivityBean> getActivityByIds(String strActivityIds) {
        List<Long> activityIds = VerifyUtil.parseLongIds(strActivityIds);
        return getActivityByIds(activityIds);
    }

    public List<ActivityBean> getActivityByIds(List<Long> activityIds) {
        if (null==activityIds || activityIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<ActivityEntity> resultSet = repository.findAll(activityIds);
        List<ActivityBean>   activities = entities2Beans(resultSet);
        fillOtherProperties(activities);
        return activities;
    }

    public List<ActivityBean> getActivityByStatus(String strStatus) {
        logger.info("get activity by status {}", strStatus);

        // get nuser by authority
        List<ActivityEntity> resultSet = (List<ActivityEntity>) getActivityByStatus(strStatus, sort);

        // parse to bean
        List<ActivityBean> beans = entities2Beans(resultSet);
        fillOtherProperties(beans);
        logger.info("get activity size={}", beans.size());
        return beans;
    }

    public List<ActivityBean> getActivityByStatus(String strStatus, int pageIndex, int number) {
        logger.info("get activity by status {} at page {} with number {}", strStatus, pageIndex, number);

        // get nuser by authority
        PageRequest          page          = new PageRequest(pageIndex, number, sort);
        Page<ActivityEntity> resultSetPage = (Page<ActivityEntity>) getActivityByStatus(strStatus, page);
        // parse to bean
        List<ActivityBean> beans = entities2Beans(resultSetPage);
        fillOtherProperties(beans);
        logger.info("get activity size={}", beans.size());
        return beans;
    }

    private Iterable<ActivityEntity> getActivityByStatus(String strStatus, Object sortOrPage) {
        Iterable<ActivityEntity> resultSet = null;
        if ("ALL".equalsIgnoreCase(strStatus)) {
            if (sortOrPage instanceof PageRequest) {
                resultSet = repository.findAll((Pageable) sortOrPage);
            }
            else if (sortOrPage instanceof Sort) {
                resultSet = repository.findAll((Sort) sortOrPage);
            }
        }
        else {
            ActivityStatus status = ActivityStatus.parseString(strStatus);
            if (null != status) {
                if (sortOrPage instanceof PageRequest) {
                    resultSet = repository.findByStatus(status, (Pageable) sortOrPage);
                }
                else if (sortOrPage instanceof Sort) {
                    resultSet = repository.findByStatus(status, (Sort) sortOrPage);
                }
            }
        }
        return resultSet;
    }

    private List<ActivityBean> entities2Beans(Iterable<ActivityEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<ActivityBean> retVal = new ArrayList<>();
        for (ActivityEntity tmp : entities) {
            ActivityBean bean = beanConverter.convert(tmp);
            //
            // notice : not return the content as it to large.
            //
            bean.setContent(null);
            retVal.add(bean);
        }

        return retVal;
    }

    private ActivityBean entity2BeanAndFillOtherProperties(ActivityEntity entity) {
        if (null == entity) {
            return null;
        }
        ActivityBean bean = beanConverter.convert(entity);
        List<ActivityBean> beans = new ArrayList<>();
        beans.add(bean);
        fillOtherProperties(beans);
        return bean;
    }

    private void fillOtherProperties(List<ActivityBean> activities) {
        if (null==activities || activities.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (ActivityBean tmp : activities) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            imageIds.add(tmp.getFrontCover());
        }

        Map<Long, String> imgId2Url = officialStorage.getFilePath(imageIds);
        for (ActivityBean tmp : activities) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            long   imgId  = tmp.getFrontCover();
            String imgUrl = imgId2Url.get(imgId);
            tmp.setFrontCoverUrl(imgUrl);
        }
    }

    //===========================================================
    //         get for replace the image src attribute
    //===========================================================

    public ActivityBean getActivityById(long activityId, String httpNginxBaseUrl) {
        logger.info("get activity by id {}", activityId);
        ActivityEntity entity = repository.findOne(activityId);
        if (null==entity) {
            logger.warn("activity not exist.");
            return null;
        }

        ActivityBean bean = beanConverter.convert(entity);
        if (bean.getFrontCover()>0) {
            String imgUrl = officialStorage.getFilePath(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }

        addBaseUrl2ImgTagSrcAttr(bean, httpNginxBaseUrl);
        return bean;
    }

    public ActivityBean getActivityByTitle(String title, String httpNginxBaseUrl) {
        logger.info("get activity by title {}", title);
        List<ActivityEntity> entity = repository.findByTitle(title);
        if (null==entity) {
            logger.info("activity not exist.");
            return null;
        }
        if (entity.size()!=1) {
            logger.info("activity more than one={}", entity);
            return null;
        }

        ActivityBean bean = beanConverter.convert(entity.get(0));
        if (bean.getFrontCover()>0) {
            String imgUrl = officialStorage.getFilePath(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }

        addBaseUrl2ImgTagSrcAttr(bean, httpNginxBaseUrl);
        return bean;
    }

    /** the nginxBaseUrl need user judge tmp_or_storage and send*/
    private void addBaseUrl2ImgTagSrcAttr(ActivityBean activity, String nginxBaseUrl) {
        logger.info("convert activity img tags src attribute with nginxUrl={}", nginxBaseUrl);
        if (null==activity || VerifyUtil.isStringEmpty(activity.getContent())) {
            logger.warn("activity content is empty");
            return;
        }
        if (VerifyUtil.isStringEmpty(nginxBaseUrl)) {
            logger.warn("nginxUrl is empty");
            return;
        }

        nginxBaseUrl = nginxBaseUrl.replace('\\', '/');
        if (!nginxBaseUrl.endsWith("/")) {
            nginxBaseUrl = nginxBaseUrl+"/";
        }

        String tempNginxRelativePath;
        if (ActivityStatus.EDITING.equals(activity.getStatus())) {
            tempNginxRelativePath = tempStorage.getNginxRelativePath();
        }
        else {
            tempNginxRelativePath = officialStorage.getNginxRelativePath();
        }

        nginxBaseUrl += tempNginxRelativePath;

        String              content    = activity.getContent();
        HtmlParser          htmlParser = HtmlParser.newInstance();
        Map<String, String> imgTag2Src = htmlParser.getImgTag2SrcUrlMap(content);
        content = htmlParser.addPrefixToImgTagSrcUrl(content, imgTag2Src, nginxBaseUrl);
        activity.setContent(content);
    }

    //===========================================================
    //                    create
    //===========================================================

    @Transactional
    public ActivityBean createActivity(String title, String subtitle, String description,
                                       String datetime, String place, String price, String enrollUrl, int grade
    ) {
        logger.info("create an activity by title={}, subtitle={} descr={}, time={}, place={}, price={}, enrollUrl={}, grade={}",
                    title, subtitle, description, datetime, place, price, enrollUrl, grade);
        if (VerifyUtil.isStringEmpty(title)) {
            logger.error("the title is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // stop check the title is exist or not
        //List<ActivityEntity> exist = repository.findByTitle(title);
        //if (null!=exist && !exist.isEmpty()) {
        //    logger.error("the title is exist already");
        //    throw new BadRequestException(ErrorCode.DATA_ERROR);
        //}

        ActivityEntity entity = new ActivityEntity();
        entity.setTitle(title);

        if (!VerifyUtil.isStringEmpty(subtitle)) {
            entity.setSubtitle(subtitle);
        }
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }
        if (!VerifyUtil.isStringEmpty(datetime)) {
            long date = NumberUtil.getTime(datetime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
            if (date>0) {
                entity.setTime(new Date(date));
            }
        }
        if (!VerifyUtil.isStringEmpty(place)) {
            entity.setPlace(place);
        }
        if (!VerifyUtil.isStringEmpty(price)) {
            BigDecimal bdPrice = NumberUtil.getDecimal(price, 2);
            if (null!=bdPrice) {
                entity.setPrice(bdPrice);
            }
        }
        if (!VerifyUtil.isStringEmpty(enrollUrl)) {
            entity.setEnrollUrl(enrollUrl);
        }
        if (grade<0) {
            grade = 0;
        }
        entity.setGrade(grade);
        entity.setStatus(ActivityStatus.DISABLE);
        entity.setCreateTime(new Date());
        entity = repository.save(entity);
        logger.info("create an activity id={}", entity.getId());
        return beanConverter.convert(entity);
    }

    //===========================================================
    //                    update
    //===========================================================

    @Transactional
    public ActivityBean updateActivityBasicInfo(long activityId,
                                                String title, String subtitle, String description,
                                                String datetime, String place, String price,
                                                String frontCoverName, InputStream frontCover,
                                                String enrollUrl, int grade
    ) {
        logger.info("update activity {} by title={}, subtitle={} descr={}, time={}, place={}, price={}, image={}, enrollUrl={}, grade={}",
                    activityId, title, subtitle, description, datetime, place, price, frontCover!=null, enrollUrl, grade);

        ActivityEntity entity = repository.findOne(activityId);
        if (null==entity) {
            logger.error("the activity is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;

        if (!VerifyUtil.isStringEmpty(title)) {
            if (!title.equals(entity.getTitle())) {
                List<ActivityEntity> exist = repository.findByTitle(title);
                if (null==exist || exist.isEmpty()) {
                    entity.setTitle(title);
                    changed = true;
                }
            }
        }

        if (!VerifyUtil.isStringEmpty(subtitle)) {
            entity.setSubtitle(subtitle);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(datetime)) {
            long date = NumberUtil.getTime(datetime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
            if (date>0) {
                entity.setTime(new Date(date));
                changed = true;
            }
        }
        if (!VerifyUtil.isStringEmpty(place)) {
            entity.setPlace(place);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(price)) {
            BigDecimal bdPrice = NumberUtil.getDecimal(price, 2);
            if (null!=bdPrice) {
                entity.setPrice(bdPrice);
                changed = true;
            }
        }
        String imageUrl = null;
        if (null!=frontCover) {
            if (VerifyUtil.isStringEmpty(frontCoverName)) {
                frontCoverName = "frontCover"+System.currentTimeMillis();
            }
            long imageId = officialStorage.addFile(entity.getFrontCover(), frontCoverName, frontCover);
            imageUrl = officialStorage.getFilePath(imageId);
            entity.setFrontCover(imageId);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(enrollUrl)) {
            entity.setEnrollUrl(enrollUrl);
            changed = true;
        }
        if (grade>=0) {
            entity.setGrade(grade);
            changed = true;
        }

        if (changed) {
            entity.setCreateTime(new Date());
            entity = repository.save(entity);
        }

        ActivityBean bean = beanConverter.convert(entity);
        bean.setFrontCoverUrl(imageUrl);
        return bean;
    }

    @Transactional
    public ActivityBean updateActivityStatus(long activityId, String strStatus) {
        logger.info("update activity {} status to {}", activityId, strStatus);

        ActivityEntity entity = repository.findOne(activityId);
        if (null==entity) {
            logger.error("the activity is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        ActivityStatus status = ActivityStatus.parseString(strStatus);
        if (null!=status) {
            if (ActivityStatus.EDITING.equals(entity.getStatus())) {
                logger.error("the activity is editing");
                throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
            }
            if (ActivityStatus.EDITING.equals(status)) {
                logger.error("can not set activity to editing");
                throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
            }
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        ActivityBean bean = entity2BeanAndFillOtherProperties(entity);
        bean.setContent(null);
        return bean;
    }

    //===========================================================
    //         update for the image src attribute
    //===========================================================

    @Transactional
    public ActivityBean updateActivityContent(long activityId, String content) {
        logger.info("update activity {} token={} content={}", activityId, content);

        ActivityEntity entity = repository.findOne(activityId);
        if (null==entity) {
            logger.error("the activity is not exist (and clean token cache)");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!ActivityStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the activity is not editing");
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }

        if (VerifyUtil.isStringEmpty(content)) {
            content = "";  // avoid NullException
        }
        else {
            // move temporary
            moveTemporaryFileToOfficial(content);

            //
            // replace the content src url
            //
            // get a html_parser instance
            HtmlParser          htmlParser = HtmlParser.newInstance();
            // get all image tag and its src attribute value
            Map<String, String> imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(content);
            // get all src attribute value
            List<String>        srcAttValues    = htmlParser.getSrcUrls(content);
            Map<String, String> srcUrl2RelativeFilePathInStorage;
            // move all src attribute urls to storage, and get relative_file_path_in_storage
            srcUrl2RelativeFilePathInStorage = officialStorage.getRelativePathInStorage(srcAttValues);
            // replace all image src attribute to relative_file_path_in_storage
            content = htmlParser.replaceImgTagSrcUrl(content, imgTag2SrcValue, srcUrl2RelativeFilePathInStorage);
        }

        entity.setContent(content);
        entity.setStatus(ActivityStatus.ENABLE);
        entity = repository.save(entity);
        return entity2BeanAndFillOtherProperties(entity);
    }

    public ActivityBean moveActivity2Temporary(long activityId) {
        logger.info("move activity {} image file to temporary directory", activityId);

        ActivityEntity entity = repository.findOne(activityId);
        if (null==entity) {
            logger.info("the activity do not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (ActivityStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the activity is editing");
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }

        // has content need to move to temporary
        boolean needMove = !VerifyUtil.isStringEmpty(entity.getContent());
        if (needMove) {
            moveOfficialFileToTemporary(entity.getContent());
        }

        // update to editing
        entity.setStatus(ActivityStatus.EDITING);
        entity = repository.save(entity);

        ActivityBean bean = entity2BeanAndFillOtherProperties(entity);
        bean.setContent(null);
        return bean;
    }

    private void moveOfficialFileToTemporary(String htmlContent) {
        logger.info("move html img tag src 's images to temporary storage");
        if (VerifyUtil.isStringEmpty(htmlContent)) {
            logger.info("html content is empty. nothing move to temporary directory");
        }

        HtmlParser htmlParser = HtmlParser.newInstance();
        // get image tags src attribute url
        List<String> srcUrls = htmlParser.getSrcUrls(htmlContent);

        if (!VerifyUtil.isListEmpty(srcUrls)) {
            List<String> srcFilesInStorage = new ArrayList<>();
            for (String srcUrl : srcUrls) {
                String relativePath = officialStorage.getRelativePathInStorage(srcUrl);
                if (VerifyUtil.isStringEmpty(relativePath)) {
                    continue;
                }
                srcFilesInStorage.add(officialStorage.getStoragePath() + relativePath);
            }
            tempStorage.moveFileToHere(srcFilesInStorage);
        }
        else {
            logger.info("img tag is empty. nothing move to temporary directory");
        }
    }

    private void moveTemporaryFileToOfficial(String htmlContent) {
        logger.info("move html img tag src 's images to official storage");
        if (VerifyUtil.isStringEmpty(htmlContent)) {
            logger.info("html content is empty. nothing move to official directory");
        }

        HtmlParser htmlParser = HtmlParser.newInstance();
        // get image tags src attribute url
        List<String> srcUrls = htmlParser.getSrcUrls(htmlContent);

        if (!VerifyUtil.isListEmpty(srcUrls)) {
            List<String> srcFilesInStorage = new ArrayList<>();
            for (String srcUrl : srcUrls) {
                String relativePath = tempStorage.getRelativePathInStorage(srcUrl);
                if (VerifyUtil.isStringEmpty(relativePath)) {
                    continue;
                }
                srcFilesInStorage.add(tempStorage.getStoragePath() + relativePath);
            }
            officialStorage.moveFileToHere(srcFilesInStorage);
        }
        else {
            logger.info("img tag is empty. nothing move to official directory");
        }
    }

    @Transactional
    public String createTemporaryFile(long activityId, String imageName, InputStream image) {
        logger.info("create temporary file by token={} activityId={} imageName={} image={}",
                activityId, imageName, image);
        ActivityEntity entity = repository.findOne(activityId);
        if (null==entity) {
            logger.info("the activity do not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!ActivityStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the activity is not editing");
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }
        String relativePath = tempStorage.addFile(imageName, image);
        return relativePath;
    }



    //=============================================================
    //          delete   for administrator use
    //=============================================================

    @Transactional
    public String deleteByIds(String strIds) {
        logger.info("delete activity by ids={}", strIds);
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
        logger.info("delete activity by ids={}", lIds);
        if (null==lIds || lIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<ActivityEntity> resultSet = repository.findAll(lIds);

        //
        // clean front cover
        //
        List<Long> frontCoverIds = new ArrayList<>();
        for (ActivityEntity tmp : resultSet) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            frontCoverIds.add(tmp.getFrontCover());
        }
        officialStorage.deleteFiles(frontCoverIds);

        //
        // clean the activity content image
        //
        HtmlParser          htmlParser = HtmlParser.newInstance();
        Map<String, String> imgTag2SrcValue;
        List<String>        srcAttVals;
        for (ActivityEntity tmp : resultSet) {
            if (VerifyUtil.isStringEmpty(tmp.getContent())) {
                continue;
            }

            // get all image src attribute url
            srcAttVals = htmlParser.getSrcUrls(tmp.getContent());

            // remove all image src attribute url from storage
            for (String srcUrl : srcAttVals) {
                if (ActivityStatus.EDITING.equals(tmp.getStatus())) {
                    tempStorage.deleteFile(srcUrl);
                } else {
                    officialStorage.deleteFile(srcUrl);
                }
            }
        }

        //
        // remove the activities
        //
        repository.delete(resultSet);

        return lIds;
    }
}

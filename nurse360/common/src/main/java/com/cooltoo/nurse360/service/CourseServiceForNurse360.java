package com.cooltoo.nurse360.service;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.nurse360.beans.Nurse360CourseBean;
import com.cooltoo.nurse360.converters.Nurse360CourseBeanConverter;
import com.cooltoo.nurse360.entities.Nurse360CourseEntity;
import com.cooltoo.nurse360.repository.Nurse360CourseRepository;
import com.cooltoo.nurse360.service.file.NurseFileStorageServiceForNurse360;
import com.cooltoo.nurse360.service.file.TemporaryFileStorageServiceForNurse360;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import com.google.common.base.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/8.
 */
@Service("CourseServiceForNurse360")
public class CourseServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceForNurse360.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time")
    );

    @Autowired private Nurse360CourseRepository repository;
    @Autowired private Nurse360CourseBeanConverter beanConverter;
    @Autowired private NurseFileStorageServiceForNurse360 userStorage;
    @Autowired private TemporaryFileStorageServiceForNurse360 tempStorage;
    @Autowired private CourseHospitalRelationServiceForNurse360 courseHospitalRelationService;

    //===========================================================
    //                    get
    //===========================================================

    public boolean existCourse(long courseId) {
        boolean exist = repository.exists(courseId);
        logger.info("exist course={}, exists={}", courseId, exist);
        return exist;
    }

    public long countByTitleLikeAndStatus(String title, String strStatus) {
        logger.info("count all course by status={}", strStatus);
        CourseStatus status = CourseStatus.parseString(strStatus);
        title = VerifyUtil.isStringEmpty(title) ? null : VerifyUtil.reconstructSQLContentLike(title.trim());
        long count = repository.countByNameLikeAndStatus(title, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<Nurse360CourseBean> getCourseByUniqueId(String uniqueId) {
        logger.info("get course by uniqueId={}", uniqueId);
        List<Nurse360CourseEntity> resultSet = repository.findByUniqueId(uniqueId, sort);
        List<Nurse360CourseBean> beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        return beans;
    }

    public List<Nurse360CourseBean> getCourseByTitleAndStatus(String title, String strStatus) {
        logger.info("get all course by status={}", strStatus);
        CourseStatus status = CourseStatus.parseString(strStatus);
        title = VerifyUtil.isStringEmpty(title) ? null : VerifyUtil.reconstructSQLContentLike(title.trim());
        List<Nurse360CourseEntity> resultSet;
        resultSet = repository.findByNameLikeAndStatus(title, status, sort);
        List<Nurse360CourseBean> beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<Nurse360CourseBean> getCourseByTitleAndStatus(String title, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get all course by status={} at page={} sizePerPage={}", strStatus, pageIndex, sizePerPage);
        CourseStatus status = CourseStatus.parseString(strStatus);
        title = VerifyUtil.isStringEmpty(title) ? null : VerifyUtil.reconstructSQLContentLike(title.trim());
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<Nurse360CourseEntity> resultSet = null;
        resultSet = repository.findByNameLikeAndStatus(title, status, page);
        List<Nurse360CourseBean> beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<Long> getCourseIdByStatusesAndIds(List<CourseStatus> statuses, List<Long> courseIds) {
        logger.info("get courseId by statuses={} ids={}", statuses, courseIds);
        List<Long> resultSet = new ArrayList<>();
        if (VerifyUtil.isListEmpty(statuses) || VerifyUtil.isListEmpty(courseIds)) {
            return resultSet;
        }

        resultSet = repository.findCourseIdByStatusInAndIdIn(statuses, courseIds, sort);
        logger.info("count is {}", resultSet.size());
        return resultSet;
    }

    public List<Long> getCourseIdByStatusAndIds(String strStatus, List<Long> courseIds) {
        logger.info("get courseId by status={} ids={}", strStatus, courseIds);
        List<Long> resultSet = new ArrayList<>();
        if (VerifyUtil.isListEmpty(courseIds)) {
            return resultSet;
        }

        CourseStatus status = CourseStatus.parseString(strStatus);
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findCourseIdByStatusAndIdIn(null, courseIds, sort);
            }
        }
        else {
            resultSet = repository.findCourseIdByStatusAndIdIn(status, courseIds, sort);
        }
        logger.info("count is {}", resultSet.size());
        return resultSet;
    }

    public List<Nurse360CourseBean> getCourseByStatusAndIds(String strStatus, List<Long> courseIds) {
        logger.info("get course by status={} ids={}", strStatus, courseIds);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }

        CourseStatus status = CourseStatus.parseString(strStatus);
        List<Nurse360CourseEntity> resultSet = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findByIdIn(courseIds, sort);
            }
        }
        else {
            resultSet = repository.findByStatusAndIdIn(status, courseIds, sort);
        }
        List<Nurse360CourseBean>   beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<Nurse360CourseBean> getCourseByIds(List<Long> courseIds, int pageIndex, int sizePerPage) {
        logger.info("get course by ids={}", courseIds);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        List<Nurse360CourseEntity> resultSet = repository.findCourseByIdIn(courseIds, page);
        List<Nurse360CourseBean>   beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<Nurse360CourseBean> entities2BeansWithoutContent(Iterable<Nurse360CourseEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<Nurse360CourseBean> retVal = new ArrayList<>();
        for (Nurse360CourseEntity tmp : entities) {
            Nurse360CourseBean bean = beanConverter.convert(tmp);
            //
            // notice : not return the content as it to large.
            //
            bean.setContent(null);
            retVal.add(bean);
        }

        return retVal;
    }

    private Nurse360CourseBean entities2BeansWithContent(Nurse360CourseEntity entity) {
        if (null == entity) {
            return null;
        }
        Nurse360CourseBean bean = beanConverter.convert(entity);
        List<Nurse360CourseBean> beans = new ArrayList<>();
        beans.add(bean);
        fillOtherProperties(beans);
        return bean;
    }

    private void fillOtherProperties(List<Nurse360CourseBean> courses) {
        if (null==courses || courses.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (Nurse360CourseBean tmp : courses) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            imageIds.add(tmp.getFrontCover());
        }

        Map<Long, String> imageId2Url = userStorage.getFileUrl(imageIds);
        for (Nurse360CourseBean tmp : courses) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            long   imgId  = tmp.getFrontCover();
            String imgUrl = imageId2Url.get(imgId);
            tmp.setFrontCoverUrl(imgUrl);
        }
    }

    //===========================================================
    //         get for replace the image src attribute
    //===========================================================

    public Nurse360CourseBean getCourseById(long courseId, String httpNginxBaseUrl) {
        logger.info("get course by id {}", courseId);
        Nurse360CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.warn("course not exist.");
            return null;
        }

        Nurse360CourseBean bean = beanConverter.convert(entity);
        if (bean.getFrontCover()>0) {
            String imgUrl = userStorage.getFileURL(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }

        addBaseUrl2ImgTagSrcAttr(bean, httpNginxBaseUrl);
        return bean;
    }

    public Nurse360CourseBean getCourseByName(String name, String httpNginxBaseUrl) {
        logger.info("get course by title {}", name);
        List<Nurse360CourseEntity> entity = repository.findByName(name, sort);
        if (null==entity || entity.isEmpty()) {
            logger.info("course not exist.");
            return null;
        }
        if (entity.size()!=1) {
            logger.info("course more than one={}", entity);
            return null;
        }

        Nurse360CourseBean bean = beanConverter.convert(entity.get(0));
        if (bean.getFrontCover()>0) {
            String imgUrl = userStorage.getFileURL(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }

        addBaseUrl2ImgTagSrcAttr(bean, httpNginxBaseUrl);
        return bean;
    }

    /** the nginxBaseUrl need user judge tmp_or_storage and send*/
    private void addBaseUrl2ImgTagSrcAttr(Nurse360CourseBean course, String nginxBaseUrl) {
        logger.info("convert course img tags src attribute with nginxUrl={}", nginxBaseUrl);
        if (null==course || VerifyUtil.isStringEmpty(course.getContent())) {
            logger.warn("course content is empty");
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
        if (CourseStatus.EDITING.equals(course.getStatus())) {
            tempNginxRelativePath = tempStorage.getNginxRelativePath();
        }
        else {
            tempNginxRelativePath = userStorage.getNginxRelativePath();
        }

        nginxBaseUrl += tempNginxRelativePath;

        String              content    = course.getContent();
        HtmlParser          htmlParser = HtmlParser.newInstance();
        Map<String, String> imgTag2Src = htmlParser.getImgTag2SrcUrlMap(content);
        content = htmlParser.addPrefixToImgTagSrcUrl(content, imgTag2Src, nginxBaseUrl);
        course.setContent(content);
    }

    //===========================================================
    //                    create
    //===========================================================

    @Transactional
    public Nurse360CourseBean createCourse(String name, String introduction, String link, String keyword, long categoryId) {
        logger.info("create an course by name={} introduction={} link={} keyword={} and categoryId={}",
                name, introduction, link, keyword, categoryId);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("the name is empty");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
//// stop check the title is exist or not
//        long count = repository.countByName(name);
//        if (count>0) {
//            logger.error("the name is exist already");
//            throw new BadRequestException(ErrorCode.NURSE360_RECORD_EXISTS_ALREADY);
//        }

        Nurse360CourseEntity entity = new Nurse360CourseEntity();
        entity.setName(name);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction);
        }
        if (!VerifyUtil.isStringEmpty(link)) {
            entity.setLink(link);
        }
        if (categoryId>0) {
            entity.setCategoryId(categoryId);
        }

        String uniqueId = null;
        for (int i = 10; i>0; i--) {
            uniqueId = NumberUtil.randomIdentity();
            if (repository.countByUniqueId(uniqueId)<=0) {
                break;
            }
            else {
                uniqueId = null;
            }
        }
        if (VerifyUtil.isStringEmpty(uniqueId)) {
            logger.info("unique id generated failed");
            throw new BadRequestException(ErrorCode.NURSE360_UNIQUE_ID_INVALID);
        }
        entity.setUniqueId(uniqueId);

        if (!VerifyUtil.isStringEmpty(keyword)) {
            keyword = keyword.trim();
            entity.setKeyword(VerifyUtil.stringLimit(keyword, 2));
        }

        entity.setStatus(CourseStatus.DISABLE);
        entity.setTime(new Date());
        entity = repository.save(entity);
        logger.info("create an course id={}", entity.getId());
        return beanConverter.convert(entity);
    }

    //===========================================================
    //                    update
    //===========================================================

    @Transactional
    public Nurse360CourseBean updateCourseBasicInfo(long courseId,
                                                String name, String introduction,
                                                String frontCoverName, InputStream frontCover,
                                                String link, String keyword, long categoryId
    ) {
        logger.info("update course {} by name={}, introduction={} imageName={} image={} link={} keyword={} and categoryId={}",
                courseId, name, introduction, frontCoverName, frontCover!=null, link, keyword, categoryId);

        Nurse360CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.error("the course is not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        boolean changed = false;

        if (!VerifyUtil.isStringEmpty(name)) {
            if (!name.equals(entity.getName())) {
//                long count = repository.countByName(name);
//                if (count<=0) {
                    entity.setName(name);
                    changed = true;
//                }
            }
        }

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(link)) {
            entity.setLink(link);
            changed = true;
        }

        if (categoryId>0 && categoryId!=entity.getCategoryId()) {
            entity.setCategoryId(categoryId);
            changed = true;
        }

        String imageUrl = null;
        if (null!=frontCover) {
            if (VerifyUtil.isStringEmpty(frontCoverName)) {
                frontCoverName = "course_front_cover_"+System.currentTimeMillis();
            }
            long imageId = userStorage.addFile(entity.getFrontCover(), frontCoverName, frontCover);
            imageUrl = userStorage.getFileURL(imageId);
            entity.setFrontCover(imageId);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(keyword)) {
            keyword = keyword.trim();
            entity.setKeyword(VerifyUtil.stringLimit(keyword, 2));
            changed = true;
        }

        if (changed) {
            entity.setTime(new Date());
            entity = repository.save(entity);
        }

        Nurse360CourseBean bean = beanConverter.convert(entity);
        bean.setFrontCoverUrl(imageUrl);
        return bean;
    }

    @Transactional
    public Nurse360CourseBean updateCourseStatus(long courseId, String strStatus) {
        logger.info("update course {} status to {}", courseId, strStatus);

        Nurse360CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.error("the course is not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        boolean changed = false;
        CourseStatus status = CourseStatus.parseString(strStatus);
        if (null!=status) {
            if (CourseStatus.EDITING.equals(entity.getStatus())) {
                logger.error("the course is editing");
                throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
            }
            if (CourseStatus.EDITING.equals(status)) {
                logger.error("can not set course to editing");
                throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
            }
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        Nurse360CourseBean bean = entities2BeansWithContent(entity);
        bean.setContent(null);
        return bean;
    }

    //===========================================================
    //         update for the image src attribute
    //===========================================================

    @Transactional
    public Nurse360CourseBean updateCourseContent(long courseId, String content) {
        logger.info("update course {} token={} content={}", courseId, content);

        Nurse360CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.error("the course is not exist (and clean token cache)");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

//        if (!CourseStatus.EDITING.equals(entity.getStatus())) {
//            logger.error("the course is not editing");
//            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
//        }

        if (VerifyUtil.isStringEmpty(content)) {
            content = "";  // avoid NullException
        }
        else {
            // if course content is not empty, move all images to cache
            if (!VerifyUtil.isStringEmpty(entity.getContent())) {
                moveTemporaryFileToOfficial(entity.getContent());
            }
            // move temporary
            moveTemporaryFileToOfficial(content);

            //
            // replace the content src url
            //
            // get a html_parser instance
            HtmlParser htmlParser = HtmlParser.newInstance();
            // get all image tag and its src attribute value
            Map<String, String> imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(content);
            // get all src attribute value
            List<String>        srcAttValues    = htmlParser.getSrcUrls(content);
            Map<String, String> srcUrl2RelativeFilePathInStorage;
            // move all src attribute urls to storage, and get relative_file_path_in_storage
            srcUrl2RelativeFilePathInStorage = userStorage.getRelativePathInStorage(srcAttValues);
            // replace all image src attribute to relative_file_path_in_storage
            content = htmlParser.replaceImgTagSrcUrl(content, imgTag2SrcValue, srcUrl2RelativeFilePathInStorage);
        }

        if (CourseStatus.EDITING.equals(entity.getStatus())) {
            entity.setStatus(CourseStatus.ENABLE);
        }
        entity.setContent(content);
        entity = repository.save(entity);
        return entities2BeansWithContent(entity);
    }

    @Transactional
    public Nurse360CourseBean moveCourse2Temporary(long courseId) {
        logger.info("move course {} image file to temporary directory", courseId);

        Nurse360CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.info("the course do not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (CourseStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the course is editing");
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }

        // has content need to move to temporary
        boolean needMove = !VerifyUtil.isStringEmpty(entity.getContent());
        if (needMove) {
            moveOfficialFileToTemporary(entity.getContent());
        }

        // update to editing
        entity.setStatus(CourseStatus.EDITING);
        entity = repository.save(entity);

        Nurse360CourseBean bean = entities2BeansWithContent(entity);
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
                String relativePath = userStorage.getRelativePathInStorage(srcUrl);
                if (VerifyUtil.isStringEmpty(relativePath)) {
                    continue;
                }
                srcFilesInStorage.add(userStorage.getStoragePath() + relativePath);
            }
            tempStorage.moveFileToHere(srcFilesInStorage);
        }
        else {
            logger.info("img tag is empty. nothing move to temporary directory");
        }
    }

    private void moveTemporaryFileToOfficial(String htmlContent) {
        logger.info("move html img tag src 's images to user storage");
        if (VerifyUtil.isStringEmpty(htmlContent)) {
            logger.info("html content is empty. nothing move to user directory");
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
            userStorage.moveFileToHere(srcFilesInStorage);
        }
        else {
            logger.info("img tag is empty. nothing move to official directory");
        }
    }

    @Transactional
    public String createTemporaryFile(long courseId, String imageName, InputStream image) {
        logger.info("create temporary file by token={} courseId={} imageName={} image={}",
                courseId, imageName, image);
        Nurse360CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.info("the course do not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (!CourseStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the course is not editing");
            throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
        }
        String relativePath = tempStorage.addFile(imageName, image);
        return relativePath;
    }



    //=============================================================
    //          delete   for administrator use
    //=============================================================

    @Transactional
    public String deleteByIds(String strIds) {
        logger.info("delete course by ids={}", strIds);
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
        logger.info("delete course by ids={}", lIds);
        if (null==lIds || lIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Nurse360CourseEntity> resultSet = repository.findAll(lIds);

        //
        // clean front cover
        //
        List<Long> frontCoverIds = new ArrayList<>();
        for (Nurse360CourseEntity tmp : resultSet) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            frontCoverIds.add(tmp.getFrontCover());
        }
        userStorage.deleteFiles(frontCoverIds);

        //
        // clean the course content image
        //
        HtmlParser          htmlParser = HtmlParser.newInstance();
        Map<String, String> imgTag2SrcValue;
        List<String>        srcAttVals;
        for (Nurse360CourseEntity tmp : resultSet) {
            if (VerifyUtil.isStringEmpty(tmp.getContent())) {
                continue;
            }

            // get all image src attribute url
            srcAttVals = htmlParser.getSrcUrls(tmp.getContent());

            // remove all image src attribute url from storage
            for (String srcUrl : srcAttVals) {
                if (CourseStatus.EDITING.equals(tmp.getStatus())) {
                    tempStorage.deleteFile(srcUrl);
                } else {
                    userStorage.deleteFile(srcUrl);
                }
            }
        }

        //
        // remove the hospital relations
        //
        courseHospitalRelationService.deleteRelationByCourseIds(lIds);

        //
        // remove the activities
        //
        repository.delete(resultSet);

        return lIds;
    }
}

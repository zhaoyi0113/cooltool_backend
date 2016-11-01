package com.cooltoo.go2nurse.service;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.converter.CourseBeanConverter;
import com.cooltoo.go2nurse.entities.CourseEntity;
import com.cooltoo.go2nurse.repository.CourseRepository;
import com.cooltoo.go2nurse.service.file.TemporaryGo2NurseFileStorageService;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.NetworkUtil;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;


/**
 * Created by hp on 2016/6/8.
 */
@Service("CourseService")
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private CourseRepository repository;
    @Autowired private CourseBeanConverter beanConverter;
    @Autowired private UserGo2NurseFileStorageService userStorage;
    @Autowired private TemporaryGo2NurseFileStorageService tempStorage;
    @Autowired private CourseRelationManageService relationManageService;

    private FileUtil fileUtil = FileUtil.getInstance();

    //===========================================================
    //                    get
    //===========================================================

    public boolean existCourse(long courseId) {
        boolean exist = repository.exists(courseId);
        logger.info("exist course={}, exists={}", courseId, exist);
        return exist;
    }

    public List<CourseBean> getCourseByUniqueId(String uniqueId) {
        logger.info("get course by uniqueId={}", uniqueId);
        List<CourseEntity> resultSet = repository.findByUniqueId(uniqueId, sort);
        List<CourseBean> beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        return beans;
    }

    public long countByNameLikeAndStatus(String title, String strStatus) {
        logger.info("count all course by status={}", strStatus);
        CourseStatus status = CourseStatus.parseString(strStatus);
        title = VerifyUtil.isStringEmpty(title) ? null : VerifyUtil.reconstructSQLContentLike(title.trim());
        long count = repository.countByNameLikeAndStatus(title, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<CourseBean> getCourseByNameAndStatus(String title, String strStatus) {
        logger.info("get all course by status={}", strStatus);
        CourseStatus status = CourseStatus.parseString(strStatus);
        title = VerifyUtil.isStringEmpty(title) ? null : VerifyUtil.reconstructSQLContentLike(title.trim());
        List<CourseEntity> resultSet;
        resultSet = repository.findByNameLikeAndStatus(title, status, sort);
        List<CourseBean> beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<CourseBean> getCourseByNameAndStatus(String title, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get all course by status={} at page={} sizePerPage={}", strStatus, pageIndex, sizePerPage);
        CourseStatus status = CourseStatus.parseString(strStatus);
        title = VerifyUtil.isStringEmpty(title) ? null : VerifyUtil.reconstructSQLContentLike(title.trim());
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<CourseEntity> resultSet = null;
        resultSet = repository.findByNameLikeAndStatus(title, status, page);
        List<CourseBean> beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
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
                resultSet = repository.findCourseIdByStatusAndIdIn(status, courseIds, sort);
            }
        }
        else {
            resultSet = repository.findCourseIdByStatusAndIdIn(status, courseIds, sort);
        }
        logger.info("count is {}", resultSet.size());
        return resultSet;
    }

    public List<CourseBean> getCourseByStatusAndIds(CourseStatus status, List<Long> courseIds, Integer pageIndex, Integer sizePerPage) {
        logger.info("get course by status={} ids={}", status, courseIds);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }
        Iterable<CourseEntity> resultSet;
        if (null==pageIndex || null==sizePerPage) {
            resultSet = repository.findCourseByStatusAndIdIn(status, courseIds, sort);
        }
        else {
            PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
            resultSet = repository.findCourseByStatusAndIdIn(status, courseIds, page);
        }
        List<CourseBean>   beans = entities2BeansWithoutContent(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public Map<Long, CourseBean> getCourseMapByStatusAndIds(CourseStatus status, List<Long> courseIds, Integer pageIndex, Integer sizePerPage) {
        List<CourseBean> beans = getCourseByStatusAndIds(status, courseIds, pageIndex, sizePerPage);
        Map<Long, CourseBean> map = new HashMap<>();
        for (CourseBean tmp : beans) {
            map.put(tmp.getId(), tmp);
        }
        return map;
    }

    private List<CourseBean> entities2BeansWithoutContent(Iterable<CourseEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<CourseBean> retVal = new ArrayList<>();
        for (CourseEntity tmp : entities) {
            CourseBean bean = beanConverter.convert(tmp);
            //
            // notice : not return the content as it to large.
            //
            bean.setContent(null);
            retVal.add(bean);
        }

        return retVal;
    }

    private CourseBean entities2BeansWithContent(CourseEntity entity) {
        if (null == entity) {
            return null;
        }
        CourseBean bean = beanConverter.convert(entity);
        List<CourseBean> beans = new ArrayList<>();
        beans.add(bean);
        fillOtherProperties(beans);
        return bean;
    }

    private void fillOtherProperties(List<CourseBean> courses) {
        if (null==courses || courses.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (CourseBean tmp : courses) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            imageIds.add(tmp.getFrontCover());
        }

        Map<Long, String> imageId2Url = userStorage.getFileUrl(imageIds);
        for (CourseBean tmp : courses) {
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

    public CourseBean getCourseById(long courseId, String httpNginxBaseUrl) {
        logger.info("get course by id {}", courseId);
        CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.warn("course not exist.");
            return null;
        }

        CourseBean bean = beanConverter.convert(entity);
        if (bean.getFrontCover()>0) {
            String imgUrl = userStorage.getFileURL(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }


        String nginxSubUrl = "";
        if (CourseStatus.EDITING.equals(bean.getStatus())) {
            nginxSubUrl = tempStorage.getNginxRelativePath();
        }
        else {
            nginxSubUrl = userStorage.getNginxRelativePath();
        }
        String content = null==bean ? null : bean.getContent();
        if (!VerifyUtil.isStringEmpty(httpNginxBaseUrl)) {
            content = HtmlParser.newInstance().addPrefixToImgTagSrcUrl(content, httpNginxBaseUrl, nginxSubUrl);
        }
        bean.setContent(content);

        return bean;
    }

    public CourseBean getCourseByName(String name, String httpNginxBaseUrl) {
        logger.info("get course by title {}", name);
        List<CourseEntity> entity = repository.findByName(name, sort);
        if (null==entity || entity.isEmpty()) {
            logger.info("course not exist.");
            return null;
        }
        if (entity.size()!=1) {
            logger.info("course more than one={}", entity);
            return null;
        }

        CourseBean bean = beanConverter.convert(entity.get(0));
        if (bean.getFrontCover()>0) {
            String imgUrl = userStorage.getFileURL(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }


        String nginxSubUrl = "";
        if (CourseStatus.EDITING.equals(bean.getStatus())) {
            nginxSubUrl = tempStorage.getNginxRelativePath();
        }
        else {
            nginxSubUrl = userStorage.getNginxRelativePath();
        }
        String content = null==bean ? null : bean.getContent();
        content = HtmlParser.newInstance().addPrefixToImgTagSrcUrl(content, httpNginxBaseUrl, nginxSubUrl);
        bean.setContent(content);

        return bean;
    }

    //===========================================================
    //                    create
    //===========================================================

    @Transactional
    public CourseBean createCourse(String name, String introduction, String link, String keyword) {
        logger.info("create an course by name={} introduction={} link={} keyword={}",
                name, introduction, link, keyword);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("the name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
//// stop check the title is exist or not
//        long count = repository.countByName(name);
//        if (count>0) {
//            logger.error("the name is exist already");
//            throw new BadRequestException(ErrorCode.DATA_ERROR);
//        }

        CourseEntity entity = new CourseEntity();
        entity.setName(name);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction);
        }
        if (!VerifyUtil.isStringEmpty(link)) {
            entity.setLink(link);
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
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setUniqueId(uniqueId);

        if (!VerifyUtil.isStringEmpty(keyword)) {
            keyword = keyword.trim();
            if (keyword.length()>1) {
                keyword = keyword.substring(0, 1);
            }
            entity.setKeyword(keyword);
        }

        entity.setStatus(CourseStatus.ENABLE);
        entity.setTime(new Date());
        entity = repository.save(entity);
        logger.info("create an course id={}", entity.getId());
        return beanConverter.convert(entity);
    }

    //===========================================================
    //                    update
    //===========================================================

    @Transactional
    public CourseBean updateCourseBasicInfo(long courseId,
                                                String name, String introduction,
                                                String frontCoverName, InputStream frontCover,
                                                String link, String keyword
    ) {
        logger.info("update course {} by name={}, introduction={} imageName={} image={} link={} keyword={}",
                courseId, name, introduction, frontCoverName, frontCover!=null, link, keyword);

        CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.error("the course is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
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
            if (keyword.length()>1) {
                keyword = keyword.substring(0, 1);
            }
            entity.setKeyword(keyword);
            changed = true;
        }

        if (changed) {
            entity.setTime(new Date());
            entity = repository.save(entity);
        }

        CourseBean bean = beanConverter.convert(entity);
        bean.setFrontCoverUrl(imageUrl);
        return bean;
    }

    @Transactional
    public CourseBean updateCourseStatus(long courseId, String strStatus) {
        logger.info("update course {} status to {}", courseId, strStatus);

        CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.error("the course is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        CourseStatus status = CourseStatus.parseString(strStatus);
        if (null!=status) {
            if (CourseStatus.EDITING.equals(entity.getStatus())) {
                logger.error("the course is editing");
                throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
            }
            if (CourseStatus.EDITING.equals(status)) {
                logger.error("can not set course to editing");
                throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
            }
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        CourseBean bean = entities2BeansWithContent(entity);
        bean.setContent(null);
        return bean;
    }

    //===========================================================
    //         update for the image src attribute
    //===========================================================

    @Transactional
    public CourseBean updateCourseContent(long courseId, String content) {
        logger.info("update course {} token={} content={}", courseId, content);

        CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.error("the course is not exist (and clean token cache)");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!CourseStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the course is not editing");
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

        entity.setContent(content);
        entity.setStatus(CourseStatus.ENABLE);
        entity = repository.save(entity);
        return entities2BeansWithContent(entity);
    }

    @Transactional
    public CourseBean moveCourse2Temporary(long courseId) {
        logger.info("move course {} image file to temporary directory", courseId);

        CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.info("the course do not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (CourseStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the course is editing");
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }

        // has content need to move to temporary
        boolean needMove = !VerifyUtil.isStringEmpty(entity.getContent());
        if (needMove) {
            moveOfficialFileToTemporary(entity.getContent());
        }

        // update to editing
        entity.setStatus(CourseStatus.EDITING);
        entity = repository.save(entity);

        CourseBean bean = entities2BeansWithContent(entity);
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
        CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.info("the course do not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!CourseStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the course is not editing");
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }
        String relativePath = tempStorage.addFile(imageName, image);
        return relativePath;
    }

    @Transactional
    public CourseBean setCourseContentWithHtml(long courseId, String htmlContent) {
        logger.info("update course {} token={} content={}", courseId, htmlContent);

        CourseEntity entity = repository.findOne(courseId);
        if (null==entity) {
            logger.error("the course is not exist (and clean token cache)");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!CourseStatus.EDITING.equals(entity.getStatus())) {
            logger.error("the course is not editing");
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }

        if (VerifyUtil.isStringEmpty(htmlContent)) {
            htmlContent = "";  // avoid NullException
        }
        else {
            HtmlParser htmlParser = HtmlParser.newInstance();
            // get image tags src attribute url
            List<String> srcUrls = htmlParser.getSrcUrls(htmlContent);

            // fetch the image tags src to /temp path
            Map<String, String> srcUrlToFileInTempBasePath = NetworkUtil.fetchAllWebFile(srcUrls, tempStorage.getStoragePath());

            // move image tags file from /temp/xxxxxxx  path to temp/xx/xxxxxxxxxxxxxxxxx path
            Map<String, String> fileInTempBaseToRelativeTempPath = new HashMap<>();
            Map<String, String> srcUrlsToRelativeUrl = new HashMap<>();
            Set<String> fetchUrls = srcUrlToFileInTempBasePath.keySet();
            for (String url : fetchUrls) {
                try {
                    String[] relativePath = fileUtil.encodeFilePath(fileUtil.getFileName(url));
                    String fileInTempBase = srcUrlToFileInTempBasePath.get(url);

                    srcUrlsToRelativeUrl.put(url, relativePath[0] + File.separator + relativePath[1]);
                    fileInTempBaseToRelativeTempPath.put(fileInTempBase, tempStorage.getStoragePath() + relativePath[0] + File.separator + relativePath[1]);

                } catch (Exception ex) {
                    logger.error("move temp files to directory failed!");
                    throw new BadRequestException(ErrorCode.DATA_ERROR);
                }
            }
            fileUtil.moveFiles(fileInTempBaseToRelativeTempPath);

            // get all image tag and its src attribute value
            Map<String, String> imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(htmlContent);
            // replace image tags src url
            htmlContent = htmlParser.replaceImgTagSrcUrl(htmlContent, imgTag2SrcValue, srcUrlsToRelativeUrl);

            // move temporary
            moveTemporaryFileToOfficial(htmlContent);
        }
        entity.setContent(htmlContent);
        entity.setStatus(CourseStatus.ENABLE);
        entity = repository.save(entity);
        return entities2BeansWithContent(entity);
    }


    //=============================================================
    //          delete   for administrator use
    //=============================================================
    @Transactional
    public CourseBean deleteCourseFrontCover(long courseId) {
        logger.info("delete course={} 's front cover", courseId);
        CourseEntity course = repository.findOne(courseId);
        if (null==course) {
            logger.info("the course do not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        long frontCoverId = course.getFrontCover();
        String frontCoverUrl = userStorage.getFileURL(frontCoverId);
        if (userStorage.fileExist(frontCoverId)) {
            boolean deleted = userStorage.deleteFile(frontCoverId);
            if (deleted) {
                frontCoverUrl = "";
                course.setFrontCover(0);
                repository.save(course);
            }
        }

        CourseBean bean = beanConverter.convert(course);
        bean.setFrontCoverUrl(frontCoverUrl);
        return bean;
    }

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

        List<CourseEntity> resultSet = repository.findAll(lIds);

        //
        // clean front cover
        //
        List<Long> frontCoverIds = new ArrayList<>();
        for (CourseEntity tmp : resultSet) {
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
        for (CourseEntity tmp : resultSet) {
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
        // remove the course
        //
        repository.delete(resultSet);


        //
        // remove the course relation with department, diagnostic, category  permanently
        //
        relationManageService.deleteRelationsByCourseId(lIds);

        return lIds;
    }
}

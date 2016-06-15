package com.cooltoo.services;

import com.cooltoo.beans.EmploymentInformationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.EmploymentType;
import com.cooltoo.converter.EmploymentInformationBeanConverter;
import com.cooltoo.entities.EmploymentInformationEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.EmploymentInformationRepository;
import com.cooltoo.services.file.OfficialFileStorageService;
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
 * Created by hp on 2016/4/20.
 */
@Service("EmploymentInformationService")
public class EmploymentInformationService {

    private static final Logger logger = LoggerFactory.getLogger(EmploymentInformationService.class.getName());

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.DESC, "time")
            );

    @Autowired private EmploymentInformationRepository repository;
    @Autowired private EmploymentInformationBeanConverter beanConverter;
    @Autowired private OfficialFileStorageService officialStorage;

    //===========================================================
    //                    get
    //===========================================================

    public long countEmploymentInfoByStatus(String strStatus, String strType) {
        logger.info("count all employment information by status={} and type={}", strStatus, strType);

        CommonStatus status = CommonStatus.parseString(strStatus);
        EmploymentType type = EmploymentType.parseString(strType);
        long count = repository.countByStatusAndType(status, type);

        logger.info("count all Employment Information, size={}", count);
        return count;
    }

    public List<EmploymentInformationBean> getEmploymentInfoByIds(String strEmploymentInfoIds) {
        List<Long> employmentInfoIds = VerifyUtil.parseLongIds(strEmploymentInfoIds);
        return getEmploymentInfoByIds(employmentInfoIds);
    }

    public List<EmploymentInformationBean> getEmploymentInfoByIds(List<Long> employmentInfoIds) {
        if (null==employmentInfoIds || employmentInfoIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<EmploymentInformationEntity> resultSet = repository.findAll(employmentInfoIds);
        List<EmploymentInformationBean>   employments = entities2Beans(resultSet);
        fillOtherProperties(employments);
        return employments;
    }

    public List<EmploymentInformationBean> getEmploymentInfoByStatus(String strStatus, String strType) {
        logger.info("get employment information by status {} type={}", strStatus, strType);

        // get nuser by authority
        List<EmploymentInformationEntity> resultSet = (List<EmploymentInformationEntity>) getEmploymentInfoByStatus(strStatus, strType, sort);

        // parse to bean
        List<EmploymentInformationBean> beans = entities2Beans(resultSet);
        fillOtherProperties(beans);
        logger.info("get employment information size={}", beans.size());
        return beans;
    }

    public List<EmploymentInformationBean> getEmploymentInfoByStatus(String strStatus, String strType, int pageIndex, int number) {
        logger.info("get employment information by status {} type={} at page {} with number {}", strStatus,strType, pageIndex, number);

        // get nuser by authority
        PageRequest page = new PageRequest(pageIndex, number, sort);
        Page<EmploymentInformationEntity> resultSetPage = (Page<EmploymentInformationEntity>) getEmploymentInfoByStatus(strStatus, strType, page);
        // parse to bean
        List<EmploymentInformationBean> beans = entities2Beans(resultSetPage);
        fillOtherProperties(beans);
        logger.info("get employment information size={}", beans.size());
        return beans;
    }

    private Iterable<EmploymentInformationEntity> getEmploymentInfoByStatus(String strStatus, String strType, Object sortOrPage) {
        Iterable<EmploymentInformationEntity> resultSet = null;
        CommonStatus status = CommonStatus.parseString(strStatus);
        EmploymentType type = EmploymentType.parseString(strType);
        if (sortOrPage instanceof PageRequest) {
            resultSet = repository.findByStatusAndType(status, type, (Pageable) sortOrPage);
        }
        else if (sortOrPage instanceof Sort) {
            resultSet = repository.findByStatusAndType(status, type, (Sort) sortOrPage);
        }
        return resultSet;
    }

    private List<EmploymentInformationBean> entities2Beans(Iterable<EmploymentInformationEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<EmploymentInformationBean> retVal = new ArrayList<>();
        for (EmploymentInformationEntity tmp : entities) {
            EmploymentInformationBean bean = beanConverter.convert(tmp);
            retVal.add(bean);
        }

        return retVal;
    }

    private void fillOtherProperties(List<EmploymentInformationBean> employmentInfo) {
        if (null==employmentInfo || employmentInfo.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (EmploymentInformationBean tmp : employmentInfo) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            imageIds.add(tmp.getFrontCover());
        }

        Map<Long, String> imgId2Url = officialStorage.getFilePath(imageIds);
        for (EmploymentInformationBean tmp : employmentInfo) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            long   imgId  = tmp.getFrontCover();
            String imgUrl = imgId2Url.get(imgId);
            tmp.setFrontCoverUrl(imgUrl);
        }
    }

    public EmploymentInformationBean getEmploymentInfoById(long employmentInfoId) {
        logger.info("get employment information by id {}", employmentInfoId);
        EmploymentInformationEntity entity = repository.findOne(employmentInfoId);
        if (null==entity) {
            logger.warn("employment information not exist.");
            return null;
        }

        EmploymentInformationBean bean = beanConverter.convert(entity);
        if (bean.getFrontCover()>0) {
            String imgUrl = officialStorage.getFilePath(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }

        return bean;
    }

    public EmploymentInformationBean getEmploymentInfoByTitle(String title) {
        logger.info("get employment information by title {}", title);
        List<EmploymentInformationEntity> entity = repository.findByTitle(title);
        if (null==entity) {
            logger.info("employment information not exist.");
            return null;
        }
        if (entity.size()!=1) {
            logger.info("employment information more than one={}", entity);
            return null;
        }

        EmploymentInformationBean bean = beanConverter.convert(entity.get(0));
        if (bean.getFrontCover()>0) {
            String imgUrl = officialStorage.getFilePath(bean.getFrontCover());
            bean.setFrontCoverUrl(imgUrl);
        }

        return bean;
    }

    //===========================================================
    //                    create
    //===========================================================

    @Transactional
    public EmploymentInformationBean createEmploymentInfo(String title, String url, int grade, String strType) {
        logger.info("create an employment information by title={}, url={}, grade={}, type={}", title, url, grade, strType);
        if (VerifyUtil.isStringEmpty(title)) {
            logger.error("the title is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        EmploymentInformationEntity entity = new EmploymentInformationEntity();
        entity.setTitle(title.trim());

        if (grade<0) {
            grade = 0;
        }
        entity.setGrade(grade);

        if (!VerifyUtil.isStringEmpty(url)) {
            entity.setUrl(url.trim());
        }

        EmploymentType type = EmploymentType.parseString(strType);
        if (null!=type) {
            entity.setType(type);
        }

        entity.setStatus(CommonStatus.DISABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);
        logger.info("create an employment information id={}", entity.getId());
        return beanConverter.convert(entity);
    }

    //===========================================================
    //                    update
    //===========================================================

    @Transactional
    public EmploymentInformationBean updateEmploymentInfo(long employmentInfoId,
                                                          String title, String url, int grade, String strType, String strStatus,
                                                          String frontCoverName, InputStream frontCover
    ) {
        logger.info("update employment information {} by title={}, imageName={} image={}, url={}, grade={}, type={}, status={}",
                    employmentInfoId, title, frontCoverName, frontCover!=null, url, grade, strType, strStatus);

        EmploymentInformationEntity entity = repository.findOne(employmentInfoId);
        if (null==entity) {
            logger.error("the employment information is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;

        if (!VerifyUtil.isStringEmpty(title)) {
            title = title.trim();
            if (!title.equals(entity.getTitle())) {
                List<EmploymentInformationEntity> exist = repository.findByTitle(title);
                if (null==exist || exist.isEmpty()) {
                    entity.setTitle(title);
                    changed = true;
                }
            }
        }

        String frontCoverUrl = null;
        if (null!=frontCover) {
            frontCoverName = VerifyUtil.isStringEmpty(frontCoverName) ? "frontCover"+System.currentTimeMillis() : frontCoverName;
            long imageId = officialStorage.addFile(entity.getFrontCover(), frontCoverName, frontCover);
            frontCoverUrl = officialStorage.getFilePath(imageId);
            entity.setFrontCover(imageId);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(url)) {
            entity.setUrl(url);
            changed = true;
        }
        if (grade>=0) {
            entity.setGrade(grade);
            changed = true;
        }
        EmploymentType type = EmploymentType.parseString(strType);
        if (null!=type && !type.equals(entity.getType())) {
            entity.setType(type);
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

        EmploymentInformationBean bean = beanConverter.convert(entity);
        bean.setFrontCoverUrl(frontCoverUrl);
        logger.info("update employment information is {}", bean);
        return bean;
    }

    //=============================================================
    //          delete   for administrator use
    //=============================================================

    @Transactional
    public String deleteByIds(String strIds) {
        logger.info("delete employment information by ids={}", strIds);
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
        logger.info("delete employment information by ids={}", lIds);
        if (null==lIds || lIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<EmploymentInformationEntity> resultSet = repository.findAll(lIds);

        //
        // clean front cover
        //
        List<Long> frontCoverIds = new ArrayList<>();
        for (EmploymentInformationEntity tmp : resultSet) {
            if (tmp.getFrontCover()<=0) {
                continue;
            }
            frontCoverIds.add(tmp.getFrontCover());
        }
        officialStorage.deleteFiles(frontCoverIds);

        //
        // remove the Employment Information
        //
        repository.delete(resultSet);

        return lIds;
    }
}

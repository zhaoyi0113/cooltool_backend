package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.converter.DoctorBeanConverter;
import com.cooltoo.go2nurse.entities.DoctorEntity;
import com.cooltoo.go2nurse.repository.DoctorRepository;
import com.cooltoo.go2nurse.service.file.TemporaryGo2NurseFileStorageService;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.NetworkUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.html.HTMLPreElement;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by hp on 2016/7/25.
 */
@Service("DoctorService")
public class DoctorService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorService.class);

    @Autowired private DoctorRepository repository;
    @Autowired private DoctorBeanConverter beanConverter;
    @Autowired private UserGo2NurseFileStorageService userFileStorage;

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private Go2NurseUtility utility;
    @Autowired private NurseDoctorScoreService nurseDoctorScoreService;
    @Autowired private DoctorOrderService doctorOrderService;

    @Autowired private TemporaryGo2NurseFileStorageService tempStorage;
    @Autowired private UserGo2NurseFileStorageService userStorage;
    private final HtmlParser htmlParser = HtmlParser.newInstance();
    private final FileUtil fileUtil = FileUtil.getInstance();


    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    //=====================================================================
    //                   getting
    //=====================================================================
    public DoctorBean getDoctorById(long doctorId, String nginxBaseUrl) {
        logger.info("get doctor by id={}", doctorId);
        DoctorEntity doctor = repository.findOne(doctorId);
        if (null!=doctor) {
            DoctorBean bean = beanConverter.convert(doctor);

            // fill other properties
            List<DoctorBean> beans = new ArrayList<>();
            beans.add(bean);
            fillOtherProperties(beans);

            if (VerifyUtil.isStringEmpty(nginxBaseUrl)) {
                bean.setIntroduction(null);
            }
            else {
                String introduction = bean.getIntroduction();
                introduction = htmlParser.addPrefixToImgTagSrcUrl(introduction, nginxBaseUrl, userStorage.getNginxRelativePath());
                bean.setIntroduction(introduction);
            }
            logger.info("result is {}", bean);
            return bean;
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    public boolean existDoctor(long doctorId) {
        return repository.exists(doctorId);
    }

    public Map<Long, DoctorBean> getDoctorIdToBean(List<Long> doctorIds) {
        logger.info("get doctor by doctorIds size={}", null==doctorIds ? 0 : doctorIds.size());
        Map<Long, DoctorBean> map = new HashMap<>();
        if (!VerifyUtil.isListEmpty(doctorIds)) {
            List<DoctorEntity> entities = repository.findAll(doctorIds);
            List<DoctorBean> beans = entitiesToBeans(entities);
            fillOtherProperties(beans);
            for (DoctorBean tmp : beans) {
                map.put(tmp.getId(), tmp);
            }
        }
        else {
            logger.warn("statuses is empty");
        }
        logger.info("count is ={}", map.size());
        return map;
    }

    public long countDoctor(List<CommonStatus> statuses) {

        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            count = repository.countByStatusIn(statuses);
        }
        logger.info("count doctor by statuses={}, size is {}", statuses, count);
        return count;
    }

    public List<DoctorBean> getDoctor(List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get doctor by statuses={} at page={} size={}", statuses, pageIndex, sizePerPage);
        List<DoctorBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
            Page<DoctorEntity> entities = repository.findByStatusIn(statuses, pageRequest);
            beans = entitiesToBeans(entities);
            fillOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }
        logger.info("count is ={}", beans.size());
        return beans;
    }

    public long countDoctor(boolean orderByHospital, Integer hospitalId, Integer departmentId, List<CommonStatus> statuses) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            List<Long> doctorIdsInHospital = doctorOrderService.getDoctorOrderedList(orderByHospital, hospitalId, departmentId);
            if (!VerifyUtil.isListEmpty(doctorIdsInHospital)) {
                count = repository.countByIdInAndStatusIn(doctorIdsInHospital, statuses);
            }
        }
        logger.info("count doctor by hospital={} department={} and status={}, size is {}",
                hospitalId, departmentId, statuses, count);
        return count;
    }

    public List<DoctorBean> getDoctor(boolean orderByHospital, Integer hospitalId, Integer departmentId, List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get doctor by hospital={} department={} and status={} at page={} size={}",
                hospitalId, departmentId, statuses, pageIndex, sizePerPage);
        List<DoctorBean> beans = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(statuses)) {
            List<Long> doctorIdsInHospital = doctorOrderService.getDoctorOrderedList(orderByHospital, hospitalId, departmentId);
            if (!VerifyUtil.isListEmpty(doctorIdsInHospital)) {
                List<DoctorEntity> doctorsReturn = repository.findEntityByIdInAndStatusIn(doctorIdsInHospital, statuses);
                List<DoctorEntity> doctorsSorted = new ArrayList<>();
                for (Long tmpId : doctorIdsInHospital) {
                    for (DoctorEntity tmp : doctorsReturn) {
                        if (tmp.getId()==tmpId) {
                            doctorsSorted.add(tmp);
                            break;
                        }
                    }
                }
                doctorsReturn.clear();
                int startIndex = (pageIndex*sizePerPage)<0 ? 0 : (pageIndex*sizePerPage);
                for (int i=startIndex; i<doctorsSorted.size(); i++) {
                    if (i<(pageIndex*sizePerPage+sizePerPage)) {
                        doctorsReturn.add(doctorsSorted.get(i));
                    }
                }
                beans = entitiesToBeans(doctorsReturn);
            }
            fillOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }
        logger.info("count is ={}", beans.size());
        return beans;
    }

    private List<DoctorBean> entitiesToBeans(Iterable<DoctorEntity> entities) {
        List<DoctorBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (DoctorEntity entity : entities) {
            entity.setIntroduction(null);
            DoctorBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<DoctorBean> items) {
        if (VerifyUtil.isListEmpty(items)) {
            return;
        }

        List<Long> doctorIds = new ArrayList<>();
        List<Long> imagesId = new ArrayList<>();
        List<Integer> hospitalIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (DoctorBean item : items) {
            if (!imagesId.contains(item.getImageId())) {
                imagesId.add(item.getImageId());
            }
            if (!imagesId.contains(item.getHeadImageId())) {
                imagesId.add(item.getHeadImageId());
            }
            if (!hospitalIds.contains(item.getHospitalId())) {
                hospitalIds.add(item.getHospitalId());
            }
            if (!departmentIds.contains(item.getDepartmentId())) {
                departmentIds.add(item.getDepartmentId());
            }
            if (!doctorIds.contains(item.getId())) {
                doctorIds.add(item.getId());
            }
        }

        Map<Long, String> imageIdToUrl = userFileStorage.getFileUrl(imagesId);
        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalIds);
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefixForNurseGo());
        Map<Long, Float> doctorIdToScore = nurseDoctorScoreService.getScoreByReceiverTypeAndIds(UserType.DOCTOR, doctorIds);
        for (DoctorBean item : items) {
            String imageUrl = imageIdToUrl.get(item.getImageId());
            if (!VerifyUtil.isStringEmpty(imageUrl)) {
                item.setImageUrl(imageUrl);
            }
            imageUrl = imageIdToUrl.get(item.getHeadImageId());
            if (!VerifyUtil.isStringEmpty(imageUrl)) {
                item.setHeadImageUrl(imageUrl);
            }
            HospitalBean hospital = hospitalIdToBean.get(item.getHospitalId());
            if (null!=hospital) {
                item.setHospital(hospital);
            }
            for (HospitalDepartmentBean tmp : departments) {
                if (tmp.getId()==item.getDepartmentId()) {
                    item.setDepartment(tmp);
                }
            }
            Float score = doctorIdToScore.get(item.getId());
            item.setScore(null==score ? 0F : score);
        }
    }

    //=====================================================================
    //                   deleting
    //=====================================================================
    @Transactional
    public List<Long> deleteDoctorByIds(List<Long> doctorIds) {
        logger.info("delete doctor by doctorIds={}", doctorIds);
        if (VerifyUtil.isListEmpty(doctorIds)) {
            return doctorIds;
        }
        List<DoctorEntity> entities = repository.findAll(doctorIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return doctorIds;
        }

        List<Long> imagesId = new ArrayList<>();
        for (DoctorEntity entity : entities) {
            if (!imagesId.contains(entity.getImageId())) {
                imagesId.add(entity.getImageId());
            }
            if (!imagesId.contains(entity.getHeadImageId())) {
                imagesId.add(entity.getHeadImageId());
            }
        }
        userFileStorage.deleteFiles(imagesId);
        repository.delete(entities);

        logger.info("delete doctor={}", entities);
        return doctorIds;
    }

    //=====================================================================
    //                   updating
    //=====================================================================
    @Transactional
    public DoctorBean updateDoctor(long doctorId,
                                   String name, String post, String jobTitle, String beGoodAt,
                                   int departmentId, String strStatus, int grade
    ) {
        logger.info("update doctor={} by name={} post={} jobTitle={} beGoodAt={} departmentId={} status={} grade={} introduction={}",
                doctorId, name, post, jobTitle, beGoodAt, departmentId, strStatus, grade);
        DoctorEntity entity = repository.findOne(doctorId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name)) {
            name = name.trim();
            if (!name.equals(entity.getName())) {
                entity.setName(name.trim());
                changed = true;
            }
        }
        if (!VerifyUtil.isStringEmpty(post)) {
            entity.setPost(post.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(jobTitle)) {
            entity.setJobTitle(jobTitle.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(beGoodAt)) {
            entity.setBeGoodAt(beGoodAt.trim());
            changed = true;
        }
        if (departmentId>=0 && departmentId!=entity.getDepartmentId() && departmentRepository.exists(departmentId)) {
            HospitalDepartmentEntity department = departmentRepository.findOne(departmentId);
            if (null!=department) {
                entity.setHospitalId(department.getHospitalId());
                entity.setDepartmentId(departmentId);
                changed = true;
            }
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status) {
            entity.setStatus(status);
            changed = true;
        }
        if (grade>=0 && grade!=entity.getGrade()) {
            entity.setGrade(grade);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        DoctorBean bean = beanConverter.convert(entity);
        logger.info("doctor updated is {}", bean);
        return bean;
    }

    @Transactional
    public DoctorBean updateDoctorHeadImage(long doctorId, String imageName, InputStream image, boolean isHeaderImage) {
        logger.info("update doctor={} by imageName={} image={}",
                doctorId, imageName, null!=image);

        DoctorEntity entity = repository.findOne(doctorId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        String imageUrl = "";
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "doctor_head_" + System.nanoTime();
            }
            long imageId = 0;
            if (isHeaderImage) {
                imageId = entity.getHeadImageId();
            }
            else {
                imageId = entity.getImageId();
            }
            imageId = userFileStorage.addFile(imageId, imageName, image);
            imageUrl = userFileStorage.getFileURL(imageId);
            if (isHeaderImage) {
                entity.setHeadImageId(imageId);
            }
            else {
                entity.setImageId(imageId);
            }
            repository.save(entity);
        }

        DoctorBean bean = beanConverter.convert(entity);
        if (isHeaderImage) {
            bean.setHeadImageUrl(imageUrl);
        }
        else {
            bean.setImageUrl(imageUrl);
        }
        logger.info("doctor updated is {}", bean);
        return bean;
    }

    @Transactional
    public DoctorBean setDoctorIntroductionWithHtml(long doctorId, String htmlContent) {
        logger.info("update doctor {} introduction={}", doctorId, htmlContent);

        DoctorEntity entity = repository.findOne(doctorId);
        if (null==entity) {
            logger.error("the doctor is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (VerifyUtil.isStringEmpty(htmlContent)) {
            htmlContent = "";  // avoid NullException
        }
        else {
            // move all file to temporary
            moveOfficialFileToTemporary(htmlContent);

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

            // move temporary file to official path
            moveTemporaryFileToOfficial(htmlContent);
        }
        entity.setIntroduction(htmlContent);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    private void moveTemporaryFileToOfficial(String htmlContent) {
        logger.info("move html img tag src 's images to user storage");
        if (VerifyUtil.isStringEmpty(htmlContent)) {
            logger.info("html content is empty. nothing move to user directory");
        }

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

    private void moveOfficialFileToTemporary(String htmlContent) {
        logger.info("move html img tag src 's images to temporary storage");
        if (VerifyUtil.isStringEmpty(htmlContent)) {
            logger.info("html content is empty. nothing move to temporary directory");
        }

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

    //=====================================================================
    //                   adding
    //=====================================================================

    @Transactional
    public DoctorBean addDoctor(String name, String post, String jobTitle, String beGoodAt, int departmentId, int grade) {
        logger.info("add doctor by name={} post={} jobTitle={} beGoodAt={} departmentId={} grade={} introduction={}",
                name, post, jobTitle, beGoodAt, departmentId, grade);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();

        DoctorEntity entity = new DoctorEntity();
        entity.setName(name);
        if (!VerifyUtil.isStringEmpty(post)) {
            entity.setPost(post.trim());
        }
        if (!VerifyUtil.isStringEmpty(jobTitle)) {
            entity.setJobTitle(jobTitle.trim());
        }
        if (!VerifyUtil.isStringEmpty(beGoodAt)) {
            entity.setBeGoodAt(beGoodAt.trim());
        }

        entity.setHospitalId(0);
        entity.setDepartmentId(0);
        HospitalDepartmentEntity department = departmentRepository.findOne(departmentId);
        if (null!=department) {
            entity.setHospitalId(department.getHospitalId());
            entity.setDepartmentId(departmentId);
        }

        entity.setGrade(grade);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);
        DoctorBean bean = beanConverter.convert(entity);
        logger.info("doctor added is {}", bean);
        return bean;
    }

}

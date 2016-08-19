package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.converter.DoctorBeanConverter;
import com.cooltoo.go2nurse.entities.DoctorEntity;
import com.cooltoo.go2nurse.repository.DoctorRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.VerifyUtil;
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
    @Autowired private Go2NurseUtility utility;

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    //=====================================================================
    //                   getting
    //=====================================================================
    public DoctorBean getDoctorById(long doctorId) {
        logger.info("get doctor by id={}", doctorId);
        DoctorEntity doctor = repository.findOne(doctorId);
        if (null!=doctor) {
            DoctorBean bean = beanConverter.convert(doctor);

            // fill other properties
            List<DoctorBean> beans = new ArrayList<>();
            beans.add(bean);
            fillOtherProperties(beans);

            logger.info("result is {}", bean);
            return bean;
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    public boolean existDoctor(long doctorId) {
        return repository.exists(doctorId);
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

    public long countDoctor(Integer hospitalId, Integer departmentId, List<CommonStatus> statuses) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            count = repository.countByHospitalDepartmentStatusIn(hospitalId, departmentId, statuses);
        }
        logger.info("count doctor by hospital={} department={} and status={}, size is {}",
                hospitalId, departmentId, statuses, count);
        return count;
    }

    public List<DoctorBean> getDoctor(Integer hospitalId, Integer departmentId, List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get doctor by hospital={} department={} and status={} at page={} size={}",
                hospitalId, departmentId, statuses, pageIndex, sizePerPage);
        List<DoctorBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
            Page<DoctorEntity> entities = repository.findByHospitalDepartmentStatusIn(hospitalId, departmentId, statuses, pageRequest);
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

    private List<DoctorBean> entitiesToBeans(Iterable<DoctorEntity> entities) {
        List<DoctorBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (DoctorEntity entity : entities) {
            DoctorBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<DoctorBean> items) {
        if (VerifyUtil.isListEmpty(items)) {
            return;
        }

        List<Long> imagesId = new ArrayList<>();
        List<Integer> hospitalIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (DoctorBean item : items) {
            if (!imagesId.contains(item.getImageId())) {
                imagesId.add(item.getImageId());
            }
            if (!hospitalIds.contains(item.getHospitalId())) {
                hospitalIds.add(item.getHospitalId());
            }
            if (!departmentIds.contains(item.getDepartmentId())) {
                departmentIds.add(item.getDepartmentId());
            }
        }

        Map<Long, String> imageIdToUrl = userFileStorage.getFileUrl(imagesId);
        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalIds);
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefixForNurseGo());
        for (DoctorBean item : items) {
            String imageUrl = imageIdToUrl.get(item.getImageId());
            if (!VerifyUtil.isStringEmpty(imageUrl)) {
                item.setImageUrl(imageUrl);
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
            if (imagesId.contains(entity.getImageId())) {
                continue;
            }
            imagesId.add(entity.getImageId());
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
                                   int hospitalId, int departmentId, String strStatus, int grade) {
        logger.info("update doctor={} by name={} post={} jobTitle={} beGoodAt={} hospitalId={} departmentId={} status={} grade={}",
                doctorId, name, post, jobTitle, beGoodAt, hospitalId, departmentId, strStatus, grade);
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
        if (hospitalId>=0 && hospitalId!=entity.getHospitalId()) {
            entity.setHospitalId(hospitalId);
            changed = true;
        }
        if (departmentId>=0 && departmentId!=entity.getDepartmentId()) {
            entity.setDepartmentId(departmentId);
            changed = true;
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
    public DoctorBean updateDoctorHeadImage(long doctorId, String imageName, InputStream image) {
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
            long imageId = userFileStorage.addFile(entity.getImageId(), imageName, image);
            imageUrl = userFileStorage.getFileURL(imageId);
            entity.setImageId(imageId);
        }

        DoctorBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        logger.info("doctor updated is {}", bean);
        return bean;
    }

    //=====================================================================
    //                   adding
    //=====================================================================

    @Transactional
    public DoctorBean addDoctor(String name, String post, String jobTitle, String beGoodAt, int hospitalId, int departmentId, int grade) {
        logger.info("add doctor by name={} post={} jobTitle={} beGoodAt={} hospitalId={} departmentId={} grade={}",
                name, post, jobTitle, beGoodAt, hospitalId, departmentId, grade);
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
        entity.setHospitalId(hospitalId<0 ? 0 : hospitalId);
        entity.setDepartmentId(departmentId<0 ? 0 : departmentId);
        entity.setGrade(grade);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);
        DoctorBean bean = beanConverter.convert(entity);
        logger.info("doctor added is {}", bean);
        return bean;
    }

}

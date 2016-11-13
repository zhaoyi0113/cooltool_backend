package com.cooltoo.nurse360.hospital.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.converters.HospitalAdminBeanConverter;
import com.cooltoo.nurse360.entities.HospitalAdminEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.repository.HospitalAdminRepository;
import com.cooltoo.nurse360.util.Nurse360Utility;
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

import java.util.*;

/**
 * Created by zhaolisong on 2016/11/9.
 */
@Service("HospitalAdminService")
public class HospitalAdminService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalAdminService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );


    @Autowired private HospitalAdminRepository repository;
    @Autowired private HospitalAdminBeanConverter beanConverter;

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;

    @Autowired private Nurse360Utility utility;


    //=================================================================
    //                 getter for administrator
    //=================================================================
    public long countAdminUser(String name, String telephone, String email, Integer hospitalId, Integer departmentId, AdminUserType adminType, CommonStatus status) {
        name = VerifyUtil.isStringEmpty(name) ? null : (name+"%");
        telephone = VerifyUtil.isStringEmpty(telephone) ? null : (telephone+"%");
        email = VerifyUtil.isStringEmpty(email) ? null : (email+"%");

        long count = repository.countByConditions(name, telephone, email, hospitalId, departmentId, adminType, status);
        logger.info("count hospital admin user by name={} telephone={} email={} hospitalId={} departmentId={} adminType={} status={}, count is {}",
                name, telephone, email, hospitalId, departmentId, adminType, status, count);
        return count;
    }

    public List<HospitalAdminBean> getAdminUser(String name,
                                                String telephone, String email,
                                                Integer hospitalId, Integer departmentId,
                                                AdminUserType adminType,
                                                CommonStatus status,
                                                int pageIndex, int sizePerPage)
    {
        logger.info("get hospital admin user by name={} telephone={} email={} hospitalId={} departmentId={} adminType={} status={} at page={} sizePerPage={}",
                name, telephone, email, hospitalId, departmentId, adminType, status, pageIndex, sizePerPage);
        name = VerifyUtil.isStringEmpty(name) ? null : (name+"%");
        telephone = VerifyUtil.isStringEmpty(telephone) ? null : (telephone+"%");
        email = VerifyUtil.isStringEmpty(email) ? null : (email+"%");

        List<HospitalAdminBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<HospitalAdminEntity> resultSet = repository.findByConditions(name, telephone, email, hospitalId, departmentId, adminType, status, request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);

        logger.warn("hospital admin user count={}", beans.size());
        return beans;
    }

    public Map<Long, HospitalAdminBean> getAdminUserIdToBean(List<Long> adminIds) {
        Map<Long, HospitalAdminBean> value = new HashMap<>();
        if (VerifyUtil.isListEmpty(adminIds)) {
            return value;
        }
        List<HospitalAdminEntity> entities = repository.findAll(adminIds);
        List<HospitalAdminBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);

        for (HospitalAdminBean tmp : beans) {
            value.put(tmp.getId(), tmp);
        }

        return value;
    }

    //==================================================================
    //                   getter
    //==================================================================
    public boolean isSuperAdmin(long adminId) {
        return adminId==1;
    }

    public List<HospitalAdminBean> getAdminUserWithoutInfo(String name, String password) {
        logger.info("get hospital admin user by name={} password={}", name, password);
        List<HospitalAdminEntity> users = repository.findAdminByNameAndPassword(name, password);
        if (VerifyUtil.isListEmpty(users)) {
            logger.info("there is no record");
            return null;
        }
        return entitiesToBeans(users);
    }

    public HospitalAdminBean getAdminUserWithoutInfo(long adminUserId) {
        logger.info("get hospital admin user by adminUserId={}", adminUserId);
        HospitalAdminEntity one = repository.findOne(adminUserId);
        if (null==one) {
            logger.info("there is no record");
            return null;
        }
        return beanConverter.convert(one);
    }

    public HospitalAdminBean getAdminUser(long adminUserId) {
        logger.info("get hospital admin user adminUserId={}", adminUserId);
        HospitalAdminEntity one = repository.findOne(adminUserId);
        if (null==one) {
            logger.info("there is no record");
            return null;
        }
        List<HospitalAdminEntity> entities = new ArrayList<>();
        entities.add(one);

        List<HospitalAdminBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        return beans.get(0);
    }

    public boolean existsAdminUser(long adminUserId) {
        return repository.exists(adminUserId);
    }

    public boolean existsAdminUser(long adminUserId, CommonStatus status) {
        return repository.countAdminByIdAndStatus(adminUserId, status)==1;
    }

    private List<HospitalAdminBean> entitiesToBeans(Iterable<HospitalAdminEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<HospitalAdminBean> beans = new ArrayList<>();
        for(HospitalAdminEntity tmp : entities) {
            HospitalAdminBean bean = beanConverter.convert(tmp);
            bean.setPassword("");
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<HospitalAdminBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Integer> hospitalId = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (HospitalAdminBean tmp : beans) {
            if (!hospitalId.contains(tmp.getHospitalId())) {
                hospitalId.add(tmp.getHospitalId());
            }
            if (!departmentIds.contains(tmp.getDepartmentId())) {
                departmentIds.add(tmp.getDepartmentId());
            }
        }

        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalId);
        Map<Integer, HospitalDepartmentBean> departmentIdToBean = departmentService.getDepartmentIdToBean(departmentIds, utility.getHttpPrefixForNurseGo());

        // fill properties
        for (HospitalAdminBean tmp : beans) {
            HospitalBean hospital = hospitalIdToBean.get(tmp.getHospitalId());
            HospitalDepartmentBean department = departmentIdToBean.get(tmp.getDepartmentId());
            tmp.setHospital(hospital);
            tmp.setDepartment(department);
        }
    }


    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public HospitalAdminBean updateAdminUser(long adminId, String name, String password, String telephone, String email, int hospitalId, int departmentId, CommonStatus status) {
        logger.info("update hospital admin user={} with password={} telephone={} email={} hospitalId={} departmentId={}",
                adminId, password, telephone, email, hospitalId, departmentId);
        HospitalAdminEntity entity = repository.findOne(adminId);
        if (null==entity) {
            logger.error("record is empty");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }


        if (!VerifyUtil.isStringEmpty(name)) {
            long count = repository.countByConditions(name, null, null, null, null, null, null);
            if (count<=0) {
                entity.setName(name);
            }
            else {
                throw new BadRequestException(ErrorCode.NURSE360_RECORD_EXISTS_ALREADY);
            }
        }
        if (!VerifyUtil.isStringEmpty(password)) {
            entity.setPassword(password);
        }
        if (!VerifyUtil.isStringEmpty(telephone)) {
            entity.setTelephone(telephone);
        }
        if (!VerifyUtil.isStringEmpty(email)) {
            entity.setEmail(email);
        }
        if (null!=status) {
            entity.setStatus(status);
        }

        if (hospitalId>0 && hospitalService.existHospital(hospitalId)) {
            if (departmentId>0 && departmentService.existsDepartment(departmentId)) {
                HospitalDepartmentBean department = departmentService.getById(departmentId, null);
                if (department.getHospitalId() == hospitalId) {
                    entity.setHospitalId(hospitalId);
                    entity.setDepartmentId(departmentId);
                }
            }
        }

        entity = repository.save(entity);

        List<HospitalAdminEntity> entities = new ArrayList<>();
        entities.add(entity);
        List<HospitalAdminBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        return beans.get(0);
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addAdminUser(String name, String password, String telephone, String email, int hospitalId, int departmentId) {
        logger.info("add hospital admin user with name={} password={} telephone={} email={} hospitalId={} departmentId={}",
                name, password, telephone, email, hospitalId, departmentId);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        if (VerifyUtil.isStringEmpty(password)) {
            logger.error("password is empty");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }

        if (hospitalId>0 && !hospitalService.existHospital(hospitalId)) {
            logger.error("hospital not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (departmentId>0 && !departmentService.existsDepartment(departmentId)) {
            logger.error("department not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (departmentId>0) {
            HospitalDepartmentBean department = departmentService.getById(departmentId, null);
            if (department.getHospitalId()!=hospitalId) {
                logger.error("department={} is not belong to hospital={}", departmentId, hospitalId);
                throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
            }
        }

        long count = repository.countByConditions(name, null, null, null, null, null, null);
        if (count > 0) {
            logger.error("name has exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_EXISTS_ALREADY);
        }


        email = VerifyUtil.isStringEmpty(email) ? "" : email.trim();
        telephone = VerifyUtil.isStringEmpty(telephone) ? "" : telephone.trim();
        hospitalId   = hospitalId<0 ? 0 : hospitalId;
        departmentId = departmentId<0 ? 0 : departmentId;

        HospitalAdminEntity entity = new HospitalAdminEntity();
        entity.setName(name);
        entity.setPassword(password);
        entity.setTelephone(telephone);
        entity.setEmail(email);
        entity.setHospitalId(hospitalId);
        entity.setDepartmentId(departmentId);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity.setAdminType(AdminUserType.NORMAL);
        entity = repository.save(entity);

        return entity.getId();
    }
}

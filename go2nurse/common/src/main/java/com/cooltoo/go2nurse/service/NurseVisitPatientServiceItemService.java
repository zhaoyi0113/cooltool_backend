package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.converter.NurseVisitPatientServiceItemBeanConverter;
import com.cooltoo.go2nurse.entities.NurseVisitPatientServiceItemEntity;
import com.cooltoo.go2nurse.repository.NurseVisitPatientServiceItemRepository;
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

import java.util.*;

/**
 * Created by zhaolisong on 2016/11/6.
 */
@Service("NurseVisitPatientServiceItemService")
public class NurseVisitPatientServiceItemService {

    private static final Logger logger = LoggerFactory.getLogger(NurseVisitPatientServiceItemService.class);

    private static final Sort descSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    private static final Sort ascSort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private NurseVisitPatientServiceItemRepository repository;
    @Autowired private NurseVisitPatientServiceItemBeanConverter beanConverter;

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;

    @Autowired private Go2NurseUtility utility;

    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countVisitPatientServiceItem(Integer hospitalId, Integer departmentId) {
        long count = 0;
        if (null!=hospitalId && null!=departmentId) {
            count = repository.countByConditions(hospitalId, departmentId, CommonStatus.getAll());
        }
        logger.info("count visit patient service item by hospitalId={} departmentId={}, count is {}",
                hospitalId, departmentId, count);
        return count;
    }

    public List<NurseVisitPatientServiceItemBean> getVisitPatientServiceItem(Integer hospitalId, Integer departmentId, int pageIndex, int sizePerPage) {
        logger.info("get visit patient service item by hospitalId={} departmentId={} at page={} sizePerPage={}",
                hospitalId, departmentId, pageIndex, sizePerPage);
        List<NurseVisitPatientServiceItemBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, descSort);
        Page<NurseVisitPatientServiceItemEntity> resultSet = repository.findByConditions(hospitalId, departmentId, CommonStatus.getAll(), request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.warn("visit patient service item count={}", beans.size());
        return beans;
    }


    //===============================================================
    //             get ----  nurse using
    //===============================================================
    public List<NurseVisitPatientServiceItemBean> getVisitPatientServiceItem(int hospitalId, int departmentId) {
        logger.info("get visit patient service item by hospitalId={} departmentId={}",
                hospitalId, departmentId);
        List<CommonStatus> statuses = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        List<NurseVisitPatientServiceItemBean> beans;

        List<NurseVisitPatientServiceItemEntity> resultSet = repository.findByConditions(hospitalId, departmentId, statuses, ascSort);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.warn("visit patient service item count={}", beans.size());
        return beans;
    }

    public List<NurseVisitPatientServiceItemBean> getVisitPatientServiceItem(String serviceItemIds) {
        logger.info("get visit patient service item by serviceItemIds={}", serviceItemIds);
        List<Long> serviceItems = VerifyUtil.isIds(serviceItemIds) ? VerifyUtil.parseLongIds(serviceItemIds) : null;
        List<NurseVisitPatientServiceItemBean> items = null;
        if (!VerifyUtil.isListEmpty(serviceItems)) {
            List<CommonStatus> statuses = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
            List<NurseVisitPatientServiceItemEntity> resultSet = repository.findByIdInAndStatusIn(serviceItems, statuses, ascSort);
            items = entitiesToBeans(resultSet);
            fillOtherProperties(items);
        }
        return items;
    }

    private List<NurseVisitPatientServiceItemBean> entitiesToBeans(Iterable<NurseVisitPatientServiceItemEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        NurseVisitPatientServiceItemBean bean;
        List<NurseVisitPatientServiceItemBean> resultSet = new ArrayList<>();
        for (NurseVisitPatientServiceItemEntity tmp : entities) {
            bean = beanConverter.convert(tmp);
            resultSet.add(bean);
        }
        return resultSet;
    }

    private void fillOtherProperties(List<NurseVisitPatientServiceItemBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Integer> hospitalIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (NurseVisitPatientServiceItemBean tmp : beans) {
            if (!hospitalIds.contains(tmp.getHospitalId())) {
                hospitalIds.add(tmp.getHospitalId());
            }
            if (!departmentIds.contains(tmp.getDepartmentId())) {
                departmentIds.add(tmp.getDepartmentId());
            }
        }

        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalIds);
        Map<Integer, HospitalDepartmentBean> departmentIdToBean = departmentService.getDepartmentIdToBean(departmentIds, utility.getHttpPrefixForNurseGo());

        for (NurseVisitPatientServiceItemBean tmp : beans) {
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
    public List<Long> deleteServiceItemByIds(List<Long> servicetItemIds) {
        logger.info("set visit patient service item to deleted by servicetItemIds={}.", servicetItemIds);
        List<Long> retValue = new ArrayList<>();
        if (VerifyUtil.isListEmpty(servicetItemIds)) {
            return retValue;
        }

        List<NurseVisitPatientServiceItemEntity> serviceItems = repository.findAll(servicetItemIds);
        if (VerifyUtil.isListEmpty(serviceItems)) {
            logger.info("nothing to set");
            return retValue;
        }

        for (NurseVisitPatientServiceItemEntity tmp : serviceItems) {
            tmp.setStatus(CommonStatus.DELETED);
            retValue.add(tmp.getId());
        }
        repository.save(serviceItems);


        return retValue;
    }

    @Transactional
    public NurseVisitPatientServiceItemBean updateServiceItem(Long serviceItemId, String name, String description) {
        logger.info("update visit patient service item={} with name={} description={}",
                serviceItemId, name, description);
        NurseVisitPatientServiceItemEntity entity = repository.findOne(serviceItemId);
        if (null==entity) {
            logger.error("visit patient service item is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name) && !name.trim().equals(entity.getItemName())) {
            entity.setItemName(name.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(description) && !description.trim().equals(entity.getItemDescription())) {
            entity.setItemDescription(description.trim());
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }

        NurseVisitPatientServiceItemBean bean = beanConverter.convert(entity);
        List<NurseVisitPatientServiceItemBean> beans = Arrays.asList(new NurseVisitPatientServiceItemBean[]{bean});
        fillOtherProperties(beans);
        return bean;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addServiceItem(int hospitalId, int departmentId, String name, String description) {
        logger.info("add visit patient service item with hospitalId={} departmentId={} description={} name={}",
                hospitalId, departmentId, description, name);
        if (!hospitalService.existHospital(hospitalId)) {
            logger.error("hospital not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!departmentService.existsDepartment(departmentId)) {
            logger.error("department not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("item name is empty");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        description = VerifyUtil.isStringEmpty(description) ? "" : description.trim();

        NurseVisitPatientServiceItemEntity entity = new NurseVisitPatientServiceItemEntity();
        entity.setHospitalId(hospitalId<0 ? 0 : hospitalId);
        entity.setDepartmentId(departmentId);
        entity.setItemName(name);
        entity.setItemDescription(description.trim());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        return entity.getId();
    }
}

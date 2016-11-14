package com.cooltoo.nurse360.hospital.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.HospitalAdminRolesBean;
import com.cooltoo.nurse360.constants.AdminRole;
import com.cooltoo.nurse360.converters.HospitalAdminRolesBeanConverter;
import com.cooltoo.nurse360.entities.HospitalAdminRolesEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.repository.HospitalAdminRolesRepository;
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
 * Created by zhaolisong on 2016/11/10.
 */
@Service("HospitalAdminRolesService")
public class HospitalAdminRolesService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalAdminRolesService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private HospitalAdminRolesRepository repository;
    @Autowired private HospitalAdminRolesBeanConverter beanConverter;

    @Autowired private HospitalAdminService adminService;

    //=================================================================
    //                 getter for administrator
    //=================================================================
    public long countAdminRole(Long adminId, AdminRole role, CommonStatus status) {
        long count = repository.countByConditions(adminId, role, status);
        logger.info("count admin role by adminId={} role={} status={}, count is {}",
                adminId, role, status, count);
        return count;
    }

    public List<HospitalAdminRolesBean> getAdminRole(Long adminId, AdminRole role, CommonStatus status, int pageIndex, int sizePerPage) {
        logger.info("get admin role by adminId={} role={} status={} at page={} sizePerPage={}",
                adminId, role, status, pageIndex, sizePerPage);
        List<HospitalAdminRolesBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<HospitalAdminRolesEntity> resultSet = repository.findByConditions(adminId, role, status, request);
        beans = entitiesToBeans(resultSet);

        logger.warn("admin role count={}", beans.size());
        return beans;
    }

    private List<HospitalAdminRolesBean> entitiesToBeans(Iterable<HospitalAdminRolesEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<HospitalAdminRolesBean> beans = new ArrayList<>();
        for(HospitalAdminRolesEntity tmp : entities) {
            HospitalAdminRolesBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }


    //==================================================================
    //                   getter for admin_user
    //==================================================================
    public List<AdminRole> getAdminRoleByAdminId(long adminId) {
        logger.info("get admin role by adminId={}", adminId);
        List<HospitalAdminRolesEntity> entities = repository.findByConditions(adminId, null, CommonStatus.ENABLED, sort);
        List<AdminRole> beans = new ArrayList<>();
        for (HospitalAdminRolesEntity tmp : entities) {
            beans.add(tmp.getRole());
        }
        return beans;
    }

    public Map<Long, List<AdminRole>> getAdminRoleByAdminIds(List<Long> adminIds) {
        logger.info("get admin role by adminId={}", adminIds);
        Map<Long, List<AdminRole>> adminRoles = new HashMap<>();
        if (VerifyUtil.isListEmpty(adminIds)) {
            return adminRoles;
        }

        List<HospitalAdminRolesEntity> entities = repository.findByConditions(adminIds, null, CommonStatus.ENABLED, sort);


        List<AdminRole> roles;
        for (HospitalAdminRolesEntity tmp : entities) {
            roles = adminRoles.get(tmp.getAdminId());
            if (null==roles) {
                roles = new ArrayList<>();
                adminRoles.put(tmp.getAdminId(), roles);
            }
            if (!roles.contains(tmp.getRole())) {
                roles.add(tmp.getRole());
            }
        }
        return adminRoles;
    }

    //===============================================================
    //             delete
    //===============================================================
    @Transactional
    public List<Long> deleteAdminRole(long adminId, AdminRole role) {
        logger.info("delete admin role by adminId={} role={}", adminId, role);
        List<HospitalAdminRolesEntity> ones = repository.findByConditions(adminId, role, null, sort);
        List<Long> deletedIds = new ArrayList<>();
        if (null==ones) {
            logger.info("delete nothing");
            return deletedIds;
        }

        for (HospitalAdminRolesEntity tmp : ones) {
            deletedIds.add(tmp.getId());
        }
        repository.delete(ones);
        return deletedIds;
    }

    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public long updateAccessibleRole(long accessId, CommonStatus status) {
        logger.info("update admin management accessId={} status={}", accessId, status);

        HospitalAdminRolesEntity one = repository.findOne(accessId);
        if (null==one) {
            logger.info("delete nothing");
            return accessId;
        }

        if (null!=status && !status.equals(one.getStatus())) {
            one.setStatus(status);
        }
        repository.save(one);


        return accessId;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addAdminRole(long adminId, AdminRole adminRole) {
        logger.info("add admin role by adminId={} adminRole={}", adminId, adminRole);

        if (!adminService.existsAdminUser(adminId)) {
            logger.error("admin not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (null==adminRole) {
            logger.error("adminRole not exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        HospitalAdminRolesEntity entity = null;
        List<HospitalAdminRolesEntity> entities = repository.findByConditions(adminId, adminRole, null, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            entity = new HospitalAdminRolesEntity();
        }
        else {
            entity = entities.get(0);
        }

        entity.setAdminId(adminId);
        entity.setRole(adminRole);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        entity = repository.save(entity);

        entities = repository.findByConditions(adminId, adminRole, null, sort);
        for (int i=0; i<entities.size(); i++) {
            HospitalAdminRolesEntity tmp = entities.get(i);
            if (tmp.getId()==entity.getId()) {
                entities.remove(i);
                break;
            }
        }

        if (!VerifyUtil.isListEmpty(entities)) {
            repository.delete(entities);
        }

        return entity.getId();
    }
}

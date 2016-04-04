package com.cooltoo.admin.services;

import com.cooltoo.admin.beans.AdminUserBean;
import com.cooltoo.admin.converter.AdminUserBeanConverter;
import com.cooltoo.admin.entities.AdminUserEntity;
import com.cooltoo.admin.repository.AdminUserRepository;
import com.cooltoo.constants.AdminUserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Service(value = "AdminUserService")
public class AdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class.getName());

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AdminUserBeanConverter beanConverter;

    public AdminUserBean createUserByAdmin(long adminUserId, String newUserName, String password, String phone, String email) {
        logger.info("create normal user");
        if (!isAdmin(adminUserId)) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }
        if (!VerifyUtil.isAdminUserNameValid(newUserName)) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_NAME_INVALID);
        }
        if (null==password || "".equals(password)) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_PASSWORD_INVALID);
        }
        AdminUserEntity entity = adminUserRepository.findAdminUserByUserName(newUserName);
        if (null!=entity) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_EXISTED);
        }

        entity = new AdminUserEntity();
        entity.setUserType(AdminUserType.NORMAL);
        entity.setUserName(newUserName);
        entity.setPassword(password);
        if (NumberUtil.isMobileValid(phone)) {
            entity.setPhoneNumber(phone);
        }
        if (null!=email || !"".equals(email)) {
            entity.setEmail(email);
        }
        entity.setTimeCreated(new Date());

        entity = adminUserRepository.save(entity);
        return beanConverter.convert(entity);
    }

    public AdminUserBean updateUserByAdmin(long adminUserId, long updatingId, String newUserName, String password, String phone, String email) {
        if (!isAdmin(adminUserId)) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }
        boolean isUpdateAdmin = (adminUserId==updatingId);
        if (updatingId <= 0) {
            isUpdateAdmin = true;
        }
        AdminUserEntity entity = null;
        if (!isUpdateAdmin) {
            entity = adminUserRepository.findOne(updatingId);
            if (null!=entity && VerifyUtil.isAdminUserNameValid(newUserName)) {
                entity.setUserName(newUserName);
            }
            logger.info("update normal user. user id is " + updatingId);
        }
        else {
            entity = adminUserRepository.findOne(adminUserId);
            logger.info("update administrator");
        }
        if (null != password && !"".equals(password)) {
            entity.setPassword(password);
        }
        if (NumberUtil.isMobileValid(phone)) {
            entity.setPhoneNumber(phone);
        }
        if (null != email && !"".equals(email)) {
            entity.setEmail(email);
        }

        entity = adminUserRepository.save(entity);
        return beanConverter.convert(entity);
    }

    public void deleteUserByAdmin(long adminUserId, long deletingUserId) {
        logger.info("delete normal user");
        if (!isAdmin(adminUserId)) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }
        if (adminUserId==deletingUserId) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_DELETE_ADMIN_DENIED);
        }
        if (!adminUserRepository.exists(deletingUserId)) {
            logger.info("user not existed already");
            return;
        }
        adminUserRepository.delete(deletingUserId);
    }

    public List<AdminUserBean> getAllUsersByAdmin(long adminUserId) {
        logger.info("get all normal user");
        if (!isAdmin(adminUserId)) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }
        Iterable<AdminUserEntity> entities = adminUserRepository.findAll();
        List<AdminUserBean> beans = new ArrayList<AdminUserBean>();
        for (AdminUserEntity entity : entities) {
            if (AdminUserType.ADMINISTRATOR.equals(entity.getUserType())) {
                continue;
            }
            AdminUserBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    public AdminUserBean getUserByAdmin(long adminUserId, long userId) {
        if (!isAdmin(adminUserId)) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED);
        }
        AdminUserEntity entity = adminUserRepository.findOne(userId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_NOT_EXISTED);
        }
        return beanConverter.convert(entity);
    }

    private boolean isAdmin(long adminUserId) {
        AdminUserEntity entity = adminUserRepository.findOne(adminUserId);
        return (null!=entity && AdminUserType.ADMINISTRATOR.equals(entity.getUserType()));
    }

    public AdminUserBean updateUser(long id, String password, String phone, String email) {
        logger.info("update user");
        AdminUserEntity entity = adminUserRepository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_NOT_EXISTED);
        }
        if (null!=password && !"".equals(password)) {
            entity.setPassword(password);
        }
        if (NumberUtil.isMobileValid(phone)) {
            entity.setPhoneNumber(phone);
        }
        if (null!=email && !"".equals(email)) {
            entity.setEmail(email);
        }

        entity = adminUserRepository.save(entity);
        return beanConverter.convert(entity);
    }
}

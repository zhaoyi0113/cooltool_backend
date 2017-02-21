package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.converter.UserBeanConverter;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.repository.UserOpenAppRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.leancloud.LeanCloudService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Service("UserService")
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static boolean isInitDenyUserIds = false;
    private static final List<Long> denyUserIds = new ArrayList<>();


    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserBeanConverter beanConverter;
    @Autowired
    private UserGo2NurseFileStorageService userStorage;
    @Autowired
    private LeanCloudService leanCloudService;
    @Autowired
    private UserOpenAppRepository openAppRepository;
    @Autowired
    private UserLoginService loginService;

    //==================================================================
    //         add
    //==================================================================
    @Transactional
    public UserBean registerUser(String name, int gender, String strBirthday, String mobile, String password, String smsCode, String strHasDecide) {
        logger.info("register new user with name={} gender={} birthday={} mobile={} password={} smsCode={} hasDecide={}",
                name, gender, strBirthday, mobile, password, smsCode, strHasDecide);
        leanCloudService.verifySmsCode(smsCode, mobile);

        if (VerifyUtil.isStringEmpty(password)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!repository.findByMobile(mobile).isEmpty()) {
            throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
        }
        GenderType genderType = GenderType.parseInt(gender);

        String uniqueId = null;
        for (int i = 10; i > 0; i--) {
            uniqueId = NumberUtil.randomIdentity();
            if (repository.countByUniqueId(uniqueId) <= 0) {
                break;
            } else {
                uniqueId = null;
            }
        }
        if (VerifyUtil.isStringEmpty(uniqueId)) {
            logger.info("unique id generated failed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        Long birthdayMilliSecond = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        UserHospitalizedStatus hasDecide = UserHospitalizedStatus.parseString(strHasDecide);

        UserEntity entity = new UserEntity();
        entity.setName(name);
        entity.setGender(null == genderType ? GenderType.SECRET : genderType);
        entity.setBirthday(birthdayMilliSecond == null ? new Date(0) : new Date(birthdayMilliSecond));
        entity.setMobile(mobile);
        entity.setPassword(password);
        entity.setAuthority(UserAuthority.AGREE_ALL);
        entity.setType(UserType.NORMAL_USER);
        entity.setUniqueId(uniqueId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setHasDecide(null == hasDecide ? UserHospitalizedStatus.NONE : hasDecide);
        entity = repository.save(entity);

        logger.info("add user={}", entity);
        return beanConverter.convert(entity);
    }

    public UserBean registerUser(String name, int gender, String strBirthday, String mobile, String password, String smsCode, String hasDecide, String channel, String channelid, String openid) {
        if (repository.findByMobile(mobile).isEmpty() && channel == null) {
            //first time register
            return registerUser(name, gender, strBirthday, mobile, password, smsCode, hasDecide);
        }
        //check channel register
        if (channel != null) {
            return registerWithChannel(name, gender, strBirthday, mobile, password, smsCode, hasDecide, channel, channelid, openid);
        }
        throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private UserBean registerWithChannel(String name, int gender, String strBirthday, String mobile, String password, String smsCode, String strHasDecide, String channel, String channelid, String openid) {
        AppChannel appChannel = AppChannel.valueOf(channel);
        switch (appChannel) {
            case WECHAT:
                List<UserOpenAppEntity> wechatusers = null;
                if (channelid != null) {
                    wechatusers = openAppRepository.findByUnionid(channelid);
                }
                if((wechatusers == null || wechatusers.isEmpty()) && openid != null){
                    wechatusers = openAppRepository.findByOpenid(openid);
                }
                if (wechatusers != null && !wechatusers.isEmpty()) {
                    List<UserEntity> currentUsers = repository.findByMobile(mobile);
                    if (currentUsers.isEmpty()) {
                        UserBean userBean = registerUser(name, gender, strBirthday, mobile, password, smsCode, strHasDecide);
                        wechatusers.get(0).setUserId(userBean.getId());
                        wechatusers.get(0).setStatus(CommonStatus.ENABLED);
                        openAppRepository.save(wechatusers.get(0));
                        loginService.login(mobile, password, channel, channelid, openid);
                        return userBean;
                    } else {
                        //already existed such user, link with channel user
                        UserHospitalizedStatus hasDecide = UserHospitalizedStatus.parseString(strHasDecide);
                        UserEntity currentUser = currentUsers.get(0);
                        currentUser.setPassword(password);
                        currentUser.setName(name);
                        currentUser.setStatus(CommonStatus.ENABLED);
                        Long birthdayMilliSecond = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
                        currentUser.setBirthday(birthdayMilliSecond == null ? new Date(0) : new Date(birthdayMilliSecond));
                        if (null != hasDecide && !hasDecide.equals(currentUser.getHasDecide())) {
                            currentUser.setHasDecide(hasDecide);
                        }
                        repository.save(currentUser);
                        loginService.login(mobile, password, channel, channelid, openid);
                        wechatusers.get(0).setUserId(currentUser.getId());
                        wechatusers.get(0).setStatus(CommonStatus.ENABLED);
                        openAppRepository.save(wechatusers.get(0));
                        return beanConverter.convert(currentUser);
                    }
                }else{
                    logger.error("not able to find wechat user "+openid);
                }
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    //==================================================================
    //         get
    //==================================================================
    public List<Long> getAllDenyUserIds() {
        logger.info("get all denied users' id");
        if (!isInitDenyUserIds) {
            List<Long> denyIds = repository.findIdsByAuthority(UserAuthority.DENY_ALL);
            if (!VerifyUtil.isListEmpty(denyIds)) {
                denyUserIds.addAll(denyIds);
            }
            isInitDenyUserIds = true;
        }
        List<Long> tmp = new ArrayList<>();
        for (Long denyUseId : denyUserIds) {
            tmp.add(denyUseId);
        }
        logger.info("denied users' id is={}", tmp);
        return tmp;
    }

    public List<UserBean> getUserByUniqueId(String uniqueId) {
        logger.info("get user by uniqueId={}", uniqueId);
        List<UserEntity> resultSet = repository.findByUniqueId(uniqueId, sort);
        List<UserBean> beans = entities2Beans(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public boolean existUser(long userId) {
        boolean exists = repository.exists(userId);
        logger.info("user={} is exist={}", userId, exists);
        return exists;
    }

    public UserBean getUserWithoutOtherInfo(long userId) {
        logger.info("get user without other info by id={}", userId);
        UserEntity entity = repository.findOne(userId);
        logger.info("get user without other info is={}", entity);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    public UserBean getUserWithoutOtherInfo(String mobile) {
        logger.info("get user without other info by mobile={}", mobile);
        UserEntity user = null;
        List<UserEntity> users = repository.findByMobile(mobile);
        if (null != users && !users.isEmpty() && users.size() == 1) {
            user = users.get(0);
        }
        logger.error("get index=0, result is {}.", users);
        if (null == user) {
            return null;
        }
        return beanConverter.convert(user);
    }

    public long countUserByUserIds(List<Long> userIds, UserAuthority authority) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(userIds)) {
            count = repository.countByAuthorityAndIdIn(authority, userIds);
        }
        logger.info("count user by authority={} and userIds size={}, count={}", authority, null==userIds ? 0 : userIds.size(), count);
        return count;
    }

    public List<UserBean> getUserByUserIds(List<Long> userIds, UserAuthority authority) {
        logger.info("get user by authority={} and userIds size={}", authority, null==userIds ? 0 : userIds.size());
        List<UserBean> users = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(userIds)) {
            List<UserEntity> resultSet = repository.findByAuthorityAndIdIn(authority, userIds, sort);
            List<UserBean> originalUsers = entities2Beans(resultSet);
            fillOtherProperties(originalUsers);

            Map<Long, UserBean> userMap = new HashMap<>();
            for (UserBean tmp : originalUsers) {
                userMap.put(tmp.getId(), tmp);
            }

            for (Long tmp : userIds) {
                UserBean user = userMap.get(tmp);
                if (null!=user) {
                    users.add(user);
                }
            }
        }
        logger.info("count is {}", users.size());
        return users;
    }

    public List<UserBean> getUser(List<Long> userIds, UserAuthority authority) {
        logger.info("get user by authority={} and userIds size={}", authority, null==userIds ? 0 : userIds.size());
        List<UserBean> users = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(userIds)) {
            Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
            List<UserEntity> resultSet = repository.findByAuthorityAndIdIn(authority, userIds, sort);
            users = entities2Beans(resultSet);
            fillOtherProperties(users);
        }
        logger.info("count is {}", users.size());
        return users;
    }

    public UserBean getUser(long userId) {
        logger.info("get user={} without information", userId);
        UserBean user = getUserWithoutOtherInfo(userId);
        fillOtherProperties(user);
        return user;
    }

    public UserBean getUser(String mobile) {
        logger.info("get user without information by mobile={}", mobile);
        UserBean user = getUserWithoutOtherInfo(mobile);
        fillOtherProperties(user);
        return user;
    }

    public Map<Long, UserBean> getUserIdToBean(List<Long> userIds) {
        logger.info("get user by userIds={}", userIds);
        if (VerifyUtil.isListEmpty(userIds)) {
            return new HashMap<>();
        }
        List<UserEntity> resultSet = repository.findAll(userIds);
        List<UserBean> beans = entities2Beans(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        Map<Long, UserBean> map = new HashMap<>();
        for (UserBean tmp : beans) {
            map.put(tmp.getId(), tmp);
        }
        return map;
    }

    private void fillOtherProperties(UserBean user) {
        String profilePhotoUrl = userStorage.getFileURL(user.getProfilePhoto());
        if (!VerifyUtil.isStringEmpty(profilePhotoUrl)) {
            user.setProfilePhotoUrl(profilePhotoUrl);
        }
    }
    //==============================================================
    //             get used by administrator
    //==============================================================

    public long countByAuthorityAndFuzzyName(String strAuthority, String fuzzyName) {
        logger.info("get user count by authority={} fuzzyName={}", strAuthority, fuzzyName);
        UserAuthority authority = UserAuthority.parseString(strAuthority);
        if (VerifyUtil.isStringEmpty(fuzzyName)) {
            fuzzyName = null;
        } else {
            fuzzyName = VerifyUtil.reconstructSQLContentLike(fuzzyName);
        }
        long count;
        if (null == authority && null == fuzzyName) {
            count = repository.count();
        } else {
            count = repository.countByAuthorityAndName(authority, fuzzyName);
        }
        logger.info("count is {}", count);
        return count;
    }

    public List<UserBean> getAllByAuthorityAndFuzzyName(String strAuthority, String fuzzyName, int pageIndex, int number) {
        logger.info("get user by authority={} fuzzyName={} at page {} with number {}",
                strAuthority, fuzzyName, pageIndex, number);
        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "id");
        Page<UserEntity> resultSet = null;

        // get nuser by authority
        UserAuthority authority = UserAuthority.parseString(strAuthority);
        if (VerifyUtil.isStringEmpty(fuzzyName)) {
            fuzzyName = null;
        } else {
            fuzzyName = VerifyUtil.reconstructSQLContentLike(fuzzyName);
        }
        if (null == authority && null == fuzzyName) {
            resultSet = repository.findAll(page);
        } else {
            resultSet = repository.findByAuthorityAndName(authority, fuzzyName, page);
        }

        // parse to bean
        List<UserBean> beanList = entities2Beans(resultSet);
        fillOtherProperties(beanList);
        logger.info("count is {}", beanList.size());
        return beanList;
    }

    private List<UserBean> entities2Beans(Iterable<UserEntity> entities) {
        if (null == entities) {
            return new ArrayList<>();
        }
        List<UserBean> beans = new ArrayList<>();
        for (UserEntity tmp : entities) {
            UserBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<UserBean> users) {
        if (null == users || users.isEmpty()) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (UserBean bean : users) {
            long imageId = bean.getProfilePhoto();
            if (!imageIds.contains(imageId)) {
                imageIds.add(imageId);
            }
        }

        Map<Long, String> imageId2Path = userStorage.getFileUrl(imageIds);
        for (UserBean bean : users) {
            long imageId = bean.getProfilePhoto();
            String imgPath = imageId2Path.get(imageId);
            if (!VerifyUtil.isStringEmpty(imgPath)) {
                bean.setProfilePhotoUrl(imgPath);
            }
        }
    }

    //==================================================================
    //         update
    //==================================================================
    @Transactional
    public UserBean updateUser(long userId, String name, int iGender, String strBirthday, int iAuthority, String address, String strHasDecide) {
        logger.info("update user={} with name={} gender={} birthday={} authority={} address={} hasDecide={}",
                userId, name, iGender, strBirthday, iAuthority, address, strHasDecide);
        UserEntity entity = repository.findOne(userId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name)) {
            name = name.trim();
            entity.setName(name);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(strBirthday)) {
            Long time = NumberUtil.getTime(strBirthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
            if (null!=time) {
                Date birthday = new Date(time);
                entity.setBirthday(birthday);
                changed = true;
            }
        }
        GenderType gender = GenderType.parseInt(iGender);
        if (null != gender) {
            entity.setGender(gender);
            changed = true;
        }
        UserHospitalizedStatus hasDecide = UserHospitalizedStatus.parseString(strHasDecide);
        if (null != hasDecide) {
            entity.setHasDecide(hasDecide);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(address)) {
            address = address.trim();
            if (!address.equals(entity.getAddress())) {
                entity.setAddress(address);
                changed = true;
            }
        }

        boolean authorityChanged = false;
        UserAuthority authority = UserAuthority.parseInt(iAuthority);
        if (null != authority) {
            entity.setAuthority(authority);
            authorityChanged = true;
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }
        if (authorityChanged) {
            modifyDenyUserIdsCache(entity);
        }
        UserBean bean = beanConverter.convert(entity);
        bean.setProfilePhotoUrl(userStorage.getFileURL(bean.getProfilePhoto()));
        logger.info("after updating user is {}", bean);
        return bean;
    }

    @Transactional
    public UserBean updateProfilePhoto(long userId, String imageName, InputStream image) {
        logger.info("update profile photo for user={}, imageName={}, image={}", userId, imageName, image);
        UserEntity entity = repository.findOne(userId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        long imageId = 0;
        String imageUrl = "";
        imageId = userStorage.addFile(entity.getProfilePhoto(), imageName, image);
        if (imageId > 0) {
            entity.setProfilePhoto(imageId);
            imageUrl = userStorage.getFileURL(imageId);
            entity = repository.save(entity);
        }
        UserBean bean = beanConverter.convert(entity);
        bean.setProfilePhotoUrl(imageUrl);
        return bean;
    }

    @Transactional
    public UserBean validateMobile(long userId, String smsCode, String mobile) {
        logger.info("verify by userId={} smsCode={} mobile={}", userId, smsCode, mobile);
        leanCloudService.verifySmsCode(smsCode, mobile);
        UserBean user = getUserWithoutOtherInfo(mobile);
        if (null == user) {
            user = getUserWithoutOtherInfo(userId);
        }
        return user;
    }

    @Transactional
    public UserBean updateMobilePassword(long userId, String newMobile, String newPassword) {
        logger.info("modify the password or mobile by userId={} newMobile={} newPwd={}",
                userId, newMobile, newPassword);

        UserEntity user = repository.getOne(userId);
        if (null == user) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // if not modify the mobile
        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(newMobile)) {
            newMobile = newMobile.trim();
            if (!newMobile.equals(user.getMobile())) {
                user.setMobile(newMobile);
                changed = true;
            }
        }

        // check password
        if (!VerifyUtil.isStringEmpty(newPassword)) {
            newPassword = newPassword.trim();
            if (!newPassword.equals(user.getPassword())) {
                user.setPassword(newPassword);
                changed = true;
            }
        }
        if (changed) {
            user = repository.save(user);
        }
        UserBean userBean = beanConverter.convert(user);
        userBean.setProfilePhotoUrl(userStorage.getFileURL(userBean.getProfilePhoto()));
        return userBean;
    }

    @Transactional
    public UserBean updateMobile(long userId, String smsCode, String newMobile) {
        logger.info("modify the mobile by userId={} newMobile={} smsCode={}",
                userId, newMobile, smsCode);

        UserEntity user = repository.getOne(userId);
        if (null == user) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // if not modify the mobile
        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(newMobile) && !VerifyUtil.isStringEmpty(smsCode)) {
            leanCloudService.verifySmsCode(smsCode.trim(), newMobile.trim());
            newMobile = newMobile.trim();
            List<UserEntity> userExist = repository.findByMobile(newMobile);
            if (null != userExist && !userExist.isEmpty()) {
                throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
            }
            if (!newMobile.equals(user.getMobile())) {
                user.setMobile(newMobile);
                changed = true;
            }
        }

        if (changed) {
            user = repository.save(user);
        }
        UserBean userBean = beanConverter.convert(user);
        userBean.setProfilePhotoUrl(userStorage.getFileURL(userBean.getProfilePhoto()));
        return userBean;
    }

    @Transactional
    public UserBean updatePassword(long userId, String oldPassword, String newPassword) {
        logger.info("modify the password by userId={} oldPwd={} newPwd={}",
                userId, oldPassword, newPassword);

        UserEntity user = repository.getOne(userId);
        if (null == user) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;

        // check password
        if (user.getPassword().equals(oldPassword) && !VerifyUtil.isStringEmpty(newPassword)) {
            newPassword = newPassword.trim();
            if (!newPassword.equals(user.getPassword())) {
                user.setPassword(newPassword);
                changed = true;
            }
        }
        if (changed) {
            user = repository.save(user);
            UserBean userBean = beanConverter.convert(user);
            userBean.setProfilePhotoUrl(userStorage.getFileURL(userBean.getProfilePhoto()));
            return userBean;
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    private void modifyDenyUserIdsCache(UserEntity user) {
        if (null == user) {
            return;
        }
        long userId = user.getId();
        UserAuthority userAuthority = user.getAuthority();
        if (UserAuthority.DENY_ALL.equals(userAuthority)) {
            if (!denyUserIds.contains(userId)) {
                denyUserIds.add(userId);
            }
        }
        if (UserAuthority.AGREE_ALL.equals(userAuthority)) {
            if (denyUserIds.contains(userId)) {
                denyUserIds.remove(userId);
            }
        }
    }
}

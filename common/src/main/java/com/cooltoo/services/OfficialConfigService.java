package com.cooltoo.services;

import com.cooltoo.beans.OfficialConfigBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.converter.OfficialConfigBeanConverter;
import com.cooltoo.entities.OfficialConfigEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.OfficialConfigRepository;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 16/5/9.
 */
@Service("OfficialConfigService")
public class OfficialConfigService {

    private static final Logger logger = LoggerFactory.getLogger(OfficialConfigService.class.getName());

    @Autowired
    private OfficialConfigRepository repository;
    @Autowired
    private OfficialConfigBeanConverter beanConverter;
    @Autowired
    private OfficialFileStorageService officialStorage;

    public static final Map<String, OfficialConfigBean> configKey2Bean = new HashMap<String, OfficialConfigBean>();
    public static final String OFFICIAL_SPEAK_PROFILE = "OfficialSpeakProfile";

    //===============================================
    //               get
    //===============================================
    public List<String> getKeys() {
        logger.info("get all config keys");
        String[] keys = {OFFICIAL_SPEAK_PROFILE};
        return Arrays.asList(keys);
    }

    public long countConfig(String strStatus) {
        long count = 0;
        if ("ALL".equalsIgnoreCase(strStatus)) {
            count = repository.count();
        }
        else {
            CommonStatus status = CommonStatus.parseString(strStatus);
            if (null!=status) {
                count = repository.countByStatus(status);
            }
        }
        logger.info("count official configurations by status={}, size={}", strStatus, count);
        return count;
    }

    public OfficialConfigBean getConfig(String name) {
        logger.info("get official config by name={}", name);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            return null;
        }
        name = name.trim();
        if (configKey2Bean.containsKey(name)) {
            OfficialConfigBean config = configKey2Bean.get(name);
            if (null!=config) {
                return config;
            }
        }

        OfficialConfigEntity configEntity = repository.findByName(name);
        if (null!=configEntity) {
            OfficialConfigBean configBean = beanConverter.convert(configEntity);
            if (configBean.getImageId() > 0) {
                String imageUrl = officialStorage.getFilePath(configBean.getImageId());
                configBean.setImageUrl(imageUrl);
            }
            logger.info("config={}", configBean);
            configKey2Bean.put(name, configBean);
            return configBean;
        }
        logger.error("not exist");
        return null;
    }

    public List<OfficialConfigBean> getConfig(String strStatus, int pageIndex, int numberPerPage) {
        logger.info("get official config at status={} page={} size/page={}", strStatus, pageIndex, numberPerPage);
        PageRequest page = new PageRequest(pageIndex, numberPerPage, Sort.Direction.DESC, "createTime");
        Iterable<OfficialConfigEntity> configs = null;
        if ("ALL".equalsIgnoreCase(strStatus)) {
            configs = repository.findAll(page);
        }
        else {
            CommonStatus status = CommonStatus.parseString(strStatus);
            if (null!=status) {
                configs = repository.findByStatus(status, page);
            }
        }
        List<OfficialConfigBean> configBeans = entitiesToBeans(configs);
        fillOtherProperties(configBeans);
        logger.info("count of config={}", configBeans.size());
        return configBeans;
    }

    private List<OfficialConfigBean> entitiesToBeans(Iterable<OfficialConfigEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<OfficialConfigBean> beans = new ArrayList<>();
        for (OfficialConfigEntity entity : entities) {
            OfficialConfigBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<OfficialConfigBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (OfficialConfigBean bean : beans) {
            if (bean.getImageId()<=0) {
                continue;
            }
            if (imageIds.contains(bean.getImageId())) {
                continue;
            }
            imageIds.add(bean.getImageId());
        }
        if (imageIds.isEmpty()) {
            return;
        }

        Map<Long, String> imageId2Url = officialStorage.getFilePath(imageIds);
        if (imageId2Url.isEmpty()) {
            return;
        }
        for (OfficialConfigBean bean : beans) {
            String imageUrl = imageId2Url.get(bean.getImageId());
            bean.setImageUrl(imageUrl);
        }
    }

    private boolean existName(String name) {
        if (VerifyUtil.isStringEmpty(name)) {
            return false;
        }
        name = name.trim();
        return null!=repository.findByName(name);
    }

    //=============================================
    //                 delete
    //=============================================
    public void deleteByIds(String strIds) {
        logger.info("delete by ids={}", strIds);
        if (!VerifyUtil.isIds(strIds)) {
            return;
        }
        List<Integer> ids = VerifyUtil.parseIntIds(strIds);
        repository.deleteByIdIn(ids);
    }

    //=============================================
    //                 update
    //=============================================
    public OfficialConfigBean updateConfig(int id, String name, String value, String strStatus, String imageName, InputStream image) {
        logger.info("update config={} by name={} value={} status={} imageName={} image={}", id, name, value, strStatus, imageName, image!=null);
        OfficialConfigEntity entity = repository.findOne(Integer.valueOf(id));
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!VerifyUtil.isStringEmpty(name)) {
            name = name.trim();
            if (!name.equals(entity.getName()) && !existName(name)) {
                entity.setName(name);
            }
        }

        if (!VerifyUtil.isStringEmpty(value) && !value.equals(entity.getValue())) {
            entity.setValue(value);
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
        }

        long imageId = 0;
        String imageUrl = "";
        if (image!=null) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "official_config_image";
            }
            imageId = officialStorage.addFile(entity.getImageId(), imageName, image);
            imageUrl = officialStorage.getFilePath(imageId);
            entity.setImageId(imageId);
        }
        entity.setCreateTime(new Date());

        entity = repository.save(entity);
        OfficialConfigBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        return bean;
    }

    //=============================================
    //                 add
    //=============================================
    public OfficialConfigBean addConfig(String name, String value, String strStatus, String imageName, InputStream image) {
        logger.info("add config by name={} value={} status={} imageName={} image={}", name, value, strStatus, imageName, image!=null);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();
        if(existName(name)) {
            throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            status = CommonStatus.ENABLED;
        }
        long imageId = 0;
        String imageUrl = "";
        if (image!=null) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "official_config_image";
            }
            imageId = officialStorage.addFile(-1, imageName, image);
            imageUrl = officialStorage.getFilePath(imageId);
        }
        if (VerifyUtil.isStringEmpty(value)) {
            value = "";
        }

        OfficialConfigEntity entity = new OfficialConfigEntity();
        entity.setName(name);
        entity.setValue(value);
        entity.setStatus(status);
        entity.setImageId(imageId);
        entity.setCreateTime(new Date());
        entity = repository.save(entity);
        OfficialConfigBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        return bean;
    }
}

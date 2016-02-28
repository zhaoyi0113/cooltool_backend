package com.cooltoo.serivces;

import com.cooltoo.beans.BadgeBean;
import com.cooltoo.converter.BadgeBeanConverter;
import com.cooltoo.converter.BadgeEntityConverter;
import com.cooltoo.entities.BadgeEntity;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 2/24/16.
 */
@Service("BadgeService")
public class BadgeService {

    @Autowired
    private BadgeRepository repository;

    @Autowired
    private BadgeEntityConverter entityConverter;

    @Autowired
    private BadgeBeanConverter beanConverter;

    @Autowired
    private StorageService storageService;

    @Transactional
    public int createNewBadge(BadgeBean badgeBean) {
        BadgeEntity entity = entityConverter.convert(badgeBean);
        BadgeEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Transactional
    public int createNewBadge(String name, int point, int grade, InputStream fileInputStream, String fileName) {
        long fileId = storageService.saveFile(fileName, fileInputStream);
        String fileUrl = storageService.getFileUrl(fileId);
        BadgeBean bean = new BadgeBean();
        bean.setName(name);
        bean.setFileId(fileId);
        bean.setPoint(point);
        bean.setGrade(grade);
        //bean.setImageUrl(fileUrl);
        return createNewBadge(bean);
    }

    public List<BadgeBean> getAllBadge() {
        Iterable<BadgeEntity> all = repository.findAll();
        List<BadgeBean> beanList = new ArrayList<BadgeBean>();
        for (BadgeEntity entity : all) {
            BadgeBean bean = beanConverter.convert(entity);
            beanList.add(bean);
        }
        return beanList;
    }

    public BadgeBean getBadgeById(int id) {
        if (repository.exists(id)) {
            BadgeEntity entity = repository.findOne(id);
            return beanConverter.convert(entity);
        }
        return null;
    }

    @Transactional
    public BadgeBean deleteBadge(int id) {
        BadgeEntity badge = repository.findOne(id);
        if (badge == null) {
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(badge);
    }

    @Transactional
    public BadgeBean updateBadge(int id, String name, int point, int grade, InputStream fileInputStream, String fileName) {
        if (!repository.exists(id)) {
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }
        boolean changed = false;
        BadgeEntity bean = repository.findOne(id);
        if (null != fileInputStream) {
            long fileId = storageService.saveFile(fileName, fileInputStream);
            String fileUrl = storageService.getFileUrl(fileId);
            bean.setFileId(fileId);
            bean.setImageUrl(fileUrl);
            changed = true;
        }
        bean.setId(id);
        if (null != name && !bean.getName().equals(name)) {
            bean.setName(name);
            changed = true;
        }
        if (0!=point && bean.getPoint() != point) {
            bean.setPoint(point);
            changed = true;
        }
        if (0!=grade && bean.getGrade() != grade) {
            bean.setGrade(grade);
            changed = true;
        }
        if (changed) {
            bean = repository.save(bean);
        }
        return beanConverter.convert(bean);
    }
}

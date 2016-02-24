package com.cooltoo.serivces;

import com.cooltoo.beans.BadgeBean;
import com.cooltoo.converter.BadgeBeanConverter;
import com.cooltoo.converter.BadgeEntityConverter;
import com.cooltoo.entities.BadgeEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public int createNewBadge(BadgeBean badgeBean) {
        BadgeEntity entity = entityConverter.convert(badgeBean);
        BadgeEntity saved = repository.save(entity);
        return saved.getId();
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

    @Transactional
    public BadgeBean deleteBadge(int id){
        BadgeEntity badge = repository.findOne(id);
        if(badge == null){
            throw new BadRequestException(ErrorCode.BADGE_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(badge);
    }
}

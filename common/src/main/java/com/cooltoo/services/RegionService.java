package com.cooltoo.services;

import com.cooltoo.beans.RegionBean;
import com.cooltoo.converter.RegionBeanConverter;
import com.cooltoo.entities.RegionEntity;
import com.cooltoo.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Service("RegionService")
public class RegionService {

    private static final Logger logger = Logger.getLogger(RegionService.class.getName());

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "parentId"),
            new Sort.Order(Sort.Direction.ASC, "id"));

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionBeanConverter beanConverter;

    public List<RegionBean> getProvince() {
        List<RegionEntity> countries  = regionRepository.findByParentId(1, sort);
        List<RegionBean>   countriesB = new ArrayList<RegionBean>();
        for (RegionEntity county : countries) {
            countriesB.add(beanConverter.convert(county));
        }
        return countriesB;
    }

    public List<RegionBean> getSubRegion(int parentId) {
        List<RegionEntity> subRegions  = regionRepository.findByParentId(parentId, sort);
        List<RegionBean>   subRegionsB = new ArrayList<RegionBean>();
        for (RegionEntity subRegion : subRegions) {
            subRegionsB.add(beanConverter.convert(subRegion));
        }
        return subRegionsB;
    }

    public List<RegionBean> getRegion(List<Integer> ids) {
        List<RegionEntity> regions  = regionRepository.findByIdIn(ids);
        List<RegionBean>   regionsB = new ArrayList<RegionBean>();
        for (RegionEntity region : regions) {
            regionsB.add(beanConverter.convert(region));
        }
        return regionsB;
    }
}

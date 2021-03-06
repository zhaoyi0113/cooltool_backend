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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Service("RegionService")
public class RegionService {

    private static final Logger logger = LoggerFactory.getLogger(RegionService.class.getName());

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

    public List<RegionBean> getProvinceWithSubRegion() {
        Iterable<RegionEntity> resultSet = regionRepository.findAll();
        List<RegionBean>   provinces = new ArrayList<>();
        for (RegionEntity tmp : resultSet) {
            if (tmp.getParentId()!=1) {
                continue;
            }
            provinces.add(beanConverter.convert(tmp));
        }
        for (RegionEntity tmp : resultSet) {
            if (tmp.getParentId()==1) {
                continue;
            }
            for (RegionBean province : provinces) {
                if (tmp.getParentId() == province.getId()) {
                    province.getSubRegions().add(beanConverter.convert(tmp));
                    break;
                }
            }
        }
        return provinces;
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

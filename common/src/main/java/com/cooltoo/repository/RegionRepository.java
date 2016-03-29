package com.cooltoo.repository;

import com.cooltoo.entities.RegionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/3/29.
 */
public interface RegionRepository extends CrudRepository<RegionEntity, Integer> {

    public List<RegionEntity> findByIdIn(List<Integer> ids);

    public List<RegionEntity> findByParentId(int parentId, Sort sort);

    public List<RegionEntity> findByParentIdIn(List<Integer> parentIds, Sort sort);
}

package com.cooltoo.repository;

import com.cooltoo.entities.HospitalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalRepository extends CrudRepository<HospitalEntity, Integer> {
    Page<HospitalEntity> findAll(Pageable page);
    List<HospitalEntity> findByIdIn(List<Integer> ids);
    List<HospitalEntity> findByName(String name);

    List<HospitalEntity> findByProvinceAndEnable(int provinceId, int enable);
//    @Query("FROM HospitalEntity h WHERE ")
//    List<HospitalEntity> findByNameAddressAndRegion(@Param)
}

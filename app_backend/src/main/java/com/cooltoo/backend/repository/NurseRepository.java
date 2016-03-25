package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
public interface NurseRepository extends CrudRepository<NurseEntity, Long> {

    List<NurseEntity> findNurseByMobile(String mobile);

    List<NurseEntity> findNurseByNameContaining(String name);

    List<NurseEntity> findNurseByName(String name);

    List<NurseEntity> findNurseByIdIn(List<Long> ids);
}

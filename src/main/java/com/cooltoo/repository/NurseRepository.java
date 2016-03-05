package com.cooltoo.repository;

import com.cooltoo.entities.NurseEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
public interface NurseRepository extends CrudRepository<NurseEntity, Long> {

    public List<NurseEntity> findNurseByMobile(String mobile);
}

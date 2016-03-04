package com.cooltoo.repository;

import com.cooltoo.entities.NurseEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by lg380357 on 2016/3/2.
 */
public interface NurseRepository extends CrudRepository<NurseEntity, Long> {

    public List<NurseEntity> findNurseByMobile(String mobile);
}

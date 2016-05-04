package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.constants.UserAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
public interface NurseRepository extends CrudRepository<NurseEntity, Long> {
    Page<NurseEntity> findAll(Pageable page);
    List<NurseEntity> findByMobile(String mobile);
    List<NurseEntity> findByNameContaining(String name);

    @Query("SELECT n.id FROM NurseEntity n WHERE n.name like %?1% ")
    List<Long>        findIdsByFuzzyName(String fuzzyName);

    List<NurseEntity> findByName(String name);
    List<NurseEntity> findByIdIn(List<Long> ids);
    Page<NurseEntity> findByAuthority(UserAuthority authority, Pageable page);
    long              countByAuthority(UserAuthority authority);
}

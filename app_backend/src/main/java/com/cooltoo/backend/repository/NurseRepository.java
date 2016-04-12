package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.constants.UserAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
public interface NurseRepository extends CrudRepository<NurseEntity, Long> {
    Page<NurseEntity> findAll(Pageable page);
    List<NurseEntity> findByMobile(String mobile);
    List<NurseEntity> findByNameContaining(String name);
    List<NurseEntity> findByName(String name);
    List<NurseEntity> findByIdIn(List<Long> ids);
    Page<NurseEntity> findByAuthority(UserAuthority authority, Pageable page);
    long              countByAuthority(UserAuthority authority);
}

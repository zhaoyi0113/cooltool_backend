package com.cooltoo.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.OfficialConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/5/9.
 */
public interface OfficialConfigRepository extends JpaRepository<OfficialConfigEntity, Integer> {
    OfficialConfigEntity findByName(String name);
    Page<OfficialConfigEntity> findByStatus(CommonStatus status, Pageable page);
    long countByStatus(CommonStatus status);
    void deleteByIdIn(Iterable<Integer> ids);
}

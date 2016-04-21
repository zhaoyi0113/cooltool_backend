package com.cooltoo.repository;

import com.cooltoo.constants.ActivityStatus;
import com.cooltoo.entities.ActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/4/20.
 */
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {
    List<ActivityEntity> findByTitle(String title);
    List<ActivityEntity> findByStatus(ActivityStatus status, Sort sort);
    Page<ActivityEntity> findByStatus(ActivityStatus status, Pageable page);
    long countByStatus(ActivityStatus status);
    void deleteByIdIn(List<Long> ids);
}

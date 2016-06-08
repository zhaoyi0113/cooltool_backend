package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.entities.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/6/8.
 */
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    long countByStatus(CourseStatus status);
    long countByName(String name);
    List<CourseEntity> findByName(String name, Sort sort);
    Page<CourseEntity> findByStatus(CourseStatus status, Pageable page);
    List<CourseEntity> findByIdIn(List<Long> ids, Sort sort);
    List<CourseEntity> findByStatusAndIdIn(CourseStatus status, List<Long> ids, Sort sort);

}

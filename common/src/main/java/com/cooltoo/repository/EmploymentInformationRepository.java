package com.cooltoo.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.EmploymentInformationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/4/20.
 */
public interface EmploymentInformationRepository extends JpaRepository<EmploymentInformationEntity, Long> {
    List<EmploymentInformationEntity> findByTitle(String title);
    List<EmploymentInformationEntity> findByStatus(CommonStatus status, Sort sort);
    Page<EmploymentInformationEntity> findByStatus(CommonStatus status, Pageable page);
    long countByStatus(CommonStatus status);
}

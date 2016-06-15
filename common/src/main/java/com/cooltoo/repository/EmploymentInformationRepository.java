package com.cooltoo.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.EmploymentType;
import com.cooltoo.entities.EmploymentInformationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/4/20.
 */
public interface EmploymentInformationRepository extends JpaRepository<EmploymentInformationEntity, Long> {
    List<EmploymentInformationEntity> findByTitle(String title);
    @Query("FROM EmploymentInformationEntity info" +
            " WHERE (?1 IS NULL OR info.status=?1)" +
            " AND (?2 IS NULL OR info.type=?2)")
    List<EmploymentInformationEntity> findByStatusAndType(CommonStatus status, EmploymentType type, Sort sort);
    @Query("FROM EmploymentInformationEntity info" +
            " WHERE (?1 IS NULL OR info.status=?1)" +
            " AND (?2 IS NULL OR info.type=?2)")
    Page<EmploymentInformationEntity> findByStatusAndType(CommonStatus status, EmploymentType type, Pageable page);
    @Query("SELECT count(info.id) FROM EmploymentInformationEntity info" +
            " WHERE (?1 IS NULL OR info.status=?1)" +
            " AND (?2 IS NULL OR info.type=?2)")
    long countByStatusAndType(CommonStatus status, EmploymentType type);
}

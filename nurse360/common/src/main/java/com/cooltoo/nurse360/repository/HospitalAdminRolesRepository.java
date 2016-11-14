package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.constants.AdminRole;
import com.cooltoo.nurse360.entities.HospitalAdminRolesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/10.
 */
public interface HospitalAdminRolesRepository extends JpaRepository<HospitalAdminRolesEntity, Long> {


    @Query("SELECT count(haau.id) FROM HospitalAdminRolesEntity haau" +
            " WHERE (?1 IS NULL OR haau.adminId=?1)" +
            "   AND (?2 IS NULL OR haau.role=?2)" +
            "   AND (?3 IS NULL OR haau.status=?3)")
    long countByConditions(Long adminId, AdminRole role, CommonStatus status);


    @Query("FROM HospitalAdminRolesEntity haau" +
            " WHERE (?1 IS NULL OR haau.adminId=?1)" +
            "   AND (?2 IS NULL OR haau.role=?2)" +
            "   AND (?3 IS NULL OR haau.status=?3)")
    Page<HospitalAdminRolesEntity> findByConditions(Long adminId, AdminRole role, CommonStatus status, Pageable page);


    @Query("FROM HospitalAdminRolesEntity haau" +
            " WHERE (?1 IS NULL OR haau.adminId=?1)" +
            "   AND (?2 IS NULL OR haau.role=?2)" +
            "   AND (?3 IS NULL OR haau.status=?3)")
    List<HospitalAdminRolesEntity> findByConditions(Long adminId, AdminRole role, CommonStatus status, Sort sort);

    @Query("FROM HospitalAdminRolesEntity haau" +
            " WHERE (haau.adminId IN ?1)" +
            "   AND (?2 IS NULL OR haau.role=?2)" +
            "   AND (?3 IS NULL OR haau.status=?3)")
    List<HospitalAdminRolesEntity> findByConditions(List<Long> adminId, AdminRole role, CommonStatus status, Sort sort);
}

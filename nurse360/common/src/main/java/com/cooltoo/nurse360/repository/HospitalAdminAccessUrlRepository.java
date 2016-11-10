package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.entities.HospitalAdminAccessUrlEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/10.
 */
public interface HospitalAdminAccessUrlRepository extends JpaRepository<HospitalAdminAccessUrlEntity, Long> {


    @Query("SELECT count(haau.id) FROM HospitalAdminAccessUrlEntity haau" +
            " WHERE (?1 IS NULL OR haau.adminId=?1)" +
            "   AND (?2 IS NULL OR haau.urlId=?2)" +
            "   AND (?3 IS NULL OR haau.status=?3)")
    long countByConditions(Long adminId, Long httpUrlId, CommonStatus status);


    @Query("FROM HospitalAdminAccessUrlEntity haau" +
            " WHERE (?1 IS NULL OR haau.adminId=?1)" +
            "   AND (?2 IS NULL OR haau.urlId=?2)" +
            "   AND (?3 IS NULL OR haau.status=?3)")
    Page<HospitalAdminAccessUrlEntity> findByConditions(Long adminId, Long httpUrlId, CommonStatus status, Pageable page);


    @Query("FROM HospitalAdminAccessUrlEntity haau" +
            " WHERE (?1 IS NULL OR haau.adminId=?1)" +
            "   AND (?2 IS NULL OR haau.urlId=?2)" +
            "   AND (?3 IS NULL OR haau.status=?3)")
    List<HospitalAdminAccessUrlEntity> findByConditions(Long adminId, Long httpUrlId, CommonStatus status, Sort sort);
}

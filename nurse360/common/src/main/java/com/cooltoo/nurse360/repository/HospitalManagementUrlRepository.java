package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.RequestMethod;
import com.cooltoo.nurse360.entities.HospitalManagementUrlEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public interface HospitalManagementUrlRepository extends JpaRepository<HospitalManagementUrlEntity, Long> {

    @Query("SELECT count(hmu.id) FROM HospitalManagementUrlEntity hmu" +
            " WHERE (?1 IS NULL OR hmu.httpType=?1)" +
            "   AND (?2 IS NULL OR hmu.httpUrl=?2)" +
            "   AND (?3 IS NULL OR hmu.status=?3)")
    long countByConditions(RequestMethod httpType, String httpUrl, CommonStatus status);


    @Query("FROM HospitalManagementUrlEntity hmu" +
            " WHERE (?1 IS NULL OR hmu.httpType=?1)" +
            "   AND (?2 IS NULL OR hmu.httpUrl=?2)" +
            "   AND (?3 IS NULL OR hmu.status=?3)")
    Page<HospitalManagementUrlEntity> findByConditions(RequestMethod httpType, String httpUrl, CommonStatus status, Pageable page);


    @Query("FROM HospitalManagementUrlEntity hmu" +
            " WHERE (?1 IS NULL OR hmu.httpType=?1)" +
            "   AND (?2 IS NULL OR hmu.httpUrl=?2)" +
            "   AND (?3 IS NULL OR hmu.status=?3)")
    List<HospitalManagementUrlEntity> findByConditions(RequestMethod httpType, String httpUrl, CommonStatus status, Sort sort);
}

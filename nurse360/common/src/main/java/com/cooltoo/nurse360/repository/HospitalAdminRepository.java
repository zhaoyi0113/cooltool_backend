package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.entities.HospitalAdminEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public interface HospitalAdminRepository extends JpaRepository<HospitalAdminEntity, Long> {

    List<HospitalAdminEntity> findAdminByNameAndPassword(String name, String password);
    HospitalAdminEntity findFirstByNameAndStatus(String name, CommonStatus status);
    long countAdminByIdAndStatus(long adminId, CommonStatus status);
    HospitalAdminEntity findById(long id);

    @Query("SELECT count(ha.id) FROM HospitalAdminEntity ha" +
            " WHERE (?1 IS NULL OR ha.name      LIKE %?1)" +
            "   AND (?2 IS NULL OR ha.telephone LIKE %?2)" +
            "   AND (?3 IS NULL OR ha.email     LIKE %?3)" +
            "   AND (?4 IS NULL OR ha.hospitalId=?4)" +
            "   AND (?5 IS NULL OR ha.departmentId=?5)" +
            "   AND (?6 IS NULL OR ha.adminType=?6)" +
            "   AND (?7 IS NULL OR ha.status=?7)")
    long countByConditions(String name,
                           String telephone, String email,
                           Integer hospitalId, Integer departmentId,
                           AdminUserType type,
                           CommonStatus status);


    @Query("FROM HospitalAdminEntity ha" +
            " WHERE (?1 IS NULL OR ha.name      LIKE %?1)" +
            "   AND (?2 IS NULL OR ha.telephone LIKE %?2)" +
            "   AND (?3 IS NULL OR ha.email     LIKE %?3)" +
            "   AND (?4 IS NULL OR ha.hospitalId=?4)" +
            "   AND (?5 IS NULL OR ha.departmentId=?5)" +
            "   AND (?6 IS NULL OR ha.adminType=?6)" +
            "   AND (?7 IS NULL OR ha.status=?7)")
    Page<HospitalAdminEntity> findByConditions(String name,
                                               String telephone, String email,
                                               Integer hospitalId, Integer departmentId,
                                               AdminUserType type,
                                               CommonStatus status,
                                               Pageable page);


    @Query("FROM HospitalAdminEntity ha" +
            " WHERE (?1 IS NULL OR ha.name      LIKE %?1)" +
            "   AND (?2 IS NULL OR ha.telephone LIKE %?2)" +
            "   AND (?3 IS NULL OR ha.email     LIKE %?3)" +
            "   AND (?4 IS NULL OR ha.hospitalId=?4)" +
            "   AND (?5 IS NULL OR ha.departmentId=?5)" +
            "   AND (?6 IS NULL OR ha.adminType=?6)" +
            "   AND (?7 IS NULL OR ha.status=?7)")
    List<HospitalAdminEntity> findByConditions(String name,
                                               String telephone, String email,
                                               Integer hospitalId, Integer departmentId,
                                               AdminUserType type,
                                               CommonStatus status,
                                               Sort sort);
}

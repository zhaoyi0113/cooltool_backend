package com.cooltoo.repository;

import com.cooltoo.entities.HospitalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalRepository extends CrudRepository<HospitalEntity, Integer> {
    Page<HospitalEntity> findAll(Pageable page);
    HospitalEntity findById(int id);
    List<HospitalEntity> findByIdIn(List<Integer> ids);
    List<HospitalEntity> findByName(String name);
    List<HospitalEntity> findByUniqueId(String uniqueId);
    long countByUniqueId(String uniqueId);
    List<HospitalEntity> findByProvinceAndEnable(int provinceId, int enable);

    List<HospitalEntity> findByEnable(int enable, Sort sort);
    List<HospitalEntity> findBySupportGo2nurse(int supportGo2nurse, Sort sort);

    @Query("FROM HospitalEntity h" +
            " WHERE (?1 IS NOT NULL AND (h.name LIKE %?1 OR h.aliasName LIKE %?1))" +
            " AND   (h.enable=1)" +
            " AND   (?2 IS NULL OR h.supportGo2nurse=?2)")
    List<HospitalEntity> findByNameLike(String nameLike, Integer supportGo2nurse);

    @Query("SELECT count(h.id) FROM HospitalEntity h" +
            " WHERE (?1 IS NULL OR (h.name LIKE %?1 OR h.aliasName LIKE %?1))" +
            " AND (?2 IS NULL OR h.province=?2)" +
            " AND (?3 IS NULL OR h.city=?3)" +
            " AND (?4 IS NULL OR h.district=?4)" +
            " AND (?5 IS NULL OR h.address LIKE %?5%)" +
            " AND (?6 IS NULL OR h.enable=?6)" +
            " AND (?7 IS NULL OR h.supportGo2nurse=?7)")
    long countByConditionsAND(String nameLike,
                              Integer province, Integer city, Integer district, String addressLike,
                              Integer enable, Integer supportGo2nurse);
    @Query("FROM HospitalEntity h" +
            " WHERE (?1 IS NULL OR (h.name LIKE %?1 OR h.aliasName LIKE %?1))" +
            " AND (?2 IS NULL OR h.province=?2)" +
            " AND (?3 IS NULL OR h.city=?3)" +
            " AND (?4 IS NULL OR h.district=?4)" +
            " AND (?5 IS NULL OR h.address LIKE %?5%)" +
            " AND (?6 IS NULL OR h.enable=?6)" +
            " AND (?7 IS NULL OR h.supportGo2nurse=?7)")
    Page<HospitalEntity> findByConditionsAND(String nameLike,
                                             Integer province, Integer city, Integer district, String addressLike,
                                             Integer enable, Integer supportGo2nurse, Pageable page);

    @Query("SELECT count(h.id) FROM HospitalEntity h" +
            " WHERE (h.name LIKE %?1% OR h.aliasName LIKE %?1%)" +
            " OR (h.province=?2)" +
            " OR (h.city=?3)" +
            " OR (h.district=?4)" +
            " OR (h.address LIKE %?5%)" +
            " OR (h.enable=?6)" +
            " OR (h.supportGo2nurse=?7)")
    long countByConditionsOR(String nameLike,
                             Integer province, Integer city, Integer district, String addressLike,
                             Integer enable, Integer supportGo2nurse);
    @Query("FROM HospitalEntity h" +
            " WHERE (h.name LIKE %?1% OR h.aliasName LIKE %?1%)" +
            " OR (h.province=?2)" +
            " OR (h.city=?3)" +
            " OR (h.district=?4)" +
            " OR (h.address LIKE %?5%)" +
            " OR (h.enable=?6)" +
            " OR (h.supportGo2nurse=?7)")
    Page<HospitalEntity> findByConditionsOR(String nameLike,
                                            Integer province, Integer city, Integer district, String addressLike,
                                            Integer enable, Integer supportGo2nurse, Pageable page);
}

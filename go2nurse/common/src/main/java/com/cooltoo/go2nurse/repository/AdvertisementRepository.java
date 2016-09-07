package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.AdvertisementType;
import com.cooltoo.go2nurse.entities.AdvertisementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/9/6.
 */
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long> {
    @Query("FROM AdvertisementEntity ad" +
            " WHERE (?1 IS NULL OR ad.status=?1)" +
            " AND   (ad.type=?2)")
    List<AdvertisementEntity> findByStatusAndType(CommonStatus status, AdvertisementType type, Sort sort);
    @Query("FROM AdvertisementEntity ad" +
            " WHERE (?1 IS NULL OR ad.status=?1)" +
            " AND   (ad.type=?2)")
    Page<AdvertisementEntity> findByStatusAndType(CommonStatus status, AdvertisementType type, Pageable page);
    @Query("SELECT count(ad.id) FROM AdvertisementEntity ad" +
            " WHERE (?1 IS NULL OR ad.status=?1)" +
            " AND   (ad.type=?2)")
    long countByStatusAndType(CommonStatus status, AdvertisementType type);
}

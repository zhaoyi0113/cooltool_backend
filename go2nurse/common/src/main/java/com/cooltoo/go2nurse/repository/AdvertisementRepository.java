package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.AdvertisementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/9/6.
 */
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long> {
}

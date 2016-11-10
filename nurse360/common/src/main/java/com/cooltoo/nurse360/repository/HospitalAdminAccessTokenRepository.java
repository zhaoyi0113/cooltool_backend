package com.cooltoo.nurse360.repository;

import com.cooltoo.nurse360.entities.HospitalAdminAccessTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/9.
 */
public interface HospitalAdminAccessTokenRepository extends CrudRepository<HospitalAdminAccessTokenEntity, Long> {
    List<HospitalAdminAccessTokenEntity> findTokenByAdminId(long adminId);
    List<HospitalAdminAccessTokenEntity> findTokenByToken(String token);
}

package com.cooltoo.admin.repository;

import com.cooltoo.admin.entities.AdminUserTokenAccessEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/3/22.
 */
public interface AdminUserTokenAccessRepository extends CrudRepository<AdminUserTokenAccessEntity, Long> {


    List<AdminUserTokenAccessEntity> findAdminUserTokenAccessByUserId(long userId);

    List<AdminUserTokenAccessEntity> findAdminUserTokenAccessByToken(String token);
}

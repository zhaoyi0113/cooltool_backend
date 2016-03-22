package com.cooltoo.admin.repository;

import com.cooltoo.admin.entities.AdminUserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by zhaolisong on 16/3/22.
 */
public interface AdminUserRepository extends CrudRepository<AdminUserEntity, Long> {

    public AdminUserEntity findAdminUserByUserName(String userName);
}

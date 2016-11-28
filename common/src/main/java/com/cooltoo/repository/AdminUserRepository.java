package com.cooltoo.repository;

import com.cooltoo.entities.AdminUserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by zhaolisong on 16/3/22.
 */
public interface AdminUserRepository extends CrudRepository<AdminUserEntity, Long> {

    AdminUserEntity findAdminUserByUserName(String userName);
    AdminUserEntity findAdminUserByUserNameAndPassword(String userName, String password);
}

package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.UserAuthority;
import com.cooltoo.go2nurse.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/13.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByMobile(String mobile);

    @Query("SELECT user.id FROM UserEntity user WHERE user.authority=?1")
    List<Long> findIdsByAuthority(UserAuthority authority);

    @Query("FROM UserEntity user" +
            " WHERE (user.authority=?1  OR ?1 IS NULL)" +
            " AND   (user.name LIKE %?2 OR ?2 IS NULL)")
    Page<UserEntity> findByAuthorityAndName(UserAuthority authority, String fuzzyName, Pageable page);
    @Query("SELECT count(user.id) FROM UserEntity user" +
            " WHERE (user.authority=?1  OR ?1 IS NULL)" +
            " AND   (user.name LIKE %?2 OR ?2 IS NULL)")
    long countByAuthorityAndName(UserAuthority authority, String fuzzyName);
}

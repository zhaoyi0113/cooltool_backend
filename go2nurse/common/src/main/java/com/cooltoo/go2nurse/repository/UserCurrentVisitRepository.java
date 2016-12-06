package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.UserCurrentVisitEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 2016/12/6.
 */
public interface UserCurrentVisitRepository extends JpaRepository<UserCurrentVisitEntity, Long> {
    List<UserCurrentVisitEntity> findByUserId(long userId, Sort sort);
    long countByUserId(long userId);
}

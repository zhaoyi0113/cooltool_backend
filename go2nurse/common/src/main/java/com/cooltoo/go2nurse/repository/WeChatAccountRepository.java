package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.WeChatAccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaoyi0113 on 10/3/16.
 */
public interface WeChatAccountRepository extends JpaRepository<WeChatAccountEntity, Integer> {

    WeChatAccountEntity findFirstByAppId(String appId);

    List<WeChatAccountEntity> findByStatus(CommonStatus status);

    long countByStatus(CommonStatus status);
    Page<WeChatAccountEntity> findByStatus(CommonStatus status, Pageable page);
    List<WeChatAccountEntity> findByAppId(String appId);
    List<WeChatAccountEntity> findByAppIdAndAppSecret(String appId, String appSecret);
}

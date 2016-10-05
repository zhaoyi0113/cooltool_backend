package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.entities.UserWeChatTokenAccessEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaoyi0113 on 10/3/16.
 */
public interface UserWeChatTokenAccessRepository extends CrudRepository<UserWeChatTokenAccessEntity, Long>{

    Long countByTokenAndStatus(String token, CommonStatus status);

    UserWeChatTokenAccessEntity findFirstByTokenAndStatus(String token, CommonStatus status);

    @Query("SELECT account.appId FROM UserWeChatTokenAccessEntity tokenAccess, WeChatAccountEntity account" +
            " WHERE tokenAccess.wechatAccountId=account.id" +
            " AND (tokenAccess.token = ?1)" +
            " AND (tokenAccess.status = ?2)" )
    String findAppIdFromToken(String token, CommonStatus status);

}

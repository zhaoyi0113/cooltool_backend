package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.WeChatAccountEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by zhaoyi0113 on 10/3/16.
 */
public interface WeChatAccountRepository extends CrudRepository<WeChatAccountEntity, Long>{

    WeChatAccountEntity findFirstByAppId(String appId);
}

package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lg380357 on 2016/3/7.
 */
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}

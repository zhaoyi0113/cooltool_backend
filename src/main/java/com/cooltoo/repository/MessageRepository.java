package com.cooltoo.repository;

import com.cooltoo.entities.MessageEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/7.
 */
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}

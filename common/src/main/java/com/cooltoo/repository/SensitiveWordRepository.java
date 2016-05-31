package com.cooltoo.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SensitiveWordType;
import com.cooltoo.entities.SensitiveWordEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/5/31.
 */
public interface SensitiveWordRepository extends JpaRepository<SensitiveWordEntity, Integer> {
    long countByWord(String word);
    long countByStatus(CommonStatus status);
    long countByType(SensitiveWordType type);
    long countByTypeAndStatus(SensitiveWordType type, CommonStatus status);
    List<SensitiveWordEntity> findByStatus(CommonStatus status, Sort sort);
    List<SensitiveWordEntity> findByType(SensitiveWordType type, Sort sort);
    List<SensitiveWordEntity> findByTypeAndStatus(SensitiveWordType type, CommonStatus status, Sort sort);
}

package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.SpeakTypeEntity;
import com.cooltoo.constants.SpeakType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/3/28.
 */
public interface SpeakTypeRepository extends CrudRepository<SpeakTypeEntity, Integer> {
    @Query("SELECT st.id FROM SpeakTypeEntity st WHERE st.type IN(?1)")
    List<Integer> findByTypeIn(List<SpeakType> speakTypes);
    @Query("FROM SpeakTypeEntity st WHERE st.type=?1")
    SpeakTypeEntity findOneBySpeakType(SpeakType speakType);
    List<SpeakTypeEntity> findAll(Sort sort);
}

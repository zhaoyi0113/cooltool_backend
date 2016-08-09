package com.cooltoo.repository;

import com.cooltoo.constants.PlatformType;
import com.cooltoo.constants.UserType;
import com.cooltoo.entities.SuggestionEntity;
import com.cooltoo.constants.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
public interface SuggestionRepository extends CrudRepository<SuggestionEntity, Long> {

    Page<SuggestionEntity> findAll(Pageable page);
    Page<SuggestionEntity> findByStatus(ReadingStatus status, Pageable page);
    List<SuggestionEntity> findByIdIn(List<Long> ids);
    List<SuggestionEntity> deleteByIdIn(List<Long> ids);
    long countByStatus(ReadingStatus status);

    @Query("FROM SuggestionEntity se" +
            " WHERE (?1 IS NULL OR se.userType=?1)" +
            " AND (?2 IS NULL OR se.platform=?2)" +
            " AND (?3 IS NULL OR se.version=?3)" +
            " AND (?4 IS NULL OR se.status=?4)")
    Page<SuggestionEntity> findByConditions(UserType userType, PlatformType platform, String version, ReadingStatus status, Pageable page);

    @Query("SELECT COUNT(se.id) FROM SuggestionEntity se" +
            " WHERE (?1 IS NULL OR se.userType=?1)" +
            " AND (?2 IS NULL OR se.platform=?2)" +
            " AND (?3 IS NULL OR se.version=?3)" +
            " AND (?4 IS NULL OR se.status=?4)")
    long countByConditions(UserType userType, PlatformType platform, String version, ReadingStatus status);
}

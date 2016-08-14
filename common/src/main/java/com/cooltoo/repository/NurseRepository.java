package com.cooltoo.repository;

import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.constants.UserAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
public interface NurseRepository extends JpaRepository<NurseEntity, Long> {
    Page<NurseEntity> findAll(Pageable page);
    List<NurseEntity> findByMobile(String mobile);
    List<NurseEntity> findByNameContaining(String name);

    @Query("SELECT n.id FROM NurseEntity n WHERE n.authority=?1")
    List<Long> findIdsByAuthority(UserAuthority authority);

    long countByAuthorityAndIdIn(UserAuthority authority, List<Long> ids);
    @Query("SELECT n.id FROM NurseEntity n WHERE n.authority=?1 AND n.id IN (?2)")
    List<Long> findByAuthorityAndIdIn(UserAuthority authority, List<Long> ids);

    @Query("SELECT count(n.id) FROM NurseEntity n WHERE n.name like %?1")
    long      countByFuzzyName(String fuzzyName);
    @Query("SELECT n.id FROM NurseEntity n WHERE n.name like %?1")
    List<Long> findIdsByFuzzyName(String fuzzyName, Pageable page);

    List<NurseEntity> findByName(String name);
    List<NurseEntity> findByIdIn(List<Long> ids);

    //==================================================================
    //              for administrator user
    //==================================================================
    @Query("SELECT n FROM NurseEntity n LEFT JOIN n.extensions ne" +
            " WHERE (?1 IS NULL OR n.authority=?1)" +
            " AND   (?2 IS NULL OR n.name LIKE %?2)" +
            " AND   (?3 IS NULL OR ne.answerNursingQuestion=?3)")
    Page<NurseEntity> findByAuthority(UserAuthority authority, String fuzzyName, YesNoEnum answerNursingQuestion, Pageable page);
    @Query("SELECT count(n.id) FROM NurseEntity n LEFT JOIN n.extensions ne" +
            " WHERE (?1 IS NULL OR n.authority=?1)" +
            " AND   (?2 IS NULL OR n.name LIKE %?2)" +
            " AND   (?3 IS NULL OR ne.answerNursingQuestion=?3)")
    long countByAuthority(UserAuthority authority, String fuzzyName, YesNoEnum answerNursingQuestion);
}

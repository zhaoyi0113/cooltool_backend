package com.cooltoo.repository;

import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.constants.UserAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
public interface NurseRepository extends JpaRepository<NurseEntity, Long> {
    Page<NurseEntity> findAll(Pageable page);
    List<NurseEntity> findByMobile(String mobile);
    NurseEntity findByMobileAndPassword(String mobile, String password);
    List<NurseEntity> findByNameContaining(String name);

    @Query("SELECT n.id FROM NurseEntity n WHERE n.authority=?1")
    List<Long> findIdsByAuthority(UserAuthority authority);

    long countByAuthorityAndIdIn(UserAuthority authority, List<Long> ids);
    @Query("SELECT n.id FROM NurseEntity n WHERE n.authority=?1 AND n.id IN (?2)")
    List<Long> findByAuthorityAndIdIn(UserAuthority authority, List<Long> ids);

    @Query("SELECT count(n.id) FROM NurseEntity n WHERE n.name like %?1")
    long countByFuzzyName(String fuzzyName);
    @Query("SELECT n.id FROM NurseEntity n WHERE n.name like %?1")
    List<Long> findIdsByFuzzyName(String fuzzyName, Pageable page);

    List<NurseEntity> findByName(String name);
    List<NurseEntity> findByIdIn(List<Long> ids);

//=========================================================
// query nurse by realName or hospitalId or departmentId
//=========================================================
    @Query("SELECT n FROM NurseEntity n LEFT JOIN n.extensions ne LEFT JOIN n.hospitalRelation nhr" +
            " WHERE (n.authority=?1)" +
            " AND   (?2 IS NULL OR ne.answerNursingQuestion=?2)" +
            " AND   (?6 IS NULL OR ne.isExpert=?6)" +
            " AND   ((?3 IS NULL OR n.realName LIKE %?3) OR (nhr.hospitalId IN (?4)) OR (nhr.departmentId IN (?5)))")
    List<NurseEntity> findByQueryString(UserAuthority authority, YesNoEnum answerNursingQuestion, String fuzzyName, List<Integer> hospitalId, List<Integer> departmentId, YesNoEnum isExpert, Sort sort);


    //==================================================================
    //              for administrator user
    //==================================================================
    @Query("SELECT n FROM NurseEntity n LEFT JOIN n.extensions ne LEFT JOIN n.hospitalRelation nhr" +
            " WHERE (?1 IS NULL OR n.authority=?1)" +
            " AND   (?2 IS NULL OR n.name LIKE %?2 OR n.realName LIKE %?2)" +
            " AND   (?3 IS NULL OR ne.answerNursingQuestion=?3)" +
            " AND   (?4 IS NULL OR nhr.hospitalId=?4)" +
            " AND   (?5 IS NULL OR nhr.departmentId=?5)" +
            " AND   (?6 IS NULL OR n.registerFrom=?6)" +
            " AND   (?7 IS NULL OR ne.seeAllOrder=?7)")
    Page<NurseEntity> findByAuthority(UserAuthority authority, String fuzzyName, YesNoEnum answerNursingQuestion, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom, YesNoEnum seeAllOrder, Pageable page);
    @Query("SELECT n FROM NurseEntity n LEFT JOIN n.extensions ne LEFT JOIN n.hospitalRelation nhr" +
            " WHERE (?1 IS NULL OR n.authority=?1)" +
            " AND   (?2 IS NULL OR n.name LIKE %?2 OR n.realName LIKE %?2)" +
            " AND   (?3 IS NULL OR ne.answerNursingQuestion=?3)" +
            " AND   (?4 IS NULL OR nhr.hospitalId=?4)" +
            " AND   (?5 IS NULL OR nhr.departmentId=?5)" +
            " AND   (?6 IS NULL OR n.registerFrom=?6)" +
            " AND   (?7 IS NULL OR ne.seeAllOrder=?7)")
    List<NurseEntity> findByAuthority(UserAuthority authority, String fuzzyName, YesNoEnum answerNursingQuestion, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom, YesNoEnum seeAllOrder, Sort sort);
    @Query("SELECT count(n.id) FROM NurseEntity n LEFT JOIN n.extensions ne LEFT JOIN n.hospitalRelation nhr" +
            " WHERE (?1 IS NULL OR n.authority=?1)" +
            " AND   (?2 IS NULL OR n.name LIKE %?2 OR n.realName LIKE %?2)" +
            " AND   (?3 IS NULL OR ne.answerNursingQuestion=?3)" +
            " AND   (?4 IS NULL OR nhr.hospitalId=?4)" +
            " AND   (?5 IS NULL OR nhr.departmentId=?5)" +
            " AND   (?6 IS NULL OR n.registerFrom=?6)" +
            " AND   (?7 IS NULL OR ne.seeAllOrder=?7)")
    long countByAuthority(UserAuthority authority, String fuzzyName, YesNoEnum answerNursingQuestion, Integer hospitalId, Integer departmentId, RegisterFrom registerFrom, YesNoEnum seeAllOrder);

}

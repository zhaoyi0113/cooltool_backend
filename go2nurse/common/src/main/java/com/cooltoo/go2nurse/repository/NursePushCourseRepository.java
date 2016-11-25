package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.entities.NursePushCourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/4.
 */
public interface NursePushCourseRepository extends JpaRepository<NursePushCourseEntity, Long> {


    @Query("SELECT count(npc.id) FROM NursePushCourseEntity npc" +
            " WHERE (?1 IS NULL OR npc.nurseId=?1)" +
            " AND   (?2 IS NULL OR npc.userId=?2)" +
            " AND   (?3 IS NULL OR npc.patientId=?3)" +
            " AND   (?4 IS NULL OR npc.read=?4)")
    long countPushCourseByNurseIdAndUserIdAndRead(Long nurseId, Long userId, Long patientId, ReadingStatus read);
    @Query("FROM NursePushCourseEntity npc" +
            " WHERE (?1 IS NULL OR npc.nurseId=?1)" +
            " AND   (?2 IS NULL OR npc.userId=?2)" +
            " AND   (?3 IS NULL OR npc.patientId=?3)" +
            " AND   (?4 IS NULL OR npc.read=?4)")
    Page<NursePushCourseEntity> findPushCourseByNurseIdAndUserIdAndRead(Long nurseId, Long userId, Long patientId, ReadingStatus read, Pageable page);
    @Query("FROM NursePushCourseEntity npc" +
            " WHERE (?1 IS NULL OR npc.nurseId=?1)" +
            " AND   (?2 IS NULL OR npc.userId=?2)" +
            " AND   (?3 IS NULL OR npc.patientId=?3)" +
            " AND   (?4 IS NULL OR npc.read=?4)")
    List<NursePushCourseEntity> findPushCourseByNurseIdAndUserIdAndRead(Long nurseId, Long userId, Long patientId, ReadingStatus read, Sort sort);

    List<NursePushCourseEntity> findPushCourseByCourseIdIn(List<Long> courseIds);
}

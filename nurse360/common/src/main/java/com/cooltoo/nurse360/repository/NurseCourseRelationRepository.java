package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.nurse360.entities.NurseCourseRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/11.
 */
public interface NurseCourseRelationRepository extends JpaRepository<NurseCourseRelationEntity, Long> {

    @Query("SELECT course.courseId FROM NurseCourseRelationEntity course" +
            " WHERE (?1 IS NULL OR course.nurseId=?1)" +
            " AND   (?2 IS NULL OR course.readingStatus=?2)")
    List<Long> findCourseIdByNurseIdAndReadingStatus(Long nurseId, ReadingStatus readingStatus);

    @Query("FROM NurseCourseRelationEntity course" +
            " WHERE (?1 IS NULL OR course.nurseId=?1)" +
            " AND   (?2 IS NULL OR course.courseId=?2)")
    List<NurseCourseRelationEntity> findByNurseIdAndCourseId(Long nurseId, Long courseId);
}

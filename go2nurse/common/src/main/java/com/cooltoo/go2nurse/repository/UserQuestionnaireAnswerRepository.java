package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.entities.UserQuestionnaireAnswerEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
public interface UserQuestionnaireAnswerRepository extends JpaRepository<UserQuestionnaireAnswerEntity, Long> {

    List<UserQuestionnaireAnswerEntity> findByQuestionnaireIdIn(List<Long> questionnaireIds, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserIdAndGroupId(Long userId, Long groupId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserIdAndGroupIdAndQuestionId(Long userId, Long groupId, Long questionId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserIdAndQuestionnaireId(Long userId, Long questionnaireId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserIdAndQuestionIdIn(Long userId, List<Long> questionIds, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserId(long userId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByQuestionIdIn(List<Long> questionIds, Sort sort);

    void deleteByUserIdAndAnswerCompleted(Long userId, YesNoEnum answerCompleted);

    @Modifying
    @Query("UPDATE UserQuestionnaireAnswerEntity answer" +
            " SET answer.answerCompleted=1, answer.questionnaireConclusion=?3" +
            " WHERE answer.userId=?1" +
            " AND answer.groupId=?2")
    int completeUserQuestionnaire(Long userId, Long groupId, String conclusion);

//            + " AND   (answer.time IS NULL OR answer.time>=?10)"
//            + " AND   (answer.time IS NULL OR answer.time<=?11)"
    @Query("FROM UserQuestionnaireAnswerEntity answer" +
            " WHERE answer.answerCompleted=?1" +
            " AND   (?2 IS NULL OR answer.userId=?2)" +
            " AND   (?3 IS NULL OR answer.patientId=?3)" +
            " AND   (?4 IS NULL OR answer.patientGender=?4)" +
            " AND   (?5 IS NULL OR answer.hospitalId=?5)" +
            " AND   (?6 IS NULL OR answer.departmentId=?6)" +
            " AND   (?7 IS NULL OR answer.questionnaireId=?7)" +
            " AND   (?8 IS NULL OR answer.patientAge>=?8)" +
            " AND   (?9 IS NULL OR answer.patientAge<=?9)")
    List<UserQuestionnaireAnswerEntity> findAnswerToExport(YesNoEnum answerCompleted,
                                                           Long userId, Long patientId, GenderType gender,
                                                           Integer hospitalId, Integer departmentId, Long questionnaireId,
                                                           Integer ageStart, Integer ageEnd,
                                                         // , Date timeStart, Date timeEnd
                                                           Sort sort

    );
}

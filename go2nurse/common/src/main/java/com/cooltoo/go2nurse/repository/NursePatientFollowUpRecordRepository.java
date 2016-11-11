package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.entities.NursePatientFollowUpRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/8.
 */
public interface NursePatientFollowUpRecordRepository extends JpaRepository<NursePatientFollowUpRecordEntity, Long> {

    @Query("SELECT count(npfur.id) FROM NursePatientFollowUpRecordEntity npfur" +
            " WHERE (?1 IS NULL OR npfur.status<>?1)" +
            "   AND (?2 IS NULL OR npfur.followUpType=?2)" +
            "   AND (?3 IS NULL OR npfur.patientReplied=?3)" +
            "   AND (?4 IS NULL OR npfur.nurseRead=?4)" +
            "   AND (npfur.followUpId IN (?5))")
    long countByConditionsByFollowUpIds(CommonStatus statusNot,
                                        PatientFollowUpType followUpType,
                                        YesNoEnum patientReplied,
                                        YesNoEnum nurseRead,
                                        List<Long> followUpIds);
    @Query("FROM NursePatientFollowUpRecordEntity npfur" +
            " WHERE (?1 IS NULL OR npfur.status<>?1)" +
            "   AND (?2 IS NULL OR npfur.followUpType=?2)" +
            "   AND (?3 IS NULL OR npfur.patientReplied=?3)" +
            "   AND (?4 IS NULL OR npfur.nurseRead=?4)" +
            "   AND (npfur.followUpId IN (?5))")
    Page<NursePatientFollowUpRecordEntity> findByConditionsByFollowUpIds(CommonStatus statusNot,
                                                                         PatientFollowUpType followUpType,
                                                                         YesNoEnum patientReplied,
                                                                         YesNoEnum nurseRead,
                                                                         List<Long> followUpIds,
                                                                         Pageable page);
    @Query("FROM NursePatientFollowUpRecordEntity npfur" +
            " WHERE (?1 IS NULL OR npfur.status<>?1)" +
            "   AND (?2 IS NULL OR npfur.followUpType=?2)" +
            "   AND (?3 IS NULL OR npfur.patientReplied=?3)" +
            "   AND (?4 IS NULL OR npfur.nurseRead=?4)" +
            "   AND (npfur.followUpId IN (?5))")
    List<NursePatientFollowUpRecordEntity> findByConditionsByFollowUpIds(CommonStatus statusNot,
                                                                         PatientFollowUpType followUpType,
                                                                         YesNoEnum patientReplied,
                                                                         YesNoEnum nurseRead,
                                                                         List<Long> followUpIds,
                                                                         Sort sort);



    @Query("FROM NursePatientFollowUpRecordEntity npfur" +
            " WHERE (?1 IS NULL OR npfur.status<>?1)" +
            "   AND (?2 IS NULL OR npfur.followUpType=?2)" +
            "   AND (?3 IS NULL OR npfur.patientReplied=?3)" +
            "   AND (?4 IS NULL OR npfur.nurseRead=?4)" +
            "   AND (npfur.followUpId IN (?5))")
    Page<NursePatientFollowUpRecordEntity> findByConditionsByIds(CommonStatus statusNot,
                                                                 PatientFollowUpType followUpType,
                                                                 YesNoEnum patientReplied,
                                                                 YesNoEnum nurseRead,
                                                                 List<Long> ids,
                                                                 Pageable page);
    @Query("FROM NursePatientFollowUpRecordEntity npfur" +
            " WHERE (?1 IS NULL OR npfur.status<>?1)" +
            "   AND (?2 IS NULL OR npfur.followUpType=?2)" +
            "   AND (?3 IS NULL OR npfur.patientReplied=?3)" +
            "   AND (?4 IS NULL OR npfur.nurseRead=?4)" +
            "   AND (npfur.followUpId IN (?5))")
    List<NursePatientFollowUpRecordEntity> findByConditionsByIds(CommonStatus statusNot,
                                                                 PatientFollowUpType followUpType,
                                                                 YesNoEnum patientReplied,
                                                                 YesNoEnum nurseRead,
                                                                 List<Long> ids,
                                                                 Sort sort);

    List<NursePatientFollowUpRecordEntity> findByStatusAndFollowUpTypeAndRelativeConsultationId(CommonStatus status, PatientFollowUpType followUpType, Long relativeConsultationId);

}

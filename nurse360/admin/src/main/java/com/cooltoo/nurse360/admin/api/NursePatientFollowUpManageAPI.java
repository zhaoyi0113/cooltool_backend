package com.cooltoo.nurse360.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.service.NursePatientFollowUpRecordService;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/8.
 */
@Path("/admin/nurse/follow-up/patient")
public class NursePatientFollowUpManageAPI {

    @Autowired private NursePatientFollowUpService patientFollowUpService;
    @Autowired private NursePatientFollowUpRecordService patientFollowUpRecordService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countPatientFollowUp(@Context HttpServletRequest request,
                                         @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                         @QueryParam("department_id") @DefaultValue("-1") int departmentId,
                                         @QueryParam("nurse_id") @DefaultValue("-1") long nurseId,
                                         @QueryParam("user_id") @DefaultValue("-1") long userId,
                                         @QueryParam("patient_id") @DefaultValue("-1") long patientId
    ) {
        List<CommonStatus> notDeleted = CommonStatus.getAll();
        notDeleted.remove(CommonStatus.DELETED);
        long followUpCount = patientFollowUpService.countPatientFollowUp(
                0>hospitalId ? null : hospitalId,
                0>departmentId ? null : departmentId,
                0>nurseId ? null : nurseId,
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                notDeleted
                );
        return Response.ok(followUpCount).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientFollowUp(@Context HttpServletRequest request,
                                       @QueryParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                       @QueryParam("department_id") @DefaultValue("-1") int departmentId,
                                       @QueryParam("nurse_id") @DefaultValue("-1") long nurseId,
                                       @QueryParam("user_id") @DefaultValue("-1") long userId,
                                       @QueryParam("patient_id") @DefaultValue("-1") long patientId,
                                       @QueryParam("index") @DefaultValue("0") int pageIndex,
                                       @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> notDeleted = CommonStatus.getAll();
        notDeleted.remove(CommonStatus.DELETED);
        List<NursePatientFollowUpBean> followUps = patientFollowUpService.getPatientFollowUp(
                0>hospitalId ? null : hospitalId,
                0>departmentId ? null : departmentId,
                0>nurseId ? null : nurseId,
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                pageIndex, sizePerPage,
                notDeleted
        );
        return Response.ok(followUps).build();
    }

    @Path("/{follow_up_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientFollowUp(@Context HttpServletRequest request,
                                       @PathParam("follow_up_id") @DefaultValue("-1") long followUpId
    ) {
        NursePatientFollowUpBean followUp = patientFollowUpService.getPatientFollowUp(followUpId);
        List<NursePatientFollowUpRecordBean> records = patientFollowUpRecordService.getPatientFollowUpRecordByFollowUpIds(null, null, null, null, Arrays.asList(new Long[]{followUpId}), ConsultationTalkStatus.NONE, 0, 0, true);
        followUp.setProperties(NursePatientFollowUpBean.RECORDS, records);
        return Response.ok(followUp).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPatientFollowUpStatusToDelete(@Context HttpServletRequest request,
                                                     @FormParam("follow_up_id") @DefaultValue("0") long followUpId
    ) {
        List<Long> updateIds = patientFollowUpService.setDeleteStatusPatientFollowUpByIds(null, Arrays.asList(new Long[]{followUpId}));
        return Response.ok(updateIds).build();
    }

    //==========================================================================
    //              follow-up record service
    //==========================================================================

    @Path("/record/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countPatientFollowUpRecords(@Context HttpServletRequest request,
                                                @QueryParam("follow_up_id") @DefaultValue("-1") long followUpId,
                                                @QueryParam("follow_up_type") @DefaultValue("") String followUpType, /* Consultation(提问), Questionnaire(发问卷) */
                                                @QueryParam("patient_replied") @DefaultValue("") String patientReplied, /* YES/NO */
                                                @QueryParam("nurse_read") @DefaultValue("") String nurseRead /* YES/NO */
    ) {
        long count = patientFollowUpRecordService.countPatientFollowUpRecordByFollowUpIds(
                null,
                PatientFollowUpType.parseString(followUpType),
                YesNoEnum.parseString(patientReplied),
                YesNoEnum.parseString(nurseRead),
                Arrays.asList(new Long[]{followUpId}),
                ConsultationTalkStatus.NONE);
        return Response.ok(count).build();
    }

    @Path("/record")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientFollowUpRecords(@Context HttpServletRequest request,
                                              @QueryParam("follow_up_id") @DefaultValue("-1") long followUpId,
                                              @QueryParam("follow_up_type") @DefaultValue("") String followUpType, /* Consultation(提问), Questionnaire(发问卷) */
                                              @QueryParam("patient_replied") @DefaultValue("") String patientReplied, /* YES/NO */
                                              @QueryParam("nurse_read") @DefaultValue("") String nurseRead, /* YES/NO */
                                              @QueryParam("index") @DefaultValue("0") int pageIndex,
                                              @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<NursePatientFollowUpRecordBean> followUpRecord = patientFollowUpRecordService.getPatientFollowUpRecordByFollowUpIds(
                null,
                PatientFollowUpType.parseString(followUpType),
                YesNoEnum.parseString(patientReplied),
                YesNoEnum.parseString(nurseRead),
                Arrays.asList(new Long[]{followUpId}),
                ConsultationTalkStatus.NONE,
                pageIndex, sizePerPage, false);
        return Response.ok(followUpRecord).build();
    }


    @Path("/record/{record_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientFollowUpRecord(@Context HttpServletRequest request,
                                             @QueryParam("follow_up_record_id") @DefaultValue("0") long followUpRecordId
    ) {
        NursePatientFollowUpRecordBean followUpRecord = patientFollowUpRecordService.getPatientFollowUpRecordById(followUpRecordId);
        return Response.ok(followUpRecord).build();
    }

    @Path("/record")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response setPatientFollowUpRecordStatusToDelete(@Context HttpServletRequest request,
                                                           @FormParam("follow_up_record_id") @DefaultValue("0") long followUpRecordId
    ) {
        List<Long> updateIds = patientFollowUpRecordService.setDeleteStatusPatientFollowUpRecordByIds(Arrays.asList(new Long[]{followUpRecordId}));
        return Response.ok(updateIds).build();
    }
}

package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.CaseBean;
import com.cooltoo.go2nurse.beans.CasebookBean;
import com.cooltoo.go2nurse.service.CasebookService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/8/28.
 */
@Path("/nurse/casebook")
public class CasebookAPI {

    private static final Logger logger = LoggerFactory.getLogger(CasebookAPI.class);

    @Autowired private CasebookService casebookService;
    @Autowired private NurseServiceForNurse360 nurseService;

    //=================================================================================================================
    //                                           casebook service
    //=================================================================================================================
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCasebook(@Context HttpServletRequest request,
                                @QueryParam("user_id") @DefaultValue("") String strUserId,
                                @QueryParam("patient_id") @DefaultValue("") String strPatientId,
                                @QueryParam("content") @DefaultValue("") String content,
                                @QueryParam("index") @DefaultValue("0") int pageIndex,
                                @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Long userId = !VerifyUtil.isIds(strUserId) ? null : VerifyUtil.parseLongIds(strUserId).get(0);
        Long patientId = !VerifyUtil.isIds(strPatientId) ? null : VerifyUtil.parseLongIds(strPatientId).get(0);

        // get department public casebooks and self
        NurseBean nurse = nurseService.getNurseById(nurseId);
        Integer hospitalId = null;
        Integer departmentId = null;
        NurseHospitalRelationBean hospitalRelation = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);
        if (null!=hospitalRelation) {
            if (0!=hospitalRelation.getHospitalId()) {
                hospitalId = hospitalRelation.getHospitalId();
            }
            if (0!=hospitalRelation.getDepartmentId()) {
                departmentId = hospitalRelation.getDepartmentId();
            }
        }

        List<CasebookBean> casebook = casebookService.getUserCasebook(null, userId, patientId, content, hospitalId, departmentId, nurseId/* self */, null, pageIndex, sizePerPage);
        return Response.ok(casebook).build();
    }

    @Path("/{casebook_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCasebookWithCases(@Context HttpServletRequest request,
                                         @PathParam("casebook_id") @DefaultValue("0") long casebookId
    ) {
        CasebookBean casebook = casebookService.getCasebookWithCases(casebookId);
        return Response.ok(casebook).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteCasebook(@Context HttpServletRequest request,
                                   @FormParam("casebook_id") @DefaultValue("0") long casebookId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> allIds = new ArrayList<>();
        allIds.add(casebookId);
        allIds = casebookService.deleteCasebookByIds(userId, allIds);
        return Response.ok(allIds).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addCasebook(@Context HttpServletRequest request,
                                @FormParam("user_id") @DefaultValue("0") long userId,
                                @FormParam("patient_id") @DefaultValue("0") long patientId,
                                @FormParam("description") @DefaultValue("") String description,
                                @FormParam("name") @DefaultValue("") String name,
                                @FormParam("hidden") @DefaultValue("") String hidden /* YES, NO */
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long casebookId = casebookService.addCasebook(0, 0, nurseId, userId, patientId, description, name, YesNoEnum.parseString(hidden));
        Map<String, Long> retValue = new HashMap<>();
        retValue.put("id", casebookId);
        return Response.ok(retValue).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response editCasebook(@Context HttpServletRequest request,
                                 @FormParam("casebook_id") @DefaultValue("0") long casebookId,
                                 @FormParam("name") @DefaultValue("") String name,
                                 @FormParam("description") @DefaultValue("") String description,
                                 @FormParam("hidden") @DefaultValue("") String hidden /* YES, NO */
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        CasebookBean casebook = casebookService.updateCasebook(nurseId, casebookId, name, description, YesNoEnum.parseString(hidden));
        return Response.ok(casebook).build();
    }


    //=================================================================================================================
    //                                           case service
    //=================================================================================================================
    @Path("/case")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCase(@Context HttpServletRequest request,
                            @QueryParam("case_id") @DefaultValue("0") long caseId
    ) {
        CaseBean _case = casebookService.getCaseById(caseId);
        return Response.ok(_case).build();
    }

    @Path("/case")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteCase(@Context HttpServletRequest request,
                               @FormParam("case_id") @DefaultValue("0") long caseId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> allIds = casebookService.deleteCase(nurseId, caseId);
        return Response.ok(allIds).build();
    }

    @Path("/case")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addCase(@Context HttpServletRequest request,
                            @FormParam("casebook_id") @DefaultValue("0") long casebookId,
                            @FormParam("case_record") @DefaultValue("") String caseRecord
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long caseId = casebookService.addCase(casebookId, nurseId, caseRecord);
        Map<String, Long> returnValue = new HashMap<>();
        returnValue.put("case_id", caseId);
        return Response.ok(returnValue).build();
    }

    @Path("/case")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response updateCase(@Context HttpServletRequest request,
                            @FormParam("case_id") @DefaultValue("0") long caseId,
                            @FormParam("case_record") @DefaultValue("") String caseRecord
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        caseId = casebookService.updateCase(nurseId, caseId, caseRecord);
        Map<String, Long> returnValue = new HashMap<>();
        returnValue.put("case_id", caseId);
        return Response.ok(returnValue).build();
    }

    @Path("/case/add_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addCaseImage(@Context HttpServletRequest request,
                                 @FormDataParam("casebook_id") @DefaultValue("0") long casebookId,
                                 @FormDataParam("case_id") @DefaultValue("0") long caseId,
                                 @FormDataParam("image_name") @DefaultValue("") String imageName,
                                 @FormDataParam("image") InputStream image
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Map<String, String> imageIdToUrl = casebookService.addCaseImage(nurseId, casebookId, caseId, imageName, image);
        return Response.ok(imageIdToUrl).build();
    }

    @Path("/case/image")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addCaseImage(@Context HttpServletRequest request,
                                 @FormParam("casebook_id") @DefaultValue("0") long casebookId,
                                 @FormParam("case_id") @DefaultValue("0") long caseId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        casebookService.deleteCaseImage(nurseId, casebookId, caseId);
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("deleted", Boolean.TRUE);
        return Response.ok(retVal).build();
    }

}

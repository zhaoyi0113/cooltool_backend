package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CasebookBean;
import com.cooltoo.go2nurse.service.CasebookService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
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

    //=================================================================================================================
    //                                           casebook service
    //=================================================================================================================
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getCasebook(@Context HttpServletRequest request,
                                @QueryParam("user_id") @DefaultValue("") String strUserId,
                                @QueryParam("content") @DefaultValue("") String content,
                                @QueryParam("index") @DefaultValue("0") int pageIndex,
                                @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Long userId = !VerifyUtil.isIds(strUserId) ? null : VerifyUtil.parseLongIds(strUserId).get(0);
        List<CasebookBean> casebook = casebookService.getUserCasebook(userId, null, content, pageIndex, sizePerPage);
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
                                @FormParam("name") @DefaultValue("") String name
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long casebookId = casebookService.addCasebook(nurseId, userId, patientId, description, name);
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
                                 @FormParam("description") @DefaultValue("") String description
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        CasebookBean casebook = casebookService.updateCasebook(nurseId, casebookId, name, description);
        return Response.ok(casebook).build();
    }


    //=================================================================================================================
    //                                           case service
    //=================================================================================================================

    @Path("/case")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteCase(@Context HttpServletRequest request,
                               @FormParam("case_id") @DefaultValue("0") long caseId
    ) {
        List<Long> allIds = new ArrayList<>();
        allIds.add(caseId);
        allIds = casebookService.deleteCase(allIds);
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
        caseId = casebookService.updateCase(caseId, caseRecord);
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
}

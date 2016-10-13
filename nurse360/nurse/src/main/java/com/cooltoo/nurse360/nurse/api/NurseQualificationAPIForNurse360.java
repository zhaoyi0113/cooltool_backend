package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseQualificationBean;
import com.cooltoo.beans.NurseQualificationFileBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.NurseQualificationService;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import java.util.List;

/**
 * Created by zhaolisong on 16/4/1.
 */
@Path("/nurse/qualification")
public class NurseQualificationAPIForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationAPIForNurse360.class.getName());

    @Autowired private NurseQualificationService service;
    @Autowired private Nurse360Utility utility;

    @Path("/type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getWorkFileType() {
        return Response.ok(WorkFileType.getAllValues()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getAllQualification(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseQualificationBean> qualifications = service.getAllNurseQualifications(userId, utility.getHttpPrefixForNurseGo());
        return Response.ok(qualifications).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addQualification(@Context HttpServletRequest request,
                                     @FormDataParam("name")      String name,
                                     @FormDataParam("type")      String type,
                                     @FormDataParam("file_name") String fileName,
                                     @FormDataParam("file") InputStream fileInputStream,
                                     @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("add qualification name={} type={}, file_name={}, fileDisp={}, file={}", name, type, fileName, disposition, Boolean.valueOf(fileInputStream!=null));
        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = disposition.getFileName();
        }
        String qualificationPath = service.addWorkFile(userId, name, type, fileName, fileInputStream);
        logger.info("add qualification work file : " + qualificationPath);
        if (null == qualificationPath) {
            return Response.ok().build();
        }
        return Response.ok(qualificationPath).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response resetQualification(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseQualificationBean> qualifications = service.deleteNurseQualificationByUserId(userId);
        logger.info("user {} delete qualifications is {}." , userId, qualifications);
        return Response.ok().build();
    }

    @Path("/work_file")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteQualificationFile(@Context HttpServletRequest request,
                                            @FormParam("qualification_file_id") long fileId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user {} delete qualification file", userId);
        NurseQualificationFileBean qualificationFile = service.deleteFileByFileId(fileId);
        logger.info("user {} delete qualification file is {}." , userId, qualificationFile);
        return Response.ok().build();
    }
}

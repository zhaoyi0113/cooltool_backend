package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseQualificationService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/4/1.
 */
@Path("/nurse/qualification")
public class NurseQualificationAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationAPI.class.getName());

    @Autowired
    private NurseQualificationService service;

    @GET
    @Path("/type")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getWorkFileType() {
        return Response.ok(WorkFileType.getAllValues()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getAllQualification(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseQualificationBean> qualifications = service.getAllNurseQualifications(userId);
        return Response.ok(qualifications).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addQualification(
            @Context HttpServletRequest        request,
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
        NurseQualificationBean one = service.addWorkFile(userId, name, type, fileName, fileInputStream);
        logger.info("add qualification work file : " + one);
        if (null == one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }
}

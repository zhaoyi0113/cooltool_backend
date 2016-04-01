package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.services.NurseQualificationService;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.DenyAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by hp on 2016/3/24.
 */
@Path("/admin/nurse/qualification")
public class NurseQualificationManageAPI {

    private static final Logger logger = Logger.getLogger(NurseQualificationManageAPI.class.getName());

    @Autowired
    private NurseQualificationService qualificationService;

    @Path("/{nurse_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllNurseQualification(
            @Context HttpServletRequest request,
            @PathParam("nurse_id") long nurseId
    ) {
        List<NurseQualificationBean> qualifications = qualificationService.getAllNurseQualifications(nurseId);
        return Response.ok(qualifications).build();
    }

    @POST
    @Path("/approve")
    @Produces(MediaType.APPLICATION_JSON)
//    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response approveNurseQualification(
            @Context HttpServletRequest request,
            @FormParam("id") long id
    ) {
        NurseQualificationBean bean = qualificationService.updateNurseQualification(id, null, null, null, null, VetStatus.COMPLETED, null, null);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/deny")
    @Produces(MediaType.APPLICATION_JSON)
//    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response denyNurseQualification(
            @Context HttpServletRequest request,
            @FormParam("id") long id,
            @FormParam("reason") String statusDescr
    ) {
        if (VerifyUtil.isStringEmpty(statusDescr)) {
            logger.severe("The deny reason is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseQualificationBean bean = qualificationService.updateNurseQualification(id, null, null, null, null, VetStatus.FAILED, statusDescr, null);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/edit")
    @Produces(MediaType.MULTIPART_FORM_DATA)
//    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateNurseQualification(
            @Context HttpServletRequest request,
            @FormDataParam("id") long id,
            @FormDataParam("expiry") String expiry,
            @FormDataParam("file_name") String fileName,
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        Date expiryTime = null;
        if (!VerifyUtil.isStringEmpty(expiry)) {
            long millisecond = NumberUtil.getTime(expiry, "yyyy-MM-dd HH:mm:ss");
            expiryTime = new Date(millisecond);
        }
        if (VerifyUtil.isStringEmpty(fileName)) {
            if (null!=disposition) {
                fileName = disposition.getFileName();
            }
        }
        NurseQualificationBean bean = qualificationService.updateNurseQualification(id, null, null, fileName, file, null, null, expiryTime);
        return Response.ok(bean).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteNurseQualification(
            @Context HttpServletRequest request,
            @FormParam("id") long id
    ) {
        NurseQualificationBean bean = qualificationService.deleteNurseQualification(id);
        return Response.ok(bean).build();
    }
}

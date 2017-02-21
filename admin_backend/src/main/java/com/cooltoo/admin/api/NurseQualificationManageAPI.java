package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.beans.NurseQualificationBean;
import com.cooltoo.beans.NurseQualificationFileBean;
import com.cooltoo.services.NurseQualificationService;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hp on 2016/3/24.
 */
@Path("/admin/nurse/qualification")
public class NurseQualificationManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationManageAPI.class.getName());

    @Autowired
    private NurseQualificationService qualificationService;

    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllQualificationCount(@Context HttpServletRequest request,
                                             @PathParam("status") String status
    ) {
        logger.info("get all qualification count");
        long count = qualificationService.getAllQualificationCount(status);
        return Response.ok(count).build();
    }

    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllQualification(@Context HttpServletRequest request,
                                        @PathParam("status") String status,
                                        @PathParam("index")  @DefaultValue("0")  int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get qualification by status {} at page {} numberOfPage {}", status, index, number);
        List<NurseQualificationBean> qualifications = qualificationService.getAllQualifications(status, index, number, "");
        logger.info("get qualification by status {} at page {} numberOfPage {}, count={}", status, index, number, qualifications.size());
        return Response.ok(qualifications).build();
    }

    @Path("/{nurse_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllNurseQualification(@Context HttpServletRequest request,
                                             @PathParam("nurse_id") long nurseId
    ) {
        List<NurseQualificationBean> qualifications = qualificationService.getAllNurseQualifications(nurseId, "");
        return Response.ok(qualifications).build();
    }

    @POST
    @Path("/approve")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response approveNurseQualification(@Context HttpServletRequest request,
                                              @FormParam("id") long qualificationId
    ) {
        NurseQualificationBean bean = qualificationService.updateQualification(qualificationId, null, VetStatus.COMPLETED, null);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/deny")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response denyNurseQualification(@Context HttpServletRequest request,
                                           @FormParam("id") long qualificationId,
                                           @FormParam("reason") String statusDescr
) {
        if (VerifyUtil.isStringEmpty(statusDescr)) {
            logger.info("The deny reason is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseQualificationBean bean = qualificationService.updateQualification(qualificationId, null, VetStatus.FAILED, statusDescr);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/edit")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateNurseQualification(@Context HttpServletRequest request,
                                             @FormDataParam("qualification_file_id") long fileId,
                                             @FormDataParam("type") String workfileType,
                                             @FormDataParam("expiry") String expiry,
                                             @FormDataParam("file_name") String fileName,
                                             @FormDataParam("file") InputStream file,
                                             @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        Date expiryTime = null;
        if (!VerifyUtil.isStringEmpty(expiry)) {
            Long millisecond = NumberUtil.getTime(expiry, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
            expiryTime = null==millisecond ? null : new Date(millisecond);
        }
        if (VerifyUtil.isStringEmpty(fileName)) {
            if (null!=disposition) {
                fileName = disposition.getFileName();
            }
        }
        NurseQualificationFileBean qualificationFile = qualificationService.updateQualificationFile(fileId, workfileType, fileName, file, expiryTime, "");
        return Response.ok(qualificationFile).build();
    }

    @Path("/work_file")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteNurseQualification(@Context HttpServletRequest request,
                                             @FormParam("qualification_file_id") long fileId
    ) {
        NurseQualificationFileBean bean = qualificationService.deleteFileByFileId(fileId);
        return Response.ok(bean).build();
    }

//    @DELETE
//    @Produces(MediaType.APPLICATION_JSON)
//    @AdminUserLoginAuthentication(requireUserLogin = true)
//    public Response deleteNurseQualification(@Context HttpServletRequest request,
//                                             @FormParam("id") long qualificationId
//    ) {
//        NurseQualificationBean bean = qualificationService.deleteNurseQualification(qualificationId);
//        return Response.ok(bean).build();
//    }
}

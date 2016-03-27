package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.services.NurseQualificationService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;
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
import java.util.List;

/**
 * Created by hp on 2016/3/24.
 */
@Path("/admin/nurse/qualification")
public class NurseQualificationAPI {

    @Autowired
    private NurseQualificationService qualificationService;

    @Path("/{nurse_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllNurseQualification(
            @Context HttpServletRequest request,
            @PathParam("nurse_id") long nurseId
    ) {
        List<NurseQualificationBean> qualifications = qualificationService.getAllNurseQualifications(nurseId);
        return Response.ok(qualifications).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateNurseQualification(
            @Context HttpServletRequest request,
            @FormParam("id") long id,
            @FormParam("name") String name,
            @FormParam("file_type") String fileType
    ) {
        WorkFileType workFileType = WorkFileType.parseString(fileType);
        NurseQualificationBean bean = qualificationService.updateNurseQualification(id, name, workFileType, null, null, null);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/approve")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response approveNurseQualification(
            @Context HttpServletRequest request,
            @FormParam("id") long id
    ) {
        NurseQualificationBean bean = qualificationService.updateNurseQualification(id, null, null, null, null, VetStatus.COMPLETED);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/deny")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response denyNurseQualification(
            @Context HttpServletRequest request,
            @FormParam("id") long id
    ) {
        NurseQualificationBean bean = qualificationService.updateNurseQualification(id, null, null, null, null, VetStatus.FAILED);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/edit/id_file")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateNurseIdentificationFile(
            @Context HttpServletRequest request,
            @FormDataParam("id") long id,
            @FormDataParam("file_name") String fileName,
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = disposition.getFileName();
        }
        NurseQualificationBean bean = qualificationService.updateNurseIdentificationFile(id, null, fileName, file, null);
        return Response.ok(bean).build();
    }


    @POST
    @Path("/edit/work_file")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateNurseWorkFile(
            @Context HttpServletRequest request,
            @FormDataParam("id") long id,
            @FormDataParam("file_name") String fileName,
            @FormDataParam("file") InputStream file,
            @FormDataParam("file") FormDataContentDisposition disposition
    ) {
        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = disposition.getFileName();
        }
        NurseQualificationBean bean = qualificationService.updateNurseWorkFile(id, null, fileName, file, null);
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

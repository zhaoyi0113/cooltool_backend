package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.beans.DoctorOrderBean;
import com.cooltoo.go2nurse.service.DoctorOrderService;
import com.cooltoo.go2nurse.service.DoctorService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/7/25.
 */
@Path("/admin/doctor")
public class DoctorManageAPI {

    @Autowired private DoctorService doctorService;
    @Autowired private DoctorOrderService doctorOrderService;
    @Autowired private Go2NurseUtility utility;

    @Path("/doctor")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDoctorById(@Context HttpServletRequest request,
                                  @QueryParam("doctor_id") @DefaultValue("0") long doctorId
    ) {
        DoctorBean doctor = doctorService.getDoctorById(doctorId, utility.getHttpPrefix());
        List<DoctorOrderBean> orders = doctorOrderService.getOrderByDoctorId(doctorId);
        doctor.setProperties(DoctorBean.ORDER, orders);
        return Response.ok(doctor).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countByStatuses(@Context HttpServletRequest request) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        statuses.add(CommonStatus.DISABLED);
        long doctorsCount = doctorService.countDoctor(statuses);
        return Response.ok(doctorsCount).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countByStatuses(@Context HttpServletRequest request,
                                    @QueryParam("index") @DefaultValue("0") int pageIndex,
                                    @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        statuses.add(CommonStatus.DISABLED);
        List<DoctorBean> doctors = doctorService.getDoctor(statuses, pageIndex, sizePerPage);
        return Response.ok(doctors).build();
    }

    @Path("/count/by_hospital_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countByHospitalDepartment(@Context HttpServletRequest request,
                                              @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                              @QueryParam("department_id") @DefaultValue("") String strDepartmentId
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        statuses.add(CommonStatus.DISABLED);
        Integer hospitalId = !VerifyUtil.isIds(strHospitalId) ? null : VerifyUtil.parseIntIds(strHospitalId).get(0);
        Integer departmentId = !VerifyUtil.isIds(strDepartmentId) ? null : VerifyUtil.parseIntIds(strDepartmentId).get(0);
        long doctorsCount = doctorService.countDoctor(hospitalId, departmentId, statuses, false);
        return Response.ok(doctorsCount).build();
    }

    @Path("/by_hospital_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByHospitalDepartment(@Context HttpServletRequest request,
                                              @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                              @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                              @QueryParam("index") @DefaultValue("0") int pageIndex,
                                              @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statuses = new ArrayList<>();
        statuses.add(CommonStatus.ENABLED);
        statuses.add(CommonStatus.DISABLED);
        Integer hospitalId = !VerifyUtil.isIds(strHospitalId) ? null : VerifyUtil.parseIntIds(strHospitalId).get(0);
        Integer departmentId = !VerifyUtil.isIds(strDepartmentId) ? null : VerifyUtil.parseIntIds(strDepartmentId).get(0);
        List<DoctorBean> doctors = doctorService.getDoctor(hospitalId, departmentId, statuses, false, pageIndex, sizePerPage);
        return Response.ok(doctors).build();
    }

    //==============================================================
    //                         adding
    //==============================================================

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDoctor(@Context HttpServletRequest request,
                              @FormParam("name") @DefaultValue("") String name,
                              @FormParam("post") @DefaultValue("") String post,
                              @FormParam("job_title") @DefaultValue("") String jobTitle,
                              @FormParam("be_good_at") @DefaultValue("") String beGoodAt,
                              @FormParam("grade") @DefaultValue("0") int grade,
                              @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                              @FormParam("department_id") @DefaultValue("0") int departmentId
    ) {
        DoctorBean doctor = doctorService.addDoctor(name, post, jobTitle, beGoodAt, departmentId, grade);
        if (hospitalId>0 && departmentId>0) {
            doctorOrderService.setDoctorOrder(-1, doctor.getId(), -1, doctor.getHospitalId(), -1, doctor.getDepartmentId(), -1);
        }
        return Response.ok(doctor).build();
    }

    //==============================================================
    //                         editing
    //==============================================================

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response editDoctor(@Context HttpServletRequest request,
                               @FormParam("doctor_id") @DefaultValue("0") long doctorId,
                               @FormParam("name") @DefaultValue("") String name,
                               @FormParam("post") @DefaultValue("") String post,
                               @FormParam("job_title") @DefaultValue("") String jobTitle,
                               @FormParam("be_good_at") @DefaultValue("") String beGoodAt,
                               @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                               @FormParam("department_id") @DefaultValue("0") int departmentId,
                               @FormParam("status") @DefaultValue("") String status,
                               @FormParam("grade") @DefaultValue("0") int grade
    ) {
        DoctorBean doctor = doctorService.updateDoctor(doctorId, name, post, jobTitle, beGoodAt, departmentId, status, grade);
        if (hospitalId>0 && departmentId>0) {
            List<DoctorOrderBean> orders = doctorOrderService.getOrderByDoctorId(doctorId);
            if (VerifyUtil.isListEmpty(orders)) {
                doctorOrderService.setDoctorOrder(-1, doctorId, -1, hospitalId, -1, departmentId, -1);
            }
            else {
                doctorOrderService.setDoctorOrder(orders.get(0).getId(), doctorId, -1, hospitalId, -1, departmentId, -1);
            }
        }
        return Response.ok(doctor).build();
    }

    @Path("/image")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response editDoctorHeaderImage(@Context HttpServletRequest request,
                                          @FormDataParam("doctor_id") @DefaultValue("0") long doctorId,
                                          @FormDataParam("image_name") @DefaultValue("") String imageName,
                                          @FormDataParam("image") InputStream image,
                                          @FormDataParam("image")FormDataContentDisposition disposition
    ) {
        DoctorBean doctor = doctorService.updateDoctorHeadImage(doctorId, imageName, image, true);
        return Response.ok(doctor).build();
    }

    @Path("/full_length_image")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response editDoctorFullLengthImage(@Context HttpServletRequest request,
                                              @FormDataParam("doctor_id") @DefaultValue("0") long doctorId,
                                              @FormDataParam("image_name") @DefaultValue("") String imageName,
                                              @FormDataParam("image") InputStream image,
                                              @FormDataParam("image")FormDataContentDisposition disposition
    ) {
        DoctorBean doctor = doctorService.updateDoctorHeadImage(doctorId, imageName, image, false);
        return Response.ok(doctor).build();
    }

    @Path("/introduction")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response editDoctorFullLengthImage(@Context HttpServletRequest request,
                                              @FormParam("doctor_id") @DefaultValue("0") long doctorId,
                                              @FormParam("introduction") @DefaultValue("") String introduction
    ) {
        DoctorBean doctor = doctorService.setDoctorIntroductionWithHtml(doctorId, introduction);
        return Response.ok(doctor).build();
    }
}

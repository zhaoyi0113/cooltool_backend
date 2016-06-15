package com.cooltoo.backend.api;

import com.cooltoo.beans.EmploymentInformationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.services.EmploymentInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/4/21.
 */
@Path("/employment_information")
public class EmploymentInformationAPI {

    private static final Logger logger = LoggerFactory.getLogger(EmploymentInformationAPI.class.getName());

    @Autowired private EmploymentInformationService employmentService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmploymentInfoByStatus(@Context HttpServletRequest request,
                                              @QueryParam("employment_type") @DefaultValue("ALL") String employmentType,
                                              @QueryParam("index")  @DefaultValue("0") int index,
                                              @QueryParam("number") @DefaultValue("10") int number
    ) {
        String status = CommonStatus.ENABLED.name();
        logger.info(" get employment information by status={} at page={}, {}/page", status, index, number);
        List<EmploymentInformationBean> employmentInfo = employmentService.getEmploymentInfoByStatus(status, employmentType, index, number);
        logger.info("count = {}", employmentInfo.size());
        return Response.ok(employmentInfo).build();
    }
}

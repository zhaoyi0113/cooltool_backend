package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.CathartProfilePhotoBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.CathartProfilePhotoService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
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
 * Created by hp on 2016/4/22.
 */
@Path("/speak/cathart_profile")
public class CathartProfilePhotoAPI {

    private static final Logger logger = LoggerFactory.getLogger(CathartProfilePhotoAPI.class.getName());

    @Autowired
    private CathartProfilePhotoService cathartPhotoService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getByStatus(@Context HttpServletRequest request
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        String status = CommonStatus.ENABLED.name();
        logger.info("nurse {} get enable cathart profile photo", userId, status);
        List<CathartProfilePhotoBean> beans = cathartPhotoService.getAllByStatus(status);
        logger.info("count is {}", beans.size());
        return Response.ok(beans).build();
    }
}

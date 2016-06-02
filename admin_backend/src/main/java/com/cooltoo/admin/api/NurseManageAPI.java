package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseRelationshipBean;
import com.cooltoo.backend.services.NurseRelationshipService;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.UserAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/12.
 */
@Path("/admin/nurse")
public class NurseManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseManageAPI.class);

    @Autowired private NurseService nurseService;
    @Autowired private NurseRelationshipService nurseRelationshipService;

    @Path("/{nurse_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @PathParam("nurse_id") @DefaultValue("0") long nurseId) {
        logger.info("get nurse information by nurse id={}", nurseId);
        NurseBean nurse = nurseService.getNurse(nurseId);
        logger.info("nurse is {}", nurse);
        return Response.ok(nurse).build();
    }

    @Path("/authority_type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAuthorityType(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get authority type", userId);
        List<String> allType = UserAuthority.getUserAuthority();
        logger.info("user {} get authority type {}", userId, allType);
        return Response.ok(allType).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countNurseByAuthorityAndName(@Context HttpServletRequest request,
                                                 @QueryParam("authority") @DefaultValue("") String authority,
                                                 @QueryParam("fuzzy_name") @DefaultValue("") String fuzzyName
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get nurse record count by authority {} fuzzyName={}", userId, authority, fuzzyName);
        long count = nurseService.countByAuthorityAndFuzzyName(authority, fuzzyName);
        logger.info("count={}", count);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseByAuthorityAndName(@Context HttpServletRequest request,
                                               @QueryParam("authority") @DefaultValue("") String strAuthority,
                                               @QueryParam("fuzzy_name") @DefaultValue("") String fuzzyName,
                                               @QueryParam("index")  @DefaultValue("0")  int index,
                                               @QueryParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get nurse record count by authority {} fuzzyName{} at page {} with {} record/page",
                userId, strAuthority, fuzzyName, index, number);
        List<NurseBean> nurses = nurseService.getAllByAuthorityAndFuzzyName(strAuthority, fuzzyName, index, number);
        logger.info("count={}", userId, strAuthority, fuzzyName, index, number, nurses.size());
        return Response.ok(nurses).build();
    }

    @Path("/update/authority")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateAuthority(@Context HttpServletRequest request,
                                    @FormParam("nurse_ids") String nurseIds,
                                    @FormParam("authority") String authority
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update nurse {} 's authority property to {}", userId, nurseIds, authority);
        List<NurseBean> nurses = nurseService.updateAuthority(nurseIds, authority);
        logger.info("user {} update nurse {} 's authority property to {}, count={}", userId, nurseIds, authority, nurses.size());
        return Response.ok(nurses).build();
    }

    //============================================================================================
    //                    用户关系管理
    //============================================================================================
    // 所有条件是 与 的关系
    @Path("/relation/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countNurseRelation(@Context HttpServletRequest request,
                                  @QueryParam("user_id") @DefaultValue("0") long userId,
                                  @QueryParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                  @QueryParam("relation_type") @DefaultValue("") String relationType,
                                  @QueryParam("status") @DefaultValue("") String status
    ) {
        long adminUserId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin user {} count nurse relation by condition userId={} relativeUserId={}, relationType={} status={}",
                adminUserId, userId, relativeUserId, relationType, status
        );
        long count = nurseRelationshipService.countCondition(userId, relativeUserId, relationType, status);
        return Response.ok(count).build();
    }

    @Path("/relation")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseRelation(@Context HttpServletRequest request,
                                     @QueryParam("user_id") @DefaultValue("0") long userId,
                                     @QueryParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                     @QueryParam("relation_type") @DefaultValue("") String relationType,
                                     @QueryParam("status") @DefaultValue("") String status,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long adminUserId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("admin user {} count nurse relation by condition userId={} relativeUserId={}, relationType={} status={}",
                adminUserId, userId, relativeUserId, relationType, status
        );
        List<NurseRelationshipBean> relation = nurseRelationshipService.getRelation(userId, relativeUserId, relationType, status, pageIndex, sizePerPage);
        return Response.ok(relation).build();
    }

    @Path("/relation/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateRelationship(@Context HttpServletRequest request,
                                       @FormParam("user_id") @DefaultValue("0") long userId,
                                       @FormParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                       @FormParam("relation_type") @DefaultValue("") String relationType,
                                       @FormParam("status") @DefaultValue("0") int status
    ) {
        CommonStatus commonStatus = CommonStatus.parseInt(status);
        String strStatus = null==commonStatus ? "" : commonStatus.name();
        NurseRelationshipBean relationship = nurseRelationshipService.updateRelationStatus(userId, relativeUserId, relationType, strStatus);
        return Response.ok(relationship).build();
    }

    @Path("/relation/edit_by_id")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateRelationship(@Context HttpServletRequest request,
                                       @FormParam("relation_id") @DefaultValue("0") long relationId,
                                       @FormParam("status") @DefaultValue("0") int status
    ) {
        CommonStatus commonStatus = CommonStatus.parseInt(status);
        String strStatus = null==commonStatus ? "" : commonStatus.name();
        NurseRelationshipBean relationship = nurseRelationshipService.updateRelationStatus(relationId, strStatus);
        return Response.ok(relationship).build();
    }
}

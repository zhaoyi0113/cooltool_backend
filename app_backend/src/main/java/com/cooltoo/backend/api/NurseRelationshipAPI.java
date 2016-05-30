package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseRelationshipBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseRelationshipService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/5/30.
 */
@Path("/nurse/relationship")
public class NurseRelationshipAPI {

    @Autowired private NurseRelationshipService relationshipService;

    @Path("/type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRelationType() {
        return Response.ok(RelationshipType.getAllType()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getRelationship(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseRelationshipBean> relationships = relationshipService.getRelation(true, userId, 0, "", CommonStatus.ENABLED.name());
        return Response.ok(relationships).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response setRelationship(@Context HttpServletRequest request,
                                    @FormParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                    @FormParam("relation_type") @DefaultValue("") String relationType
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseRelationshipBean relationship = relationshipService.addRelation(userId, relativeUserId, relationType);
        return Response.ok(relationship).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response updateRelationship(@Context HttpServletRequest request,
                                       @FormParam("relative_user_id") @DefaultValue("0") long relativeUserId,
                                       @FormParam("relation_type") @DefaultValue("") String relationType,
                                       @FormParam("status") @DefaultValue("0") int status
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        CommonStatus commonStatus = CommonStatus.parseInt(status);
        String strStatus = null==commonStatus ? "" : commonStatus.name();
        NurseRelationshipBean relationship = relationshipService.updateRelationStatus(userId, relativeUserId, relationType, strStatus);
        return Response.ok(relationship).build();
    }
}

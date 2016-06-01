package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.beans.SensitiveWordBean;
import com.cooltoo.services.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/5/31.
 */
@Path("/admin/sensitive_word")
public class SensitiveWordManageAPI {

    @Autowired private SensitiveWordService wordService;

    @Path("/type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin =  true)
    public Response getAllType(@Context HttpServletRequest request) {
        List<String> allType = wordService.getAllType();
        return Response.ok(allType).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countWords(@Context HttpServletRequest request,
                               @QueryParam("type") @DefaultValue("") String wordType,
                               @QueryParam("status") @DefaultValue("") String status) {
        long count = wordService.countWords(wordType, status);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getWords(@Context HttpServletRequest request,
                             @QueryParam("type") @DefaultValue("") String wordType,
                             @QueryParam("status") @DefaultValue("") String status,
                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                             @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<SensitiveWordBean> words = wordService.getWords(wordType, status, pageIndex, sizePerPage);
        return Response.ok(words).build();
    }

    @Path("/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addWord(@Context HttpServletRequest request,
                            @FormParam("word") @DefaultValue("") String word,
                            @FormParam("type") @DefaultValue("") String wordType
    ) {
        SensitiveWordBean newWord = wordService.addWord(word, wordType);
        return Response.ok(newWord).build();
    }

    @Path("/update_status")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateStatus(@Context HttpServletRequest request,
                                 @FormParam("word_id") @DefaultValue("0") int wordId,
                                 @FormParam("status") @DefaultValue("") String status
    ) {
        SensitiveWordBean newStatus = wordService.updateWord(wordId, "", status);
        return Response.ok(newStatus).build();
    }

}

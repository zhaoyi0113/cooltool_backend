package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.beans.DiagnosticEnumerationBean;
import com.cooltoo.go2nurse.service.DiagnosticPointService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/6/13.
 */
@Path("/diagnostic")
public class DiagnosticAPI {

    @Autowired private DiagnosticPointService diagnosticService;

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDiagnostic(@Context HttpServletRequest request) {
        List<DiagnosticEnumerationBean> diagnostics = diagnosticService.getAllDiagnostic();
        return Response.ok(diagnostics).build();
    }
}

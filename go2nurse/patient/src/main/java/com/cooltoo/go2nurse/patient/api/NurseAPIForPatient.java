package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/8/12.
 */
@Path("/user/nurse")
public class NurseAPIForPatient {

    @Autowired private NurseServiceForGo2Nurse nurseServiceForGo2Nurse;

    @Path("/{nurse_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @PathParam("nurse_id") @DefaultValue("0") long nurseId
    ) {
        NurseBean nurse = nurseServiceForGo2Nurse.getNurseById(nurseId);
        return Response.ok(nurse).build();
    }


    @Path("/by_condition")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @QueryParam("can_answer_nursing_question") @DefaultValue("") String canAnswerNursingQuestion,
                                 @QueryParam("index")  @DefaultValue("0")  int index,
                                 @QueryParam("number") @DefaultValue("10") int number

    ) {
        List<NurseBean> nurses = nurseServiceForGo2Nurse.getNurseByCanAnswerQuestion(canAnswerNursingQuestion, index, number);
        return Response.ok(nurses).build();
    }
}

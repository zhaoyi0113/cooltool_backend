package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.go2nurse.service.UserHospitalizedRelationService;
import com.cooltoo.go2nurse.service.UserPatientRelationService;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.util.SetUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Created by zhaolisong on 2016/11/17.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalPatientAPI {

    @Autowired private UserHospitalizedRelationService userHospitalizedService;
    @Autowired private UserService userService;
    @Autowired private UserPatientRelationService userPatientRelation;
    @Autowired private PatientService patientService;

    //=============================================================
    //            Permit ALL Role
    //=============================================================
    @RequestMapping(path = "/patient/information", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, Object> getPatient(@RequestParam(defaultValue = "0", name = "user_id")    long userId,
                                          @RequestParam(defaultValue = "0", name = "patient_id") long patientId
    ) {
        Map<String, Object> returnValue = new HashMap<>();
        UserBean user = userService.getUser(userId);
        returnValue.put("user", user);

        PatientBean patient = patientService.getOneById(patientId);
        List<Long> relativeUserIds = userPatientRelation.getUserIdByPatient(Arrays.asList(new Long[]{patientId}), CommonStatus.ENABLED.name());
        if (!VerifyUtil.isListEmpty(relativeUserIds) && relativeUserIds.contains(userId)) {
            returnValue.put("patient", patient);
        }

        return returnValue;
    }


    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================
    @RequestMapping(path = "/admin/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countPatient(@RequestParam(defaultValue = "0",       name = "hospital_id")   int hospitalId,
                             @RequestParam(defaultValue = "0",       name = "department_id") int departmentId,
                             @RequestParam(defaultValue = "enabled", name = "status")     String status /* enabled, disabled, deleted */
    ) {
        List<Long> userIds = userHospitalizedService.getUserInHospital(hospitalId, departmentId, CommonStatus.parseString(status));
        long count = userService.countUserByUserIds(userIds, UserAuthority.AGREE_ALL);
        return count;
    }

    @RequestMapping(path = "/admin/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<UserBean> getPatient(@RequestParam(defaultValue = "0",       name = "hospital_id")   int hospitalId,
                                     @RequestParam(defaultValue = "0",       name = "department_id") int departmentId,
                                     @RequestParam(defaultValue = "enabled", name = "status")     String status, /* enabled, disabled, deleted */
                                     @RequestParam(defaultValue = "0",       name = "index")  int index,
                                     @RequestParam(defaultValue = "10",      name = "number") int number
    ) {
        List<Long> userIds = userHospitalizedService.getUserInHospital(hospitalId, departmentId, CommonStatus.parseString(status));
        List<Long> pageUserIds = SetUtil.newInstance().getSetByPage(userIds, index, number, null);
        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        List<UserBean> users = userService.getUserByUserIds(pageUserIds, UserAuthority.AGREE_ALL);
        return users;
    }


    //=============================================================
    //            Authentication of MANAGER Role
    //=============================================================
    @RequestMapping(path = "/manager/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countPatientByManager() {
        return countPatientByAdmin();
    }

    @RequestMapping(path = "/manager/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<UserBean> getPatientByManger(@RequestParam(defaultValue = "0",  name = "index")  int index,
                                             @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        return getPatientByAdmin(index, number);
    }


    //=============================================================
    //            Authentication of NURSE Role
    //=============================================================
    @RequestMapping(path = "/nurse/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countPatientByNurse() {
        return countPatientByAdmin();
    }

    @RequestMapping(path = "/nurse/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<UserBean> getPatientByNurse(@RequestParam(defaultValue = "0",  name = "index")  int index,
                                            @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        return getPatientByAdmin(index, number);
    }




    //=============================================================
    //            Common Method
    //=============================================================
    private long countPatientByAdmin() {
        HospitalAdminAuthentication authentication = (HospitalAdminAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HospitalAdminBean adminBean = (HospitalAdminBean) authentication.getDetails();

        List<Long> userIds = userHospitalizedService.getUserInHospital(adminBean.getHospitalId(), adminBean.getDepartmentId(), CommonStatus.ENABLED);
        long count = userService.countUserByUserIds(userIds, UserAuthority.AGREE_ALL);
        return count;
    }

    private List<UserBean> getPatientByAdmin(int index, int number) {
        HospitalAdminAuthentication authentication = (HospitalAdminAuthentication) SecurityContextHolder.getContext().getAuthentication();
        HospitalAdminBean adminBean = (HospitalAdminBean) authentication.getDetails();

        List<Long> userIds = userHospitalizedService.getUserInHospital(adminBean.getHospitalId(), adminBean.getDepartmentId(), CommonStatus.ENABLED);
        List<Long> pageUserIds = SetUtil.newInstance().getSetByPage(userIds, index, number, null);
        List<CommonStatus> statuses = CommonStatus.getAll();
        statuses.remove(CommonStatus.DELETED);
        List<UserBean> users = userService.getUserByUserIds(pageUserIds, UserAuthority.AGREE_ALL);
        return users;
    }
}

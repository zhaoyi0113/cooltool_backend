package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.DiagnosticEnumerationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.DiagnosticEnumerationBeanConverter;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

/**
 * Created by hp on 6/21/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
/*@Ignore*/
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class CoursesCreator {

    @Autowired private DiagnosticEnumerationBeanConverter diagnosticBeanConverter;
    @Autowired private CourseRelationManageService courseRelationService;
    @Autowired private CourseService courseService;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;

    @Test
    public void createCourses(){
        List<Long> coursesId = new ArrayList<>();
        for(int i=0; i<2100;i++) {
            String name = getRandomString(10);
            String introduction = getRandomString(140);
            String enrollUrl = "http://" + getRandomString(30);
            CourseBean course = courseService.createCourse(name, introduction, enrollUrl, 0);
            coursesId.add(course.getId());
        }
        List<HospitalBean> hospitals = hospitalService.searchHospital(true, false, null, -1, -1, -1, null, 1, 1, 0, 0);
        List<HospitalDepartmentBean> departments = departmentService.getAll("");
        List<DiagnosticEnumeration> diagnosticsEnum = DiagnosticEnumeration.getAllDiagnostic();

        int departmentCount = departments.size();
        int diagnosticCount = diagnosticsEnum.size() - 1;
        int courseInEveryDiagnostic = 5;
        int hospitalCount = coursesId.size()/(courseInEveryDiagnostic*diagnosticCount*departmentCount);

        int coursesInEveryHospital = courseInEveryDiagnostic*diagnosticCount*departmentCount;
        int coursesInEveryDepartment = courseInEveryDiagnostic*diagnosticCount;
        for (int i=0, count=coursesId.size(); i<count; i++) {
            Long courseId = coursesId.get(i);
            int hospitalIndex = i/coursesInEveryHospital;
            int departmentIndex = i/coursesInEveryDepartment;
            int diagnosticIndex = i%courseInEveryDiagnostic;
            if (hospitalIndex<hospitalCount) {
                HospitalBean hospital = hospitals.get(hospitalIndex);
                courseRelationService.addCourseToHospital(courseId, hospital.getId());
                HospitalDepartmentBean department = departments.get(departmentIndex);
                courseRelationService.addCourseToDepartment(courseId, department.getId());
                DiagnosticEnumeration diagnosticEnum = diagnosticsEnum.get(diagnosticIndex + 1);
                DiagnosticEnumerationBean diagnostic = diagnosticBeanConverter.convert(diagnosticEnum);
                courseRelationService.addCourseToDiagnostic(courseId, diagnostic.getId());
            }
            else {
                courseRelationService.addCourseToHospital(courseId, -1);
            }
        }
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}

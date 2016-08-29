package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.converter.DoctorAppointmentBeanConverter;
import com.cooltoo.go2nurse.entities.DoctorAppointmentEntity;
import com.cooltoo.go2nurse.repository.DoctorAppointmentRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hp on 2016/8/14.
 */
@Service("DoctorAppointmentService")
public class DoctorAppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAppointmentService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "clinicDate"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    private static final long hour4PMToMidnightMilliSecond = 8 * 60 * 60 * 1000;
    private static final Go2NurseUtility utility = new Go2NurseUtility();

    @Autowired private DoctorAppointmentRepository repository;
    @Autowired private DoctorAppointmentBeanConverter beanConverter;
    @Autowired private DoctorClinicDateHoursService clinicDateHoursService;
    @Autowired private DoctorService doctorService;
    @Autowired private PatientService patientService;

    //=========================================================================
    //                    getting
    //=========================================================================
    //============================================
    //          getting for user
    //============================================
    public List<DoctorAppointmentBean> getDoctorAppointment(boolean checkUser, Long userId, Long appointmentId) {
        logger.info("user={} get doctor appointment by appointmentId={}", userId, appointmentId);
        DoctorAppointmentEntity entity = repository.findOne(appointmentId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser && userId!=entity.getUserId()) {
            logger.error("this appointment not belong to user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        List<DoctorAppointmentEntity> entities = new ArrayList<>();
        entities.add(entity);
        List<DoctorAppointmentBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<DoctorAppointmentBean> getDoctorAppointment(Long userId, String strOrderStatuses) {
        logger.info("user={} get doctor appointment by orderStatuses={}", userId, strOrderStatuses);
        List<OrderStatus> orderStatuses = OrderStatus.parseStrings(strOrderStatuses);
        if (VerifyUtil.isListEmpty(orderStatuses)) {
            return new ArrayList<>();
        }
        List<DoctorAppointmentEntity> entities = repository.findByConditionsForUser(userId, orderStatuses, sort);
        List<DoctorAppointmentBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<DoctorAppointmentBean> getDoctorAppointment(Long userId, String strOrderStatuses, int pageIndex, int sizePerPage) {
        logger.info("user={} get doctor appointment by orderStatuses={} at pageIndex={} sizePerPage={}",
                userId, strOrderStatuses, pageIndex, sizePerPage);
        List<OrderStatus> orderStatuses = OrderStatus.parseStrings(strOrderStatuses);
        if (VerifyUtil.isListEmpty(orderStatuses)) {
            return new ArrayList<>();
        }
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<DoctorAppointmentEntity> entities = repository.findByConditionsForUser(userId, orderStatuses, page);
        List<DoctorAppointmentBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }


    //============================================
    //          getting for administrator
    //============================================
    public long countDoctorAppointment(Integer hospitalId, Integer departmentId, Long doctorId,
                                       Long clinicDateId, Long clinicHoursId,
                                       Date startDate, Date endDate) {
        logger.info("get doctor appointments by hospitalId={} departmentId={} doctorId={} clinicDateId={} clinicHoursId={} startDate={} endDate={}",
                hospitalId, departmentId, doctorId, clinicDateId, clinicHoursId, startDate, endDate);
        long count = repository.countByConditionsForAdmin(hospitalId, departmentId, doctorId, clinicDateId, clinicHoursId, startDate, endDate);
        logger.info("count is {}", count);
        return count;
    }

    public List<DoctorAppointmentBean> findDoctorAppointment(Integer hospitalId, Integer departmentId, Long doctorId,
                                                             Long clinicDateId, Long clinicHoursId,
                                                             Date startDate, Date endDate,
                                                             int pageIndex, int sizePerPage) {
        logger.info("get doctor appointments by hospitalId={} departmentId={} doctorId={} clinicDateId={} clinicHoursId={} startDate={} endDate={}, pageIndex={} sizePerPage={}",
                hospitalId, departmentId, doctorId, clinicDateId, clinicHoursId, startDate, endDate, pageIndex, sizePerPage);
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<DoctorAppointmentEntity> entities = repository.findByConditionsForAdmin(hospitalId, departmentId, doctorId, clinicDateId, clinicHoursId, startDate, endDate, page);
        List<DoctorAppointmentBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<DoctorAppointmentBean> entitiesToBeans(Iterable<DoctorAppointmentEntity> entities) {
        List<DoctorAppointmentBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (DoctorAppointmentEntity tmp : entities) {
            DoctorAppointmentBean bean = beanConverter.convert(tmp);
            bean.setProperty(DoctorAppointmentBean.FLAG, "appointment");
            beans.add(bean);
        }
        return beans;
    }

    //=========================================================================
    //                    updating
    //=========================================================================
    @Transactional
    public DoctorAppointmentBean completeAppointment(long userId, long doctorId, long appointmentId) {
        logger.info("user={} doctor={} appointment={} completed!", userId, doctorId, appointmentId);
        DoctorAppointmentEntity entity = repository.findOne(appointmentId);
        if (null==entity) {
            logger.error("appointment is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId>0 && userId!=entity.getUserId()) {
            logger.error("not user's appointment");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (doctorId>0 && doctorId!=entity.getDoctorId()) {
            logger.error("not doctor's appointment");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setOrderStatus(OrderStatus.COMPLETED);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public DoctorAppointmentBean cancelAppointment(long userId, long doctorId, long appointmentId) {
        logger.info("user={} doctor={} appointment={} cancelled!", userId, doctorId, appointmentId);
        DoctorAppointmentEntity entity = repository.findOne(appointmentId);
        if (null==entity) {
            logger.error("appointment is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId>0 && userId!=entity.getUserId()) {
            logger.error("not user's appointment");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (doctorId>0 && doctorId!=entity.getDoctorId()) {
            logger.error("not doctor's appointment");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setOrderStatus(OrderStatus.CANCELLED);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public DoctorAppointmentBean modifyAppointment(long appointmentId, long userId, long patientId, long newClinicHoursId) {
        logger.info("user={} patient={} modify appointment={} with new clinicHour={}",
                userId, patientId, appointmentId, newClinicHoursId);

        // get entity
        DoctorAppointmentEntity entity = repository.findOne(appointmentId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        DoctorClinicHoursBean clinicHoursBean = clinicDateHoursService.getClinicHourById(newClinicHoursId);
        DoctorClinicDateBean clinicDateBean = clinicDateHoursService.getClinicDateById(clinicHoursBean.getClinicDateId());
        DoctorBean doctorBean = doctorService.getDoctorById(clinicHoursBean.getDoctorId());

        // check time is valid
        Calendar calendar = Calendar.getInstance();
        long currentMilli = calendar.getTimeInMillis();
        long clinicDateMilli = clinicDateBean.getClinicDate().getTime();
        if (!isTimeValid(clinicDateMilli, currentMilli)) {
            logger.error("cannot appoint at this clinic date");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // check number is valid, and user not appoint
        int number = clinicHoursBean.getNumberCount();
        List<DoctorAppointmentEntity> entities = repository.findByClinicHoursId(newClinicHoursId, sort);
        int isNumberOrUserValid = isNumberConsumedOrUserHasAppointed(entities, patientId, number);
        if (isNumberOrUserValid==-1) {
            logger.error("user has appointed");
            throw new BadRequestException(ErrorCode.PATIENT_HAS_APPOINT_DOCTOR_TODAY);
        }
        if (isNumberOrUserValid==-2) {
            logger.error("there is no more number to appointed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // check patient is valid
        PatientBean patientBean = patientService.getOneById(patientId);
        if (null==patientBean) {
            logger.error("patient not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // 下单
        entity.setStatus(CommonStatus.ENABLED);
        entity.setHospitalId(doctorBean.getHospitalId());
        if (null!=doctorBean.getHospital()) {
            entity.setHospitalJson(utility.toJsonString(doctorBean.getHospital()));
        }
        entity.setDepartmentId(doctorBean.getDepartmentId());
        if (null!=doctorBean.getDepartment()) {
            entity.setDepartmentJson(utility.toJsonString(doctorBean.getDepartment()));
        }
        entity.setDoctorId(doctorBean.getId());
        entity.setDoctorJson(utility.toJsonString(doctorBean));
        entity.setClinicDateId(clinicDateBean.getId());
        entity.setClinicDate(clinicDateBean.getClinicDate());
        entity.setClinicHoursId(newClinicHoursId);
        entity.setClinicHoursStart(clinicHoursBean.getClinicHourStart());
        entity.setClinicHoursEnd(clinicHoursBean.getClinicHourEnd());
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setPatientJson(utility.toJsonString(patientBean));
        entity.setOrderStatus(OrderStatus.TO_SERVICE);
        entity = repository.save(entity);

        return beanConverter.convert(entity);
    }

    //=========================================================================
    //                    adding
    //=========================================================================
    @Transactional
    public DoctorAppointmentBean appointDoctor(long userId, long patientId, long clinicHoursId) {
        Calendar calendar = Calendar.getInstance();
        logger.info("userId={} appoint doctor for patientId={} at clinicHoursId={} at time={}",
                userId, patientId, clinicHoursId, calendar.getTime());

        DoctorClinicHoursBean clinicHoursBean = clinicDateHoursService.getClinicHourById(clinicHoursId);
        DoctorClinicDateBean clinicDateBean = clinicDateHoursService.getClinicDateById(clinicHoursBean.getClinicDateId());
        DoctorBean doctorBean = doctorService.getDoctorById(clinicHoursBean.getDoctorId());

        // check time is valid
        long currentMilli = calendar.getTimeInMillis();
        long clinicDateMilli = clinicDateBean.getClinicDate().getTime();
        if (!isTimeValid(clinicDateMilli, currentMilli)) {
            logger.error("cannot appoint at this clinic date");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // check number is valid, and user not appoint
        int number = clinicHoursBean.getNumberCount();
        List<DoctorAppointmentEntity> entities = repository.findByClinicHoursId(clinicHoursId, sort);
        int isNumberOrUserValid = isNumberConsumedOrUserHasAppointed(entities, patientId, number);
        if (isNumberOrUserValid==-1) {
            logger.error("user has appointed");
            throw new BadRequestException(ErrorCode.PATIENT_HAS_APPOINT_DOCTOR_TODAY);
        }
        if (isNumberOrUserValid==-2) {
            logger.error("there is no more number to appointed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // check patient is valid
        PatientBean patientBean = patientService.getOneById(patientId);
        if (null==patientBean) {
            logger.error("patient not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // 下单
        DoctorAppointmentEntity entity = new DoctorAppointmentEntity();
        entity.setTime(new Date(System.currentTimeMillis()));
        entity.setStatus(CommonStatus.ENABLED);
        entity.setOrderNo(NumberUtil.getUniqueString());
        entity.setHospitalId(doctorBean.getHospitalId());
        if (null!=doctorBean.getHospital()) {
            entity.setHospitalJson(utility.toJsonString(doctorBean.getHospital()));
        }
        entity.setDepartmentId(doctorBean.getDepartmentId());
        if (null!=doctorBean.getDepartment()) {
            entity.setDepartmentJson(utility.toJsonString(doctorBean.getDepartment()));
        }
        entity.setDoctorId(doctorBean.getId());
        entity.setDoctorJson(utility.toJsonString(doctorBean));
        entity.setClinicDateId(clinicDateBean.getId());
        entity.setClinicDate(clinicDateBean.getClinicDate());
        entity.setClinicHoursId(clinicHoursId);
        entity.setClinicHoursStart(clinicHoursBean.getClinicHourStart());
        entity.setClinicHoursEnd(clinicHoursBean.getClinicHourEnd());
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setPatientJson(utility.toJsonString(patientBean));
        entity.setOrderStatus(OrderStatus.TO_SERVICE);
        entity = repository.save(entity);

        // check after insert
        entities = repository.findByClinicHoursId(clinicHoursId, sort);
        for (int i=entities.size()-1, valid=0; i>=0; i--) {
            DoctorAppointmentEntity tmp = entities.get(i);
            if (!OrderStatus.TO_SERVICE.equals(tmp.getOrderStatus()) && !OrderStatus.COMPLETED.equals(tmp.getOrderStatus())) {
                continue;
            }
            valid ++;
            if (valid>number) {
                tmp.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            }
            if (entity.getId() == tmp.getId()) {
                entity = tmp;
            }
        }
        repository.save(entities);

        // return
        return beanConverter.convert(entity);
    }

    private boolean isTimeValid(long clinicDate, long currentDate) {
        return !((clinicDate-currentDate)<=hour4PMToMidnightMilliSecond);
    }

    private int isNumberConsumedOrUserHasAppointed(List<DoctorAppointmentEntity> appointments, long patientId, int clinicNumber) {
        if (VerifyUtil.isListEmpty(appointments)) {
            return 0;
        }

        int countValid = 0;
        boolean userHasAppointed = false;
        for (DoctorAppointmentEntity tmp : appointments) {
            OrderStatus status = tmp.getOrderStatus();
            if (!OrderStatus.TO_SERVICE.equals(status) && !OrderStatus.COMPLETED.equals(status)) {
                continue;
            }
            if (tmp.getPatientId()==patientId) {
                userHasAppointed = true;
                break;
            }
            countValid++;
        }
        if (userHasAppointed) {
            return -1;
        }
        if (countValid>=clinicNumber) {
            return -2;
        }
        return 0;
    }
}

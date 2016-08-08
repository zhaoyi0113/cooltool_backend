package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.DoctorClinicDateBean;
import com.cooltoo.go2nurse.beans.DoctorClinicHoursBean;
import com.cooltoo.go2nurse.converter.DoctorClinicDateBeanConverter;
import com.cooltoo.go2nurse.converter.DoctorClinicHoursBeanConverter;
import com.cooltoo.go2nurse.entities.DoctorClinicDateEntity;
import com.cooltoo.go2nurse.entities.DoctorClinicHoursEntity;
import com.cooltoo.go2nurse.repository.DoctorClinicDateRepository;
import com.cooltoo.go2nurse.repository.DoctorClinicHoursRepository;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hp on 2016/8/4.
 */
@Service("DoctorClinicDateHoursService")
public class DoctorClinicDateHoursService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorClinicDateHoursService.class);
    private static final Sort clinicDateSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "clinicDate"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    private static final Sort clinicHoursSort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "clinicDateId"),
            new Sort.Order(Sort.Direction.ASC, "clinicHourStart"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private DoctorClinicDateRepository dateRepository;
    @Autowired private DoctorClinicDateBeanConverter dateBeanConverter;
    @Autowired private DoctorClinicHoursRepository hoursRepository;
    @Autowired private DoctorClinicHoursBeanConverter hoursBeanConverter;

    @Autowired private DoctorService doctorService;

    //==============================================================
    //                      getting
    //==============================================================

    public List<DoctorClinicDateBean> getClinicDateWithHours(long doctorId, int flag, List<CommonStatus> statuses) {
        logger.info("get clinic dates by doctorId={} flag={} status={}", doctorId, flag, statuses);
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarStart.set(
                calendarStart.get(Calendar.YEAR),
                calendarStart.get(Calendar.MONTH)+flag,
                1,0,0,0);
        calendarEnd.set(
                calendarEnd.get(Calendar.YEAR),
                calendarEnd.get(Calendar.MONTH)+1+flag,
                1,0,0,0);
        Date dateStart = new Date(calendarStart.getTimeInMillis());
        Date dateEnd = new Date(calendarEnd.getTimeInMillis());
        logger.info("dateStart={} dateEnd={}", dateStart, dateEnd);
        List<DoctorClinicDateEntity> entities = dateRepository.findDoctorByConditions(doctorId, statuses, dateStart, dateEnd, clinicDateSort);
        List<DoctorClinicDateBean> beans = dateEntitiesToBeans(entities);
        fillDateOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public long countClinicDateByDoctorId(long doctorId, List<CommonStatus> statuses) {
        long count = dateRepository.countDoctorByConditions(doctorId, statuses, null, null);
        logger.info("count clinic dates by doctorId={} status={}, size is {}", doctorId, statuses, count);
        return count;
    }

    public List<DoctorClinicDateBean> getClinicDateWithHoursByDoctorId(long doctorId, List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get clinic dates by doctorId={} status={}", doctorId, statuses);
        Pageable page = new PageRequest(pageIndex, sizePerPage, clinicDateSort);
        Page<DoctorClinicDateEntity> entities = dateRepository.findDoctorByConditions(doctorId, statuses, null, null, page);
        List<DoctorClinicDateBean> beans = dateEntitiesToBeans(entities);
        fillDateOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    private List<DoctorClinicHoursBean> getClinicHoursByDateIds(List<Long> dateIds) {
        if (VerifyUtil.isListEmpty(dateIds)) {
            logger.warn("date ids is empty");
            return new ArrayList<>();
        }
        List<DoctorClinicHoursEntity> entities = hoursRepository.findByClinicDateIdIn(dateIds, clinicHoursSort);
        List<DoctorClinicHoursBean> beans = hourEntitiesToBeans(entities);
        logger.info("clinic hours count is {}", beans.size());
        return beans;
    }

    private List<DoctorClinicDateBean> dateEntitiesToBeans(Iterable<DoctorClinicDateEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<DoctorClinicDateBean> beans = new ArrayList<>();
        for (DoctorClinicDateEntity tmp : entities) {
            DoctorClinicDateBean bean = dateBeanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private List<DoctorClinicHoursBean> hourEntitiesToBeans(Iterable<DoctorClinicHoursEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<DoctorClinicHoursBean> beans = new ArrayList<>();
        for (DoctorClinicHoursEntity tmp : entities) {
            DoctorClinicHoursBean bean = hoursBeanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private void fillDateOtherProperties(List<DoctorClinicDateBean> dates) {
        if (VerifyUtil.isListEmpty(dates)) {
            return;
        }
        List<Long> dateIds = new ArrayList<>();
        for (DoctorClinicDateBean tmp : dates) {
            if (dateIds.contains(tmp.getId())) {
                continue;
            }
            dateIds.add(tmp.getId());
        }

        List<DoctorClinicHoursBean> hours = getClinicHoursByDateIds(dateIds);
        for (DoctorClinicDateBean tmp : dates) {
            long dateId = tmp.getId();
            for (DoctorClinicHoursBean tmpH : hours) {
                if (dateId==tmpH.getClinicDateId()) {
                    tmp.getClinicHours().add(tmpH);
                }
            }
        }
    }

    //==============================================================
    //                      deleting
    //==============================================================
    @Transactional
    public DoctorClinicDateBean deleteClinicDateById(long clinicDateId) {
        logger.info("delete clinic date by clinicDateId={}", clinicDateId);
        DoctorClinicDateEntity date = dateRepository.findOne(clinicDateId);
        if (null==date) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        dateRepository.delete(clinicDateId);
        int countEffected = hoursRepository.deleteByClinicDateId(clinicDateId);
        logger.info("deleted clinic hour count={}", countEffected);
        return dateBeanConverter.convert(date);
    }

    @Transactional
    public DoctorClinicHoursBean deleteClinicHoursById(long clinicHoursId) {
        logger.info("delete clinic hours by clinicHoursId={}", clinicHoursId);
        DoctorClinicHoursEntity hours = hoursRepository.findOne(clinicHoursId);
        if (null==hours) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        hoursRepository.delete(clinicHoursId);
        return hoursBeanConverter.convert(hours);
    }

    //==============================================================
    //                      updating
    //==============================================================
    @Transactional
    public DoctorClinicDateBean updateClinicDate(long clinicDateId, String strClinicDate, String strStatus) {
        logger.info("update clinicDateId={} with date={} status={}", clinicDateId, strClinicDate, strStatus);
        DoctorClinicDateEntity entity = dateRepository.findOne(clinicDateId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;

        long lClinicDate = NumberUtil.getTime(strClinicDate, NumberUtil.DATE_YYYY_MM_DD);
        if (lClinicDate>0 && entity.getClinicDate().getTime()!=lClinicDate) {
            Date clinicDate = new Date(lClinicDate);
            long count = dateRepository.countByDoctorIdAndClinicDate(entity.getDoctorId(), clinicDate);
            if (count>0) {
                throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
            }
            entity.setClinicDate(clinicDate);
            changed = true;
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && entity.getStatus().equals(status)) {
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = dateRepository.save(entity);
        }

        DoctorClinicDateBean bean = dateBeanConverter.convert(entity);
        logger.info("clinic date after updated is {}", bean);
        return bean;
    }

    @Transactional
    public DoctorClinicHoursBean updateClinicHours(long clinicHoursId, String strClinicStart, String strClinicEnd, int numberCount, String strStatus) {
        logger.info("update clinicHoursId={} with start={} end={} status={}", clinicHoursId, strClinicStart, strClinicEnd, strStatus);
        DoctorClinicHoursEntity entity = hoursRepository.findOne(clinicHoursId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        DoctorClinicHoursBean bean = hoursBeanConverter.convert(entity);
        List<DoctorClinicHoursEntity> hoursExisted = hoursRepository.findByClinicDateId(entity.getClinicDateId(), clinicHoursSort);

        boolean changed = false;

        long lClinicStart = NumberUtil.getTime(strClinicStart, NumberUtil.TIME_HH_MM);
        long lClinicEnd = NumberUtil.getTime(strClinicEnd, NumberUtil.TIME_HH_MM);
        if (lClinicStart>0 || lClinicEnd>0) {
            Time startTime = (lClinicStart < 0) ? entity.getClinicHourStart() : new Time(lClinicStart);
            Time endTime = (lClinicEnd < 0) ? entity.getClinicHourEnd() : new Time(lClinicEnd);
            for (DoctorClinicHoursEntity tmp : hoursExisted) {
                if (tmp.getId()==bean.getId()) {
                    continue;
                }
                boolean covered = isClinicHourCovered(tmp.getClinicHourStart(), tmp.getClinicHourEnd(), startTime, endTime);
                if (covered) {
                    logger.error("clinic hour are covered");
                    throw new BadRequestException(ErrorCode.DATA_ERROR);
                }
            }

            if (entity.getClinicHourStart().getTime() != startTime.getTime()) {
                Time clinicHourStart = new Time(lClinicStart);
                entity.setClinicHourStart(clinicHourStart);
                changed = true;
            }

            if (entity.getClinicHourEnd().getTime() != endTime.getTime()) {
                Time clinicEnd = new Time(lClinicEnd);
                entity.setClinicHourEnd(clinicEnd);
                changed = true;
            }
        }

        if (numberCount>0 && numberCount!=entity.getNumberCount()) {
            entity.setNumberCount(numberCount);
            changed = true;
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && entity.getStatus().equals(status)) {
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = hoursRepository.save(entity);
        }

        bean = hoursBeanConverter.convert(entity);
        logger.info("clinic hours after updated is {}", bean);
        return bean;
    }

    //==============================================================
    //                      adding
    //==============================================================
    @Transactional
    public DoctorClinicDateBean addClinicDate(long doctorId, Date clinicDate, String clinicStart, String clinicEnd, int numberCount) {
        logger.info("doctor={} add clinicDate={} clinicStart={} clinicEnd={} numberCount={}",
                doctorId, clinicDate, clinicStart, clinicEnd, numberCount);
        if (!doctorService.existDoctor(doctorId)){
            logger.error("doctor not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null==clinicDate) {
            logger.error("clinic date is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // add date
        DoctorClinicDateEntity dateEntity = new DoctorClinicDateEntity();
        List<DoctorClinicDateEntity> existed = dateRepository.findByDoctorIdAndClinicDate(doctorId, clinicDate, clinicDateSort);
        if (VerifyUtil.isListEmpty(existed)) {
            // not exist, then create one
            dateEntity.setTime(new Date(System.currentTimeMillis()));
            dateEntity.setStatus(CommonStatus.ENABLED);
            dateEntity.setDoctorId(doctorId);
            dateEntity.setClinicDate(clinicDate);
            dateEntity = dateRepository.save(dateEntity);
        }
        else {
            // has existed, set the first one enabled and others disabled
            dateEntity = existed.get(0);
            for (int i=0; i<existed.size(); i++) {
                DoctorClinicDateEntity tmp = existed.get(i);
                tmp.setStatus(i==0 ? CommonStatus.ENABLED : CommonStatus.DISABLED);
            }
            dateRepository.save(existed);
        }

        DoctorClinicDateBean dateBean = dateBeanConverter.convert(dateEntity);
        DoctorClinicHoursBean hoursBean = addClinicHours(dateBean.getId(), clinicStart, clinicEnd, numberCount);
        if (null!=hoursBean) {
            dateBean.getClinicHours().add(hoursBean);
        }

        logger.info("create clinic date {}", dateBean);
        return dateBean;
    }

    @Transactional
    public List<DoctorClinicDateBean> addClinicDates(long doctorId, List<Date> clinicDates, String clinicStart, String clinicEnd, int numberCount) {
        logger.info("doctor={} add clinicStart={} clinicEnd={} numberCount={} clinicDate={}",
                doctorId, clinicStart, clinicEnd, numberCount, clinicDates);

        List<DoctorClinicDateBean> newClinicDates = new ArrayList<>();
        DoctorClinicDateBean newDate = null;
        for (Date tmp : clinicDates) {
            newDate = addClinicDate(doctorId, tmp, clinicStart, clinicEnd, numberCount);
            if (!newClinicDates.contains(newDate)) {
                newClinicDates.add(newDate);
            }
            else {
                if (newDate.existsClinicHours()) {
                    DoctorClinicHoursBean hours = newDate.getClinicHours().get(0);
                    int index = newClinicDates.indexOf(newDate);
                    DoctorClinicDateBean saved = newClinicDates.get(index);
                    saved.getClinicHours().add(hours);
                }
            }
        }

        logger.info("create clinic date {}", newClinicDates);
        return newClinicDates;
    }

    @Transactional
    public DoctorClinicHoursBean addClinicHours(long clinicDateId, String clinicStart, String clinicEnd, int numberCount) {
        logger.info("clinicDateId={} add clinic hours with clinicStart={} clinicEnd={} numberCount={}",
                clinicDateId, clinicStart, clinicEnd, numberCount);

        DoctorClinicHoursBean hoursBean = null;
        DoctorClinicHoursEntity hoursEntity;
        DoctorClinicDateEntity date = dateRepository.findOne(clinicDateId);

        long clinicStartMilliSec = NumberUtil.getTime(clinicStart, NumberUtil.TIME_HH_MM);
        logger.info("clinicStartMilliSec={}", clinicStartMilliSec);
        long clinicEndMilliSec = NumberUtil.getTime(clinicEnd, NumberUtil.TIME_HH_MM);
        logger.info("clinicEndMilliSec={}", clinicEndMilliSec);

        if (clinicStartMilliSec>0 && clinicEndMilliSec>0 && null!=date) {
            Time clinicStartTime = new Time(clinicStartMilliSec);
            Time clinicEndTime = new Time(clinicEndMilliSec);
            numberCount = numberCount < 0 ? 0 : numberCount;
            List<DoctorClinicHoursEntity> existed = hoursRepository.findByClinicDateId(clinicDateId, clinicHoursSort);
            for (int i=0; (null!=existed && i<existed.size()); i++) {
                DoctorClinicHoursEntity tmp = existed.get(i);
                boolean covered = isClinicHourCovered(tmp.getClinicHourStart(), tmp.getClinicHourEnd(), clinicStartTime, clinicEndTime);
                if (covered) {
                    logger.error("clinic hour are covered");
                    throw new BadRequestException(ErrorCode.DATA_ERROR);
                }
            }

            hoursEntity = new DoctorClinicHoursEntity();
            hoursEntity.setTime(new Date(System.currentTimeMillis()));
            hoursEntity.setStatus(CommonStatus.ENABLED);
            hoursEntity.setDoctorId(date.getDoctorId());
            hoursEntity.setClinicDateId(clinicDateId);
            hoursEntity.setClinicHourStart(clinicStartTime);
            hoursEntity.setClinicHourEnd(clinicEndTime);
            hoursEntity.setNumberCount(numberCount);
            hoursEntity = hoursRepository.save(hoursEntity);
            hoursBean = hoursBeanConverter.convert(hoursEntity);
        }

        logger.info("clinicHour={}", hoursBean);
        return hoursBean;
    }

    private boolean isClinicHourCovered(Time start1, Time end1, Time start2, Time end2) {
        logger.info("judge start1-end1:{}-{}  start2-end2:{}-{}", start1, end1, start2, end2);
        long lStart1 = start1.getTime();
        long lStart2 = start2.getTime();
        long lEnd1 = end1.getTime();
        long lEnd2 = end2.getTime();
        if (lStart1>lEnd1 || lStart2>lEnd2) {
            logger.error("start time < end time");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (lStart1==lStart2 && lEnd1==lEnd2) {
            return true;
        }
        else if (lStart1<lStart2 && lStart2<lEnd1) {
            return true;
        }
        else if (lStart2<lStart1 && lStart1<lEnd2) {
            return true;
        }
        else if (lStart1<lEnd2 && lEnd2<lEnd1) {
            return true;
        }
        else if (lStart2<lEnd1 && lEnd1<lEnd2) {
            return true;
        }
        else if (lStart1<lStart2 && lEnd2<lEnd1) {
            return true;
        }
        else if (lStart2<lStart1 && lEnd1<lEnd2) {
            return true;
        }
        return false;
    }
}

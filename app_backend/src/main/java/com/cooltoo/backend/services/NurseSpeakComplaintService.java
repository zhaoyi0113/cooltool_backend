package com.cooltoo.backend.services;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakComplaintBean;
import com.cooltoo.backend.converter.NurseSpeakComplaintBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakComplaintEntity;
import com.cooltoo.backend.repository.NurseSpeakComplaintRepository;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

;

/**
 * Created by hp on 2016/5/30.
 */
@Service("NurseSpeakComplaintService")
public class NurseSpeakComplaintService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakComplaintService.class);

    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "time"));

    @Autowired private NurseSpeakComplaintRepository repository;
    @Autowired private NurseSpeakComplaintBeanConverter beanConverter;
    @Autowired private NurseService nurseService;
    @Autowired private NurseSpeakService nurseSpeakService;

    //========================================================================
    //             get
    //========================================================================
    public long countBySpeakId(long speakId) {
        logger.info("count by speak id={}", speakId);
        long count = repository.countBySpeakId(speakId);
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseSpeakComplaintBean> getComplaintBySpeakId(long speakId, int pageIndex, int sizePerPage) {
        logger.info("get complaint by speakId={} at page={} sizePerPage={}",
                speakId, pageIndex, sizePerPage);
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseSpeakComplaintEntity> resultSet=null;
        resultSet = repository.findBySpeakId(speakId, page);
        List<NurseSpeakComplaintBean> list = entitiesToBeans(resultSet);
        fillOtherProperties(list);
        logger.info("count of complaint is {}", list.size());
        return list;
    }

    public List<String> getAllStatus() {
        List<String> status = ReadingStatus.getAllStatusString();
        logger.info("get all complaint status={}", status);
        return status;
    }

    public long countByStatus(String strStatus) {
        logger.info("count by status={}", strStatus);
        ReadingStatus status  = ReadingStatus.parseString(strStatus);
        long count = 0;
        if ("ALL".equalsIgnoreCase(strStatus)) {
            count = repository.count();
        }
        else if (null!=status) {
            count = repository.countByStatus(status);
        }
        logger.info("count is {}", count);
        return count;
    }

    public List<NurseSpeakComplaintBean> getComplaintByStatus(String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get speak complaint by status={} at page={} number={}", strStatus, pageIndex, sizePerPage);
        ReadingStatus status = ReadingStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseSpeakComplaintEntity> resultSet=null;
        if ("ALL".equalsIgnoreCase(strStatus)) {
            resultSet = repository.findAll(page);
        }
        else if (null!=status) {
            resultSet = repository.findByStatus(status, page);
        }
        List<NurseSpeakComplaintBean> list = entitiesToBeans(resultSet);
        fillOtherProperties(list);
        logger.info("count of complaint is {}", list.size());
        return list;
    }

    private List<NurseSpeakComplaintBean> entitiesToBeans(Iterable<NurseSpeakComplaintEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        NurseSpeakComplaintBean complaint;
        List<NurseSpeakComplaintBean> list = new ArrayList<>();
        for (NurseSpeakComplaintEntity entity : entities) {
            complaint = beanConverter.convert(entity);
            list.add(complaint);
        }
        return list;
    }
    private void fillOtherProperties(List<NurseSpeakComplaintBean> beans) {
        List<Long> speakIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (NurseSpeakComplaintBean complaint : beans) {
            speakIds.add(complaint.getSpeakId());
            userIds.add(complaint.getInformantId());
        }

        NurseBean nurse = null;
        NurseSpeakBean speak = null;
        List<NurseBean> nurses = nurseService.getNurseWithoutOtherInfo(userIds);
        List<NurseSpeakBean> speaks = nurseSpeakService.getSpeakByIds(speakIds, false);
        for (NurseSpeakComplaintBean complaint : beans) {
            for (NurseBean tmp_nurse : nurses) {
                if (complaint.getInformantId() == tmp_nurse.getId()) {
                    nurse = tmp_nurse;
                    break;
                }
            }
            for (NurseSpeakBean tmp_speak : speaks) {
                if (complaint.getSpeakId() == tmp_speak.getId()) {
                    speak = tmp_speak;
                    break;
                }
            }
            complaint.setInformant(nurse);
            complaint.setSpeakBean(speak);
        }
    }
    //========================================================================
    //            add
    //========================================================================
    @Transactional
    public NurseSpeakComplaintBean addComplaint(long informantId, long speakId, String reason) {
        logger.info("add complaint by user={} speakId={} reason={}", informantId, speakId, reason);
        if (!nurseSpeakService.existsSpeak(speakId)) {
            logger.info("speak record not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(reason)) {
            logger.info("reason is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseSpeakComplaintEntity complaint = new NurseSpeakComplaintEntity();
        complaint.setInformantId(informantId);
        complaint.setSpeakId(speakId);
        complaint.setReason(reason);
        complaint.setStatus(ReadingStatus.UNREAD);
        complaint.setTime(new Date());
        repository.save(complaint);

        return beanConverter.convert(complaint);
    }

    //========================================================================
    //            update
    //========================================================================
    @Transactional
    public NurseSpeakComplaintBean updateCompliant(long complaintId, String reason, String strStatus) {
        logger.info("update complaint by id={} reason={} status={}",
                complaintId, reason, strStatus);

        NurseSpeakComplaintEntity complaint = repository.getOne(complaintId);
        NurseSpeakComplaintBean complaintB =  beanConverter.convert(complaint);
        if (null==complaint) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        logger.info("complaint is {}", complaint);
        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(reason) && !reason.equals(complaint.getReason())) {
            complaint.setReason(reason);
            changed = true;
        }
        ReadingStatus status = ReadingStatus.parseString(strStatus);
        if (null!=status && !status.equals(complaint.getStatus())) {
            complaint.setStatus(status);
            changed = true;
        }
        if (changed) {
            repository.save(complaint);
        }
        return beanConverter.convert(complaint);
    }
}

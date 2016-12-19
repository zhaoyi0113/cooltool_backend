package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseWalletBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.WalletInOutType;
import com.cooltoo.go2nurse.constants.WalletProcess;
import com.cooltoo.go2nurse.converter.NurseWalletBeanConverter;
import com.cooltoo.go2nurse.entities.NurseWalletEntity;
import com.cooltoo.go2nurse.repository.NurseWalletRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhaolisong on 12/12/2016.
 */
@Service("NurseWalletService")
public class NurseWalletService {

    private static final Logger logger = LoggerFactory.getLogger(NurseWalletService.class);
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "nurseId"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    private static final Sort sortId = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private NurseWalletRepository repository;
    @Autowired private NurseWalletBeanConverter beanConverter;

    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private NurseOrderRelationService nurseOrderRelation;
    @Autowired private ServiceOrderService serviceOrderService;
    @Autowired private ServiceOrderService orderService;

    //=========================================================================
    //                       GET Wallet Balance Record
    //=========================================================================
    //=======================================
    //   GET Wallet Balance Record -- Admin
    //=======================================
    public long countNurseWalletFlowRecord(Long nurseId, WalletInOutType reason, WalletProcess process, String summary) {
        summary = VerifyUtil.isStringEmpty(summary) ? null : VerifyUtil.reconstructSQLContentLike(summary);
        long count = repository.countByConditions(nurseId, process, reason, summary);
        logger.debug("count nurse wallet balance by nurseId={} reason={} process={} summary={}, count={}",
                nurseId, reason, process, summary, count);
        return count;
    }

    public List<NurseWalletBean> getNurseWalletFlowRecord(Long nurseId,
                                                          WalletInOutType reason,
                                                          WalletProcess process,
                                                          String summary,
                                                          int pageIndex, int sizePerPage
    ) {
        logger.debug("get nurse wallet balance by nurseId={} reason={} process={} summary={} at pageIndex={} sizePerPage",
                nurseId, reason, process, summary, pageIndex, sizePerPage);
        summary = VerifyUtil.isStringEmpty(summary) ? null : VerifyUtil.reconstructSQLContentLike(summary);
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sortId);
        Page<NurseWalletEntity> entities = repository.findByConditions(nurseId, process, reason, summary, page);
        List<NurseWalletBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        return beans;
    }

    //=======================================
    //   GET Wallet Balance Record -- Nurse
    //=======================================
    public Map<Long, Long> getNurseWalletBalance(List<Long> nurseIds) {
        logger.debug("get nurse wallet balance by nurseIds={}", nurseIds);
        Map<Long, Long> nurseBalance = new HashMap<>();
        if (!VerifyUtil.isListEmpty(nurseIds)) {
            List<NurseWalletEntity> nurseToAmount = repository.findNurseWalletInOut(nurseIds, null);
            if (!VerifyUtil.isListEmpty(nurseToAmount)) {
                for (NurseWalletEntity tmp : nurseToAmount) {
                    if (null==tmp) { continue; }
                    if (!WalletProcess.COMPLETED.equals(tmp.getProcess())
                     && !WalletProcess.PROCESSING.equals(tmp.getProcess())) {
                        continue;
                    }
                    Long nurseId = tmp.getNurseId();
                    Long amount = tmp.getAmount();
                    Long exist = nurseBalance.get(nurseId);
                    exist = null==exist ? 0L : exist;
                    nurseBalance.put(nurseId, exist+amount);
                }
            }
        }
        return nurseBalance;
    }

    public long getNurseWalletBalance(long nurseId) {
        logger.debug("get nurse wallet balance by nurseId={}", nurseId);
        long nurseBalance = 0;
        List<NurseWalletEntity> nurseToAmount = repository.findNurseWalletInOut(Arrays.asList(new Long[]{nurseId}), null);
        if (!VerifyUtil.isListEmpty(nurseToAmount)) {
            for (NurseWalletEntity tmp : nurseToAmount) {
                if (null==tmp) { continue; }
                if (!WalletProcess.COMPLETED.equals(tmp.getProcess())
                 && !WalletProcess.PROCESSING.equals(tmp.getProcess())) {
                    continue;
                }

                Long tmpAmount = tmp.getAmount();
                nurseBalance += tmpAmount;
            }
        }
        logger.debug("get nurse wallet balance={} by nurseId={}", nurseBalance, nurseId);
        return nurseBalance;
    }

    public List<NurseWalletBean> getNurseWalletRecord(long nurseId, int pageIndex, int sizePerPage) {
        logger.info("get nurse wallet flow record by nurseId={} at pageIndex={} sizePerPage={}", nurseId, pageIndex, sizePerPage);
        if (!nurseService.existsNurse(nurseId)) {
            logger.error("nurse not exist!");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<CommonStatus> statuses = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseWalletEntity> entities = repository.findByNurseIdAndStatus(nurseId, null, statuses, page);
        List<NurseWalletBean> beans = entitiesToBeans(entities);

        logger.info("get nurse wallet record, count={}", beans.size());
        return beans;
    }

    public NurseWalletBean getNurseWalletRecord(long walletRecordId) {
        NurseWalletEntity entity = repository.findOne(walletRecordId);
        if (null==entity) {
            return null;
        }
        NurseWalletBean bean = beanConverter.convert(entity);
        fillOtherProperties(Arrays.asList(new NurseWalletBean[]{bean}));
        return bean;
    }

    private List<NurseWalletBean> entitiesToBeans(Iterable<NurseWalletEntity> entities) {
        List<NurseWalletBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }

        for (NurseWalletEntity tmp : entities) {
            if (null==tmp) { continue; }
            NurseWalletBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<NurseWalletBean> beans) {
        if (null == beans || beans.isEmpty()) {
            return;
        }

        List<Long> orderIds = new ArrayList<>();
        List<Long> nurseIds = new ArrayList<>();
        for (NurseWalletBean tmp : beans) {
            if (WalletInOutType.ORDER_INCOME.equals(tmp.getReason()) && !orderIds.contains(tmp.getReasonId())) {
                orderIds.add(tmp.getReasonId());
            }
            if (!nurseIds.contains(tmp.getNurseId())) {
                nurseIds.add(tmp.getNurseId());
            }
        }

        Map<Long, NurseBean> nurseIdToBean = nurseService.getNurseIdToBean(nurseIds);
        List<ServiceOrderBean> orders = orderService.getOrderByIds(orderIds);
        Map<Long, ServiceOrderBean> orderIdToBean = new HashMap<>();
        for (ServiceOrderBean tmp : orders) {
            orderIdToBean.put(tmp.getId(), tmp);
        }

        for (NurseWalletBean tmp : beans) {
            if (WalletInOutType.ORDER_INCOME.equals(tmp.getReason())) {
                ServiceOrderBean order = orderIdToBean.get(tmp.getReasonId());
                tmp.setProperties(NurseWalletBean.REASON_ORDER, order);
            }
            NurseBean nurse = nurseIdToBean.get(tmp.getNurseId());
            tmp.setNurse(nurse);
        }
    }

    //=========================================================================
    //                       DELETE Wallet In-Out Record
    //=========================================================================
    @Transactional
    public long deleteWalletInOut(Long nurseId, long recordId) {
        logger.debug("delete wallet record by walletRecordId={}", recordId);
        if (!repository.exists(recordId)) {
            logger.error("wallet record not exist!");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NurseWalletEntity entity = repository.findOne(recordId);
        if (null!=nurseId && nurseId!=entity.getNurseId()) {
            logger.error("wallet record not belong to nurse!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!WalletProcess.COMPLETED.equals(entity.getProcess())) {
            logger.error("wallet record process status is not Completed!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setStatus(CommonStatus.DELETED);
        repository.save(entity);
        return recordId;
    }

    @Transactional
    public List<Long> deleteNurseWalletInOut(long nurseId) {
        logger.debug("delete wallet record by nurseId={}", nurseId);
        if (!nurseService.existsNurse(nurseId)) {
            logger.error("nurse not exist!");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<Long> deletedIds = new ArrayList<>();
        List<NurseWalletEntity> entities = repository.findByNurseId(nurseId);
        if (!VerifyUtil.isListEmpty(entities)) {
            for (NurseWalletEntity tmp : entities){
                if (CommonStatus.ENABLED.equals(tmp.getStatus()) && WalletProcess.COMPLETED.equals(tmp.getProcess())) {
                    tmp.setStatus(CommonStatus.DELETED);
                    deletedIds.add(tmp.getId());
                }
            }
            repository.save(entities);
        }

        return deletedIds;
    }

    //=========================================================================
    //                       Update Wallet In-Out Record
    //=========================================================================
    @Transactional
    public NurseWalletBean updateWalletInOutStatus(long walletInOutId, WalletProcess process, String processRecord) {
        logger.debug("update wallet record={} process status to={}", walletInOutId, process);
        if (null==process) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        NurseWalletEntity entity = repository.findOne(walletInOutId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (!WalletInOutType.WITHDRAW.equals(entity.getReason())) {
            logger.error("wallet record reason={} is not expected", entity.getReason());
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_STATUS_NOT_EXPECTED);
        }
        if (!WalletProcess.PROCESSING.equals(entity.getProcess())) {
            logger.error("wallet record process={} is not expected", entity.getProcess());
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_STATUS_NOT_EXPECTED);
        }
        if (!process.equals(entity.getProcess())) {
            entity.setProcess(process);
            entity.setProcessTime(new Date());
            if (!VerifyUtil.isStringEmpty(processRecord)) {
                entity.setProcessRecord(processRecord);
            }
            entity = repository.save(entity);
        }
        return beanConverter.convert(entity);
    }


    //=========================================================================
    //                       Create Wallet In-Out Record
    //=========================================================================
    @Transactional
    public NurseWalletBean orderCompleted(long orderId) {
        List<ServiceOrderBean> orders = serviceOrderService.getOrderByOrderId(orderId);
        if (orders.size()!=1) {
            logger.warn("order is not unique.");
            return null;
        }
        ServiceOrderBean order = orders.get(0);
        Map<Long, Long> orderIdToNurseId = nurseOrderRelation.getOrdersWaitStaffId(Arrays.asList(new Long[]{orderId}));
        if (null!=orderIdToNurseId && orderIdToNurseId.size()==1 && (orderIdToNurseId.get(orderId) instanceof Long)) {
            Long nurseId = orderIdToNurseId.get(orderId);
            String summary = "订单";
            if (order.getServiceItem() instanceof ServiceItemBean) {
                summary = summary + " " + order.getServiceItem().getName();
            }
            return recordWalletInOut(nurseId, order.getTotalServerIncomeCent(), summary, WalletProcess.COMPLETED, WalletInOutType.ORDER_INCOME, orderId);
        }
        logger.warn("order has no server.");
        return null;
    }

    @Transactional
    public NurseWalletBean recordWalletInOut(long nurseId, long amount, String summary, WalletProcess process, WalletInOutType reason, long reasonId) {
        logger.debug("create wallet record by nurseId={} amount={} summary={} reason={} reasonId={}",
                nurseId, amount, summary, reason, reasonId);
        if (!nurseService.existsNurse(nurseId)) {
            logger.error("nurse not exist!");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null==reason) {
            logger.error("wallet in-out type is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        NurseWalletEntity entity = null;
        List<NurseWalletEntity> wallets = repository.findByNurseIdAndReasonAndReasonId(nurseId, reason, reasonId, sort);
        if (!WalletInOutType.WITHDRAW.equals(reason)) {
            if (VerifyUtil.isListEmpty(wallets)) {
                entity = new NurseWalletEntity();
            } else {
                entity = wallets.get(wallets.size() - 1);
            }
        }
        else {
            entity = new NurseWalletEntity();
        }
        entity.setNurseId(nurseId);
        entity.setReason(reason);
        entity.setReasonId(reasonId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setAmount(amount);
        entity.setSummary(summary);
        entity.setProcess(process);
        entity.setProcessTime(new Date());
        entity = repository.save(entity);

        if (!WalletInOutType.WITHDRAW.equals(reason)) {
            wallets = repository.findByNurseIdAndReasonAndReasonId(nurseId, reason, reasonId, sort);
            if (!VerifyUtil.isListEmpty(wallets)) {
                for (int i = 0; i < wallets.size(); i++) {
                    NurseWalletEntity tmp = wallets.get(i);
                    if (tmp.getId() == entity.getId()) {
                        wallets.remove(i);
                        break;
                    }
                }
                if (!VerifyUtil.isListEmpty(wallets)) {
                    repository.delete(wallets);
                }
            }
        }

        return beanConverter.convert(entity);
    }
}

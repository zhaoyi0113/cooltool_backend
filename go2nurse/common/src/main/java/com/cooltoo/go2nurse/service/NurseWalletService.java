package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseWalletBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.WalletInOutType;
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

    @Autowired private NurseWalletRepository repository;
    @Autowired private NurseWalletBeanConverter beanConverter;

    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private NurseOrderRelationService nurseOrderRelation;
    @Autowired private ServiceOrderService serviceOrderService;

    //=========================================================================
    //                       GET Wallet In-Out Record
    //=========================================================================
    public Map<Long, Long> getNurseWalletRemain(List<Long> nurseIds) {
        logger.debug("get nurse wallet remain by nurseIds={}", nurseIds);
        Map<Long, Long> nurseRemain = new HashMap<>();
        if (!VerifyUtil.isListEmpty(nurseIds)) {
            List<Object[]> nurseToAmount = repository.findNurseWalletInOut(nurseIds);
            if (!VerifyUtil.isListEmpty(nurseToAmount)) {
                for (Object[] tmp : nurseToAmount) {
                    if (null==tmp || tmp.length!=2) {
                        continue;
                    }
                    Long nurseId = (tmp[0] instanceof Long) ? (Long)tmp[0] : null;
                    if (null==nurseId) {
                        continue;
                    }
                    Long amount  = (tmp[1] instanceof Long) ? (Long)tmp[1] : null;
                    if (null==amount) {
                        continue;
                    }
                    Long exist = nurseRemain.get(nurseId);
                    exist = null==exist ? 0L : exist;
                    nurseRemain.put(nurseId, exist+amount);
                }
            }
        }
        return nurseRemain;
    }

    public List<NurseWalletBean> getNurseWalletRecord(long nurseId, int pageIndex, int sizePerPage) {
        logger.info("get nurse wallet remain by nurseId={} at pageIndex={} sizePerPage={}", nurseId, pageIndex, sizePerPage);
        if (!nurseService.existsNurse(nurseId)) {
            logger.error("nurse not exist!");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<CommonStatus> statuses = Arrays.asList(new CommonStatus[]{CommonStatus.ENABLED});
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseWalletEntity> entities = repository.findByNurseIdAndStatus(nurseId, statuses, page);
        List<NurseWalletBean> beans = entitiesToBeans(entities);

        logger.info("get nurse wallet record, count={}", beans.size());
        return beans;
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
                if (CommonStatus.ENABLED.equals(tmp.getStatus())) {
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
                summary = summary + "-" + order.getServiceItem().getName();
            }
            return recordWalletInOut(nurseId, order.getTotalServerIncomeCent(), summary, WalletInOutType.ORDER_IN, orderId);
        }
        logger.warn("order has no server.");
        return null;
    }

    @Transactional
    public NurseWalletBean recordWalletInOut(long nurseId, long amount, String summary, WalletInOutType reason, long reasonId) {
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
        List<NurseWalletEntity> wallets = repository.findByNurseIdAndReasonAndReasonId(nurseId, reason, reasonId, sort);
        NurseWalletEntity entity = null;
        if (VerifyUtil.isListEmpty(wallets)) {
            entity = new NurseWalletEntity();
        }
        else {
            entity = wallets.get(wallets.size()-1);
        }
        entity.setNurseId(nurseId);
        entity.setReason(reason);
        entity.setReasonId(reasonId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setAmount(amount);
        entity.setSummary(summary);
        entity = repository.save(entity);

        wallets = repository.findByNurseIdAndReasonAndReasonId(nurseId, reason, reasonId, sort);
        if (!VerifyUtil.isListEmpty(wallets)) {
            for (int i=0; i<wallets.size(); i++) {
                NurseWalletEntity tmp = wallets.get(i);
                if (tmp.getId()==entity.getId()) {
                    wallets.remove(i);
                    break;
                }
            }
            if (!VerifyUtil.isListEmpty(wallets)) {
                repository.delete(wallets);
            }
        }

        return beanConverter.convert(entity);
    }
}

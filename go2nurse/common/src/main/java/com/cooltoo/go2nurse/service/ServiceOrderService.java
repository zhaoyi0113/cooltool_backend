package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.TimeUnit;
import com.cooltoo.go2nurse.converter.ServiceOrderBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import com.cooltoo.go2nurse.repository.ServiceOrderRepository;
import com.cooltoo.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
@Service("ServiceOrderService")
public class ServiceOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ServiceOrderRepository repository;
    @Autowired private ServiceOrderBeanConverter beanConverter;

    @Autowired private ServiceCategoryAndItemService serviceCategoryItemService;
    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private UserAddressService addressService;
    @Autowired private ServiceOrderPingPPService orderPingPPService;

    //=====================================================================
    //                   getting
    //=====================================================================

    public long countAllOrder() {
        long count = repository.count();
        logger.info("count all service order is {}", count);
        return count;
    }

    public List<ServiceOrderBean> getOrder(int pageIndex, int sizePerPage) {
        logger.info("get service order at page={} number={}", pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
        Page<ServiceOrderEntity> resultSet = repository.findAll(pageRequest);
        List<ServiceOrderBean> beans = entitiesToBeans(resultSet);
        logger.info("service order count is {}", beans.size());
        return beans;
    }

    public List<ServiceOrderBean> getOrderByUserId(long userId) {
        logger.info("get service order by userId={}", userId);
        List<ServiceOrderEntity> entities = repository.findByUserId(userId, sort);
        List<ServiceOrderBean> beans = entitiesToBeans(entities);
        logger.info("service order count is {}", beans.size());
        return beans;
    }

    private List<ServiceOrderBean> entitiesToBeans(Iterable<ServiceOrderEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<ServiceOrderBean> beans = new ArrayList<>();
        for (ServiceOrderEntity entity : entities) {
            ServiceOrderBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    //=====================================================================
    //                   deleting
    //=====================================================================

    //=====================================================================
    //                   updating
    //=====================================================================
    @Transactional
    public ServiceOrderBean updateOrder(long orderId, long patientId, long addressId,
                                     String strStartTime, int timeDuration, String timeUnit, String totalConsumption) {
        logger.info("update service order={} by patientId={} addressId={} strStartTime={} timeDuration={} timeUnit={} totalConsumption={}",
                serviceItemId, userId, patientId, addressId, strStartTime, timeDuration, timeUnit, totalConsumption);

        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (OrderStatus.CANCELLED.equals(entity.getOrderStatus())
                || OrderStatus.COMPLETED.equals(entity.getOrderStatus())
                || OrderStatus.IN_PROCESS.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be modified", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;
        if (patientId!=0 && patientService.existPatient(patientId)) {
            entity.setPatientId(patientId);
            changed = true;
        }

        if (addressId!=0 && addressService.existAddress(addressId)) {
            entity.setAddressId(addressId);
            changed = true;
        }

        long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lStartTime>0) {
            entity.setServiceStartTime(new Date(lStartTime));
            changed = true;
        }

        if (timeDuration>0) {
            entity.setServiceTimeDuration(timeDuration);
            changed = true;
        }

        TimeUnit serviceTimeUnit = TimeUnit.parseString(timeUnit);
        if (null!=serviceTimeUnit) {
            entity.setServiceTimeUnit(serviceTimeUnit);
            changed = true;
        }

        BigDecimal serviceTotalConsumption = NumberUtil.getDecimal(totalConsumption, 2);
        if (null!=serviceTotalConsumption) {
            entity.setTotalConsumption(serviceTotalConsumption);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        ServiceOrderBean bean = beanConverter.convert(entity);
        logger.info("service order added is {}", bean);
        return bean;
    }

    @Transactional
    public ServiceOrderBean updateOrder(long orderId, OrderStatus orderStatus, String payTime, String paymentAmount) {
        logger.info("update service order={} by orderStatus={} payTime={} paymentAmount={}",
                orderId, orderStatus, payTime, paymentAmount);

        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (null!=orderStatus) {
            entity.setOrderStatus(orderStatus);
            changed = true;
        }

        long lPayTime = NumberUtil.getTime(payTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lPayTime>0) {
            entity.setPayTime(new Date(lPayTime));
            changed = true;
        }

        BigDecimal bdPaymentAmount = NumberUtil.getDecimal(paymentAmount, 2);
        if (null!=bdPaymentAmount) {
            entity.setPaymentAmount(bdPaymentAmount);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        ServiceOrderBean bean = beanConverter.convert(entity);
        logger.info("service order added is {}", bean);
        return bean;
    }

    //=====================================================================
    //                   adding
    //=====================================================================
    private long id;
    private Date time;
    private CommonStatus status;


    private long serviceItemId;
    private long userId;
    private long patientId;
    private long addressId;
    private Date serviceStartTime;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private BigDecimal totalConsumption;


    private OrderStatus orderStatus;
    private Date payTime;
    private BigDecimal paymentAmount;

    @Transactional
    public ServiceOrderBean addOrder(long serviceItemId, long userId, long patientId, long addressId,
                                     String strStartTime, int timeDuration, String timeUnit, String totalConsumption) {
        logger.info("add service order by serviceItemId={} userId={} patientId={} addressId={} strStartTime={} timeDuration={} timeUnit={} totalConsumption={}",
                serviceItemId, userId, patientId, addressId, strStartTime, timeDuration, timeUnit, totalConsumption);
        if (!serviceCategoryItemService.existItem(serviceItemId)) {
            logger.error("service item not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!userService.existUser(userId)) {
            logger.error("user not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (patientId!=0 && !patientService.existPatient(patientId)) {
            logger.error("patient not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!addressService.existAddress(addressId)) {
            logger.error("address not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lStartTime<0) {
            logger.error("start time not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (timeDuration<0) {
            logger.error("time duration not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        TimeUnit serviceTimeUnit = TimeUnit.parseString(timeUnit);
        if (null==serviceTimeUnit) {
            logger.error("time unit not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        BigDecimal serviceTotalConsumption = NumberUtil.getDecimal(totalConsumption, 2);
        if (null==serviceTotalConsumption) {
            logger.error("service total consumption not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setServiceItemId(serviceItemId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setAddressId(addressId);
        entity.setServiceStartTime(new Date(lStartTime));
        entity.setServiceTimeDuration(timeDuration);
        entity.setServiceTimeUnit(serviceTimeUnit);
        entity.setTotalConsumption(serviceTotalConsumption);

        entity.setOrderStatus(OrderStatus.TO_PAY);
//      entity.setPayTime(null);
//      entity.setPaymentAmount(null);

        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        entity = repository.save(entity);
        ServiceOrderBean bean = beanConverter.convert(entity);
        logger.info("service order added is {}", bean);
        return bean;
    }
}

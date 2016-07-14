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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hp on 2016/7/13.
 */
@Service("ServiceOrderService")
public class ServiceOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderService.class);

    @Autowired private ServiceOrderRepository repository;
    @Autowired private ServiceOrderBeanConverter beanConverter;

    @Autowired private ServiceCategoryAndItemService serviceCategoryItemService;
    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private UserAddressService addressService;

    //=====================================================================
    //                   getting
    //=====================================================================
    //=====================================================================
    //                   deleting
    //=====================================================================
    //=====================================================================
    //                   updating
    //=====================================================================

//    @Transactional
//    public ServiceOrderBean payToOrder()
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

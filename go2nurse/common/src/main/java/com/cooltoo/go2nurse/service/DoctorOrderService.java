package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.DoctorBean;
import com.cooltoo.go2nurse.beans.DoctorOrderBean;
import com.cooltoo.go2nurse.converter.DoctorOrderBeanConverter;
import com.cooltoo.go2nurse.entities.DoctorEntity;
import com.cooltoo.go2nurse.entities.DoctorOrderEntity;
import com.cooltoo.go2nurse.repository.DoctorOrderRepository;
import com.cooltoo.go2nurse.repository.DoctorRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
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
import java.util.Map;

/**
 * Created by zhaolisong on 2016/10/14.
 */
@Service("DoctorOrderService")
public class DoctorOrderService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorOrderService.class);

    private static final Sort doctorSort = new Sort(new Sort.Order(Sort.Direction.ASC, "doctorOrder"));
    private static final Sort hospitalSort = new Sort(new Sort.Order(Sort.Direction.ASC, "hospitalOrder"));
    private static final Sort departmentSort = new Sort(new Sort.Order(Sort.Direction.ASC, "departmentOrder"));


    @Autowired private DoctorOrderRepository orderRepository;
    @Autowired private DoctorOrderBeanConverter orderBeanConverter;
    @Autowired private CommonHospitalService hospitalRepository;
    @Autowired private CommonDepartmentService departmentRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private DoctorService doctorService;
    @Autowired private Go2NurseUtility utility;


    //============================================================
    //                       get
    //============================================================
    public DoctorOrderBean getOrder(long orderId) {
        logger.info("get order by orderId={}", orderId);

        DoctorOrderEntity entity = orderRepository.findOne(orderId);
        if (null==entity) {
            logger.error("the doctor order is not exist");
            return null;
        }

        DoctorOrderBean bean = orderBeanConverter.convert(entity);
        logger.info("get order={}", bean);
        return bean;
    }

    public List<DoctorOrderBean> getOrderByDoctorId(long doctorId) {
        logger.info("get doctorId ordered by doctorId={}", doctorId);
        List<DoctorOrderEntity> entities = orderRepository.findOrderByDoctorId(doctorId, doctorSort);
        List<DoctorOrderBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("get order size={}", beans.size());
        return beans;
    }

    public List<Long> getDoctorOrderedList(boolean orderByHospital, Integer hospitalId, Integer departmentId) {
        logger.info("get doctorId ordered by hospitalId={} departmentId={}, use {}", hospitalId, departmentId,
                orderByHospital ? "hospitalId" : "departmentId");

        List<Long> doctorIds = new ArrayList<>();
        if (orderByHospital && hospitalRepository.existHospital(hospitalId)) {
            doctorIds = orderRepository.findDoctorIdByHospitalId(hospitalId, hospitalSort);
        }
        else if (!orderByHospital && departmentRepository.existsDepartment(departmentId)) {
            doctorIds = orderRepository.findDoctorIdByHospitalIdAndDepartmentId(hospitalId, departmentId, departmentSort);
        }

        logger.info("get doctorId ordered={}", doctorIds);
        return doctorIds;
    }

    public long countDoctorOrder(boolean orderByHospital, Integer hospitalId, Integer departmentId) {
        logger.info("count order by hospitalId={} departmentId={}, use {}", hospitalId, departmentId,
                orderByHospital ? "hospitalId" : "departmentId");

        long count = 0;
        if (orderByHospital && hospitalRepository.existHospital(hospitalId)) {
            count = orderRepository.countOrderByHospitalId(hospitalId);
        }
        else if (!orderByHospital && departmentRepository.existsDepartment(departmentId)) {
            count = orderRepository.countOrderByHospitalIdAndDepartmentId(hospitalId, departmentId);
        }

        logger.info("count={}", count);
        return count;
    }

    public List<DoctorOrderBean> getDoctorOrder(boolean orderByHospital, Integer hospitalId, Integer departmentId, int pageIndex, int sizePerPage) {
        logger.info("get order by hospitalId={} departmentId={}, use {}, pageIndex={}, sizePerPage={}",
                hospitalId, departmentId, orderByHospital ? "hospitalId" : "departmentId", pageIndex, sizePerPage);

        Page<DoctorOrderEntity> orders = null;
        if (orderByHospital && hospitalRepository.existHospital(hospitalId)) {
            PageRequest page = new PageRequest(pageIndex, sizePerPage, hospitalSort);
            orders = orderRepository.findOrderByHospitalId(hospitalId, page);
        }
        else if (!orderByHospital && departmentRepository.existsDepartment(departmentId)) {
            PageRequest page = new PageRequest(pageIndex, sizePerPage, departmentSort);
            orders = orderRepository.findOrderByHospitalIdAndDepartmentId(hospitalId, departmentId, page);
        }

        List<DoctorOrderBean> beans = entitiesToBeans(orders);
        fillOtherProperties(beans);
        logger.info("count={}", beans.size());
        return beans;
    }


    private List<DoctorOrderBean> entitiesToBeans(Iterable<DoctorOrderEntity> entities) {
        List<DoctorOrderBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (DoctorOrderEntity tmp : entities) {
            DoctorOrderBean tmpBean = orderBeanConverter.convert(tmp);
            beans.add(tmpBean);
        }
        return beans;
    }

    private void fillOtherProperties(List<DoctorOrderBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }
        List<Long> doctorIds = new ArrayList<>();
        List<Integer> hospitalIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (DoctorOrderBean bean :beans) {
            long doctorId = bean.getDoctorId();
            if (!doctorIds.contains(doctorId)) {
                doctorIds.add(doctorId);
            }

            int hospitalId = bean.getHospitalId();
            if (!hospitalIds.contains(hospitalId)) {
                hospitalIds.add(hospitalId);
            }

            int departmentId = bean.getDepartmentId();
            if (!departmentIds.contains(departmentId)) {
                departmentIds.add(departmentId);
            }
        }

        Map<Integer, HospitalBean> hospitalIdToBean = hospitalRepository.getHospitalIdToBeanMapByIds(hospitalIds);
        Map<Integer, HospitalDepartmentBean> departmentIdToBean = departmentRepository.getDepartmentIdToBean(departmentIds, utility.getHttpPrefixForNurseGo());
        Map<Long, DoctorBean> doctorIdId2Bean = doctorService.getDoctorIdToBean(doctorIds);
        for (DoctorOrderBean tmp : beans) {
            DoctorBean doctor = doctorIdId2Bean.get(tmp.getDoctorId());
            HospitalBean hospital = hospitalIdToBean.get(tmp.getHospitalId());
            HospitalDepartmentBean department = departmentIdToBean.get(tmp.getDepartmentId());

            if (null!=doctor) {
                tmp.setDoctor(doctor);
            }
            if (null!=hospital) {
                tmp.setHospital(hospital);
            }
            if (null!=department) {
                tmp.setDepartment(department);
            }
        }
    }

    //============================================================
    //                       delete
    //============================================================
    @Transactional
    public DoctorOrderBean deleteOrder(long doctorId, int hospitalId, int departmentId) {
        logger.info("delete order with doctorId={} hospitalId={} departmentId={}", doctorId, hospitalId, departmentId);
        List<DoctorOrderEntity> entities = orderRepository.findOrderByHospitalIdAndDepartmentIdAndDoctorId(hospitalId, departmentId, doctorId, hospitalSort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.error("the doctor order is not exist");
            return null;
        }

        DoctorEntity doctor = doctorRepository.findOne(doctorId);
        if (null!=doctor) {
            if (doctor.getHospitalId()==hospitalId && doctor.getDepartmentId()==departmentId) {
                logger.error("doctor belongs to this order's hospital and department");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }

        DoctorOrderBean bean = orderBeanConverter.convert(entities.get(0));
        orderRepository.delete(entities);

        return bean;
    }

    @Transactional
    public void deleteOrder(long orderId) {
        logger.info("delete order with orderId={}", orderId);
        DoctorOrderEntity entity = orderRepository.findOne(orderId);
        if (null==entity) {
            logger.error("the doctor order is not exist");
            return;
        }
        DoctorEntity doctor = doctorRepository.findOne(entity.getDoctorId());
        if (null!=doctor) {
            if (doctor.getHospitalId()==entity.getHospitalId() && doctor.getDepartmentId()==entity.getDepartmentId()) {
                logger.error("doctor belongs to this order's hospital and department");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        orderRepository.delete(entity);
        return;
    }


    //============================================================
    //                       update
    //============================================================
    @Transactional
    public void changeTwoDoctorOrderInDoctor(long firstOrderId, long secondOrderId) {
        logger.info("change two doctor order in hospital 1stId={}, 2ndId={}",
                firstOrderId, secondOrderId);
        DoctorOrderEntity _1st = orderRepository.findOne(firstOrderId);
        DoctorOrderEntity _2nd = orderRepository.findOne(secondOrderId);
        if (null==_1st || null==_2nd) {
            logger.error("the doctor order in hospital is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (_1st.getDoctorId()!=_2nd.getDoctorId()) {
            logger.error("the doctor order not belong to the same doctor");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        int swapOrder1 = _1st.getDoctorOrder();
        int swapOrder2 = _2nd.getDoctorOrder();
        _1st.setDoctorOrder(swapOrder2);
        _2nd.setDoctorOrder(swapOrder1);
        orderRepository.save(_1st);
        orderRepository.save(_2nd);
        return;
    }

    @Transactional
    public void changeTwoDoctorOrderInHospital(long firstOrderId, long secondOrderId) {
        logger.info("change two doctor order in hospital 1stId={}, 2ndId={}",
                firstOrderId, secondOrderId);
        DoctorOrderEntity _1st = orderRepository.findOne(firstOrderId);
        DoctorOrderEntity _2nd = orderRepository.findOne(secondOrderId);
        if (null==_1st || null==_2nd) {
            logger.error("the doctor order in hospital is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (_1st.getHospitalId()!=_2nd.getHospitalId()) {
            logger.error("the doctor order not belong to the same hospital");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        int swapOrder1 = _1st.getHospitalOrder();
        int swapOrder2 = _2nd.getHospitalOrder();
        _1st.setHospitalOrder(swapOrder2);
        _2nd.setHospitalOrder(swapOrder1);
        orderRepository.save(_1st);
        orderRepository.save(_2nd);
        return;
    }

    @Transactional
    public void changeTwoDoctorOrderInDepartment(long firstOrderId, long secondOrderId) {
        logger.info("change two doctor order in department 1stId={}, 2ndId={}",
                firstOrderId, secondOrderId);
        DoctorOrderEntity _1st = orderRepository.findOne(firstOrderId);
        DoctorOrderEntity _2nd = orderRepository.findOne(secondOrderId);
        if (null==_1st || null==_2nd) {
            logger.error("the doctor order in department is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (_1st.getHospitalId()!=_2nd.getHospitalId() || _1st.getDepartmentId()!=_2nd.getDepartmentId()) {
            logger.error("the doctor order not belong to the same department");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        int swapOrder1 = _1st.getDepartmentOrder();
        int swapOrder2 = _2nd.getDepartmentOrder();
        _1st.setDepartmentOrder(swapOrder2);
        _2nd.setDepartmentOrder(swapOrder1);
        orderRepository.save(_1st);
        orderRepository.save(_2nd);
        return;
    }


    //============================================================
    //                       add
    //============================================================
    @Transactional
    public DoctorOrderBean setDoctorOrder(long orderId,
                                          long doctorId, int doctorOrder,
                                          int hospitalId, int hospitalOrder,
                                          int departmentId, int departmentOrder) {
        logger.info("set doctor order with doctor_order={}_{} hospital_order={}_{} department_order={}_{}",
                doctorId, doctorOrder, hospitalId, hospitalOrder, departmentId, departmentOrder);

        //==================================================================
        //                   check parameters
        //
        // NOTE : doctorId and hospitalId are the primary key of this table
        //==================================================================

        if (!doctorRepository.exists(doctorId)) {
            logger.error("doctor not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!hospitalRepository.existHospital(hospitalId)) {
            logger.error("hospital not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (departmentId>0) {
            if (!departmentRepository.existsDepartment(departmentId)) {
                logger.error("department not exist");
                throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
            }

            HospitalDepartmentBean department = departmentRepository.getById(departmentId, "");
            if (department.getHospitalId()!=hospitalId) {
                logger.error("department not belong to the hospital");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        departmentId = departmentId<0  ? 0 : departmentId;

        //=====================================================
        //                   get order exist
        //=====================================================
        DoctorOrderEntity order = null;
        List<DoctorOrderEntity> tmpMatchOrders = orderRepository.findOrderByHospitalIdAndDoctorId(hospitalId, doctorId, hospitalSort);
        // order in hospital exist, selected one
        if (!VerifyUtil.isListEmpty(tmpMatchOrders)) {
            for (int i=0; i<tmpMatchOrders.size(); i++) {
                DoctorOrderEntity tmp = tmpMatchOrders.get(i);
                if (tmp.getId() == orderId) {
                    order = tmp;
                    tmpMatchOrders.remove(i);
                    break;
                }
            }
            if (null==order) {
                order = tmpMatchOrders.get(0);
                tmpMatchOrders.remove(0);
            }
        }

        // if update by orderId,
        // recheck the orderId to make sure it in the same hospital.
        // if not, must delete all the tmpMatchOrders
        if (orderId>0) {
            if (!orderRepository.exists(orderId)) {
                logger.error("order not exist");
                throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
            }

            if (null!=order && order.getId()!=orderId) {
                // add order to tmpMatchOrders for removing
                tmpMatchOrders.add(order);

                // get the specific order
                order = orderRepository.findOne(orderId);
            }
        }

        // order not exist, new one
        if (null==order) {
            order = new DoctorOrderEntity();
        }

        // delete extra records
        if (!VerifyUtil.isListEmpty(tmpMatchOrders)) {
            orderRepository.delete(tmpMatchOrders);
        }


        //=====================================================
        //              replace settings
        //=====================================================
        List<DoctorOrderEntity> tmpOrders = null;
        // get doctor max order
        order.setDoctorId(doctorId);
        if (orderId>0) {
            if (doctorOrder>0){
                order.setDoctorOrder(doctorOrder);
            }
        }
        else {
            if (doctorOrder <= 0) {
                tmpOrders = orderRepository.findOrderByDoctorId(doctorId, doctorSort);
                order.setDoctorOrder(VerifyUtil.isListEmpty(tmpOrders) ? 1 : (tmpOrders.get(tmpOrders.size() - 1).getDoctorOrder() + 1));
            } else {
                order.setDoctorOrder(doctorOrder);
            }
        }

        // get hospital max order
        order.setHospitalId(hospitalId);
        if (orderId>0) {
            if (hospitalOrder>0) {
                order.setHospitalOrder(hospitalOrder);
            }
        }
        else {
            if (hospitalOrder <= 0) {
                tmpOrders = orderRepository.findOrderByHospitalId(hospitalId, hospitalSort);
                order.setHospitalOrder(VerifyUtil.isListEmpty(tmpOrders) ? 1 : (tmpOrders.get(tmpOrders.size() - 1).getHospitalOrder() + 1));
            } else {
                order.setHospitalOrder(hospitalOrder);
            }
        }

        // get department max order (department order can be zero)
        order.setDepartmentId(departmentId);
        if (orderId>0) {
            if (departmentId>=0) {
                order.setDepartmentOrder(departmentOrder);
            }
        }
        else {
            if (departmentOrder < 0) {
                tmpOrders = orderRepository.findOrderByHospitalIdAndDepartmentId(hospitalId, departmentId, departmentSort);
                order.setDepartmentOrder(VerifyUtil.isListEmpty(tmpOrders) ? 1 : (tmpOrders.get(tmpOrders.size() - 1).getDepartmentOrder() + 1));
            } else {
                order.setDepartmentOrder(departmentOrder);
            }
        }

        order.setStatus(CommonStatus.ENABLED);
        order.setTime(new Date());
        order = orderRepository.save(order);

        DoctorOrderBean bean = orderBeanConverter.convert(order);
        logger.info("doctor order in department is=={}", bean);
        return bean;
    }
}

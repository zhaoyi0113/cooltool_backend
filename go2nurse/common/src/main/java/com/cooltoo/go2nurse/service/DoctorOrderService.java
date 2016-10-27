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
    public void changeTwoDoctorOrderInHospital(long firstOrderId, long secondOrderId) {
        logger.info("change two doctor order in hospital 1stId={}, 2ndId={}",
                firstOrderId, secondOrderId);
        DoctorOrderEntity _1st = orderRepository.findOne(firstOrderId);
        DoctorOrderEntity _2nd = orderRepository.findOne(secondOrderId);
        if (null==_1st || null==_2nd) {
            logger.error("the doctor order in hospital is not exist");
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
    public DoctorOrderBean addDoctorOrder(long doctorId, int hospitalId, int departmentId) {
        logger.info("set doctor={} order in hospital={} department={}");
        if (!doctorRepository.exists(doctorId)) {
            logger.error("doctor not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!hospitalRepository.existHospital(hospitalId)) {
            logger.error("hospital not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!departmentRepository.existsDepartment(departmentId)) {
            logger.error("department not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        HospitalDepartmentBean department = departmentRepository.deleteById(departmentId);
        if (department.getHospitalId()!=hospitalId) {
            logger.error("department not belong to the hospital");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        DoctorOrderEntity order = null;
        boolean existOrderInDepartment = false;

        List<DoctorOrderEntity> orders = orderRepository.findOrderByHospitalIdAndDoctorId(hospitalId, doctorId, hospitalSort);
        // order has exist
        if (!VerifyUtil.isListEmpty(orders)) {
            for (int i = 0; i < orders.size(); i++) {
                DoctorOrderEntity tmp = orders.get(i);
                if (tmp.getDepartmentId() == departmentId) {
                    order = orders.get(i);
                    orders.remove(i);
                    existOrderInDepartment = true;
                    break;
                }
                if (null==order) {
                    order = orders.get(0);
                    orders.remove(0);
                }
            }
        }
        // order not exist
        List<DoctorOrderEntity> tmpOrders = null;
        if (null==order) {
            order = new DoctorOrderEntity();
            order.setDoctorId(doctorId);

            // get hospital max order
            order.setHospitalId(hospitalId);
            tmpOrders = orderRepository.findOrderByHospitalId(hospitalId, hospitalSort);
            order.setHospitalOrder(VerifyUtil.isListEmpty(tmpOrders) ? 1 : (tmpOrders.get(tmpOrders.size()-1).getHospitalOrder() + 1));

            // get department max order
            order.setDepartmentId(departmentId);
            tmpOrders = orderRepository.findOrderByHospitalIdAndDepartmentId(hospitalId, departmentId, hospitalSort);
            order.setDepartmentOrder(VerifyUtil.isListEmpty(tmpOrders) ? 1 : (tmpOrders.get(tmpOrders.size()-1).getDepartmentOrder() + 1));

            // clean
            tmpOrders = null;
        }
        // order exist
        else {
            if (!existOrderInDepartment) {
                // get hospital max order
                order.setHospitalId(hospitalId);
                tmpOrders = orderRepository.findOrderByHospitalId(hospitalId, hospitalSort);
                order.setHospitalOrder(VerifyUtil.isListEmpty(tmpOrders) ? 1 : (tmpOrders.get(tmpOrders.size()-1).getHospitalOrder() + 1));

                // get department max order
                order.setDepartmentId(departmentId);
                tmpOrders = orderRepository.findOrderByHospitalIdAndDepartmentId(hospitalId, departmentId, hospitalSort);
                order.setDepartmentOrder(VerifyUtil.isListEmpty(tmpOrders) ? 1 : (tmpOrders.get(tmpOrders.size()-1).getDepartmentOrder() + 1));

                // clean
                tmpOrders = null;
            }
        }

        order.setStatus(CommonStatus.ENABLED);
        order.setTime(new Date());
        order = orderRepository.save(order);

        if (!VerifyUtil.isListEmpty(orders)) {
            orderRepository.delete(orders);
        }

        DoctorOrderBean bean = orderBeanConverter.convert(order);
        logger.info("doctor order in department is=={}", bean);
        return bean;
    }
}

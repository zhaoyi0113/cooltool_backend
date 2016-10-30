package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CategoryCourseOrderBean;
import com.cooltoo.go2nurse.beans.CategoryCoursesOrderGroup;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.converter.CategoryCourseBeanConverter;
import com.cooltoo.go2nurse.entities.CategoryCourseOrderEntity;
import com.cooltoo.go2nurse.repository.CategoryCourseOrderRepository;
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

import java.util.*;

/**
 * Created by zhaolisong on 2016/10/28.
 */
@Service("CategoryCourseOrderService")
public class CategoryCourseOrderService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryCourseOrderService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "hospitalId"),
            new Sort.Order(Sort.Direction.ASC, "departmentId"),
            new Sort.Order(Sort.Direction.ASC, "categoryId"),
            new Sort.Order(Sort.Direction.ASC, "order"),
            new Sort.Order(Sort.Direction.ASC, "id")
            );

    private static final Sort sortById = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private CategoryCourseOrderRepository repository;
    @Autowired private CategoryCourseBeanConverter beanConverter;

    @Autowired private CourseService courseService;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private CourseCategoryService categoryService;
    @Autowired private Go2NurseUtility utility;

    //==========================================================
    //               get
    //==========================================================
    public CategoryCourseOrderBean getOrderById(long orderId) {
        logger.info("get caurseOrder by orderId={}", orderId);
        CategoryCourseOrderEntity one = repository.findOne(orderId);
        if (null==one) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CategoryCourseOrderBean bean = beanConverter.convert(one);
        fillOtherProperties(Arrays.asList(new CategoryCourseOrderBean[]{ bean }));
        logger.info("order is {}", bean);
        return bean;
    }

    public long countOrderByConditions(Integer hospitalId, Integer departmentId, Long categoryId) {
        long count = repository.countOrderByConditions(hospitalId, departmentId, categoryId);
        logger.info("count course order by hospitalId={} departmentId={} categoryId={}, size={}",
                hospitalId, departmentId, categoryId, count);
        return count;
    }

    public List<CategoryCourseOrderBean> getOrderByConditions(Integer hospitalId, Integer departmentId, Long categoryId, Integer pageIndex, Integer sizePerPage) {
        logger.info("get course order by hospitalId={} departmentId={} categoryId={}, pageIndex={} sizePerPage={}",
                hospitalId, departmentId, categoryId, pageIndex, sizePerPage);
        PageRequest page;
        if (null==categoryId && null==departmentId) {
            page = new PageRequest(pageIndex, sizePerPage, sortById);
        }
        else {
            page = new PageRequest(pageIndex, sizePerPage, sort);
        }
        Page<CategoryCourseOrderEntity> entities = repository.findOrderByConditions(hospitalId, departmentId, categoryId, page);
        List<CategoryCourseOrderBean> beans = entitiesToBean(entities);
        fillOtherProperties(beans);
        return beans;
    }

    public Map<CategoryCoursesOrderGroup, List<Long>> getCategoryGroupToCourseIdsSorted(int hospital, int department, List<Long> categories) {
        logger.info("get categoryId--courseIdsSorted by hospital={} department={} category={}", hospital, department, categories);

        // get courses order sorted
        List<CategoryCourseOrderEntity> entities = null;
        if (!VerifyUtil.isListEmpty(categories)) {
            entities = repository.findOrderByHospitalIdAndDepartmentIdAndCategoryIdIn(
                    hospital, department, categories, sort);
        }
        Map<CategoryCoursesOrderGroup, List<Long>> result = parseCategoryGroup(entities);
        logger.info("get categoryId--courseIdsSorted size={}", result.size());
        return result;
    }

    private Map<CategoryCoursesOrderGroup, List<Long>> parseCategoryGroup(Iterable<CategoryCourseOrderEntity> entities) {
        Map<CategoryCoursesOrderGroup, List<Long>> result = new HashMap<>();
        if (null==entities) {
            return result;
        }

        CategoryCoursesOrderGroup group = new CategoryCoursesOrderGroup();
        for (CategoryCourseOrderEntity tmp : entities) {
            // is group changed
            if (tmp.getHospitalId()!=group.getHospitalId()
                    || tmp.getDepartmentId()!=group.getDepartmentId()
                    ||tmp.getCategoryId()!=group.getCategoryId()) {
                group.setHospitalId(tmp.getHospitalId());
                group.setDepartmentId(tmp.getDepartmentId());
                group.setCategoryId(tmp.getCategoryId());
                group.resetHashCode();
            }

            // not contain group
            if (!result.containsKey(group)) {
                CategoryCoursesOrderGroup cloneGroup = group.clone();
                result.put(cloneGroup, new ArrayList<>());
            }

            // set courseId sorted
            List<Long> courseIdsSorted = result.get(group);
            if (!courseIdsSorted.contains(tmp.getCourseId())) {
                courseIdsSorted.add(tmp.getCourseId());
            }
        }
        return result;
    }

    private List<CategoryCourseOrderBean> entitiesToBean(Iterable<CategoryCourseOrderEntity> entities) {
        List<CategoryCourseOrderBean> list = new ArrayList<>();
        if (null==entities) {
            return list;
        }
        for (CategoryCourseOrderEntity tmp : entities) {
            CategoryCourseOrderBean bean = beanConverter.convert(tmp);
            list.add(bean);
        }
        return list;
    }

    private void fillOtherProperties(List<CategoryCourseOrderBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long>    courseIds     = new ArrayList<>();
        List<Long>    categoryIds   = new ArrayList<>();
        List<Integer> hospitalIds   = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (CategoryCourseOrderBean bean : beans) {
            if (!courseIds.contains(bean.getCourseId())) {
                courseIds.add(bean.getCourseId());
            }
            if (!categoryIds.contains(bean.getCategoryId())) {
                categoryIds.add(bean.getCategoryId());
            }
            if (!hospitalIds.contains(bean.getHospitalId())) {
                hospitalIds.add(bean.getHospitalId());
            }
            if (!departmentIds.contains(bean.getDepartmentId())) {
                departmentIds.add(bean.getDepartmentId());
            }
        }

        Map<Long, CourseBean> courseIdToBean = courseService.getCourseMapByStatusAndIds(null, courseIds, null, null);
        Map<Long, CourseCategoryBean> categoryIdToBean = categoryService.getIdToBeanByStatusAndIds(null, categoryIds);
        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalIds);
        Map<Integer, HospitalDepartmentBean> departIdToBean = departmentService.getDepartmentIdToBean(departmentIds, utility.getHttpPrefixForNurseGo());

        for (CategoryCourseOrderBean bean : beans) {
            CourseBean tmp1 = courseIdToBean.get(bean.getCourseId());
            bean.setCourse(tmp1);
            CourseCategoryBean tmp2 = categoryIdToBean.get(bean.getCategoryId());
            bean.setCategory(tmp2);
            HospitalBean tmp3 = hospitalIdToBean.get(bean.getHospitalId());
            bean.setHospital(tmp3);
            HospitalDepartmentBean tmp4 = departIdToBean.get(bean.getDepartmentId());
            bean.setDepartment(tmp4);
        }
    }

    //==========================================================
    //               delete
    //==========================================================
    @Transactional
    public void deleteOrder(long orderId) {
        logger.info("delete course order with orderId={}", orderId);
        CategoryCourseOrderEntity entity = repository.findOne(orderId);
        if (null==entity) {
            logger.error("the course order is not exist");
            return;
        }
        repository.delete(entity);
        return;
    }

    //==========================================================
    //               update
    //==========================================================
    @Transactional
    public void changeTwoCategoryOrderInCategory(long firstOrderId, long secondOrderId) {
        logger.info("change two course order in department 1stId={}, 2ndId={}",
                firstOrderId, secondOrderId);
        CategoryCourseOrderEntity _1st = repository.findOne(firstOrderId);
        CategoryCourseOrderEntity _2nd = repository.findOne(secondOrderId);
        if (null==_1st || null==_2nd) {
            logger.error("the course order in department is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (   _1st.getHospitalId()==_2nd.getHospitalId()
            && _1st.getDepartmentId()==_2nd.getDepartmentId()
            && _1st.getCategoryId()==_2nd.getCategoryId()) {

            int swapOrder1 = _1st.getOrder();
            int swapOrder2 = _2nd.getOrder();
            _1st.setOrder(swapOrder2);
            _2nd.setOrder(swapOrder1);
            repository.save(_1st);
            repository.save(_2nd);

            return;
        }

        logger.error("the two order belong to different hospital or department or category");
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    //==========================================================
    //               add
    //==========================================================
    @Transactional
    public long setCourseOrder(int departmentId, long categoryId, long courseId, int order) {
        logger.info("add course={} order in department={}_categroy={} with order={}",
                courseId, departmentId, categoryId, order);
        int hospitalId = (-1==departmentId) ? -1 : 0; /* -1 : cooltoo */
        departmentId = (-1==departmentId) ? 0 : departmentId;
        if (-1!=hospitalId && !departmentService.existsDepartment(departmentId)) {
            logger.error("department not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!categoryService.existsCategory(categoryId)) {
            logger.error("category not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!courseService.existCourse(courseId)) {
            logger.error("course not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // get hospitalId if not cooltoo
        if (0!=departmentId) {
            HospitalDepartmentBean department = departmentService.getById(departmentId, null);
            hospitalId = department.getHospitalId();
        }

        List<CategoryCourseOrderEntity> existedOrders = repository.findOrderByHospitalIdAndDepartmentIdAndCategoryIdAndCourseId(
            hospitalId, departmentId, categoryId, courseId
        );
        CategoryCourseOrderEntity one = VerifyUtil.isListEmpty(existedOrders)
                ? new CategoryCourseOrderEntity()
                : existedOrders.get(0);
        one.setHospitalId(hospitalId);
        one.setDepartmentId(departmentId);
        one.setCategoryId(categoryId);
        one.setCourseId(courseId);
        one.setOrder(order);
        one.setStatus(CommonStatus.ENABLED);
        one.setTime(new Date());
        one = repository.save(one);


        existedOrders = repository.findOrderByHospitalIdAndDepartmentIdAndCategoryIdAndCourseId(
                hospitalId, departmentId, categoryId, courseId
        );
        for (int i = 0; i < existedOrders.size(); i ++) {
            CategoryCourseOrderEntity tmpDep = existedOrders.get(i);
            if (tmpDep.getId() == one.getId()) {
                existedOrders.remove(i);
                break;
            }
        }

        if (!VerifyUtil.isListEmpty(existedOrders)) {
            repository.delete(existedOrders);
        }

        return one.getId();
    }
}

package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.CategoryCourseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/28.
 */
public interface CategoryCourseOrderRepository extends JpaRepository<CategoryCourseOrderEntity, Long> {

    List<CategoryCourseOrderEntity> findOrderByHospitalIdAndDepartmentIdAndCategoryIdAndCourseId(
            int hospitalId, int departmentId, long categoryId, long courseId
    );

    List<CategoryCourseOrderEntity> findOrderByCourseIdIn(List<Long> courseIds, Sort sort);

    @Query("SELECT count(order.id) FROM CategoryCourseOrderEntity order" +
            " WHERE (?1 IS NULL OR order.hospitalId)" +
            " AND   (?2 IS NULL OR order.departmentId)" +
            " AND   (?3 IS NULL OR order.categoryId)")
    long countOrderByConditions(Integer hospitalId, Integer departmentId, Long categoryId);

    @Query("FROM CategoryCourseOrderEntity order" +
            " WHERE (?1 IS NULL OR order.hospitalId)" +
            " AND   (?2 IS NULL OR order.departmentId)" +
            " AND   (?3 IS NULL OR order.categoryId)")
    Page<CategoryCourseOrderEntity> findOrderByConditions(Integer hospitalId, Integer departmentId, Long categoryId, Pageable page);
}

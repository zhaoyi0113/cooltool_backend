package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.ServiceVendorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/7/19.
 */
public interface ServiceVendorRepository extends JpaRepository<ServiceVendorEntity, Long> {

    long countByStatusIn(List<CommonStatus> status);
    Page<ServiceVendorEntity> findByStatusIn(List<CommonStatus> status, Pageable page);
}

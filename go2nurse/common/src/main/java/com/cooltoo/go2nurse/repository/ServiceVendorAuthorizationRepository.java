package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.NursePatientRelationEntity;
import com.cooltoo.go2nurse.entities.ServiceVendorAuthorizationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 2016/12/1.
 */
public interface ServiceVendorAuthorizationRepository extends JpaRepository<ServiceVendorAuthorizationEntity, Long> {

    List<ServiceVendorAuthorizationEntity> findByStatusAndVendorTypeAndVendorIdAndDepartId(CommonStatus status, ServiceVendorType vendorType, long vendorId, long departId);
    List<ServiceVendorAuthorizationEntity> findByUserIdAndVendorTypeAndVendorIdAndDepartId(long userId, ServiceVendorType vendorType, long vendorId, long departId);
}

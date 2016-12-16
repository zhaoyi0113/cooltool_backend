package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.WhoDenyPatient;
import com.cooltoo.go2nurse.entities.DenyPatientEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 14/12/2016.
 */
public interface DenyPatientRepository extends JpaRepository<DenyPatientEntity, Long> {

    @Query("FROM DenyPatientEntity dp" +
            " WHERE (?1 IS NULL OR ?1=dp.whoDenyPatient)" +
            "   AND (?2 IS NULL OR ?2=dp.nurseId)" +
            "   AND (?3 IS NULL OR ?3=dp.vendorType)" +
            "   AND (?4 IS NULL OR ?4=dp.vendorId)" +
            "   AND (?5 IS NULL OR ?5=dp.departId)" +
            "   AND (?6 IS NULL OR ?6=dp.userId)" +
            "   AND (?7 IS NULL OR ?7=dp.patientId)")
    List<DenyPatientEntity> findByConditions(WhoDenyPatient whoDenyPatient,
                                             Long nurseId,
                                             ServiceVendorType vendorType, Long vendorId, Long departId,
                                             Long userId, Long patientId, Sort sort);

}

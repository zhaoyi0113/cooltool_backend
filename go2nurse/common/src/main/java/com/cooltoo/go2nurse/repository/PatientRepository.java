package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.PatientEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by yzzhao on 2/29/16.
 */
public interface PatientRepository extends CrudRepository<PatientEntity, Long>{

}

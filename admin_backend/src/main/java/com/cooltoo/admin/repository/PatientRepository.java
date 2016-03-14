package com.cooltoo.admin.repository;

import com.cooltoo.admin.entities.PatientEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by yzzhao on 2/29/16.
 */
public interface PatientRepository extends CrudRepository<PatientEntity, Long>{

}

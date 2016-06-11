package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.DiagnosticPointEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 6/10/16.
 */

public interface DiagnosticPointRepository extends CrudRepository<DiagnosticPointEntity, Long>{

    List<DiagnosticPointEntity> findByDorderGreaterThanOrderByDorder(int order);

    List<DiagnosticPointEntity> findByDorderLessThanOrderByDorder(int order);
}

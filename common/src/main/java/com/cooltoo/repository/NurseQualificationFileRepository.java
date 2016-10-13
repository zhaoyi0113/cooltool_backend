package com.cooltoo.repository;

import com.cooltoo.entities.NurseQualificationFileEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by hp on 2016/4/4.
 */
public interface NurseQualificationFileRepository extends CrudRepository<NurseQualificationFileEntity, Long> {

    List<NurseQualificationFileEntity> findByQualificationId(long qualificationId, Sort sort);
    List<NurseQualificationFileEntity> findByQualificationIdIn(List<Long> qualificationIds, Sort sort);
    void deleteByQualificationId(long qualificationId);
    long countByQualificationIdAndWorkfileTypeId(long qualificationId, int workfileTypeId);
}

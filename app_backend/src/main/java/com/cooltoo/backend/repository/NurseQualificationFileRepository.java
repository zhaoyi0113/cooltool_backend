package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseQualificationFileEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by hp on 2016/4/4.
 */
public interface NurseQualificationFileRepository extends CrudRepository<NurseQualificationFileEntity, Long> {

    List<NurseQualificationFileEntity> findByQualificationId(long qualificationId, Sort sort);

    void deleteByQualificationId(long qualificationId);

    long countByQualificationIdAndWorkfileTypeId(long qualificationId, int workfileTypeId);
}

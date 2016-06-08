package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.Go2NurseFileStorageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/8.
 */
public interface Go2NurseFileStorageRepository extends JpaRepository<Go2NurseFileStorageEntity, Long> {

    @Query("SELECT file.id, file.relativePath FROM Go2NurseFileStorageEntity file" +
            " WHERE file.id IN (?1)")
    List<Object[]> findIdAndPathByIdIn(List<Long> ids);

    void deleteByIdIn(List<Long> ids);
}

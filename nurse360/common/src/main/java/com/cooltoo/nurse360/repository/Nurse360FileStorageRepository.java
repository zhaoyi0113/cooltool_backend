package com.cooltoo.nurse360.repository;

import com.cooltoo.nurse360.entities.Nurse360FileStorageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/10/9.
 */
public interface Nurse360FileStorageRepository extends JpaRepository<Nurse360FileStorageEntity, Long> {

    @Query("SELECT file.id, file.relativePath FROM Nurse360FileStorageEntity file" +
            " WHERE file.id IN (?1)")
    List<Object[]> findIdAndPathByIdIn(List<Long> ids);

    void deleteByIdIn(List<Long> ids);
}

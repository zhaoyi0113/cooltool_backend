package com.cooltoo.repository;

import com.cooltoo.entities.FileStorageEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 2/26/16.
 */
public interface FileStorageRepository extends CrudRepository<FileStorageEntity, Long>{

    public List<FileStorageEntity> findStorageByIdIn(List<Long> ids, Sort sort);
}

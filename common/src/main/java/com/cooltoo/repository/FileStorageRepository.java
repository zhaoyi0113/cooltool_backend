package com.cooltoo.repository;

import com.cooltoo.entities.FileStorageEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 2/26/16.
 */
public interface FileStorageRepository extends CrudRepository<FileStorageEntity, Long>{

    FileStorageEntity findByFilePath(String filePath);
    List<FileStorageEntity> findByIdIn(List<Long> ids, Sort sort);
    List<FileStorageEntity> findByFilePathIn(List<String> filePaths, Sort sort);
    void deleteByIdIn(List<Long> ids);

}

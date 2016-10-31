package com.cooltoo.services;

import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.repository.FileStorageRepository;
import com.cooltoo.services.file.InterfaceFileStorageDB;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/4/25.
 */
@Service("FileStorageDBService")
public class FileStorageDBService implements InterfaceFileStorageDB {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageDBService.class.getName());

    @Autowired
    private FileStorageRepository repository;

    @Transactional
    public long addRecord(String fileName, String path) {
        logger.info("save to database the filename={} path={}", fileName, path);
        FileStorageEntity entity = new FileStorageEntity();
        entity.setFileRealname(fileName);
        entity.setFilePath(path);
        entity = repository.save(entity);
        logger.info("file storage id={}", entity.getId());
        return entity.getId();
    }

    public String getFilePath(long recordFileStorageId) {
        logger.info("get file path by id={}", recordFileStorageId);
        String filePath = "";
        FileStorageEntity resultSet = repository.findOne(recordFileStorageId);
        if (null!=resultSet) {
            filePath = resultSet.getFilePath();
        }
        logger.info("file path={}", filePath);
        return filePath;
    }

    public Map<Long, String> getFilePath(List<Long> recordFileStorageIds) {
        logger.info("get file path by count={}, ids={}", recordFileStorageIds.size(), recordFileStorageIds);
        Map<Long, String> ret = new HashMap<>();
        if (!VerifyUtil.isListEmpty(recordFileStorageIds)) {
            Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
            List<FileStorageEntity> resultSet =  repository.findByIdIn(recordFileStorageIds, sort);
            if (!VerifyUtil.isListEmpty(resultSet)) {
                for (FileStorageEntity tmp : resultSet) {
                    ret.put(tmp.getId(), tmp.getFilePath());
                }
            }
        }
        logger.info("file path count={}", ret.size());
        return ret;
    }

    @Transactional
    public void deleteRecord(long recordFileStorageId) {
        logger.info("delete file path by id={}", recordFileStorageId);
        if (!repository.exists(recordFileStorageId)) {
            logger.info("record not exist");
            return;
        }
        repository.delete(recordFileStorageId);
    }

    @Transactional
    public void deleteRecord(List<Long> recordFileStorageIds) {
        logger.info("delete file path by ids={}", recordFileStorageIds);
        if (VerifyUtil.isListEmpty(recordFileStorageIds)) {
            return;
        }
        repository.deleteByIdIn(recordFileStorageIds);
    }
}

package com.cooltoo.nurse360.service;

import com.cooltoo.nurse360.entities.Nurse360FileStorageEntity;
import com.cooltoo.nurse360.repository.Nurse360FileStorageRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/10/9.
 */
@Service("FileStorageDBServiceForNurse360")
public class FileStorageDBServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageDBServiceForNurse360.class);

    @Autowired private Nurse360FileStorageRepository repository;

    @Transactional
    public long recordFileStorage(String fileName, String path) {
        logger.info("save to database the filename={} path={}", fileName, path);
        Nurse360FileStorageEntity entity = new Nurse360FileStorageEntity();
        entity.setRealName(fileName);
        entity.setRelativePath(path);
        entity = repository.save(entity);
        logger.info("file storage id={}", entity.getId());
        return entity.getId();
    }

    public String getFilePath(long recordFileStorageId) {
        logger.info("get file path by id={}", recordFileStorageId);
        String filePath = "";
        Nurse360FileStorageEntity resultSet = repository.findOne(recordFileStorageId);
        if (null!=resultSet) {
            filePath = resultSet.getRelativePath();
        }
        logger.info("file path={}", filePath);
        return filePath;
    }

    public Map<Long, String> getFilePath(List<Long> recordFileStorageIds) {
        logger.info("get file path by ids, ids count={}", recordFileStorageIds.size());
        Map<Long, String> ret = new HashMap<>();
        if (null!=recordFileStorageIds && !recordFileStorageIds.isEmpty()) {
            List<Object[]> resultSet =  repository.findIdAndPathByIdIn(recordFileStorageIds);
            if (null!=resultSet && !resultSet.isEmpty()) {
                for (Object[] tmpArray : resultSet) {
                    Object id = tmpArray[0];
                    Object path = tmpArray[1];
                    if (id instanceof Long && path instanceof String) {
                        ret.put((Long)id, (String)path);
                    }
                }
            }
        }
        logger.info("result count={}", ret.size());
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

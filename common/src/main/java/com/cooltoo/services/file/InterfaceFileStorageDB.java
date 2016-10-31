package com.cooltoo.services.file;

import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/10/31.
 */
public interface IterfaceFileStorageDB {

    public String getFilePath(long recordFileStorageId);
    public Map<Long, String> getFilePath(List<Long> recordFileStorageIds);

    public void deleteRecord(long recordFileStorageId);
    public void deleteRecord(List<Long> recordFileStorageIds);

    public long addRecord(String fileName, String path);
}

package com.cooltoo.go2nurse.service.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.file.AbstractFileStorageService;
import com.cooltoo.services.file.InterfaceFileStorageDB;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/26.
 */
@Service("UserGo2NurseFileStorageService")
public class UserGo2NurseFileStorageService extends AbstractFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(UserGo2NurseFileStorageService.class.getName());

    @Value("${go2nurse.storage.user.path}")
    private String userPath;

    @Value("${go2nurse.storage.base.path}")
    private String storageBasePath;

    @Autowired
    private Go2NurseUtility utility;

    @Autowired
    @Qualifier("Go2NurseFileStorageService")
    private InterfaceFileStorageDB dbService;

    @Override
    public InterfaceFileStorageDB getDbService() {
        return dbService;
    }

    @Override
    public String getName() {
        return "user";
    }

    @Override
    public String getStoragePath() {
        StringBuilder path = new StringBuilder();
        path.append(storageBasePath).append(userPath);
        logger.info("get user storage path={}", path.toString());
        return path.toString();
    }

    @Override
    public String getNginxRelativePath() {
        logger.info("get user nginx path={}", userPath);
        return userPath;
    }

    /** srcFileAbsolutePath---->dir/sha1 */
    @Override
    public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath) {
        return fileUtil.moveFilesToDest(srcFileAbsolutePath, this, true);
    }

    public File createFileInBaseStorage(String prefix, String suffix) {
        String uniqueId = NumberUtil.timeToString(new Date(), NumberUtil.DATE_YYYY_MM_DD);
        uniqueId = uniqueId+"_"+System.nanoTime();
        if (!VerifyUtil.isStringEmpty(prefix)) {
            uniqueId = prefix.trim()+uniqueId;
        }
        if (!VerifyUtil.isStringEmpty(suffix)) {
            uniqueId = uniqueId+suffix;
        }
        File file = new File(storageBasePath+uniqueId);
        return file;
    }

    public String getFileURL(long fileId) {
        return super.getFileURL(fileId, utility.getHttpPrefix());
    }

    public Map<Long, String> getFileUrl(List<Long> fileIds) {
        return super.getFileUrl(fileIds, utility.getHttpPrefix());
    }
}

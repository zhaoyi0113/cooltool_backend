package com.cooltoo.services.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.FileStorageDBService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/4/25.
 */
@Service("OfficialFileStorageService")
public class OfficialFileStorageService extends AbstractFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(OfficialFileStorageService.class.getName());

    @Value("${storage.official.path}")
    private String officialPath;

    @Value("${storage.official.speak.profile.photo.path}")
    private String officialSpeakProfilePhotoPath;

    @Value("${storage.base.path}")
    private String storageBasePath;

    @Autowired
    @Qualifier("FileStorageDBService")
    private InterfaceFileStorageDB dbService;

    public InterfaceFileStorageDB getDbService() {
        return dbService;
    }

    public String getOfficialSpeakProfilePhotoPath() {
        StringBuilder path = new StringBuilder();
        path.append(storageBasePath);
        path.append(officialSpeakProfilePhotoPath);
        logger.info("get official speak profile photo path={}", path.toString());
        return path.toString();
    }

    public String getOfficalSpeakProfilePhotoNginxRelativePath() {
        String retVal = getNginxRelativePath() + officialSpeakProfilePhotoPath;
        logger.info("get official speak profile photo nginx relative path={}", retVal);
        return retVal;
    }

    @Override
    public String getName() {
        return "official";
    }

    @Override
    public String getStoragePath() {
        StringBuilder path = new StringBuilder();
        path.append(storageBasePath);
        path.append(officialPath);
        logger.info("get official storage path={}", path.toString());
        return path.toString();
    }

    @Override
    public String getNginxRelativePath() {
        logger.info("get official nginx path={}", officialPath);
        return officialPath;
    }

    @Override
    public boolean deleteFile(String filePath) {
        logger.info("delete official file, filepath={}", filePath);
        if (VerifyUtil.isStringEmpty(filePath)) {
            logger.info("filepath is empty");
            return true;
        }
        String[] baseurlDirSha1 = fileUtil.decodeFilePath(filePath);
        if (VerifyUtil.isStringEmpty(baseurlDirSha1[1]) && VerifyUtil.isStringEmpty(baseurlDirSha1[2])) {
            logger.info("decode dir and filename is empty");
            return false;
        }
        return super.deleteFile(baseurlDirSha1[1] + File.separator + baseurlDirSha1[2]);
    }

    /** srcFileAbsolutePath---->dir/sha1 */
    @Override
    public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath) {
        return fileUtil.moveFilesToDest(srcFileAbsolutePath, this, true);
    }
}

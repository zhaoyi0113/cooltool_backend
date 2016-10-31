package com.cooltoo.services.file;

import com.cooltoo.services.FileStorageDBService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/4/25.
 */
@Service("SecretFileStorageService")
public class SecretFileStorageService extends AbstractFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(SecretFileStorageService.class.getName());

    @Value("${storage.secret.path}")
    private String secretPath;

    @Value("${storage.base.path}")
    private String storageBasePath;

    @Autowired
    @Qualifier("FileStorageDBService")
    private InterfaceFileStorageDB dbService;

    public InterfaceFileStorageDB getDbService() {
        return dbService;
    }

    @Override
    public String getName() {
        return "secret";
    }

    @Override
    public String getStoragePath() {
        StringBuilder path = new StringBuilder();
        path.append(storageBasePath);
        path.append(secretPath);
        logger.info("get secret storage path={}", path.toString());
        return path.toString();
    }

    @Override
    public String getNginxRelativePath() {
        logger.info("get secret nginx path={}", secretPath);
        return secretPath;
    }

    @Deprecated @Override public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath) {
        throw new UnsupportedOperationException();
    }

    /** delete file path(which end with relative path in its storage) */
    @Override
    public boolean deleteFile(String filePath) {
        logger.info("delete secret file, filepath={}", filePath);
        if (VerifyUtil.isStringEmpty(filePath)) {
            logger.info("filepath is empty");
            return true;
        }
        String relativePath = getRelativePathInStorage(filePath);
        if (VerifyUtil.isStringEmpty(relativePath)) {
            logger.info("decode dir and filename is empty");
            return false;
        }
        return super.deleteFile(relativePath);
    }
}

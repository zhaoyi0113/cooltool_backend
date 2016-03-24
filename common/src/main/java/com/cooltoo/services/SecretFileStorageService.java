package com.cooltoo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by hp on 2016/3/24.
 */
@Service("SecretFileStorageService")
public class SecretFileStorageService extends StorageService {

    @Value("${storage.secret.path}")
    private String storagePath;


    @Value("${storage.secret.url}")
    private String storageUrl;

    @Override
    public String getStorageUrl() {
        return this.storageUrl;
    }

    @Override
    public String getStoragePath() {
        return this.storagePath;
    }
}

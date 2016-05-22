package com.cooltoo.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.PlatformType;
import com.cooltoo.entities.PlatformVersionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by yzzhao on 5/22/16.
 */
public interface PlatformVersionRepository extends CrudRepository<PlatformVersionEntity, Integer> {

    List<PlatformVersionEntity> findByPlatformType(PlatformType type);

    List<PlatformVersionEntity> findByPlatformTypeAndVersion(PlatformType type, String version);

    List<PlatformVersionEntity> findByPlatformTypeAndStatusOrderByTimeCreatedDesc(PlatformType type, CommonStatus status);
}

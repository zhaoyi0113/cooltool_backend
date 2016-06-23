package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.VideoInSpeakBean;
import com.cooltoo.backend.converter.VideoInSpeakBeanConverter;
import com.cooltoo.backend.entities.VideoInSpeakEntity;
import com.cooltoo.backend.repository.VideoInSpeakRepository;
import com.cooltoo.constants.CCVideoStatus;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/14.
 */
@Service("VideoInSpeakService")
public class VideoInSpeakService {

    private static final Logger logger = LoggerFactory.getLogger(VideoInSpeakService.class.getName());

    private static final Sort sort = new Sort(
                                            new Sort.Order(Sort.Direction.ASC, "speakId"),
                                            new Sort.Order(Sort.Direction.ASC, "id")
                                     );

    @Autowired private VideoInSpeakRepository repository;
    @Autowired private VideoInSpeakBeanConverter beanConverter;
    @Autowired private UserFileStorageService userStorage;

    //================================================================
    //            get
    //================================================================

    public long countVideoInSpeak(long speakId) {
        logger.info("get video count in speak {}", speakId);
        long count = repository.countBySpeakId(speakId);
        logger.info("get video count {} in speak {}", count, speakId);
        return count;
    }

    public List<VideoInSpeakBean> getVideoInSpeak(Long speakId) {
        List<Long> speakIds = new ArrayList<Long>();
        speakIds.add(speakId);
        Map<Long, List<VideoInSpeakBean>> videoInSpeak = getVideoInSpeak(speakIds);
        return videoInSpeak.get(speakId);
    }

    public Map<Long, List<VideoInSpeakBean>> getVideoInSpeak(List<Long> speakIds) {
        if (VerifyUtil.isListEmpty(speakIds)) {
            return new HashMap<>();
        }

        List<VideoInSpeakEntity> resultSet = repository.findBySpeakIdIn(speakIds, sort);
        List<VideoInSpeakBean> images = entitiesToBeans(resultSet);
        fillOtherProperties(images);
        Map<Long, List<VideoInSpeakBean>> retValue  = listToMap(images);
        return retValue;
    }

    private Map<Long, List<VideoInSpeakBean>> listToMap(List<VideoInSpeakBean> videoInSpeak) {
        if (VerifyUtil.isListEmpty(videoInSpeak)) {
            return new HashMap<>();
        }

        Map<Long, List<VideoInSpeakBean>> speakId2Map = new HashMap<>();
        for (VideoInSpeakBean bean : videoInSpeak) {
            List<VideoInSpeakBean> videos = speakId2Map.get(bean.getSpeakId());
            if (null==videos) {
                videos = new ArrayList<>();
                speakId2Map.put(bean.getSpeakId(), videos);
            }
            videos.add(bean);
        }

        return speakId2Map;
    }

    private List<VideoInSpeakBean> entitiesToBeans(Iterable<VideoInSpeakEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<VideoInSpeakBean> beans = new ArrayList<>();
        for (VideoInSpeakEntity tmp : entities) {
            VideoInSpeakBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<VideoInSpeakBean> videoInSpeak) {
        if (VerifyUtil.isListEmpty(videoInSpeak)) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (VideoInSpeakBean tmp : videoInSpeak) {
            if (tmp.getBackground()>0) {
                imageIds.add(tmp.getBackground());
            }
            if (tmp.getSnapshot()>0) {
                imageIds.add(tmp.getSnapshot());
            }
        }

        Map<Long, String> imageId2Path = userStorage.getFilePath(imageIds);
        String imagePath;
        for (VideoInSpeakBean tmp : videoInSpeak) {
            if (tmp.getBackground()>0) {
                imagePath = imageId2Path.get(tmp.getBackground());
                tmp.setBackgroundUrl(imagePath);
            }
            if (tmp.getSnapshot()>0) {
                imagePath = imageId2Path.get(tmp.getSnapshot());
                tmp.setSnapshotUrl(imagePath);
            }
        }
    }

    //====================================================
    //          delete
    //====================================================

    public VideoInSpeakBean deleteById(long videoInSpeakId) {
        logger.info("delete video in speak by id {}", videoInSpeakId);
        VideoInSpeakEntity video = repository.findOne(videoInSpeakId);
        if (null==video) {
            logger.warn("video in speak is not exist");
            return new VideoInSpeakBean();
        }

        userStorage.deleteFile(video.getBackground());
        userStorage.deleteFile(video.getSnapshot());
        repository.delete(video);

        return beanConverter.convert(video);
    }

    @Transactional
    public Map<Long, List<VideoInSpeakBean>> deleteBySpeakIds(List<Long> speakIds) {
        if (null==speakIds || speakIds.isEmpty()) {
            return new HashMap<>();
        }

        List<VideoInSpeakEntity> videos = repository.findBySpeakIdIn(speakIds);
        if (null==videos || videos.isEmpty()) {
            return new HashMap<>();
        }

        List<Long> imageIds = new ArrayList<>();
        for (VideoInSpeakEntity tmp : videos) {
            if (tmp.getBackground()>0) {
                imageIds.add(tmp.getBackground());
            }
            if (tmp.getSnapshot()>0) {
                imageIds.add(tmp.getSnapshot());
            }
        }
        userStorage.deleteFiles(imageIds);
        repository.delete(videos);

        List<VideoInSpeakBean>            beans = entitiesToBeans(videos);
        Map<Long, List<VideoInSpeakBean>> map   = listToMap(beans);
        return map;
    }

    //====================================================
    //          update
    //====================================================
    @Transactional
    public List<VideoInSpeakBean> updateVideoStatus(String videoId, String strStatus) {
        logger.info("update video status by videoId={} to status={}", videoId, strStatus);
        CCVideoStatus status = CCVideoStatus.parseString(strStatus);
        if (null==status) {
            logger.error("status is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<VideoInSpeakEntity> entities = repository.findByVideoId(videoId);
        if (!VerifyUtil.isListEmpty(entities)) {
            for (VideoInSpeakEntity entity : entities) {
                entity.setVideoStatus(status);
            }
            Iterable<VideoInSpeakEntity> savedEntities = repository.save(entities);
            List<VideoInSpeakBean> beans = entitiesToBeans(savedEntities);
            return beans;
        }
        return new ArrayList<>();
    }

    //====================================================
    //          add
    //====================================================
    @Transactional
    public VideoInSpeakBean addVideo(long speakId, String videoId,
                                      String backgroundName , InputStream background,
                                      String snapshotName, InputStream snapshot) {
        logger.info("add video={} and backgroundImage={}->{} snapshotImage={}->{} to speak {}",
                videoId, backgroundName, null!=background, snapshotName, null!=snapshot, speakId);

        if (VerifyUtil.isStringEmpty(videoId)) {
            logger.error("video is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long backgroundId = 0;
        String backgroundUrl = "";
        if (null!=background) {
            if (VerifyUtil.isStringEmpty(backgroundName)) {
                backgroundName = "video_speak_back_" + System.nanoTime();
            }
            backgroundId   = userStorage.addFile(-1, backgroundName, background);
            backgroundUrl = userStorage.getFilePath(backgroundId);
        }

        long snapshotId = 0;
        String snapshotUrl = "";
        if (null!=snapshot) {
            if (VerifyUtil.isStringEmpty(snapshotName)) {
                snapshotName = "video_speak_snap_" + System.nanoTime();
            }
            snapshotId = userStorage.addFile(-1, snapshotName, snapshot);
            snapshotUrl = userStorage.getFilePath(snapshotId);
        }

        VideoInSpeakEntity entity = new VideoInSpeakEntity();
        entity.setSpeakId(speakId);
        entity.setVideoId(videoId);
        if (backgroundId>0) {
            entity.setBackground(backgroundId);
        }
        if (snapshotId>0) {
            entity.setSnapshot(snapshotId);
        }

        entity.setVideoStatus(CCVideoStatus.OTHER);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);

        entity = repository.save(entity);

        VideoInSpeakBean bean = beanConverter.convert(entity);
        bean.setBackgroundUrl(backgroundUrl);
        bean.setSnapshotUrl(snapshotUrl);
        return bean;
    }

    //=====================================================================================
    //                check video status from CC server
    //=====================================================================================
}

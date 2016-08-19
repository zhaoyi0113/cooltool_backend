package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.VideoInSpeakBean;
import com.cooltoo.backend.converter.VideoInSpeakBeanConverter;
import com.cooltoo.backend.entities.VideoInSpeakEntity;
import com.cooltoo.backend.repository.VideoInSpeakRepository;
import com.cooltoo.constants.CCVideoStatus;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.VideoPlatform;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

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

    @Value("${nursego.qiniu.access.key}")
    private String qiniuAccessKey;
    @Value("${nursego.qiniu.secret.key}")
    private String qiniuSecretKey;
    @Value("${nursego.qiniu.video.domain}")
    private String qiniuDomain;
    @Value("${nursego.qiniu.video.callback.url}")
    private String qiniuCallbackUrl;
    @Value("${nursego.qiniu.video.callback.body}")
    private String qiniuCallbackBody;
    @Value("${nursego.qiniu.video.bucket}")
    private String bucketName;


    private static Auth QiNiuAuth = null;

    //================================================================
    //            get
    //================================================================
    public String getBucketName() {
        return bucketName;
    }

    private Auth getQiNiuAuth() {
        if (null==QiNiuAuth) {
            QiNiuAuth = Auth.create(qiniuAccessKey, qiniuSecretKey);
        }
        return QiNiuAuth;
    }

    public boolean isValidQiNiuCallback(String authority, String contentType, String callbackBody) {
        logger.info("Is valid 7-Niu callback by original_authority={} contentType={} callbackBody={} callbackUrl={}?",
                authority, contentType, callbackBody, qiniuCallbackUrl);
        if (VerifyUtil.isStringEmpty(authority)) {

        }
        Auth auth = getQiNiuAuth();
        boolean valid = auth.isValidCallback(authority, qiniuCallbackUrl, callbackBody.getBytes(), contentType);
        logger.info("Is valid? {}", valid);
        return valid;
    }

    public String getQiNiuAuthorityToken(String key, String bucketName) {
        logger.info("get 7niu token by key={} bucketName={}", key, bucketName);
        if (VerifyUtil.isStringEmpty(key)) {
            logger.error("key is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(bucketName)) {
            logger.error("bucketName is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        Auth auth = getQiNiuAuth();
        StringMap map = new StringMap();
        map.put("callbackUrl",qiniuCallbackUrl);
        map.put("callbackBody", qiniuCallbackBody);
        String token = auth.uploadToken(bucketName,key,3600,map);
        return token;
    }

    public long countVideoInSpeak(long speakId) {
        logger.info("get video count in speak {}", speakId);
        long count = repository.countBySpeakId(speakId);
        logger.info("get video count {} in speak {}", count, speakId);
        return count;
    }

    public List<VideoInSpeakBean> getVideoInSpeak(Long speakId) {
        List<Long> speakIds = new ArrayList<>();
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
        fillVideoUrl(videoInSpeak);

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

    private void fillVideoUrl(List<VideoInSpeakBean> videoInSpeak) {
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.DAY_OF_MONTH, 1);
        long second = calender.getTimeInMillis()/1000;
        Auth auth = getQiNiuAuth();

        for (VideoInSpeakBean tmp : videoInSpeak) {
            if (VerifyUtil.isStringEmpty(tmp.getVideoId())) {
                continue;
            }
            String videoUrl = qiniuDomain+tmp.getVideoId()+"?e="+second;
            String videoToken = auth.sign(videoUrl);
            videoUrl = videoUrl+"&token="+videoToken;
            tmp.setVideoUrl(videoUrl);
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
    public List<VideoInSpeakBean> updateVideoStatus(String videoId, VideoPlatform platform, String strStatus) {
        logger.info("update video status by videoId={} to status={}", videoId, strStatus);
        CCVideoStatus status = CCVideoStatus.parseString(strStatus);
        if (null==status) {
            logger.error("status is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<VideoInSpeakEntity> entities = repository.findByVideoIdAndPlatform(videoId, platform);
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

    @Transactional
    public VideoInSpeakBean updateVideo(String strPlatform, String videoId,
                                        String backgroundName , InputStream background,
                                        String snapshotName, InputStream snapshot,
                                        long speakId, String strStatus) {
        logger.info("update platform={} video={} and backgroundImage={}->{} snapshotImage={}->{} to speak {} and status={}",
                strPlatform, videoId, backgroundName, null!=background, snapshotName, null!=snapshot, speakId, strStatus);
        VideoPlatform platform = VideoPlatform.parseString(strPlatform);
        if (null==platform) {
            logger.error("platform is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(videoId)) {
            logger.error("video is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        CCVideoStatus status = CCVideoStatus.parseString(strStatus);

        List<VideoInSpeakEntity> entities = repository.findByVideoIdAndPlatform(videoId, platform);
        if (VerifyUtil.isListEmpty(entities)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (entities.size()>1) {
            logger.warn("there is more than one records, just update the 1st one");
        }

        boolean changed = false;
        VideoInSpeakEntity entity = entities.get(0);
        if (speakId>0) {
            entity.setSpeakId(speakId);
            changed = true;
        }
        if (null!=status) {
            entity.setVideoStatus(status);
            changed = true;
        }

        long backgroundId = uploadImage(backgroundName, background);
        String backgroundUrl = "";
        if (backgroundId>0) {
            entity.setBackground(backgroundId);
            backgroundUrl = userStorage.getFilePath(backgroundId);
            changed = true;
        }

        long snapshotId = uploadImage(snapshotName, snapshot);
        String snapshotUrl = "";
        if (snapshotId>0) {
            entity.setSnapshot(snapshotId);
            snapshotUrl = userStorage.getFilePath(snapshotId);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        VideoInSpeakBean bean = beanConverter.convert(entity);
        bean.setBackgroundUrl(backgroundUrl);
        bean.setSnapshotUrl(snapshotUrl);
        return bean;
    }

    //====================================================
    //          add
    //====================================================
    @Transactional
    public VideoInSpeakBean addVideo(long speakId, String strPlatform, String videoId,
                                      String backgroundName , InputStream background,
                                      String snapshotName, InputStream snapshot) {
        logger.info("add platform={} video={} and backgroundImage={}->{} snapshotImage={}->{} to speak {}",
                strPlatform, videoId, backgroundName, null!=background, snapshotName, null!=snapshot, speakId);
        VideoPlatform platform = VideoPlatform.parseString(strPlatform);
        if (null==platform) {
            logger.error("platform is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(videoId)) {
            logger.error("video is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long backgroundId = uploadImage(backgroundName, background);
        String backgroundUrl = "";

        long snapshotId = uploadImage(snapshotName, snapshot);
        String snapshotUrl = "";

        VideoInSpeakEntity entity = new VideoInSpeakEntity();
        entity.setSpeakId(speakId);
        entity.setVideoId(videoId);
        if (backgroundId>0) {
            entity.setBackground(backgroundId);
            backgroundUrl = userStorage.getFilePath(backgroundId);
        }
        if (snapshotId>0) {
            entity.setSnapshot(snapshotId);
            snapshotUrl = userStorage.getFilePath(snapshotId);
        }

        entity.setPlatform(platform);
        entity.setVideoStatus(CCVideoStatus.OTHER);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);

        entity = repository.save(entity);

        VideoInSpeakBean bean = beanConverter.convert(entity);
        bean.setBackgroundUrl(backgroundUrl);
        bean.setSnapshotUrl(snapshotUrl);
        return bean;
    }

    private long uploadImage(String imageName, InputStream image) {
        long imageId = 0;
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "video_speak_" + System.nanoTime();
            }
            imageId = userStorage.addFile(-1, imageName, image);
        }
        return imageId;
    }

    //=====================================================================================
    //                check video status from CC server
    //=====================================================================================
}

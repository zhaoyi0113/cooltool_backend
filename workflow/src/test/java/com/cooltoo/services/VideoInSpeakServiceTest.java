package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.VideoInSpeakBean;
import com.cooltoo.backend.services.VideoInSpeakService;
import com.cooltoo.constants.CCVideoStatus;
import com.cooltoo.constants.VideoPlatform;
import com.cooltoo.services.file.UserFileStorageService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/4/14.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/video_in_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml")
})
public class VideoInSpeakServiceTest extends AbstractCooltooTest{

    @Autowired private VideoInSpeakService service;
    @Autowired private UserFileStorageService userStorage;

    @Test
    public void testCountBySpeakId() {
        long speakId = 1;
        long count = service.countVideoInSpeak(speakId);
        Assert.assertEquals(1, count);

        speakId = 8;
        count = service.countVideoInSpeak(speakId);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetVideoInSpeakBySpeakId() {
        long speakId = 1;
        List<VideoInSpeakBean> videos = service.getVideoInSpeak(speakId);
        Assert.assertEquals(1, videos.size());

        speakId = 8;
        videos = service.getVideoInSpeak(speakId);
        Assert.assertEquals(2, videos.size());
    }

    @Test
    public void testGetVideoInSpeakBySpeakIds() {
        List<Long> speakIds = new ArrayList<>();
        speakIds.add(1L);
        speakIds.add(2L);
        Map<Long, List<VideoInSpeakBean>> videos = service.getVideoInSpeak(speakIds);
        Assert.assertEquals(2, videos.size());
    }

    @Test
    public void testDeleteById() {
        long videoInSpeakId = 1;
        long speakId = 1;
        VideoInSpeakBean videos = service.deleteById(videoInSpeakId);
        Assert.assertNotNull(videos);

        long count = service.countVideoInSpeak(speakId);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testDeleteBySpeakIds() {
        long speakId1 = 1;
        long speakId2 = 2;
        long speakId3 = 3;
        long count1   = service.countVideoInSpeak(speakId1);
        long count2   = service.countVideoInSpeak(speakId2);
        long count3   = service.countVideoInSpeak(speakId3);
        Assert.assertEquals(1, count1);
        Assert.assertEquals(1, count2);
        Assert.assertEquals(1, count3);

        List<Long> speakIds = new ArrayList<>();
        speakIds.add(speakId1);
        speakIds.add(speakId2);
        speakIds.add(speakId3);
        service.deleteBySpeakIds(speakIds);

        count1   = service.countVideoInSpeak(speakId1);
        count2   = service.countVideoInSpeak(speakId2);
        count3   = service.countVideoInSpeak(speakId3);
        Assert.assertEquals(0, count1);
        Assert.assertEquals(0, count2);
        Assert.assertEquals(0, count3);
    }

    @Test
    public void testAddVideo() {
        long  speakId   = 15;
        String videoId = "VideoInSpeak0001";
        String backgroundName = "background_name";
        InputStream background = new ByteArrayInputStream(backgroundName.getBytes());
        String snapshotName = "snapshot_name";
        InputStream snapshot = new ByteArrayInputStream(snapshotName.getBytes());
        VideoInSpeakBean  bean = service.addVideo(speakId, VideoPlatform.CC.name(), videoId, backgroundName, background, snapshotName, snapshot);
        Assert.assertNotNull(bean);
        Assert.assertNotNull(bean.getBackgroundUrl());
        Assert.assertTrue(bean.getBackground()>0);
        Assert.assertNotNull(bean.getSnapshotUrl());
        Assert.assertTrue(bean.getSnapshot()>0);
        Assert.assertEquals(speakId, bean.getSpeakId());
        Assert.assertEquals(videoId, bean.getVideoId());
        Assert.assertEquals(CCVideoStatus.OTHER, bean.getVideoStatus());
        Assert.assertTrue(userStorage.fileExist(bean.getBackground()));
        userStorage.deleteFile(bean.getBackground());
        Assert.assertFalse(userStorage.fileExist(bean.getBackgroundUrl()));
        Assert.assertTrue(userStorage.fileExist(bean.getSnapshot()));
        userStorage.deleteFile(bean.getSnapshot());
        Assert.assertFalse(userStorage.fileExist(bean.getSnapshotUrl()));
    }

    @Test
    public void testUpdateVideoStatus( ) {
        long speakId = 1;
        String videoId = "111";
        CCVideoStatus status = CCVideoStatus.NETWORK_ERROR;
        List<VideoInSpeakBean> videos = service.getVideoInSpeak(speakId);
        Assert.assertEquals(1, videos.size());
        Assert.assertEquals(speakId, videos.get(0).getSpeakId());
        Assert.assertNotEquals(status, videos.get(0).getVideoStatus());

        videos = service.updateVideoStatus(videoId, VideoPlatform.CC, status.name());
        Assert.assertEquals(1, videos.size());
        Assert.assertEquals(speakId, videos.get(0).getSpeakId());
        Assert.assertEquals(status, videos.get(0).getVideoStatus());
    }
}

package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.services.NurseSpeakCommentService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Test111 on 2016/3/18.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
})
public class NurseSpeakCommentServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakCommentServiceTest.class.getName());

    @Autowired
    NurseSpeakCommentService commentService;

    @Test
    public void testFindSpeakWithCommentUserMake() {
        List<Long> speakIds = commentService.findSpeakWithCommentUserMake(1);
        Assert.assertEquals(4, speakIds.size());
        speakIds = commentService.findSpeakWithCommentUserMake(2);
        Assert.assertEquals(1, speakIds.size());
        Assert.assertEquals(Long.valueOf(3L), speakIds.get(0));
        speakIds = commentService.findSpeakWithCommentUserMake(3);
        Assert.assertEquals(1, speakIds.size());
        Assert.assertEquals(Long.valueOf(4L), speakIds.get(0));
    }

    @Test
    public void testAddNurseSpeakComment() {
        String comment = "Test ping lun";
        Date time = new Date();
        NurseSpeakCommentBean commentBean = commentService.addSpeakComment(1, 1, 0, comment);
        Assert.assertTrue(commentBean.getId()>0);
        Assert.assertEquals(1, commentBean.getNurseSpeakId());
        Assert.assertEquals(1, commentBean.getCommentMakerId());
        Assert.assertEquals(0, commentBean.getCommentReceiverId());
        Assert.assertEquals(comment, commentBean.getComment());
        Assert.assertTrue(commentBean.getTime().getTime() >= time.getTime());
    }

    @Test
    public void testGetNurseSpeakCommentByNurseSpeakId() {
        long nurseSpeakId = 3;
        List<NurseSpeakCommentBean> comments = commentService.getSpeakCommentsByNurseSpeakId(nurseSpeakId);
        Assert.assertEquals(3, comments.size());
        for (NurseSpeakCommentBean comment : comments) {
            Assert.assertTrue(comment.getId()>0);
            Assert.assertEquals(3, comment.getNurseSpeakId());
            logger.info(comment.toString());
        }
    }

    @Test
    public void testGetNurseSpeakCommentByNurseSpeakIds() {
        List<Long> nurseSpeakIds = new ArrayList<Long>();
        nurseSpeakIds.add(3L);
        nurseSpeakIds.add(1L);
        List<NurseSpeakCommentBean> comments = commentService.getSpeakCommentsByNurseSpeakIds(nurseSpeakIds);
        Assert.assertEquals(5, comments.size());
        for (NurseSpeakCommentBean comment : comments) {
            Assert.assertTrue(comment.getId()>0);
            logger.info(comment.toString());
        }
    }
}

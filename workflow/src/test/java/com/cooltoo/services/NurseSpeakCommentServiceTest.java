package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.services.NurseSpeakCommentService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Test111 on 2016/3/18.
 */
@Transactional
public class NurseSpeakCommentServiceTest extends AbstractCooltooTest {
    @Autowired
    NurseSpeakCommentService commentService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml")
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
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml")
    public void testGetNurseSpeakCommentByNurseSpeakId() {
        long nurseSpeakId = 3;
        List<NurseSpeakCommentBean> comments = commentService.getSpeakCommentsByNurseSpeakId(nurseSpeakId);
        Assert.assertEquals(3, comments.size());
        for (NurseSpeakCommentBean comment : comments) {
            Assert.assertTrue(comment.getId()>0);
            Assert.assertEquals(3, comment.getNurseSpeakId());
            System.out.println(comment);
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml")
    public void testGetNurseSpeakCommentByNurseSpeakIds() {
        List<Long> nurseSpeakIds = new ArrayList<Long>();
        nurseSpeakIds.add(3L);
        nurseSpeakIds.add(1L);
        List<NurseSpeakCommentBean> comments = commentService.getSpeakCommentsByNurseSpeakIds(nurseSpeakIds);
        Assert.assertEquals(10, comments.size());
        for (NurseSpeakCommentBean comment : comments) {
            Assert.assertTrue(comment.getId()>0);
            System.out.println(comment);
        }
    }
}

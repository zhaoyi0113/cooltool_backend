package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.services.NurseSpeakThumbsUpService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/3/18.
 */
@Transactional
public class NurseSpeakThumbsUpServiceTest extends AbstractCooltooTest {

    @Autowired
    NurseSpeakThumbsUpService thumbsUpService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_thumbs_up_data.xml")
    public void testAddNurseSpeakThumbsUp() {
        long nurseSpeakId = 14;
        long thumbsUpUserId = 7;
        Date time = new Date();
        NurseSpeakThumbsUpBean bean = thumbsUpService.setSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        Assert.assertEquals(14, bean.getNurseSpeakId());
        Assert.assertEquals(7, bean.getThumbsUpUserId());
        Assert.assertTrue(bean.getId()>0);
        Assert.assertTrue(bean.getTime().getTime() >= time.getTime());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_thumbs_up_data.xml")
    public void testGetNurseSpeakThumbsUpByNurseSpeakId() {
        long nurseSpeakId = 2;
        List<NurseSpeakThumbsUpBean> thumbsUpBeans = thumbsUpService.getSpeakThumbsUpByNurseSpeakId(nurseSpeakId);
        Assert.assertEquals(3, thumbsUpBeans.size());
        for (NurseSpeakThumbsUpBean thumbsUpBean : thumbsUpBeans) {
            Assert.assertEquals(nurseSpeakId, thumbsUpBean.getNurseSpeakId());
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_thumbs_up_data.xml")
    public void testGetNurseSpeakThumbsUpByNurseSpeakIds() {
        List<Long> nurseSpeakIds = new ArrayList<Long>();
        nurseSpeakIds.add(1L);
        nurseSpeakIds.add(3L);
        nurseSpeakIds.add(5L);
        nurseSpeakIds.add(7L);
        List<NurseSpeakThumbsUpBean> thumbsUpBeans = thumbsUpService.getSpeakThumbsUpByNurseSpeakIds(nurseSpeakIds);
        Assert.assertEquals(3, thumbsUpBeans.size());

        nurseSpeakIds.clear();
        for (NurseSpeakThumbsUpBean thumbsUpBean : thumbsUpBeans) {
            nurseSpeakIds.add(thumbsUpBean.getNurseSpeakId());
        }

        Assert.assertTrue(nurseSpeakIds.contains(1L));
        Assert.assertTrue(nurseSpeakIds.contains(3L));
        Assert.assertTrue(nurseSpeakIds.contains(5L));
        Assert.assertTrue(!nurseSpeakIds.contains(7L));
    }
}

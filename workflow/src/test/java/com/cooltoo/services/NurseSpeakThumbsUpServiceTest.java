package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.services.NurseSpeakThumbsUpService;
import com.cooltoo.exception.BadRequestException;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
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
        NurseSpeakThumbsUpBean bean = thumbsUpService.addSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
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
    public void testFindAndDeleteNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId() {
        long nurseSpeakId = 2;
        long thumbsUpUserId = 4;
        NurseSpeakThumbsUpBean bean1 = thumbsUpService.findNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        Assert.assertNotNull(bean1);

        thumbsUpService.deleteNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        Exception throwable = null;
        try {
            thumbsUpService.findNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        }
        catch (Exception ex) {
            throwable = ex;
        }
        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable instanceof BadRequestException);
    }
}

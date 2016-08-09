package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.services.NurseSuggestionService;
import com.cooltoo.beans.SuggestionBean;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/user_suggestion_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/nurse_data.xml")
})
public class SuggestionServiceTest extends AbstractCooltooTest {

    @Autowired private SuggestionService service;
    @Autowired private NurseSuggestionService nurseSuggestionService;

    @Test
    public void testGetSuggestion() {
        List<SuggestionBean> beans = service.getSuggestions(null, null, null, null, 0, 3);
        Assert.assertTrue(!beans.isEmpty());
    }

    @Test
    public void testAddSuggestion() {
        long userId = 4;
        String suggestion = "Suggestion 001";
        SuggestionBean bean = nurseSuggestionService.nurseAddSuggestion(userId, "IOS", "1.5", suggestion);
        Assert.assertTrue(null!=bean);
        Assert.assertTrue(bean.getId()>0);
        Assert.assertEquals(4, bean.getUserId());
        Assert.assertEquals(suggestion, bean.getSuggestion());
    }

    @Test
    public void testDeleteSuggestion() {
        String ids1 = "1,2,3,4,5,6,7,8";
        String ids2 = "9";
        service.deleteSuggestion(ids1);
        List<SuggestionBean> beans = service.getSuggestions(null, null, null, null, 0,3);
        Assert.assertEquals(1, beans.size());
        service.deleteSuggestion(ids2);
        beans = service.getSuggestions(null, null, null, null, 0, 3);
        Assert.assertEquals(0, beans.size());
    }

}

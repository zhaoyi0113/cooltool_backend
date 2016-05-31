package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.SensitiveWordBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SensitiveWordType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hp on 2016/5/31.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/sensitive_words_data.xml")
})
public class SensitiveWordServiceTest extends AbstractCooltooTest {

    @Autowired private SensitiveWordService sensitiveWordService;

    @Test
    public void testCount() {
        long count = sensitiveWordService.countAll();
        Assert.assertEquals(6, count);

        String type = SensitiveWordType.ADD_CONTENT_FORBIDDEN.name();
        String status = CommonStatus.ENABLED.name();
        count = sensitiveWordService.countWords(type, status);
        Assert.assertEquals(4, count);

        type = "";
        status = CommonStatus.DISABLED.name();
        count = sensitiveWordService.countWords(type, status);
        Assert.assertEquals(1, count);

        type = SensitiveWordType.OTHER.name();
        status = null;
        count = sensitiveWordService.countWords(type, status);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGet() {
        List<SensitiveWordBean> words = sensitiveWordService.getAll();
        Assert.assertEquals(6, words.size());
        Assert.assertEquals(1, words.get(0).getId());
        Assert.assertEquals(6, words.get(5).getId());


        String type = SensitiveWordType.ADD_CONTENT_FORBIDDEN.name();
        String status = CommonStatus.ENABLED.name();
        words = sensitiveWordService.getWords(type, status);
        Assert.assertEquals(4, words.size());
        Assert.assertEquals(1, words.get(0).getId());
        Assert.assertEquals(4, words.get(3).getId());
    }

    @Test
    public void testAddWord() {
        String word = "aaaa";
        String type = SensitiveWordType.ADD_CONTENT_FORBIDDEN.name();;
        long count1 = sensitiveWordService.countAll();
        SensitiveWordBean wordBean = sensitiveWordService.addWord(word, type);
        long count2 = sensitiveWordService.countAll();

        Assert.assertEquals(count1+1, count2);
        Assert.assertEquals(SensitiveWordType.ADD_CONTENT_FORBIDDEN, wordBean.getType());
        Assert.assertEquals(CommonStatus.ENABLED, wordBean.getStatus());
        Assert.assertEquals(word, wordBean.getWord());
    }

    @Test
    public void testUpdateWord() {
        String status = CommonStatus.DISABLED.name();
        List<SensitiveWordBean> words = sensitiveWordService.getWords("", status);
        Assert.assertEquals(1, words.size());

        SensitiveWordBean wordB = words.get(0);
        SensitiveWordBean modified = sensitiveWordService.updateWord(wordB.getId(), SensitiveWordType.OTHER.name(), CommonStatus.ENABLED.name());
        Assert.assertEquals(wordB.getId(), modified.getId());
        Assert.assertNotEquals(wordB.getType(), modified.getType());
        Assert.assertNotEquals(wordB.getStatus(), modified.getStatus());
        Assert.assertEquals(SensitiveWordType.OTHER, modified.getType());
        Assert.assertEquals(CommonStatus.ENABLED, modified.getStatus());

    }
}

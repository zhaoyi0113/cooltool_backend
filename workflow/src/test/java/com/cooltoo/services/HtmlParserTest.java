package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.services.file.TemporaryFileStorageService;
import com.cooltoo.util.HtmlParser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hp on 2016/4/21.
 */
@Transactional
public class HtmlParserTest extends AbstractCooltooTest {

    private static final String TestData = "" +
            "<html>\n" +
            "<head>\n" +
            "\t<title>afdasf fdsaf</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<img  onerror='pullerror(this,105);' src='http://img4.imgtn.bdimg.com/it/u=227213885,3152702771.jpg'/>\n" +
            "<img  onerror='pullerror(this,106);' src='http://img4.imgtn.bdimg.com/it/u=4253490263,2515058925.jpg' >\n" +
            "<img  onerror='pullerror(this,107);' src='http://img4.imgtn.bdimg.com/it/u=3208940989,3404201031.jpg'>\n" +
            "<img  onerror='pullerror(this,108);' src='http://img4.imgtn.bdimg.com/it/u=3449727357,3515710346.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,109);' src='http://img4.imgtn.bdimg.com/it/u=715576259,3597168931.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,110);' src='http://img4.imgtn.bdimg.com/it/u=1758354233,1981598847.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,111);' src='http://img4.imgtn.bdimg.com/it/u=3149713355,2311806040.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,112);' src='http://img4.imgtn.bdimg.com/it/u=3926569211,2704912628.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,113);' src='http://img4.imgtn.bdimg.com/it/u=1267645111,31998121.jpg' alt='aaa'>\n" +
            " img  onerror='pullerror(this,114);' src='http://img4.imgtn.bdimg.com/it/u=hahahahhahah.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,115);' src='http://img4.imgtn.bdimg.com/it/u=4080960913,1004736531.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,116);' src='http://img4.imgtn.bdimg.com/it/u=1333091349,757559938.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,117);' src='http://img4.imgtn.bdimg.com/it/u=3565939884,2448047316.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,118);' src='http://img4.imgtn.bdimg.com/it/u=1988509788,1291468676.jpg' alt='aaa'>\n" +
            "<img  onerror='pullerror(this,119);' src='http://img4.imgtn.bdimg.com/it/u=2640109620,1193218977.jpg' alt='aaa'>\n" +
            "</body\n" +
            "</html>";

    @Test
    public void testGetImgTag2SrcUrlMap() {
        HtmlParser htmlParser = HtmlParser.newInstance();
        Throwable ex = null;
        Map<String, String> imgTag2SrcValue = null;
        imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(TestData);
        Assert.assertEquals(14, imgTag2SrcValue.size());
    }

    @Test
    public void testReplaceImgTagSrcUrl() {
        HtmlParser htmlParser = HtmlParser.newInstance();
        String newUrl = "aaaaaaaaaaa";

        // get all tag
        Map<String, String> imgTag2SrcValue = null;
        imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(TestData);

        // replace
        Map<String, String> news = new HashMap<>();
        Set<String>         keys = imgTag2SrcValue.keySet();
        for (String key : keys) {
            String value = imgTag2SrcValue.get(key);
            news.put(value, newUrl);
        }
        String newTestDate = htmlParser.replaceImgTagSrcUrl(TestData, imgTag2SrcValue, news);

        // get all tag in new content
        imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(newTestDate);

        // judge replace
        keys = imgTag2SrcValue.keySet();
        for (String key : keys) {
            String value = imgTag2SrcValue.get(key);
            Assert.assertEquals(newUrl, value);
        }
    }

    @Test
    public void testAddPrefixToImgTagSrcUrl() {
        HtmlParser htmlParser = HtmlParser.newInstance();
        String baseUrl = "http://fdsa.fd.sf/";

        // get all tag
        Map<String, String> imgTag2SrcValue = null;
        imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(TestData);

        // replace
        String newTestDate = htmlParser.addPrefixToImgTagSrcUrl(TestData, imgTag2SrcValue, baseUrl);

        // get all tag in new content
        imgTag2SrcValue = htmlParser.getImgTag2SrcUrlMap(newTestDate);

        // judge replace
        Set<String> imgTags = imgTag2SrcValue.keySet();
        for (String tag : imgTags) {
            String src = imgTag2SrcValue.get(tag);
            Assert.assertTrue(src.startsWith(baseUrl));
        }
    }

    @Test
    public void testGet () {
        HtmlParser htmlParser = HtmlParser.newInstance();
        List<String> srcUrls = htmlParser.getSrcUrls(TestData);
        Assert.assertEquals(14, srcUrls.size());
        for (String src : srcUrls) {
            Assert.assertTrue(src.startsWith("http://img4.imgtn.bdimg.com/it/u="));
            Assert.assertTrue(src.endsWith(".jpg"));
        }
    }
}

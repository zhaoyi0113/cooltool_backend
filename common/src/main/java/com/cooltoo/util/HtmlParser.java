package com.cooltoo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hp on 2016/4/19.
 */
public class HtmlParser {

    private static final Logger logger = LoggerFactory.getLogger(HtmlParser.class.getName());


    public static HtmlParser newInstance() {
        return new HtmlParser();
    }

    public List<String> getSrcUrls(String content) {
        List<String> imageTags = getAllImageTag(content);
        Map<String, String> imgTag2SrcUrl = getImageSrcAttribute(imageTags);
        Set<String>  imgTags = imgTag2SrcUrl.keySet();
        List<String> srcUrls = new ArrayList<>();
        for (String key : imgTags) {
            String srcUrl = imgTag2SrcUrl.get(key);
            srcUrls.add(srcUrl);
        }
        return srcUrls;
    }

    public Map<String,String> getImgTag2SrcUrlMap(String content) {
        List<String> imageTags = getAllImageTag(content);
        return getImageSrcAttribute(imageTags);
    }

    public String addPrefixToImgTagSrcUrl(String content,
                                          Map<String, String> imageTag2SrcAttrValue,
                                          String baseUrl) {
        if (null==imageTag2SrcAttrValue || imageTag2SrcAttrValue.isEmpty()) {
            logger.error("image tags is empty");
            return content;
        }
        if (VerifyUtil.isStringEmpty(baseUrl)) {
            logger.error("image src url prefix(base_url) is empty");
            return content;
        }
        if (VerifyUtil.isStringEmpty(content)) {
            logger.error("content is empty");
            return content;
        }
        logger.info("replace image url in content imageTagSize={} baseUrl={}", imageTag2SrcAttrValue.size(), baseUrl);

        Set<String> imageTags = imageTag2SrcAttrValue.keySet();
        for (String tag : imageTags) {
            String srcValue    = imageTag2SrcAttrValue.get(tag);
            if (VerifyUtil.isStringEmpty(srcValue)) {
                continue;
            }

            String newSrcValue = baseUrl + srcValue;
            content = content.replace(srcValue, newSrcValue);
        }

        return content;
    }

    public String replaceImgTagSrcUrl(String content,
                                      Map<String, String> imageTag2SrcAttrValue,
                                      Map<String, String> srcUrl2NewUrl) {
        if (null==imageTag2SrcAttrValue || imageTag2SrcAttrValue.isEmpty()) {
            logger.error("image tags is empty");
            return content;
        }
        if (null==srcUrl2NewUrl || srcUrl2NewUrl.isEmpty()) {
            logger.error("old image src url ===> new src url is empty");
            return content;
        }
        if (VerifyUtil.isStringEmpty(content)) {
            logger.error("content is empty");
            return content;
        }
        logger.info("replace image url in content imageTagSize={} oldUrl2NewUrlSize={}", imageTag2SrcAttrValue.size(), srcUrl2NewUrl.size());

        Set<String> imageTags = imageTag2SrcAttrValue.keySet();
        for (String tag : imageTags) {
            String srcValue    = imageTag2SrcAttrValue.get(tag);
            String newSrcValue = srcUrl2NewUrl.get(srcValue);
            if (VerifyUtil.isStringEmpty(srcValue)) {
                continue;
            }
            if (VerifyUtil.isStringEmpty(newSrcValue)) {
                continue;
            }
            content = content.replace(srcValue, newSrcValue);
        }

        return content;
    }

    private Map<String,String> getImageSrcAttribute(List<String> imageTags) {
        logger.info("get image tags' src attribute size ==> {}", null==imageTags ? 0 : imageTags.size());
        if (null==imageTags || imageTags.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> imageTag2SrcAttrValue = new HashMap<>();

        String  imgSrcAttributePattern = "src *={1,1} *[\'|\"].*[\'|\"]";
        Pattern pattern = Pattern.compile(imgSrcAttributePattern);
        Matcher matcher = null;
        for (String tmp : imageTags) {
            logger.info("get image tag     ==> {}", tmp);
            if (null==matcher) {
                matcher = pattern.matcher(tmp);
            }
            else {
                matcher = matcher.reset(tmp);
            }

            boolean srcAttrExist = matcher.find();
            if (!srcAttrExist) {
                logger.info("get image tag     ==> src attribute not exist");
                continue;
            }
            String srcAttribute = tmp.substring(matcher.start(), matcher.end());
            srcAttribute = srcAttribute.replace('\'', '\"');
            logger.info("get image tag src ==> {}", srcAttribute);
            int indexOfFirst = srcAttribute.indexOf('\"');
            int indexOfLast  = srcAttribute.lastIndexOf('\"');
            srcAttribute = srcAttribute.substring(indexOfFirst+1, indexOfLast);
            logger.info("get image tag src value ==> {}", srcAttribute);
            imageTag2SrcAttrValue.put(tmp, srcAttribute);
        }

        return imageTag2SrcAttrValue;
    }

    private List<String> getAllImageTag(String content) {
        logger.info("get all image tag for content");
        if (null == content || content.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> imageTags = new ArrayList();
        int indexOfImage = 0;
        int totalLength = content.length();

        while (indexOfImage >= 0 && indexOfImage < totalLength) {
            int[] startEndIdx = getNextImageTag(content, indexOfImage);
            if (null == startEndIdx) {
                break;
            }

            String imageTag = content.substring(startEndIdx[0], startEndIdx[1] + 1);
            if (!imageTags.contains(imageTag)) {
                imageTags.add(imageTag);
            }
            indexOfImage = startEndIdx[1];
        }

        logger.info("get all image tag for content, count={}", imageTags.size());
        return imageTags;
    }

    private int[] getNextImageTag(String content, int startIndex) {
        logger.info("get next image tag in content from startIndex={}", startIndex);
        int nextImageTagStart = indexOfFirstImageTag(content, startIndex);
        int nextImageTagEnd   = indexOfFirstGreaterThan(content, nextImageTagStart);
        logger.info("get next image tag startIndex={} endIndex={}", nextImageTagStart, nextImageTagEnd);
        if (nextImageTagStart < 0 || nextImageTagEnd < 0) {
            return null;
        }
        return new int[]{nextImageTagStart, nextImageTagEnd};
    }

    private boolean isImageStartTag(char c1, char c2, char c3, char c4) {
        return '<' == c1 && 'i' == c2 && 'm' == c3 && 'g' == c4;
    }

    private int indexOfFirstGreaterThan(String content, int startIndex) {
        if (null == content || content.isEmpty()) {
            return -1;
        }
        if (startIndex < 0 || startIndex >= content.length()) {
            return -1;
        }

        int index = startIndex;
        for (int count = content.length(); index < count; index++) {
            char c = content.charAt(index);
            if ('>' == c) {
                break;
            }
        }
        return index;
    }

    private int indexOfFirstImageTag(String content, int startIndex) {
        if (null == content || content.isEmpty()) {
            return -1;
        }
        if (startIndex < 0 || startIndex + 3 >= content.length()) {
            return -1;
        }

        char c1 = content.charAt(startIndex);
        char c2 = content.charAt(startIndex + 1);
        char c3 = content.charAt(startIndex + 2);
        char c4 = content.charAt(startIndex + 3);
        if (isImageStartTag(c1, c2, c3, c4)) {
            return startIndex;
        }

        int index = startIndex + 1;
        for (int count = content.length(); index < count; index++) {
            if (index + 3 >= content.length()) {
                return -1;
            }
            c1 = c2;
            c2 = c3;
            c3 = c4;
            c4 = content.charAt(index + 3);
            if (isImageStartTag(c1, c2, c3, c4)) {
                return index;
            }
        }

        return -1;
    }
}

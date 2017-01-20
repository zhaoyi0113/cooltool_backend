package com.cooltoo.go2nurse.chart.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 18/01/2017.
 */
public class TextUtil {

    public static TextUtil newInstance() {
        return new TextUtil();
    }

    public String getContentInSpecialSize(String content, int maxCharSizeInCell) {
        String[] lines = content.split("\n");
        if (null == content) {
            return "";
        }
        if (maxCharSizeInCell <= 0) {
            return content;
        }

        StringBuilder msg = new StringBuilder();
        int[] charDot = new int[content.length()];
        int tmpLength = 0;
        for (int i = 0, count = content.length(); i < count; i++) {
            char tmpChar = content.charAt(i);
            charDot[i] = ((tmpChar <= 127) ? 1 : 2);

            tmpLength += charDot[i];
            if (tmpLength <= maxCharSizeInCell) {
                msg.append(tmpChar);
                continue;
            }

            msg.deleteCharAt(i - 1);
            if (msg.length()>0) {
                msg.deleteCharAt(i - 2);
            }
            msg.append("..");
            if (charDot[i - 1] > 1) {
                msg.append(".");
            }
            break;
        }
        return msg.toString();
    }

    /**  */
    public String[] getLines(String content, int maxCharSizeInCell) {
        String[] lines = content.split("\n");
        if (lines.length == 0) {
            return new String[]{};
        }

        List<String> lineArray = new ArrayList<String>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] tmpLines = lineMatchLength(line, maxCharSizeInCell);
            for (int j = 0; j < tmpLines.length; j++) {
                String tmpLine = tmpLines[j];
                lineArray.add(tmpLine);
            }
        }
        return lineArray.toArray(new String[lineArray.size()]);
    }

    private String[] lineMatchLength(String content, int maxCharSize) {
        if (maxCharSize <= 0 || null == content) {
            return new String[]{};
        }

        int length = 0;
        for (int i = 0, count = content.length(); i < count; i++) {
            char tmpChar = content.charAt(i);
            length += ((tmpChar <= 127) ? 1 : 2);
        }
        if (maxCharSize >= length) {
            return new String[]{content};
        }

        List<String> lines = new ArrayList<String>();

        int tmpLength = 0;
        StringBuilder tmpLine = new StringBuilder();
        for (int i = 0, count = content.length(); i < count; i++) {
            char tmpChar = content.charAt(i);
            tmpLine.append(tmpChar);
            tmpLength += ((tmpChar <= 127) ? 1 : 2);

            if (tmpLength > maxCharSize) {
                String tmpSubLine = tmpLine.substring(0, tmpLine.length() - 1);
                lines.add(tmpSubLine);
                tmpLine.setLength(0);
                tmpLength = 0;
                i--;
                continue;
            }

            if (i + 1 >= count) {
                lines.add(tmpLine.toString());
            }
        }
        return lines.toArray(new String[lines.size()]);
    }
}

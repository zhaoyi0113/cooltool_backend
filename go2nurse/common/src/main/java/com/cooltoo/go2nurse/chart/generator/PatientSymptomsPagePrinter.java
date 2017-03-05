package com.cooltoo.go2nurse.chart.generator;

import com.cooltoo.go2nurse.chart.ui.CellImage;
import com.cooltoo.go2nurse.chart.ui.CellText;
import com.cooltoo.go2nurse.chart.ui.layout.*;
import com.cooltoo.go2nurse.chart.util.Align;
import com.cooltoo.go2nurse.chart.util.FontUtil;
import com.cooltoo.go2nurse.chart.util.PageSize;
import com.cooltoo.go2nurse.chart.util.TextUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 19/01/2017.
 */
public class PatientSymptomsPagePrinter {
    public final FontUtil fontUtil = FontUtil.newInstance();

    private int dpi;
    private PageSize pageSize;
    private float[] pagePadding; /* header; footer; left; right */
    private String fontName;
    private String serviceItemName;
    private String patientName;
    private String gender;
    private String age;
    private String contactPeople;
    private String homeAddress;
    private String contactPhone;

    private float contentRowHeight;
    private int contentRowSize;

    public PatientSymptomsPagePrinter(int      dpi,
                                      PageSize pageSize,
                                      float[]  pagePadding, /* header, footer, left, right */
                                      String   fontName,
                                      float    contentRowHeight,
                                      int      contentRowSize,
                                      String serviceItemName,
                                      String patientName,
                                      String gender,
                                      String age,
                                      String contactPeople,
                                      String homeAddress,
                                      String contactPhone
    ) {
        this.dpi = dpi;
        this.pageSize = pageSize;
        this.pagePadding = pagePadding;
        this.fontName = fontName;
        this.serviceItemName = serviceItemName;
        this.patientName = patientName;
        this.gender = gender;
        this.age = age;
        this.contactPeople = contactPeople;
        this.homeAddress = homeAddress;
        this.contactPhone = contactPhone;

        this.contentRowHeight = contentRowHeight;
        this.contentRowSize = contentRowSize;
    }

    public Page pageTop() {
        Page page = new Page(dpi, pageSize);
        if (null==pagePadding || pagePadding.length!=4) {
            pagePadding = new float[]{11, 11, 11, 11};
        }
        page.header(new Header(pagePadding[0]));
        page.footer(new Footer(pagePadding[1]));
        page.marginLeft(new MarginLeft(pagePadding[2]));
        page.marginRight(new MarginRight(pagePadding[3]));
        page.header().border(RectRegion.NO_BORDER);
        page.footer().border(RectRegion.NO_BORDER);
        page.marginLeft().border(RectRegion.NO_BORDER);
        page.marginRight().border(RectRegion.NO_BORDER);

        Row       row   = null;
        Cell      cell  = null;
        CellText  text  = null;
        Font      font  = null;

        //===============================================
        // 第一行
        //===============================================
        font = fontUtil.getFont(fontName, Font.BOLD, fontUtil.poundToMm(18), dpi);
        text = new CellText("ADL 评定量表", null, Align.ALIGN_CENTER|Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(page.validWidth());
        cell.text(text);
        row = new Row(13f, 0f);
        row.mmPaddingBottom(6f);
        row.addCell(cell);
        page.addRow(row);

        //===============================================
        // 第二行
        //===============================================
        row = new Row(8f, 0f);
        row.border(RectRegion.NO_BORDER);
        row.mmPaddingBottom(3f);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("服务名称：", null, Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(16f);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(serviceItemName, null, Align.ALIGN_CENTER|Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(80f);
        cell.maxCharInCell(40);
        cell.text(text);
        row.addCell(cell);


        cell = new Cell(38f);
        row.addCell(cell);

        page.addRow(row);

        //===============================================
        // 第三行
        //===============================================
        row = new Row(8f, 0f);
        row.border(RectRegion.NO_BORDER);
        row.mmPaddingBottom(3f);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("姓名：", null, Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(9f);
        cell.maxCharInCell(6);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(patientName, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(21.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(10);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("性别：", null, Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(9f);
        cell.maxCharInCell(6);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(gender, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(9.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(4);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("年龄：", null, Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(9f);
        cell.maxCharInCell(6);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(age, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(7.5f + 1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(3);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("联系人：", null, Align.ALIGN_RIGHT|Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(12f);
        cell.maxCharInCell(8);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(contactPeople, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(26f + 1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(13);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("联系电话：", null, Align.ALIGN_RIGHT|Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(15f);
        cell.maxCharInCell(10);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(contactPhone, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(23f + 1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(12);
        cell.text(text);
        row.addCell(cell);

        // place holder
        //cell = new Cell(9f + 75.5f);
        //cell.mmPaddingRight(1.5f);
        //cell.maxCharInCell(6 + 37);
        //row.addCell(cell);

        page.addRow(row);

        //===============================================
        // 第四行
        //===============================================
        row = new Row(8f, 0f);
        row.border(RectRegion.NO_BORDER);
        row.mmPaddingBottom(3f);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("家庭住址：", null, Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(15f);
        cell.maxCharInCell(10);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(homeAddress, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(135f+38f);
        cell.maxCharInCell(90);
        cell.text(text);
        row.addCell(cell);

        page.addRow(row);

        //===============================================
        // 第7行
        //===============================================

        row = new Row(contentRowHeight, 0f);
        row.border(RectRegion.NO_BORDER);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("序号", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(19f);
        cell.border(RectRegion.TOP | RectRegion.LEFT | RectRegion.RIGHT);
        cell.mmPaddingLeft(1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(12);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("评估项目", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(75f);
        cell.border(RectRegion.TOP | RectRegion.RIGHT);
        cell.mmPaddingLeft(1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(44);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("用户选项", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(94f);
        cell.border(RectRegion.TOP | RectRegion.RIGHT);
        cell.mmPaddingLeft(1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(56);
        cell.text(text);
        row.addCell(cell);

        page.addRow(row);
        page.fixedRowsSize(page.rowSize());

        return page;

    }

    public List<String> pageContent(long userId,
                                    long patientId,
                                    long orderId,
                                    Page page,
                                    List<Record> records,
                                    String  storagePath,
                                    long    startRecordId,
                                    int     startLineIndex,
                                    int     startPageIndex,
                                    boolean overridePageExisted
    ) {
        Row       row   = null;
        Cell      cell  = null;
        CellText  text  = null;
        Font      font  = null;
        String    string= null;
        List<String> fileAbsPaths = new ArrayList<>();

        //===============================================
        // 内容 - 36行
        //===============================================
        int pageIndex = startPageIndex;
        int rowSizeLeft = contentRowSize;

        long pageFirstLineRecordId = 0;
        int pageFirstLineIndex = 0;
        for (int i = 0; i < records.size(); i ++) {
            Record record = records.get(i);
            if (record.id()<startRecordId) {
                continue;
            }

            String[] itemLines = record.itemLines(44);
            int      itemLineSize = itemLines.length;
            String[] userSelectedLines = record.userSelectedLines(56);
            int      userSelectedLineSize = userSelectedLines.length;

            for (int itemLineIndex = 0, userSelectedLineIndex = 0;
                 (itemLineIndex < itemLineSize || userSelectedLineIndex < userSelectedLineSize);
                 itemLineIndex++, userSelectedLineIndex++
            ) {
                if (record.id()==startRecordId && itemLineIndex < startLineIndex) {
                    continue;
                }
                boolean needDrawNum = (itemLineIndex == 0);

                if (rowSizeLeft == contentRowSize) {
                    pageFirstLineRecordId = record.id();
                    pageFirstLineIndex = itemLineIndex;
                }

                row = new Row(contentRowHeight, 0f);
                row.border(RectRegion.NO_BORDER);

                // 序号
                font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
                string = needDrawNum ? (record.isConclusion() ? "结论" : record.id()+"") : "";
                text = new CellText(string, null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
                text.font(font);
                cell = new Cell(19f);
                cell.border(RectRegion.TOP | RectRegion.LEFT | RectRegion.RIGHT);
                cell.mmPaddingLeft(1.5f);
                cell.mmPaddingRight(1.5f);
                cell.maxCharInCell(12);
                cell.text(text);
                row.addCell(cell);

                if (record.isConclusion()) {
                    // 评估项目结论
                    font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
                    string = itemLineIndex >= itemLineSize ? "" : itemLines[itemLineIndex];
                    text = new CellText(string, null, Align.ALIGN_MIDDLE, false);
                    text.font(font);
                    cell = new Cell(169f);
                    cell.border(RectRegion.TOP | RectRegion.RIGHT);
                    cell.mmPaddingLeft(1.5f);
                    cell.mmPaddingRight(1.5f);
                    cell.maxCharInCell(100);
                    cell.text(text);
                    row.addCell(cell);
                }
                else {
                    // 评估项目
                    font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
                    string = itemLineIndex >= itemLineSize ? "" : itemLines[itemLineIndex];
                    text = new CellText(string, null, Align.ALIGN_MIDDLE, false);
                    text.font(font);
                    cell = new Cell(75f);
                    cell.border(RectRegion.TOP | RectRegion.RIGHT);
                    cell.mmPaddingLeft(1.5f);
                    cell.mmPaddingRight(1.5f);
                    cell.maxCharInCell(46);
                    cell.text(text);
                    row.addCell(cell);

                    // 用户选项
                    font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
                    string = userSelectedLineIndex >= userSelectedLineSize ? "" : userSelectedLines[userSelectedLineIndex];
                    text = new CellText(string, null, Align.ALIGN_MIDDLE, false);
                    text.font(font);
                    cell = new Cell(94f);
                    cell.border(RectRegion.TOP | RectRegion.RIGHT);
                    cell.mmPaddingLeft(1.5f);
                    cell.mmPaddingRight(1.5f);
                    cell.maxCharInCell(56);
                    cell.text(text);
                    row.addCell(cell);
                }

                // 添加到页面中
                page.addRow(row);
                rowSizeLeft--;

                // 满一页后
                if (0 == rowSizeLeft || (i+1==records.size() && (itemLineIndex+1>=itemLineSize && userSelectedLineIndex+1>=userSelectedLineSize))) {
                    // 封底
                    page.getLastRow().addCellBorders(RectRegion.BOTTOM);
                    // 保存
                    StringBuilder absPath = new StringBuilder(storagePath);
                    absPath.append("patient_symptoms_")
                            .append(userId).append("_")
                            .append(patientId).append("_")
                            .append(orderId).append("_")
                            .append(pageIndex).append("_")
                            .append(pageFirstLineRecordId).append("_")
                            .append(pageFirstLineIndex).append("_")
                            .append(record.id()).append("_")
                            .append(itemLineIndex).append("");
                    File file = new File(absPath.toString());
                    if (!overridePageExisted && file.exists()) {
                    }
                    else {
                        page.save(file);
                    }
                    fileAbsPaths.add(absPath.toString());


                    // 重置 内容行, 行数，加一页
                    page.removeUnfixedRows();
                    rowSizeLeft = contentRowSize;
                    pageIndex ++;
                }
            }
        }
        return fileAbsPaths;
    }

    public static class Record {
        long id;
        String item;
        String userSelected;
        boolean isConclusion;

        TextUtil textUtil = null;
        String[] itemLines = null;
        int[]    itemLinesInPage = null;
        String[] userSelectedLines = null;
        int[]    userSelectedLinesInPage = null;

        public Record() {}

        public long id() {
            return id;
        }
        public Record id(long id) {
            this.id = id;
            return this;
        }

        public String getItem() {
            return item;
        }
        public Record setItem(String item) {
            this.item = item;
            return this;
        }

        public String userSelected() {
            return userSelected;
        }
        public Record userSelected(String userSelected) {
            this.userSelected = userSelected;
            return this;
        }

        public boolean isConclusion() {
            return this.isConclusion;
        }
        public void isConclusion(boolean isConclusion) {
            this.isConclusion = isConclusion;
        }

        public TextUtil textUtil() {
            return textUtil;
        }
        public Record textUtil(TextUtil textUtil) {
            this.textUtil = textUtil;
            return this;
        }

        public String[] itemLines(int maxCharSizeInCell) {
            if (null== itemLines) {
                itemLines = textUtil.getLines(item, maxCharSizeInCell);
                itemLinesInPage = new int[itemLines.length];
            }
            return itemLines;
        }
        public int[] itemLinesInPage() {
            return itemLinesInPage;
        }

        public String[] userSelectedLines(int maxCharSizeInCell) {
            if (null== userSelectedLines) {
                userSelectedLines = textUtil.getLines(userSelected, maxCharSizeInCell);
                userSelectedLinesInPage = new int[userSelectedLines.length];
            }
            return userSelectedLines;
        }
        public int[] userSelectedLinesInPage() {
            return userSelectedLinesInPage;
        }
    }
}

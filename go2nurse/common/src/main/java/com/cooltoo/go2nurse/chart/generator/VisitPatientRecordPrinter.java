package com.cooltoo.go2nurse.chart.generator;

import com.cooltoo.go2nurse.chart.ui.CellImage;
import com.cooltoo.go2nurse.chart.ui.CellText;
import com.cooltoo.go2nurse.chart.ui.layout.*;
import com.cooltoo.go2nurse.chart.util.Align;
import com.cooltoo.go2nurse.chart.util.FontUtil;
import com.cooltoo.go2nurse.chart.util.PageSize;
import com.cooltoo.go2nurse.chart.util.TextUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 19/01/2017.
 */
public class VisitPatientRecordPrinter {

    public final FontUtil fontUtil = FontUtil.newInstance();
    public final TextUtil textUtil = TextUtil.newInstance();

    private int dpi;
    private PageSize pageSize;
    private float[] pagePadding; /* header; footer; left; right */
    private String fontName;
    private String hospitalName;
    private String fileNumber;
    private String patientName;
    private String gender;
    private String age;
    private String diagnosis;
    private String contactPeople;
    private String homeAddress;
    private String contactPhone;

    private float contentRowHeight;
    private int contentRowSize;

    public VisitPatientRecordPrinter(int dpi,
                                     PageSize pageSize,
                                     float[] pagePadding, /* header, footer, left, right */
                                     String fontName,
                                     String hospitalName,
                                     String fileNumber,
                                     String patientName,
                                     String gender,
                                     String age,
                                     String diagnosis,
                                     String contactPeople,
                                     String homeAddress,
                                     String contactPhone,
                                     float contentRowHeight,
                                     int contentRowSize
    ) {
        this.dpi = dpi;
        this.pageSize = pageSize;
        this.pagePadding = pagePadding;
        this.fontName = fontName;
        this.hospitalName = hospitalName;
        this.fileNumber = fileNumber;
        this.patientName = patientName;
        this.gender = gender;
        this.age = age;
        this.diagnosis = diagnosis;
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
        text = new CellText("家庭治疗、护理操作记录单", null, Align.ALIGN_CENTER|Align.ALIGN_MIDDLE, false);
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

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(hospitalName, null, Align.ALIGN_CENTER|Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(80f);
        cell.maxCharInCell(40);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("社区卫生服务中心/站", null, Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(32f);
        cell.text(text);
        row.addCell(cell);


        cell = new Cell(38f);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("档案号：", null, Align.ALIGN_RIGHT|Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(12f);
        cell.maxCharInCell(8);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(fileNumber, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(26f);
        cell.maxCharInCell(13);
        cell.text(text);
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
        cell = new Cell(7.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(3);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("诊断：", null, Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(9f);
        cell.maxCharInCell(6);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(11), dpi);
        text = new CellText(diagnosis, null, Align.ALIGN_MIDDLE, true);
        text.font(font);
        cell = new Cell(75.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(37);
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
        cell = new Cell(26f);
        cell.maxCharInCell(13);
        cell.text(text);
        row.addCell(cell);

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
        cell = new Cell(135f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(68);
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
        cell = new Cell(23f);
        cell.maxCharInCell(12);
        cell.text(text);
        row.addCell(cell);

        page.addRow(row);

        //===============================================
        // 第5-6行
        //===============================================
        String[] lines = textUtil.getLines(
                "（1）肌肉注射（2）静脉注射（3）静脉输液（4）导尿（5）下胃管" +
                "（6）膀胱冲洗（7）膀胱造瘘护理（8）喉管护理（9）肠内营养护理" +
                "（10）肠内营养护理肠外营养护理（11）换药（12）其他"
                , 98);
        for (int i = 0; i < lines.length; i ++) {
            String line = lines[i];

            row = new Row(contentRowHeight, 0f);
            row.border(RectRegion.NO_BORDER);

            font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
            if (0==i) {
                text = new CellText("治疗项目", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
                text.font(font);
            }
            else {
                text = new CellText("", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
                text.font(font);
            }
            cell = new Cell(27f);
            if (i==0) {
                cell.border(RectRegion.TOP | RectRegion.LEFT | RectRegion.RIGHT);
            }
            else {
                cell.border(RectRegion.LEFT | RectRegion.RIGHT);
            }
            cell.mmPaddingLeft(1.5f);
            cell.mmPaddingRight(1.5f);
            cell.maxCharInCell(18);
            cell.text(text);
            row.addCell(cell);
            if (0==i) {
                cell.mmHeight(row.mmHeight() * lines.length);
                cell.mmHeightModifiable(false);
            }


            font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
            text = new CellText(line, null, Align.ALIGN_MIDDLE, false);
            text.font(font);
            cell = new Cell(161f);
            if (i==0) {
                cell.border(RectRegion.TOP | RectRegion.RIGHT);
            }
            else {
                cell.border(RectRegion.RIGHT);
            }
            cell.mmPaddingLeft(1.5f);
            cell.mmPaddingRight(1.5f);
            cell.maxCharInCell(98);
            cell.text(text);
            row.addCell(cell);

            page.addRow(row);

        }

        //===============================================
        // 第7行
        //===============================================

        row = new Row(6f, 0f);
        row.border(RectRegion.NO_BORDER);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("日期时间", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(27f);
        cell.border(RectRegion.TOP | RectRegion.LEFT | RectRegion.RIGHT);
        cell.mmPaddingLeft(1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(18);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("操作及观察记录", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(119f);
        cell.border(RectRegion.TOP | RectRegion.RIGHT);
        cell.mmPaddingLeft(1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(76);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("护士签名", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(18f+1.5f+1.5f);
        cell.border(RectRegion.TOP | RectRegion.RIGHT);
        cell.mmPaddingLeft(1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(12);
        cell.text(text);
        row.addCell(cell);

        font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
        text = new CellText("患者签名", null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
        text.font(font);
        cell = new Cell(18f+1.5f+1.5f);
        cell.border(RectRegion.TOP | RectRegion.RIGHT);
        cell.mmPaddingLeft(1.5f);
        cell.mmPaddingRight(1.5f);
        cell.maxCharInCell(12);
        cell.text(text);
        row.addCell(cell);

        page.addRow(row);
        page.fixedRowsSize(page.rowSize());

        return page;

    }

    public List<String> pageContent(long userId,
                                           long patientId,
                                           Page page,
                                           List<Record> records,
                                           int maxCharSizeInCell,
                                           String storagePath
    ) {
        Row       row   = null;
        Cell      cell  = null;
        CellText  text  = null;
        CellImage image = null;
        Font      font  = null;
        String    string= null;
        List<String> fileAbsPaths = new ArrayList<>();

        //===============================================
        // 内容 - 36行
        //===============================================
        int pageIndex = 1;
        int rowSizeLeft = contentRowSize;

        long pageFirstLineRecordId = 0;
        int pageFirstLineIndex = 0;
        for (int i = 0; i < records.size(); i ++) {
            Record record = records.get(i);
            String[] lines = record.lines(maxCharSizeInCell);
            int[] linesInPage = record.linesInPage();

            for (int lineIndex = 0; lineIndex < linesInPage.length; lineIndex++) {
                if (linesInPage[lineIndex] > 0) {
                    continue;
                }
                boolean needDrawDateAndSign = (lineIndex == 0);

                if (rowSizeLeft == contentRowSize) {
                    pageFirstLineRecordId = record.id();
                    pageFirstLineIndex = lineIndex;
                }

                row = new Row(contentRowHeight, 0f);
                row.border(RectRegion.NO_BORDER);

                // 记录日期
                font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
                string = needDrawDateAndSign ? record.date() : "";
                text = new CellText(string, null, Align.ALIGN_CENTER | Align.ALIGN_MIDDLE, false);
                text.font(font);
                cell = new Cell(27f);
                cell.border(RectRegion.TOP | RectRegion.LEFT | RectRegion.RIGHT);
                cell.mmPaddingLeft(1.5f);
                cell.mmPaddingRight(1.5f);
                cell.maxCharInCell(18);
                cell.text(text);
                row.addCell(cell);

                // 记录内容
                font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
                text = new CellText(lines[lineIndex], null, Align.ALIGN_MIDDLE, false);
                text.font(font);
                cell = new Cell(119f);
                cell.border(RectRegion.TOP | RectRegion.RIGHT);
                cell.mmPaddingLeft(1.5f);
                cell.mmPaddingRight(1.5f);
                cell.maxCharInCell(72);
                cell.text(text);
                row.addCell(cell);

                // 空白
                font = fontUtil.getFont(fontName, Font.PLAIN, fontUtil.poundToMm(9), dpi);
                text = new CellText("", null, Align.ALIGN_MIDDLE, false);
                text.font(font);

                // 护士签名
                cell = new Cell(18f + 1.5f + 1.5f);
                cell.border(RectRegion.TOP | RectRegion.RIGHT);
                cell.mmPaddingLeft(1.5f);
                cell.mmPaddingRight(1.5f);
                try {
                    if (needDrawDateAndSign) {
                        image = new CellImage(record.nurseSignUrl());
                        cell.image(image);
                    } else {
                        cell.text(text);
                    }
                } catch (IOException ioe) {
                    cell.text(text);
                }
                row.addCell(cell);

                // 患者签名
                cell = new Cell(18f + 1.5f + 1.5f);
                cell.border(RectRegion.TOP | RectRegion.RIGHT);
                cell.mmPaddingLeft(1.5f);
                cell.mmPaddingRight(1.5f);
                try {
                    if (needDrawDateAndSign) {
                        image = new CellImage(record.patientSignUrl());
                        cell.image(image);
                    } else {
                        cell.text(text);
                    }
                } catch (IOException ioe) {
                    cell.text(text);
                }
                row.addCell(cell);

                // 标识该行已写入
                linesInPage[lineIndex] = pageIndex;

                // 添加到页面中
                page.addRow(row);
                rowSizeLeft--;

                // 满一页后
                if (0 == rowSizeLeft || (i+1==records.size() && lineIndex+1==linesInPage.length)) {
                    // 封底
                    page.getLastRow().addCellBorders(RectRegion.BOTTOM);
                    // 保存
                    StringBuilder absPath = new StringBuilder(storagePath);
                    absPath.append("vr_")
                            .append(userId).append("_")
                            .append(patientId).append("_")
                            .append(pageIndex).append("_")
                            .append(pageFirstLineRecordId).append("_")
                            .append(pageFirstLineIndex).append("_")
                            .append(record.id()).append("_")
                            .append(lineIndex).append("");
                    File file = new File(absPath.toString());
                    page.save(file);
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
        String content;
        String nurseSignUrl;
        String patientSignUrl;
        String date;

        TextUtil textUtil = null;
        String[] lines = null;
        int[] linesInPage = null;

        public Record() {}

        public long id() {
            return id;
        }
        public Record id(long id) {
            this.id = id;
            return this;
        }

        public String content() {
            return content;
        }
        public Record content(String content) {
            this.content = content;
            return this;
        }

        public String nurseSignUrl() {
            return nurseSignUrl;
        }
        public Record nurseSignUrl(String nurseSignUrl) {
            this.nurseSignUrl = nurseSignUrl;
            return this;
        }

        public String patientSignUrl() {
            return patientSignUrl;
        }
        public Record patientSignUrl(String patientSignUrl) {
            this.patientSignUrl = patientSignUrl;
            return this;
        }

        public String date() {
            return date;
        }
        public Record date(String date) {
            this.date = date;
            return this;
        }

        public TextUtil textUtil() {
            return textUtil;
        }
        public Record textUtil(TextUtil textUtil) {
            this.textUtil = textUtil;
            return this;
        }

        public String[] lines(int maxCharSizeInCell) {
            if (null==lines) {
                lines = textUtil.getLines(content(), maxCharSizeInCell);
                linesInPage = new int[lines.length];
            }
            return lines;
        }
        public int[] linesInPage() {
            return linesInPage;
        }
    }
}

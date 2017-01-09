package com.cooltoo.nurse360.util;

import com.cooltoo.constants.GenderType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.nurse360.service.file.TemporaryFileStorageServiceForNurse360;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Created by zhaolisong on 06/01/2017.
 */
public class PdfUtil {

    private static final Logger logger = LoggerFactory.getLogger(PdfUtil.class);


    /***************************************************************************
     *          出诊记录打印
     ***************************************************************************/
    public static String createVisitPatientRecordPrint(String vendorName,
                                                       String diagnose,
                                                       UserBean user,
                                                       UserAddressBean userAddress,
                                                       PatientBean patient,
                                                       List<NurseVisitPatientBean> records,
                                                       TemporaryFileStorageServiceForNurse360 temporaryFileStorage,
                                                       Nurse360Utility utility
    ) {
        if (VerifyUtil.isListEmpty(records)) {
            logger.error("no records to make pdf.");
            throw new BadRequestException(ErrorCode.NURSE360_NO_CONTENT);
        }
        BaseFont song = null;
        try {
            song = BaseFont.createFont(utility.getFontSimsun(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        catch (DocumentException de) {
            logger.error("load font failed. exception is {}", de);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }
        catch (IOException ioe) {
            logger.error("load font failed. exception is {}", ioe);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }

        //===============================================
        //          all table cell format---start
        //===============================================
        Font head_font_normal_row_0    = new Font(song, 18, Font.BOLD);
        Font head_font_underline_row_0 = new Font(song, 18, Font.BOLD|Font.UNDERLINE);
        Font head_font_normal_row_1    = new Font(song, 9,  Font.NORMAL);
        Font head_font_underline_row_1 = new Font(song, 11, Font.NORMAL|Font.UNDERLINE);
        Font head_font_normal_row_2    = new Font(song, 9,  Font.NORMAL);
        Font head_font_underline_row_2 = new Font(song, 11, Font.NORMAL|Font.UNDERLINE);
        Font head_font_normal_row_3    = new Font(song, 9,  Font.NORMAL);
        Font head_font_underline_row_3 = new Font(song, 11, Font.NORMAL|Font.UNDERLINE);
        Font head_font_normal_row_4    = new Font(song, 9,  Font.NORMAL);
        Font head_font_underline_row_4 = new Font(song, 11, Font.NORMAL|Font.UNDERLINE);
        Font head_font_normal_row_5    = new Font(song, 9,  Font.NORMAL);
        Font head_font_underline_row_5 = new Font(song, 11, Font.NORMAL|Font.UNDERLINE);
        Font head_font_normal_row_6    = new Font(song, 9,  Font.NORMAL);
        Font head_font_underline_row_6 = new Font(song, 11, Font.NORMAL|Font.UNDERLINE);


        int FIX_CONTENT                       = -1;
        float[] table_column_width            = new float[]{46.3f, 354f, 61f, 61f};
        // head_field_name_row_0
        int    head_alignment_h_row_0         = Element.ALIGN_CENTER;
        int    head_alignment_v_row_0         = Element.ALIGN_TOP;
        int    head_column_span_row_0         = 4;
        float  head_line_spacing_row_0        = 18;
        String head_field_name_row_0          = "家庭治疗、护理操作记录单";

        // the first row of head rows
        int    head_alignment_h_row_1_1       = Element.ALIGN_LEFT;
        int    head_alignment_v_row_1_1       = Element.ALIGN_TOP;
        int    head_column_span_row_1_1       = 2;
        float  head_line_spacing_row_1_1      = 7;
        int    head_field_name_size_row_1_1   = 20;
        int    head_completion_size_row_1_1   = 40;
        String head_field_name_row_1_1        = "社区卫生服务中心/站";

        int    head_alignment_h_row_1_2       = Element.ALIGN_LEFT;
        int    head_alignment_v_row_1_2       = Element.ALIGN_TOP;
        int    head_column_span_row_1_2       = 2;
        float  head_line_spacing_row_1_2      = 7;
        int    head_field_name_size_row_1_2   = 8;
        int    head_completion_size_row_1_2   = 14;
        String head_field_name_row_1_2        = "档案号：";

        // the second row of head rows
        int    head_alignment_h_row_2_1       = Element.ALIGN_LEFT;
        int    head_alignment_v_row_2_1       = Element.ALIGN_TOP;
        int    head_column_span_row_2_1       = 2;
        float  head_line_spacing_row_2_1      = 7;
        int    head_completion_size_row_2_1_1 = 14;
        int    head_field_name_size_row_2_1_1 = 6;
        String head_field_name_row_2_1_1      = "姓名：";
        int    head_completion_size_row_2_1_2 = 5;
        int    head_field_name_size_row_2_1_2 = 6;
        String head_field_name_row_2_1_2      = "性别：";
        int    head_completion_size_row_2_1_3 = 4;
        int    head_field_name_size_row_2_1_3 = 6;
        String head_field_name_row_2_1_3      = "年龄：";
        int    head_completion_size_row_2_1_4 = 29;
        int    head_field_name_size_row_2_1_4 = 6;
        String head_field_name_row_2_1_4      = "诊断：";

        int    head_alignment_h_row_2_2 = Element.ALIGN_LEFT;
        int    head_alignment_v_row_2_2 = Element.ALIGN_TOP;
        int    head_column_span_row_2_2 = 2;
        float  head_line_spacing_row_2_2 = 7;
        int    head_completion_size_row_2_2 = 14;
        int    head_field_name_size_row_2_2 = 8;
        String head_field_name_row_2_2 = "联系人：";

        // the third row of head rows
        int    head_alignment_h_row_3_1     = Element.ALIGN_LEFT;
        int    head_alignment_v_row_3_1     = Element.ALIGN_TOP;
        int    head_column_span_row_3_1     = 2;
        float  head_line_spacing_row_3_1    = 7;
        int    head_completion_size_row_3_1 = 63;
        int    head_field_name_size_row_3_1 = 10;
        String head_field_name_row_3_1      = "家庭住址：";

        int    head_alignment_h_row_3_2     = Element.ALIGN_LEFT;
        int    head_alignment_v_row_3_2     = Element.ALIGN_TOP;
        int    head_column_span_row_3_2     = 2;
        float  head_line_spacing_row_3_2    = 7;
        int    head_completion_size_row_3_2 = 13;
        int    head_field_name_size_row_3_2 = 10;
        String head_field_name_row_3_2      = "联系电话：";

        // the fourth row of head rows
        int    head_alignment_h_row_4_1     = Element.ALIGN_CENTER;
        int    head_alignment_v_row_4_1     = Element.ALIGN_MIDDLE;
        int    head_column_span_row_4_1     = 1;
        float  head_line_spacing_row_4_1    = 0;
        int    head_completion_size_row_4_1 = 0;
        int    head_field_name_size_row_4_1 = 8;
        String head_field_name_row_4_1      = "治疗项目";

        int    head_alignment_h_row_4_2     = Element.ALIGN_LEFT;
        int    head_alignment_v_row_4_2     = Element.ALIGN_MIDDLE;
        int    head_column_span_row_4_2     = 3;
        float  head_line_spacing_row_4_2    = 0;
        int    head_completion_size_row_4_2 = 0;
        int    head_field_name_size_row_4_2 = FIX_CONTENT;
        String head_field_name_row_4_2      = "（1）肌肉注射（2）静脉注射（3）静脉输液（4）导尿（5）下胃管（6）膀胱冲洗（7）膀胱造瘘护理（8）喉管护理（9）肠内营养护理（10）肠内营养护理肠外营养护理（11）换药（12）其他";

        // the fifth row of head rows
        int    head_alignment_h_row_5_1     = Element.ALIGN_CENTER;
        int    head_alignment_v_row_5_1     = Element.ALIGN_MIDDLE;
        int    head_column_span_row_5_1     = 1;
        float  head_line_spacing_row_5_1    = 4;
        int    head_completion_size_row_5_1 = 0;
        int    head_field_name_size_row_5_1 = 8;
        String head_field_name_row_5_1      = "日期时间";

        int    head_alignment_h_row_5_2     = Element.ALIGN_CENTER;
        int    head_alignment_v_row_5_2     = Element.ALIGN_MIDDLE;
        int    head_column_span_row_5_2     = 1;
        float  head_line_spacing_row_5_2    = 4;
        int    head_field_name_size_row_5_2 = 14;
        int    head_completion_size_row_5_2 = 0;
        String head_field_name_row_5_2      = "操作及观察记录";

        int    head_alignment_h_row_5_3     = Element.ALIGN_CENTER;
        int    head_alignment_v_row_5_3     = Element.ALIGN_MIDDLE;
        int    head_column_span_row_5_3     = 1;
        float  head_line_spacing_row_5_3    = 4;
        int    head_completion_size_row_5_3 = 0;
        int    head_field_name_size_row_5_3 = 8;
        String head_field_name_row_5_3      = "护士签名";

        int    head_alignment_h_row_5_4     = Element.ALIGN_CENTER;
        int    head_alignment_v_row_5_4     = Element.ALIGN_MIDDLE;
        int    head_column_span_row_5_4     = 1;
        float  head_line_spacing_row_5_4    = 4;
        int    head_completion_size_row_5_4 = 0;
        int    head_field_name_size_row_5_4 = 8;
        String head_field_name_row_5_4      = "患者签名";

        // the sixth row of head rows
        int   head_alignment_h_row_6_1     = Element.ALIGN_CENTER;
        int   head_alignment_v_row_6_1     = Element.ALIGN_MIDDLE;
        int   head_column_span_row_6_1     = 1;
        float head_line_spacing_row_6_1    = 4;

        int   head_alignment_h_row_6_2     = Element.ALIGN_LEFT;
        int   head_alignment_v_row_6_2     = Element.ALIGN_MIDDLE;
        int   head_column_span_row_6_2     = 1;
        float head_line_spacing_row_6_2    = 4;

        int   head_alignment_h_row_6_3  = Element.ALIGN_CENTER;
        int   head_alignment_v_row_6_3  = Element.ALIGN_MIDDLE;
        int   head_column_span_row_6_3  = 1;
        float head_line_spacing_row_6_3 = 4;

        int   head_alignment_h_row_6_4  = Element.ALIGN_CENTER;
        int   head_alignment_v_row_6_4  = Element.ALIGN_MIDDLE;
        int   head_column_span_row_6_4  = 1;
        float head_line_spacing_row_6_4 = 4;
        //===============================================
        //          all table cell format---end
        //===============================================



        File file = null;
        try {
            file = File.createTempFile(System.currentTimeMillis() + "", ".pdf");
        }
        catch (IOException ioe) {
            logger.error("create temporary file failed. exception is {}", ioe);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }

        Document document = new Document();
        document.setPageSize(PageSize.A4);

        PdfWriter pdfWriter = null;
        try {
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
        }
        catch (FileNotFoundException fnfe) {
            logger.error("file not found. exception is {}", fnfe);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }
        catch (DocumentException de) {
            logger.error("create pdf writer failed. exception is {}", de);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }

        document.open();
        //=========================
        //     head table
        //=========================
        PdfPTable header = new PdfPTable(4);
        header.setLockedWidth(true);
        try { header.setTotalWidth(table_column_width); }
        catch (DocumentException de) {
            logger.error("set table width failed. exception is {}", de);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }

        // head_field_name_row_0
        Paragraph headerTitle = new Paragraph(head_field_name_row_0, head_font_normal_row_0);
        PdfPCell cell = new PdfPCell(headerTitle);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(head_alignment_h_row_0);
        cell.setVerticalAlignment(head_alignment_v_row_0);
        cell.setColspan(head_column_span_row_0);
        cell.setPaddingBottom(head_line_spacing_row_0);
        header.addCell(cell);

        // hospital
        Paragraph headerHospital = new Paragraph();
        headerHospital.add(underline(newChunk(head_completion_size_row_1_1, vendorName, head_font_underline_row_1)));
        headerHospital.add(newChunk(head_field_name_size_row_1_1, head_field_name_row_1_1, head_font_normal_row_1));
        cell = new PdfPCell(headerHospital);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(head_alignment_h_row_1_1);
        cell.setVerticalAlignment(head_alignment_v_row_1_1);
        cell.setColspan(head_column_span_row_1_1);
        cell.setPaddingBottom(head_line_spacing_row_1_1);
        header.addCell(cell);

        // FN
        Paragraph headerFN= new Paragraph();
        headerFN.add(newChunk(head_field_name_size_row_1_2, head_field_name_row_1_2, head_font_normal_row_1));
        headerFN.add(underline(newChunk(head_completion_size_row_1_2, patient.getId()+"", head_font_underline_row_1)));
        cell = new PdfPCell(headerFN);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(head_alignment_h_row_1_2);
        cell.setVerticalAlignment(head_alignment_v_row_1_2);
        cell.setColspan(head_column_span_row_1_2);
        cell.setPaddingBottom(head_line_spacing_row_1_2);
        header.addCell(cell);

        // patient info
        Paragraph headerPatientInfo= new Paragraph();
        headerPatientInfo.add(newChunk(head_field_name_size_row_2_1_1, head_field_name_row_2_1_1, head_font_normal_row_2));
        headerPatientInfo.add(underline(newChunk(head_completion_size_row_2_1_1, patient.getName(), head_font_underline_row_2)));
        headerPatientInfo.add(newChunk(head_field_name_size_row_2_1_2, head_field_name_row_2_1_2, head_font_normal_row_2));
        headerPatientInfo.add(underline(newChunk(head_completion_size_row_2_1_2, GenderType.genderInfo(patient.getGender()), head_font_underline_row_2)));
        headerPatientInfo.add(newChunk(head_field_name_size_row_2_1_3, head_field_name_row_2_1_3, head_font_normal_row_2));
        headerPatientInfo.add(underline(newChunk(head_completion_size_row_2_1_3, patient.getAge()+"", head_font_underline_row_2)));
        headerPatientInfo.add(newChunk(head_field_name_size_row_2_1_4, head_field_name_row_2_1_4, head_font_normal_row_2));
        headerPatientInfo.add(underline(newChunk(head_completion_size_row_2_1_4, diagnose, head_font_underline_row_2)));
        cell = new PdfPCell(headerPatientInfo);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(head_alignment_h_row_2_1);
        cell.setVerticalAlignment(head_alignment_v_row_2_1);
        cell.setColspan(head_column_span_row_2_1);
        cell.setPaddingBottom(head_line_spacing_row_2_1);
        header.addCell(cell);

        // Contact us
        Paragraph headerContactUs= new Paragraph();
        headerContactUs.add(newChunk(head_field_name_size_row_2_2, head_field_name_row_2_2, head_font_normal_row_2));
        headerContactUs.add(underline(newChunk(head_completion_size_row_2_2, user.getName(), head_font_underline_row_2)));
        cell = new PdfPCell(headerContactUs);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(head_alignment_h_row_2_2);
        cell.setVerticalAlignment(head_alignment_v_row_2_2);
        cell.setColspan(head_column_span_row_2_2);
        cell.setPaddingBottom(head_line_spacing_row_2_2);
        header.addCell(cell);

        // contact way
        Paragraph headerPatientContactWay = new Paragraph();
        headerPatientContactWay.add(newChunk(head_field_name_size_row_3_1, head_field_name_row_3_1, head_font_normal_row_3));
        headerPatientContactWay.add(underline(newChunk(head_completion_size_row_3_1, userAddress.toAddress(), head_font_underline_row_3)));
        cell = new PdfPCell(headerPatientContactWay);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(head_alignment_h_row_3_1);
        cell.setVerticalAlignment(head_alignment_v_row_3_1);
        cell.setColspan(head_column_span_row_3_1);
        cell.setPaddingBottom(head_line_spacing_row_3_1);
        header.addCell(cell);

        // Telephone
        Paragraph headerTel= new Paragraph();
        headerTel.add(newChunk(head_field_name_size_row_3_2, head_field_name_row_3_2, head_font_normal_row_3));
        headerTel.add(underline(newChunk(head_completion_size_row_3_2, user.getMobile(), head_font_underline_row_3)));
        cell = new PdfPCell(headerTel);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(head_alignment_h_row_3_2);
        cell.setVerticalAlignment(head_alignment_v_row_3_2);
        cell.setColspan(head_column_span_row_3_2);
        cell.setPaddingBottom(head_line_spacing_row_3_2);
        header.addCell(cell);

        // operation head_field_name_row_0
        Paragraph headerOperationTitle = new Paragraph();
        headerOperationTitle.add(newChunk(head_field_name_size_row_4_1, head_field_name_row_4_1, head_font_normal_row_4));
        cell = new PdfPCell(headerOperationTitle);
        cell.setUseAscender(true);
        cell.setHorizontalAlignment(head_alignment_h_row_4_1);
        cell.setVerticalAlignment(head_alignment_v_row_4_1);
        cell.setColspan(head_column_span_row_4_1);
        cell.setPaddingBottom(head_line_spacing_row_4_1);
        header.addCell(cell);

        // operation item
        Paragraph headerOperationItem = new Paragraph();
        headerOperationItem.add(newChunk(head_field_name_size_row_4_2 ==FIX_CONTENT ? contentLength(head_field_name_row_4_2) : head_field_name_size_row_4_2, head_field_name_row_4_2, head_font_normal_row_4));
        cell = new PdfPCell(headerOperationItem);
        cell.setColspan(head_column_span_row_4_2);
        cell.setHorizontalAlignment(head_alignment_h_row_4_2);
        cell.setVerticalAlignment(head_alignment_v_row_4_2);
        cell.setLeading(1.5f, 1.5f);
        cell.setPaddingTop(-3f);
        cell.setPaddingBottom(4.5f);
        header.addCell(cell);

        // record time
        Paragraph headerRecordTime = new Paragraph();
        headerRecordTime.add(newChunk(head_field_name_size_row_5_1, head_field_name_row_5_1, head_font_normal_row_5));
        cell = new PdfPCell(headerRecordTime);
        cell.setVerticalAlignment(head_alignment_v_row_5_1);
        cell.setHorizontalAlignment(head_alignment_h_row_5_1);
        cell.setColspan(head_column_span_row_5_1);
        cell.setPaddingBottom(head_line_spacing_row_5_1);
        header.addCell(cell);

        // record
        Paragraph headerRecord = new Paragraph();
        headerRecord.add(newChunk(head_field_name_size_row_5_2, head_field_name_row_5_2, head_font_normal_row_5));
        cell = new PdfPCell(headerRecord);
        cell.setVerticalAlignment(head_alignment_v_row_5_2);
        cell.setHorizontalAlignment(head_alignment_h_row_5_2);
        cell.setColspan(head_column_span_row_5_2);
        cell.setPaddingBottom(head_line_spacing_row_5_2);
        header.addCell(cell);

        // record nurse sign
        Paragraph headerNurseSign = new Paragraph();
        headerNurseSign.add(newChunk(head_field_name_size_row_5_3, head_field_name_row_5_3, head_font_normal_row_5));
        cell = new PdfPCell(headerNurseSign);
        cell.setVerticalAlignment(head_alignment_v_row_5_3);
        cell.setHorizontalAlignment(head_alignment_h_row_5_3);
        cell.setColspan(head_column_span_row_5_3);
        cell.setPaddingBottom(head_line_spacing_row_5_3);
        header.addCell(cell);

        // record patient sign
        Paragraph headerPatientSign = new Paragraph();
        headerPatientSign.add(newChunk(head_field_name_size_row_5_4, head_field_name_row_5_4, head_font_normal_row_5));
        cell = new PdfPCell(headerPatientSign);
        cell.setVerticalAlignment(head_alignment_v_row_5_4);
        cell.setHorizontalAlignment(head_alignment_h_row_5_4);
        cell.setColspan(head_column_span_row_5_4);
        cell.setPaddingBottom(head_line_spacing_row_5_4);
        header.addCell(cell);


        //===================================
        //       create table
        //===================================
        PdfPTable table = new PdfPTable(4);
        table.setLockedWidth(true);
        try { table.setTotalWidth(table_column_width); }
        catch (DocumentException de) {
            logger.error("set table width failed. exception is {}", de);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }

        // add header
        cell = new PdfPCell(header);
        cell.setColspan(4);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        table.setHeaderRows(1);

        // add cells
        for (NurseVisitPatientBean tmp : records) {
            Phrase col1 = new Phrase();
            String date = NumberUtil.timeToString(tmp.getTime(), NumberUtil.DATE_YYYY_MM_DD_HH_MM);
            col1.add(newChunk(contentLength(date), date, head_font_normal_row_6));
            cell = new PdfPCell(col1);
            cell.setHorizontalAlignment(head_alignment_h_row_6_1);
            cell.setVerticalAlignment(head_alignment_v_row_6_1);
            cell.setColspan(head_column_span_row_6_1);
            cell.setPaddingBottom(head_line_spacing_row_6_1);
            table.addCell(cell);

            Phrase col2 = new Phrase();
            col2.add(newChunk(contentLength(tmp.getVisitRecord()), tmp.getVisitRecord(), head_font_normal_row_6));
            cell = new PdfPCell(col2);
            cell.setHorizontalAlignment(head_alignment_h_row_6_2);
            cell.setVerticalAlignment(head_alignment_v_row_6_2);
            cell.setColspan(head_column_span_row_6_2);
            cell.setPaddingBottom(head_line_spacing_row_6_2);
            table.addCell(cell);

            Image image = null;
            try {
                image = Image.getInstance(tmp.getNurseSignUrl());
                image.scaleToFit(57f, 57f);
            }
            catch (Exception e) {
                logger.warn("get nurse sign failed. exception is {}", e.getMessage());
            }
            if (null!=image) {
                cell = new PdfPCell(image);
            }
            else {
                cell = new PdfPCell(newChunk(1, " ", null));
            }
            cell.setPadding(1.5f);
            cell.setHorizontalAlignment(head_alignment_h_row_6_3);
            cell.setVerticalAlignment(head_alignment_v_row_6_3);
            cell.setColspan(head_column_span_row_6_3);
            cell.setPaddingBottom(head_line_spacing_row_6_3);
            table.addCell(cell);

            try {
                image = Image.getInstance(tmp.getPatientSignUrl());
                image.scaleToFit(57f, 57f);
            }
            catch (Exception e) {
                logger.warn("get patient sign failed. exception is {}", e.getMessage());
            }
            if (null!=image) {
                cell = new PdfPCell(image);
            }
            else {
                cell = new PdfPCell(newChunk(1, " ", null));
            }
            cell.setHorizontalAlignment(head_alignment_h_row_6_4);
            cell.setVerticalAlignment(head_alignment_v_row_6_4);
            cell.setColspan(head_column_span_row_6_4);
            cell.setPaddingBottom(head_line_spacing_row_6_4);
            table.addCell(cell);
        }
        try { document.add(table); }
        catch (Exception e) {
            logger.error("set table in pdf file failed. exception is {}", e);
            throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
        }

        // write document
        pdfWriter.flush();
        document.close();
        pdfWriter.close();

        FileUtil.getInstance().moveFile(file.getAbsolutePath(), temporaryFileStorage.getStoragePath()+file.getName());
        return temporaryFileStorage.getNginxRelativePath()+file.getName();
    }

    private static Phrase newChunk(int maxLength, String content, Font font) {
        Phrase phrase;
        String contentLength = contentMatchLength(content, maxLength);
        if (null!=font) {
            phrase = new Phrase(contentLength, font);
        }
        else {
            phrase = new Phrase(contentLength);
        }
        return phrase;
    }

    private static String contentMatchLength(String content, int maxLength) {
        if (maxLength<=0 || null==content) {
            return content;
        }

        int length = 0;
        for (int i = 0, count=content.length(); i < count; i ++) {
            char tmpChar = content.charAt(i);
            length += ((tmpChar<=127) ? 1 : 2);
        }

        StringBuilder msg = new StringBuilder();
        int whiteSpace = 0;
        int dotSpace = 0;
        if (maxLength>=length) {
            whiteSpace = maxLength - length;
            msg.append(content);
        }
        else {
            int tmpLength = 0;
            for (int i = 0, count=content.length(); i < count; i ++) {
                char tmpChar = content.charAt(i);
                tmpLength += ((tmpChar<=127) ? 1 : 2);
                if (tmpLength+3<=maxLength) {
                    msg.append(tmpChar);
                    continue;
                }
                tmpLength -= ((tmpChar<=127) ? 1 : 2);
                break;
            }
            dotSpace = maxLength - tmpLength;

        }
        if (whiteSpace>0) {
            for (int i = whiteSpace/2; i>0; i--) {
                msg.insert(0, "\u00a0");
                msg.append("\u00a0");
            }
            if (whiteSpace%2 > 0) {
                msg.append("\u00a0");
            }
        }
        if (dotSpace>0) {
            for (int i = dotSpace; i>0; i--) {
                msg.append(".");
            }
        }
        return msg.toString();
    }

    public static int contentLength(String content) {
        if (null==content) {
            return 0;
        }

        int length = 0;
        for (int i = 0, count=content.length(); i < count; i ++) {
            char tmpChar = content.charAt(i);
            length += ((tmpChar<=127) ? 1 : 2);
        }
        return length;
    }

    private static Phrase underline(Phrase phrase) {
        if (null!=phrase) {
//            phrase.setUnderline(0.5f, -2f);
        }
        return phrase;
    }

    public static void splitPDF(InputStream inputStream,
                                OutputStream outputStream,
                                int fromPage,
                                int toPage) {
        Document document = new Document();
        try {
            PdfReader inputPDF = new PdfReader(inputStream);
            int totalPages = inputPDF.getNumberOfPages();

            //make fromPage equals to toPage if it is greater
            if(fromPage > toPage ) {
                fromPage = toPage;
            }
            if(toPage > totalPages) {
                toPage = totalPages;
            }

            // Create a writer for the OutputStream
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF data

            PdfImportedPage page;
            while(fromPage <= toPage) {
                document.newPage();
                page = writer.getImportedPage(inputPDF, fromPage);
                cb.addTemplate(page, 0, 0);
                fromPage++;
            }
            outputStream.flush();

            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null!=document && document.isOpen()) { document.close(); }
            if (outputStream != null) { try { outputStream.close();} catch (IOException ioe){} }
        }
    }
}

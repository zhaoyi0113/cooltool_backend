package com.cooltoo.nurse360.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.chart.converter.VisitPatientRecordConverter;
import com.cooltoo.go2nurse.chart.generator.VisitPatientRecordPagePrinter;
import com.cooltoo.go2nurse.chart.ui.layout.Page;
import com.cooltoo.go2nurse.chart.util.DpiUtil;
import com.cooltoo.go2nurse.chart.util.FontUtil;
import com.cooltoo.go2nurse.chart.util.PageSize;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseVisitPatientService;
import com.cooltoo.go2nurse.service.UserAddressService;
import com.cooltoo.nurse360.service.file.TemporaryFileStorageServiceForNurse360;
import com.cooltoo.nurse360.service.file.VisitPatientFileStorageServiceForNurse360;
import com.cooltoo.nurse360.service.file.VisitPatientFileStorageServiceForNurse360.PageFile;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhaolisong on 22/01/2017.
 */
@Service("NurseVisitPatientPdfService")
public class NurseVisitPatientPdfService {

    public static final int GET_PAGE = 1;
    public static final int ADD_PAGE = 2;
    public static final int EDIT_PAGE = 3;

    private static final Logger logger = LoggerFactory.getLogger(NurseVisitPatientPdfService.class);

    @Autowired private NurseVisitPatientService nurseVisitPatientService;
    @Autowired private VisitPatientFileStorageServiceForNurse360 visitPatientFileStorageService;
    @Autowired private UserAddressService userAddressService;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService hospitalDepartmentService;
    @Autowired private TemporaryFileStorageServiceForNurse360 temporaryFileStorageService;
    @Autowired private Nurse360Utility nurse360Utility;


    public boolean recreateVisitPatientPages(Long userId, Long patientId,
                                             ServiceVendorType vendorType, Long vendorId, Long departId,
                                             Long recordId,
                                             int operation /* 1: get page; 2: add record; 3: modify or delete record*/
    ) {

        FontUtil.loadBaseFont(nurse360Utility.getFontSimsun());
        logger.info("create visit patient pages, userId={} patientId={} vendorType={} vendorId={} departId={} recordId={} isAddRecord={}",
                userId, patientId, vendorType, vendorId, departId, recordId, operation);

        long startRecordId = 0;
        int  startRecordLine = 0;
        int  startPageIndex = 1;

        PageFile firstPage = null;
        List<PageFile> pagesNeedRemoved = null;
        if (EDIT_PAGE==operation) {
            pagesNeedRemoved = visitPatientFileStorageService.getFileAfterRecord(
                    userId + "_" + patientId,
                    vendorType.ordinal() + "_" + vendorId + "_" + departId,
                    recordId);
            if (!VerifyUtil.isListEmpty(pagesNeedRemoved)) {
                for (PageFile page : pagesNeedRemoved) {
                    if (null == firstPage) {
                        firstPage = page;
                    }
                    if (firstPage.pageIndex > page.pageIndex) {
                        firstPage = page;
                    }
                }
            }
        }
        else if (EDIT_PAGE==operation) {
            firstPage = visitPatientFileStorageService.getLastFile(
                    userId + "_" + patientId,
                    vendorType.ordinal() + "_" + vendorId + "_" + departId);
        }

        //
        if (null != firstPage) {
            startRecordId = firstPage.firstRecordId;
            startRecordLine = firstPage.firstLine;
            startPageIndex = firstPage.pageIndex;
        }

        // get visit record
        List<NurseVisitPatientBean> visitPatientRecords = nurseVisitPatientService.getVisitRecordByCondition(
                userId, patientId,
                vendorType, vendorId, departId,
                CommonStatus.DELETED, startRecordId,
                NurseVisitPatientService.SORT_ID_ASC);

        if (!VerifyUtil.isListEmpty(visitPatientRecords)) {
            // delete page that need recreated.
            if (null!=pagesNeedRemoved) {
                for (PageFile page : pagesNeedRemoved) {
                    if (null!=page && null!=page.file && page.file.exists()) {
                        page.file.delete();
                    }
                }
            }
            if (null!=firstPage && null!=firstPage.file && firstPage.file.exists()) {
                firstPage.file.delete();
            }

            // get information show in top region
            UserBean user = visitPatientRecords.get(0).getUser();
            PatientBean patient = visitPatientRecords.get(0).getPatient();
            HospitalBean hospital = hospitalService.getHospital(vendorId.intValue());
            HospitalDepartmentBean department = hospitalDepartmentService.getById(departId.intValue(), "");
            UserAddressBean userAddress = userAddressService.getUserDefaultAddress(userId);
            StringBuilder vendorName = new StringBuilder();
            if (null != hospital) {
                vendorName.append(hospital.getName());
            }
            if (null != department) {
                vendorName.append(department.getName());
            }

            // convert records to show in content region
            VisitPatientRecordConverter recordConverter = new VisitPatientRecordConverter();
            List<VisitPatientRecordPagePrinter.Record> records = recordConverter.convert(visitPatientRecords);


            // create page printer
            VisitPatientRecordPagePrinter visitPatientPagePrinter = new VisitPatientRecordPagePrinter(
                    DpiUtil.DPI_300, PageSize.A4,
                    VisitPatientRecordPagePrinter.PAGE_PADDING,
                    VisitPatientRecordPagePrinter.BASE_FONT,
                    vendorName.toString(),
                    patient.getId() + "",
                    patient.getName(),
                    GenderType.genderInfo(patient.getGender()),
                    patient.getAge() + "",
                    "",
                    user.getName(),
                    userAddress.toAddress(),
                    user.getMobile(),
                    VisitPatientRecordPagePrinter.CONTENT_ROW_HEIGHT,
                    VisitPatientRecordPagePrinter.CONTENT_ROW_SIZE
            );
            // create page top region
            Page page = visitPatientPagePrinter.pageTop();
            // fill page content region
            // add save to temporary path
            List<String> savedPageFilePaths = visitPatientPagePrinter.pageContent(
                    userId, patientId,
                    vendorType.ordinal(), vendorId, departId,
                    page, records,
                    VisitPatientRecordPagePrinter.MAX_CHAR_IN_CELL,
                    temporaryFileStorageService.getStoragePath(),
                    startRecordId, startRecordLine, startPageIndex, operation!=GET_PAGE);
            // move pages to visit_patient/
            visitPatientFileStorageService.moveFileToHere(savedPageFilePaths, operation!=GET_PAGE);

            return true;
        }

        return false;
    }

    public List<String> getVisitPatientPages(Long userId, Long patientId,
                                             ServiceVendorType vendorType, Long vendorId, Long departId,
                                             String nginxPrefix) {
        List<String> pageUrls = visitPatientFileStorageService.getFileUrl(
                userId + "_" + patientId,
                vendorType.ordinal() + "_" + vendorId + "_" + departId,
                nginxPrefix);
        return pageUrls;
    }

    public boolean isVisitPatientPagesCreated(Long userId, Long patientId,
                                              ServiceVendorType vendorType, Long vendorId, Long departId
    ) {
        boolean pageHasBeenCreated = visitPatientFileStorageService.isFilePathExist(
                userId + "_" + patientId,
                vendorType.ordinal() + "_" + vendorId + "_" + departId
        );
        return pageHasBeenCreated;
    }
}

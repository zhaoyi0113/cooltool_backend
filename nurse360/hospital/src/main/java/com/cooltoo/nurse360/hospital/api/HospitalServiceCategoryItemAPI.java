package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ManagedBy;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.ServiceVendorCategoryAndItemService;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 06/03/2017.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalServiceCategoryItemAPI {

    @Autowired private ServiceVendorCategoryAndItemService vendorCategoryAndItemService;

    //=======================================================================================
    //                                   Getting
    //=======================================================================================
    private List<CommonStatus> getCommonStatus(String strStatus) {
        List<CommonStatus> statuses = new ArrayList<>();
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status) {
            statuses.add(status);
        }
        if ("all".equalsIgnoreCase(strStatus)) {
            statuses.add(CommonStatus.DISABLED);
            statuses.add(CommonStatus.ENABLED);
        }
        return statuses;
    }


    //=============================================
    //               Category Service
    //=============================================
    @RequestMapping(path = "/manager/category/{category_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public ServiceCategoryBean getServiceCategory(HttpServletRequest request,
                                                  @PathVariable(value = "category_id") long categoryId
    ) {
        List<ServiceCategoryBean> categories = vendorCategoryAndItemService.getCategoryAndParentById(categoryId);
        if (VerifyUtil.isListEmpty(categories)) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        else {
            if (categories.size()>1) {
                categories.remove(1);
            }
            return categories.get(0);
        }
    }

    /**
     * @param strLevel all/1/2
     **/
    @RequestMapping(path = "/manager/category/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countTopServiceCategory(HttpServletRequest request,
                                        @RequestParam(defaultValue = "all", name = "status") String strStatus,
                                        @RequestParam(defaultValue = "all", name = "level")  String strLevel
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        long topServiceCategoryCount = vendorCategoryAndItemService.countCategory(strLevel, statuses);
        return topServiceCategoryCount;
    }

    /**
     * @param strLevel all/1/2
     **/
    @RequestMapping(path = "/manager/category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceCategoryBean> getTopServiceCategory(HttpServletRequest request,
                                                           @RequestParam(defaultValue = "all",name = "status") String strStatus,
                                                           @RequestParam(defaultValue = "all", name = "level")  String strLevel,
                                                           @RequestParam(defaultValue = "0",  name = "index")  int pageIndex,
                                                           @RequestParam(defaultValue = "10", name = "number") int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);
        List<ServiceCategoryBean> topServiceCategories = vendorCategoryAndItemService.getCategory(strLevel, statuses, pageIndex, sizePerPage);
        return topServiceCategories;
    }

    @RequestMapping(path = "/manager/category/sub/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countSubServiceCategory(HttpServletRequest request,
                                            @RequestParam(defaultValue = "0",  name = "category_id") long categoryId,
                                            @RequestParam(defaultValue = "all",name = "status")      String strStatus
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);
        long subServiceCategoryCount = vendorCategoryAndItemService.countCategoryByParentId(categoryId, statuses);
        return subServiceCategoryCount;
    }

    @RequestMapping(path = "/manager/category/sub", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceCategoryBean> getSubServiceCategory(HttpServletRequest request,
                                                           @RequestParam(defaultValue = "0",  name = "category_id") long categoryId,
                                                           @RequestParam(defaultValue = "all",name = "status")      String strStatus,
                                                           @RequestParam(defaultValue = "0",  name = "index")       int pageIndex,
                                                           @RequestParam(defaultValue = "10", name = "number")      int sizePerPage
    ) {
        List<CommonStatus> statuses = getCommonStatus(strStatus);

        List<ServiceCategoryBean> serviceCategories = vendorCategoryAndItemService.getCategoryByParentId(
                categoryId, statuses, pageIndex, sizePerPage
        );
        return serviceCategories;
    }


    //=============================================
    //               Item Service
    //=============================================
    @RequestMapping(path = "/manager/item/{item_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public ServiceItemBean getItem(HttpServletRequest request,
                                   @PathVariable(value = "item_id") long itemId
    ) {
        ServiceItemBean item = vendorCategoryAndItemService.getItemById(itemId);
        return item;
    }

    @RequestMapping(path = "/manager/item/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countItemByCategoryAndStatus(HttpServletRequest request,
                                             @RequestParam(defaultValue = "", name = "category_id")       String strCategoryId,
                                             @RequestParam(defaultValue = "", name = "need_visit_record") String strNeedVisitRecord,
                                             @RequestParam(defaultValue = "", name = "manager_approved")  String strManagerApproved,
                                             @RequestParam(defaultValue = "", name = "status")            String strStatus
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];

        List<CommonStatus> statuses = getCommonStatus(strStatus);
        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        YesNoEnum needVisitRecord = YesNoEnum.parseString(strNeedVisitRecord);
        YesNoEnum managerApproved = YesNoEnum.parseString(strManagerApproved);
        long serviceItemCount = vendorCategoryAndItemService.countItemByCategoryId(
                ServiceVendorType.HOSPITAL, hospitalId, departmentId,
                categoryId, needVisitRecord, managerApproved, statuses
        );
        return serviceItemCount;
    }

    @RequestMapping(path = "/manager/item", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ServiceItemBean> getItemByCategoryAndStatus(HttpServletRequest request,
                                                            @RequestParam(defaultValue = "",  name = "category_id")       String strCategoryId,
                                                            @RequestParam(defaultValue = "",  name = "need_visit_record") String strNeedVisitRecord,
                                                            @RequestParam(defaultValue = "",  name = "manager_approved")  String strManagerApproved,
                                                            @RequestParam(defaultValue = "",  name = "status")            String strStatus,
                                                            @RequestParam(defaultValue = "0", name = "index")             int    pageIndex,
                                                            @RequestParam(defaultValue = "10",name = "number")            int    sizePerPage
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];

        List<CommonStatus> statuses = getCommonStatus(strStatus);
        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        YesNoEnum needVisitRecord = YesNoEnum.parseString(strNeedVisitRecord);
        YesNoEnum managerApproved = YesNoEnum.parseString(strManagerApproved);
        List<ServiceItemBean> serviceItems = vendorCategoryAndItemService.getItemByCategoryId(
                ServiceVendorType.HOSPITAL, hospitalId, departmentId,
                categoryId, needVisitRecord, managerApproved, statuses,
                pageIndex, sizePerPage
        );
        return serviceItems;
    }


    //=======================================================================================
    //                                   Adding
    //=======================================================================================


    //=============================================
    //               Category Service
    //=============================================
    @RequestMapping(path = "/manager/category", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ServiceCategoryBean addServiceCategory(HttpServletRequest request,
                                                  @RequestParam(defaultValue = "", name = "name")        String name,
                                                  @RequestParam(defaultValue = "", name = "description") String description,
                                                  @RequestParam(defaultValue = "0",name = "grade")       int    grade,
                                                  @RequestParam(defaultValue = "0",name = "parent_id")   long   parentId
    ) {
        ServiceCategoryBean category = vendorCategoryAndItemService.addCategory(name, description, grade, parentId);
        return category;
    }


    //=============================================
    //               Item Service
    //=============================================
    @RequestMapping(path = "/manager/item", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ServiceItemBean addServiceItem(HttpServletRequest request,
                                          @RequestParam(defaultValue = "", name = "name")             String name,
                                          @RequestParam(defaultValue = "", name = "clazz")            String clazz,
                                          @RequestParam(defaultValue = "", name = "description")      String description,
                                          @RequestParam(defaultValue = "", name = "price")            String price,
                                          @RequestParam(defaultValue = "", name = "discount")         String discount,
                                          @RequestParam(defaultValue = "", name = "server_income")    String serverIncome,
                                          @RequestParam(defaultValue = "", name = "need_visit_record")String strNeedVisitRecord,
                                          @RequestParam(defaultValue = "0",name = "time_duration")    int    timeDuration,
                                          @RequestParam(defaultValue = "", name = "time_unit")        String timeUnit,
                                          @RequestParam(defaultValue = "0",name = "grade")            int    grade,
                                          @RequestParam(defaultValue = "0",name = "category_id")      long   categoryId,
                                          @RequestParam(defaultValue = "", name = "managed_by")       String strManagedBy,
                                          @RequestParam(defaultValue = "", name = "need_symptoms")    String needSymptoms,
                                          @RequestParam(defaultValue = "", name = "symptoms_items")   String symptomsItems,
                                          @RequestParam(defaultValue = "", name = "questionnaire_id") String strQuestionnaireId,
                                          @RequestParam(defaultValue = "", name = "need_symptoms_detail") String needSymptomsDetail
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];

        Long questionnaireId = !VerifyUtil.isIds(strQuestionnaireId) ? null : VerifyUtil.parseLongIds(strQuestionnaireId).get(0);
        ServiceItemBean serviceItem = vendorCategoryAndItemService.addItem(
                name, clazz, description,
                price, discount, serverIncome, YesNoEnum.parseString(strNeedVisitRecord),
                timeDuration, timeUnit, grade, categoryId,
                ServiceVendorType.HOSPITAL.name(), hospitalId, departmentId,
                ManagedBy.parseString(strManagedBy),
                YesNoEnum.parseString(needSymptoms),
                symptomsItems, questionnaireId, YesNoEnum.parseString(needSymptomsDetail)
        );
        return serviceItem;
    }


    //=======================================================================================
    //                                   Editing
    //=======================================================================================


    //=============================================
    //               Category Service
    //=============================================
    @RequestMapping(path = "/manager/category/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceCategoryBean editServiceCategory(HttpServletRequest request,
                                                   @RequestParam(defaultValue = "", name = "category_id") long   categoryId,
                                                   @RequestParam(defaultValue = "", name = "name")        String name,
                                                   @RequestParam(defaultValue = "", name = "description") String description,
                                                   @RequestParam(defaultValue = "0",name = "grade")       int    grade,
                                                   @RequestParam(defaultValue = "0",name = "parent_id")   long   parentId,
                                                   @RequestParam(defaultValue = "", name = "status")      String status
    ) {
        ServiceCategoryBean category = vendorCategoryAndItemService.updateCategory(categoryId, name, description, grade, parentId, status);
        return category;
    }

    @RequestMapping(path = "/manager/category/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public ServiceCategoryBean editServiceCategoryImage(HttpServletRequest request,
                                                        @RequestParam(defaultValue = "0",name = "category_id")long          categoryId,
                                                        @RequestParam(defaultValue = "", name = "image_name") String        imageName,
                                                        @RequestPart(required = true,    name = "image")      MultipartFile image
    ) throws IOException {
        ServiceCategoryBean category = vendorCategoryAndItemService.updateCategoryImage(categoryId, imageName, image.getInputStream());
        return category;
    }


    //=============================================
    //               Item Service
    //=============================================
    @RequestMapping(path = "/manager/item/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceItemBean editServiceItem(HttpServletRequest request,
                                           @RequestParam(defaultValue = "0",name = "item_id")           long   itemId,
                                           @RequestParam(defaultValue = "", name = "name")              String name,
                                           @RequestParam(defaultValue = "", name = "clazz")             String clazz,
                                           @RequestParam(defaultValue = "", name = "description")       String description,
                                           @RequestParam(defaultValue = "", name = "price")             String price,
                                           @RequestParam(defaultValue = "", name = "discount")          String discount,
                                           @RequestParam(defaultValue = "", name = "server_income")     String serverIncome,
                                           @RequestParam(defaultValue = "", name = "need_visit_record") String strNeedVisitRecord,
                                           @RequestParam(defaultValue = "", name = "time_duration")     String strTimeDuration,
                                           @RequestParam(defaultValue = "", name = "time_unit")         String timeUnit,
                                           @RequestParam(defaultValue = "", name = "grade")             String strGrade,
                                           @RequestParam(defaultValue = "", name = "category_id")       String strCategoryId,
                                           @RequestParam(defaultValue = "", name = "status")            String status,
                                           @RequestParam(defaultValue = "", name = "managed_by")        String strManagedBy,
                                           @RequestParam(defaultValue = "", name = "need_symptoms")     String needSymptoms,
                                           @RequestParam(defaultValue = "", name = "symptoms_items")    String symptomsItems,
                                           @RequestParam(defaultValue = "", name = "questionnaire_id")  String strQuestionnaireId,
                                           @RequestParam(defaultValue = "", name = "need_symptoms_detail")String needSymptomsDetail
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];

        Integer timeDuration = !VerifyUtil.isIds(strTimeDuration) ? null : VerifyUtil.parseIntIds(strTimeDuration).get(0);
        Integer grade = !VerifyUtil.isIds(strGrade) ? null : VerifyUtil.parseIntIds(strGrade).get(0);
        Long categoryId = !VerifyUtil.isIds(strCategoryId) ? null : VerifyUtil.parseLongIds(strCategoryId).get(0);
        Long questionnaireId = !VerifyUtil.isIds(strQuestionnaireId) ? null : VerifyUtil.parseLongIds(strQuestionnaireId).get(0);
        ManagedBy managedBy = ManagedBy.parseString(strManagedBy);

        ServiceItemBean serviceItem = vendorCategoryAndItemService.updateItem(
                itemId, name, clazz, description,
                price, discount, serverIncome, YesNoEnum.parseString(strNeedVisitRecord),
                timeDuration, timeUnit, grade, categoryId,
                ServiceVendorType.HOSPITAL.name(), hospitalId, departmentId, managedBy, status,
                YesNoEnum.parseString(needSymptoms), symptomsItems, questionnaireId, YesNoEnum.parseString(needSymptomsDetail));
        return serviceItem;
    }

    @RequestMapping(path = "/manager/item/approve", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceItemBean approveServiceItem(HttpServletRequest request,
                                              @RequestParam(defaultValue = "0", name = "item_id") long itemId
    ) {
        ServiceItemBean serviceItem = vendorCategoryAndItemService.updateItemManagerApproved(itemId, YesNoEnum.YES);
        return serviceItem;
    }

    @RequestMapping(path = "/manager/item/disapprove", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public ServiceItemBean disapproveServiceItem(HttpServletRequest request,
                                                 @RequestParam(defaultValue = "0", name = "item_id") long itemId
    ) {
        ServiceItemBean serviceItem = vendorCategoryAndItemService.updateItemManagerApproved(itemId, YesNoEnum.NO);
        return serviceItem;
    }

    @RequestMapping(path = "/manager/item/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public ServiceItemBean editServiceItemImage(HttpServletRequest request,
                                                @RequestParam(defaultValue = "0",name = "item_id")    long          itemId,
                                                @RequestParam(defaultValue = "", name = "image_name") String        imageName,
                                                @RequestPart(required = true,    name = "image")      MultipartFile image
    ) throws IOException {
        ServiceItemBean serviceItem = vendorCategoryAndItemService.updateItemImage(itemId, imageName, image.getInputStream());
        return serviceItem;
    }

    @RequestMapping(path = "/manager/item/detail/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public ServiceItemBean editServiceItemDetailImage(HttpServletRequest request,
                                                      @RequestParam(defaultValue = "0",name = "item_id")    long          itemId,
                                                      @RequestParam(defaultValue = "", name = "image_name") String        imageName,
                                                      @RequestPart(required = true,    name = "image")      MultipartFile image
    ) throws IOException {
        ServiceItemBean serviceItem = vendorCategoryAndItemService.updateItemDetailImage(itemId, imageName, image.getInputStream());
        return serviceItem;
    }
}

package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceVendorBean;
import com.cooltoo.go2nurse.constants.ServiceClass;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.TimeUnit;
import com.cooltoo.go2nurse.service.ServiceVendorCategoryAndItemService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hp on 2016/9/8.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/service_category_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/service_vendor_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/service_item_data.xml")
})
public class ServiceVendorCategoryAndItemServiceTest extends AbstractCooltooTest {

    @Autowired private ServiceVendorCategoryAndItemService vendorCategoryAndItemService;

    @Test
    public void countVendor() {
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countVendor(statuses);
        Assert.assertEquals(21, count);

        statuses.remove(CommonStatus.DISABLED);
        count = vendorCategoryAndItemService.countVendor(statuses);
        Assert.assertEquals(11, count);

        statuses.remove(CommonStatus.DELETED);
        count = vendorCategoryAndItemService.countVendor(statuses);
        Assert.assertEquals(10, count);
    }

    @Test
    public void getVendor() {
        List<CommonStatus> statuses = CommonStatus.getAll();
        List<ServiceVendorBean> vendors = vendorCategoryAndItemService.getVendor(statuses, 0, 10);
        Assert.assertEquals(10, vendors.size());
        Assert.assertEquals(CommonStatus.DELETED, vendors.get(0).getStatus());
    }

    @Test
    public void countTopCategory() {
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countTopCategory(statuses);
        Assert.assertEquals(21, count);

        statuses.remove(CommonStatus.DISABLED);
        count = vendorCategoryAndItemService.countTopCategory(statuses);
        Assert.assertEquals(21, count);

        statuses.remove(CommonStatus.DELETED);
        count = vendorCategoryAndItemService.countTopCategory(statuses);
        Assert.assertEquals(21, count);
    }

    @Test
    public void getTopCategory() {
        List<CommonStatus> statuses = CommonStatus.getAll();
        List<ServiceCategoryBean> categories = vendorCategoryAndItemService.getTopCategory(statuses, 0, 10);
        Assert.assertEquals(10, categories.size());
        Assert.assertEquals(200, categories.get(0).getId());
    }

    @Test
    public void countCategoryByParentId() {
        long parentId = 100L;
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countCategoryByParentId(parentId, statuses);
        Assert.assertEquals(4, count);
    }

    @Test
    public void getCategoryByParentId() {
        long parentId = 100L;
        List<CommonStatus> statuses = CommonStatus.getAll();
        List<ServiceCategoryBean> subCategory = vendorCategoryAndItemService.getCategoryByParentId(parentId, statuses);
        Assert.assertEquals(4, subCategory.size());
        for (ServiceCategoryBean tmp : subCategory) {
            Assert.assertEquals(parentId, tmp.getParentId());
        }

        subCategory = vendorCategoryAndItemService.getCategoryByParentId(parentId, statuses, 0, 2);
        Assert.assertEquals(2, subCategory.size());
        for (ServiceCategoryBean tmp : subCategory) {
            Assert.assertEquals(parentId, tmp.getParentId());
        }
    }

    @Test
    public void getCategoryAndParentById() {
        long categoryId = 101L;
        List<ServiceCategoryBean> categoryAndParent = vendorCategoryAndItemService.getCategoryAndParentById(categoryId);
        Assert.assertEquals(2, categoryAndParent.size());
        Assert.assertEquals(categoryAndParent.get(0).getParentId(), categoryAndParent.get(1).getId());
    }

    @Test
    public void existItem() {
        long itemId = 70;
        boolean exist = vendorCategoryAndItemService.existItem(itemId);
        Assert.assertTrue(exist);

        itemId = 100;
        exist = vendorCategoryAndItemService.existItem(itemId);
        Assert.assertTrue(false == exist);
    }

    @Test
    public void countItemByCategoryId() {
        long categoryId = 101;
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countItemByCategoryId(null, null, null, categoryId, null, null, statuses);
        Assert.assertEquals(1, count);
    }

    @Test
    public void getItemByCategoryId() {
        long categoryId = 101;
        List<CommonStatus> statuses = CommonStatus.getAll();
        List<ServiceItemBean> items = vendorCategoryAndItemService.getItemByCategoryId(null, null, null, categoryId, null, null, statuses);
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(categoryId, items.get(0).getCategoryId());
    }

    @Test
    public void countItemByVendorId() {
        long vendorId = 30;
        ServiceVendorType vendorType = ServiceVendorType.parseString("company");
        List<CommonStatus> statuses = CommonStatus.getAll();
        long items = vendorCategoryAndItemService.countItemByCategoryId(vendorType, vendorId, null, null, null, null, statuses);
        Assert.assertEquals(4, items);
    }

    @Test
    public void getItemByVendorId() {
        long vendorId = 30;
        ServiceVendorType vendorType = ServiceVendorType.parseString("company");
        List<CommonStatus> statuses = CommonStatus.getAll();
        List<ServiceItemBean> items = vendorCategoryAndItemService.getItemByCategoryId(vendorType, vendorId, null, null, null, null, statuses);
        Assert.assertEquals(4, items.size());
        for (ServiceItemBean tmp : items) {
            Assert.assertEquals(vendorId, tmp.getVendorId());
        }
    }

    @Test
    public void getItemById() {
        long itemId = 30;
        ServiceItemBean items = vendorCategoryAndItemService.getItemById(itemId);
        Assert.assertEquals(itemId, items.getId());
    }

    @Test
    public void countItemByCategoryVendorAndStatus() {
        long categoryId = 124;
        long vendorId = 34;
        ServiceVendorType vendorType = ServiceVendorType.COMPANY;
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countItemByCategoryId(vendorType, vendorId, null, categoryId, null, null, statuses);
        Assert.assertEquals(1, count);
    }

    @Test
    public void getItemByCategoryVendorAndStatus() {
        long categoryId = 124;
        long vendorId = 34;
        ServiceVendorType vendorType = ServiceVendorType.COMPANY;
        List<CommonStatus> statuses = CommonStatus.getAll();
        List<ServiceItemBean> items = vendorCategoryAndItemService.getItemByCategoryId(vendorType, vendorId, null, categoryId, null, null, statuses, 0, 10);
        Assert.assertEquals(1, items.size());
        Assert.assertEquals(29, items.get(0).getId());
    }

    @Test
    public void getItemByIdIn() {
        List<Long> itemIds = Arrays.asList(new Long[]{10L, 11L});
        List<ServiceItemBean> items = vendorCategoryAndItemService.getItemByIdIn(itemIds);
        Assert.assertEquals(itemIds.get(1).longValue(), items.get(0).getId());
        Assert.assertEquals(itemIds.get(0).longValue(), items.get(1).getId());
    }

    @Test
    public void deleteVendorByIds() {
        List<Long> vendorIds = Arrays.asList(new Long[]{30L, 31L});
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countVendor(statuses);
        vendorCategoryAndItemService.deleteVendorByIds(vendorIds);
        long afterDeleted = vendorCategoryAndItemService.countVendor(statuses);
        Assert.assertEquals(count-2, afterDeleted);
    }

    @Test
    public void deleteCategoryByIds() {
        long parentId = 130;
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countCategoryByParentId(parentId, statuses);

        List<Long> categoryIds = Arrays.asList(new Long[]{131L, 132L});
        vendorCategoryAndItemService.deleteCategoryByIds(categoryIds);

        long afterDeleted = vendorCategoryAndItemService.countCategoryByParentId(parentId, statuses);
        Assert.assertEquals(count-2, afterDeleted);
    }

    @Test
    public void deleteItemByCategoryId() {
        long categoryId = 131;
        List<CommonStatus> statuses = CommonStatus.getAll();
        long count = vendorCategoryAndItemService.countItemByCategoryId(null, null, null, categoryId, null, null, statuses);
        Assert.assertTrue(0<count);

        vendorCategoryAndItemService.deleteItemByCategoryId(categoryId);

        long afterDeleted = vendorCategoryAndItemService.countItemByCategoryId(null, null, null, categoryId, null, null, statuses);
        Assert.assertEquals(0, afterDeleted);
    }

    @Test
    public void deleteItemByIds() {
        List<Long> itemIds = Arrays.asList(new Long[]{20L, 21L});
        List<ServiceItemBean> items = vendorCategoryAndItemService.getItemByIdIn(itemIds);
        Assert.assertEquals(2, items.size());

        vendorCategoryAndItemService.deleteItemByIds(itemIds);

        items = vendorCategoryAndItemService.getItemByIdIn(itemIds);
        Assert.assertEquals(0, items.size());
    }

    @Test
    public void updateVendor() {
        long vendorId = 30;
        String name = "vendor=001";
        String description = "vendor=001=description";
        CommonStatus status = CommonStatus.DELETED;
        ServiceVendorBean vendor = vendorCategoryAndItemService.updateVendor(vendorId, name, description, status.name());
        Assert.assertEquals(vendorId, vendor.getId());
        Assert.assertEquals(name, vendor.getName());
        Assert.assertEquals(description, vendor.getDescription());
        Assert.assertEquals(status, vendor.getStatus());
    }

    @Test
    public void updateCategory() {
        long categoryId = 101;
        String name = "category=001";
        String description = "category=001=description";
        int grade = 1;
        long parentId = 105;
        CommonStatus status = CommonStatus.DELETED;
        ServiceCategoryBean category = vendorCategoryAndItemService.updateCategory(categoryId, name, description, grade, parentId, status.name());
        Assert.assertEquals(categoryId, category.getId());
        Assert.assertEquals(name, category.getName());
        Assert.assertEquals(description, category.getDescription());
        Assert.assertEquals(grade, category.getGrade());
        Assert.assertEquals(parentId, category.getParentId());
        Assert.assertEquals(status, category.getStatus());
    }

    @Test
    public void updateItem() {
        long itemId = 10;
        String name = "item=001";
        ServiceClass clazz = ServiceClass.STANDARD;
        String description = "item=001=description";
        int price = 1000;
        int discount = 300;
        int serverIncome = 700;
        YesNoEnum needVisitPatientRecord = YesNoEnum.YES;
        int timeDuration = 4;
        TimeUnit timeUnit = TimeUnit.HOUR;
        int grade = 1;
        long categoryId = 130;
        long vendorId = 30;
        ServiceVendorType vendorType = ServiceVendorType.COMPANY;
        CommonStatus status = CommonStatus.DELETED;
        ServiceItemBean item = vendorCategoryAndItemService.updateItem(
                itemId, name, clazz.name(), description,
                price+"", discount+"", serverIncome+"", needVisitPatientRecord,
                timeDuration, timeUnit.name(), grade,
                categoryId, vendorId, vendorType.name(), 0L,
                status.name());
        Assert.assertEquals(itemId, item.getId());
        Assert.assertEquals(name, item.getName());
        Assert.assertEquals(clazz, item.getClazz());
        Assert.assertEquals(description, item.getDescription());
        Assert.assertEquals(price*100, item.getServicePriceCent());
        Assert.assertEquals(timeDuration, item.getServiceTimeDuration());
        Assert.assertEquals(timeUnit, item.getServiceTimeUnit());
        Assert.assertEquals(grade, item.getGrade());
        Assert.assertEquals(categoryId, item.getCategoryId());
        Assert.assertEquals(vendorId, item.getVendorId());
        Assert.assertEquals(vendorType, item.getVendorType());
        Assert.assertEquals(status, item.getStatus());
        Assert.assertEquals(needVisitPatientRecord, item.getNeedVisitPatientRecord());
        Assert.assertEquals(discount*100, item.getServiceDiscountCent());
        Assert.assertEquals(serverIncome*100, item.getServerIncomeCent());
        Assert.assertEquals(YesNoEnum.YES, item.getManagerApproved());
    }

    @Test
    public void addVendor() {
        String name = "vendor=001";
        String description = "vendor=001=description";
        ServiceVendorBean vendor = vendorCategoryAndItemService.addVendor(name, description);
        Assert.assertTrue(vendor.getId()>0);
        Assert.assertEquals(name, vendor.getName());
        Assert.assertEquals(description, vendor.getDescription());
    }

    @Test
    public void addCategory() {
        String name = "category=001";
        String description = "category=001=description";
        int grade = 1;
        long parentId = 105;
        ServiceCategoryBean category = vendorCategoryAndItemService.addCategory(name, description, grade, parentId);
        Assert.assertTrue(category.getId()>0);
        Assert.assertEquals(name, category.getName());
        Assert.assertEquals(description, category.getDescription());
        Assert.assertEquals(grade, category.getGrade());
        Assert.assertEquals(parentId, category.getParentId());
    }

    @Test
    public void addItem() {
        String name = "item=001";
        ServiceClass clazz = ServiceClass.STANDARD;
        String description = "item=001=description";
        int price = 1000;
        int discount = 300;
        int serverIncome = 800;
        YesNoEnum needVisitPatientRecord = YesNoEnum.YES;
        int timeDuration = 4;
        TimeUnit timeUnit = TimeUnit.HOUR;
        int grade = 1;
        long categoryId = 130;
        long vendorId = 30;
        ServiceVendorType vendorType = ServiceVendorType.COMPANY;
        ServiceItemBean item = vendorCategoryAndItemService.addItem(
                name, clazz.name(), description,
                price+"", discount+"", serverIncome+"", needVisitPatientRecord,
                timeDuration, timeUnit.name(), grade,
                categoryId, vendorId, vendorType.name(), 0);
        Assert.assertTrue(item.getId()>0);
        Assert.assertEquals(name, item.getName());
        Assert.assertEquals(clazz, item.getClazz());
        Assert.assertEquals(description, item.getDescription());
        Assert.assertEquals(price*100, item.getServicePriceCent());
        Assert.assertEquals(timeDuration, item.getServiceTimeDuration());
        Assert.assertEquals(timeUnit, item.getServiceTimeUnit());
        Assert.assertEquals(grade, item.getGrade());
        Assert.assertEquals(categoryId, item.getCategoryId());
        Assert.assertEquals(vendorId, item.getVendorId());
        Assert.assertEquals(vendorType, item.getVendorType());
        Assert.assertEquals(needVisitPatientRecord, item.getNeedVisitPatientRecord());
        Assert.assertEquals(discount*100, item.getServiceDiscountCent());
        Assert.assertEquals(serverIncome*100, item.getServerIncomeCent());
        Assert.assertEquals(YesNoEnum.NO, item.getManagerApproved());
    }
}
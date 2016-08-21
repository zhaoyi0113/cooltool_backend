package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/8/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
/*@Ignore*/
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
public class DepartmentDuplicatedCreator {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentDuplicatedCreator.class);

    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private HospitalRepository hospitalRepository;


    @Test
    public void createNewDepartment() {
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.ASC, "parentId"),
                new Sort.Order(Sort.Direction.ASC, "id")
        );
        List<HospitalDepartmentEntity> allDepartmentInTable = departmentRepository.findByHospitalId(0, sort);
        int newDepartmentId = 0;
        int departmentCount=0;
        for (HospitalDepartmentEntity tmp : allDepartmentInTable) {
            if (tmp.getId()>newDepartmentId) {
                newDepartmentId = tmp.getId();
            }
            departmentCount++;
        }
        newDepartmentId += 300;

        int hospitalCount = 0;
        Iterable<HospitalEntity> allHospitalInTable = hospitalRepository.findAll();
        List<HospitalDepartmentEntity> newDepartments = new ArrayList<>();
        for(HospitalEntity tmpHospital : allHospitalInTable) {
            hospitalCount ++;
            Map<Integer, Integer> oldId2NewId = new HashMap<>();
            for (HospitalDepartmentEntity tmpDepartment : allDepartmentInTable) {
                newDepartmentId++;
                HospitalDepartmentEntity newOne = cloneDepartment(newDepartmentId, tmpHospital.getId(), tmpDepartment, oldId2NewId.get(tmpDepartment.getParentId()));
                oldId2NewId.put(tmpDepartment.getId(), newOne.getId());
                newDepartments.add(newOne);
            }
        }
        logger.info("new count is {}, expected is {}", newDepartments.size(), hospitalCount*departmentCount);

        String insertSql = insertSQL(newDepartments);
        boolean writeSuccess = writeTempFile(insertSql);
        logger.info("save department_duplicated_sql file success={}", writeSuccess);
    }

    private HospitalDepartmentEntity cloneDepartment(int newDepartmentId, int hospitalId, HospitalDepartmentEntity entity, Integer newParentId) {
        HospitalDepartmentEntity newEntity = new HospitalDepartmentEntity();
        newEntity.setId(newDepartmentId);
        newEntity.setName(entity.getName());
        newEntity.setHospitalId(hospitalId);
        newEntity.setDescription(entity.getDescription());
        newEntity.setImageId(entity.getImageId());
        newEntity.setDisableImageId(entity.getDisableImageId());
        newEntity.setEnable(entity.getEnable());
        newEntity.setParentId((null==newParentId) ? -1 : newParentId);
        String uniqueId = (newDepartmentId<10) ? ("00000"+newDepartmentId) :
                                (newDepartmentId<100) ? ("0000"+newDepartmentId) :
                                        (newDepartmentId<1000) ? ("000"+newDepartmentId) :
                                                (newDepartmentId<10000) ? ("00"+newDepartmentId) :
                                                        (newDepartmentId<100000) ? ("0"+newDepartmentId) :
                                                                (newDepartmentId<1000000) ? (""+newDepartmentId) : "000000";
        newEntity.setUniqueId(uniqueId);

        return newEntity;
    }

    private String insertSQL(List<HospitalDepartmentEntity> departments) {
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT INTO `cooltoo_hospital_department`(`id`,`hospital_id`,`name`,`description`,`enable`,`image_id`,`disable_image_id`,`parent_id`,`unique_id`) VALUES").append("\r\n");
        for (HospitalDepartmentEntity tmp : departments) {
            insertSql.append("    (");
            insertSql.append("'").append(tmp.getId()).append("',");
            insertSql.append("'").append(tmp.getHospitalId()).append("',");
            insertSql.append("'").append(tmp.getName()).append("',");
            insertSql.append("'").append(null==tmp.getDescription() ? "" : tmp.getDescription()).append("',");
            insertSql.append("'").append(tmp.getEnable()).append("',");
            insertSql.append("'").append(tmp.getImageId()).append("',");
            insertSql.append("'").append(tmp.getDisableImageId()).append("',");
            insertSql.append("'").append(tmp.getParentId()).append("',");
            insertSql.append("'").append(tmp.getUniqueId()).append("'),\r\n");
        }
        int index = insertSql.lastIndexOf(",");
        insertSql.deleteCharAt(index);
        insertSql.append(";");
        return insertSql.toString();
    }

    private boolean writeTempFile(String content) {
        try {
            File file = File.createTempFile("department_duplicate_", ".sql");
            File parent = file.getParentFile();
            File dest = new File(parent, "department_dup.sql");
            FileOutputStream out = new FileOutputStream(file);
            out.write(content.getBytes("UTF-8"));
            System.out.println(file.renameTo(dest));
            logger.info("department_duplicate file is {}", file.getAbsolutePath());
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}

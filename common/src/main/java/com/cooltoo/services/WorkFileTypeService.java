package com.cooltoo.services;

import com.cooltoo.beans.WorkFileTypeBean;
import com.cooltoo.converter.WorkFileTypeBeanConverter;
import com.cooltoo.entities.WorkFileTypeEntity;
import com.cooltoo.repository.WorkFileTypeRepository;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zhaolisong on 16/3/29.
 */
@Service("WorkFileTypeService")
public class WorkFileTypeService {

    @Autowired
    private WorkFileTypeRepository workFileTypeRepository;

    @Autowired
    private WorkFileTypeBeanConverter beanConverter;

    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;


    public List<WorkFileTypeBean> getAllWorkFileType() {
        WorkFileTypeBean       bean    = null;
        List<Long>          imageIds = new ArrayList<Long>();
        List<WorkFileTypeBean> beans   = new ArrayList<WorkFileTypeBean>();

        Iterable<WorkFileTypeEntity> workfileTypes = workFileTypeRepository.findAll();
        for (WorkFileTypeEntity workfileType : workfileTypes) {
            bean = beanConverter.convert(workfileType);
            if (bean.getImageId()>0) {
                imageIds.add(bean.getImageId());
            }
            if (bean.getDisableImageId()>0) {
                imageIds.add(bean.getDisableImageId());
            }
            beans.add(bean);
        }

        Map<Long, String> idToPath = officialStorage.getFilePath(imageIds);

        for (WorkFileTypeBean tmp : beans) {
            if (tmp.getImageId()>0) {
                tmp.setImageUrl(idToPath.get(tmp.getImageId()));
            }
            if (tmp.getDisableImageId()>0) {
                tmp.setDisableImageUrl(idToPath.get(tmp.getDisableImageId()));
            }
        }
        return beans;
    }

    public WorkFileTypeBean getWorkFileTypeByType(WorkFileType workfileType) {
        List<WorkFileTypeBean> all = getAllWorkFileType();
        for (WorkFileTypeBean one : all) {
            if (one.getType().equals(workfileType)) {
                return one;
            }
        }
        return null;
    }

    public WorkFileTypeBean updateWorkfileType(int id, String name, int factor, int maxFileCount, int minFileCount, InputStream image, InputStream disableImage) {
        WorkFileTypeEntity workfileType = workFileTypeRepository.findOne(id);
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (minFileCount>0 || maxFileCount>0) {
            int oldMax = workfileType.getMaxFileCount();
            int oldMin = workfileType.getMinFileCount();
            int newMax = (maxFileCount > 0) ? maxFileCount : oldMax;
            int newMin = (minFileCount > 0) ? minFileCount : oldMin;
            if (newMin <= newMax) {
                workfileType.setMaxFileCount(newMax);
                workfileType.setMinFileCount(newMin);
                changed = true;
            }
        }
        if (!VerifyUtil.isStringEmpty(name)) {
            workfileType.setName(name);
            changed=true;
        }
        if (factor>0) {
            workfileType.setFactor(factor);
            changed = true;
        }
        String imagePath = null;
        if (null!=image) {
            long fileId = officialStorage.addFile(workfileType.getImageId(), workfileType.getName(), image);
            workfileType.setImageId(fileId);
            imagePath = officialStorage.getFilePath(fileId);
            changed = true;
        }
        String disableImagePath = null;
        if (null!=disableImage) {
            long fileId = officialStorage.addFile(workfileType.getDisableImageId(), workfileType.getName(), disableImage);
            workfileType.setDisableImageId(fileId);
            disableImagePath = officialStorage.getFilePath(fileId);
            changed=true;
        }

        if (changed) {
            workfileType = workFileTypeRepository.save(workfileType);
        }

        WorkFileTypeBean bean = beanConverter.convert(workfileType);
        bean.setImageUrl(imagePath);
        bean.setDisableImageUrl(disableImagePath);
        return bean;
    }
}

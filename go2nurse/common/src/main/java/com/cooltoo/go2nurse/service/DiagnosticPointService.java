package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.DiagnosticPointBean;
import com.cooltoo.go2nurse.converter.DiagnosticPointBeanConverter;
import com.cooltoo.go2nurse.entities.DiagnosticPointEntity;
import com.cooltoo.go2nurse.repository.DiagnosticPointRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yzzhao on 6/10/16.
 */
@Service("DiagnosticPointService")
public class DiagnosticPointService {

    @Autowired
    private DiagnosticPointRepository diagnosticPointRepository;

    @Autowired
    private UserGo2NurseFileStorageService userStorage;

    @Autowired
    private DiagnosticPointBeanConverter beanConverter;

    @Transactional
    public DiagnosticPointBean createDiagnosticPoint(String name, InputStream inputStream) {
        DiagnosticPointEntity entity = new DiagnosticPointEntity();
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTimeCreated(Calendar.getInstance().getTime());
        entity.setName(name);
        long id = userStorage.addNewFile(name, inputStream);
        entity.setImageId(id);
        DiagnosticPointEntity saved = diagnosticPointRepository.save(entity);
        return beanConverter.convert(saved);
    }

    public DiagnosticPointBean getDiagnosticPoint(long id){
        DiagnosticPointEntity entity = diagnosticPointRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        DiagnosticPointBean bean = beanConverter.convert(entity);
        saveImageUrl(bean);
        return bean;
    }

    @Transactional
    public DiagnosticPointBean editDiagnosticPointName(long id, String name) {
        DiagnosticPointEntity entity = diagnosticPointRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (name != null) {
            entity.setName(name);
            DiagnosticPointEntity saved = diagnosticPointRepository.save(entity);
            return beanConverter.convert(saved);
        }
        throw new BadRequestException(ErrorCode.UNKNOWN);
    }

    @Transactional
    public DiagnosticPointBean editDiagnosticPointStatus(long id, String s){
        DiagnosticPointEntity entity = diagnosticPointRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (s != null) {
            CommonStatus status = CommonStatus.valueOf(s);
            entity.setStatus(status);
            DiagnosticPointEntity saved = diagnosticPointRepository.save(entity);
            return beanConverter.convert(saved);
        }
        throw new BadRequestException(ErrorCode.UNKNOWN);
    }

    @Transactional
    public DiagnosticPointBean editDiagnosticImage(long id, InputStream inputStream) {
        DiagnosticPointEntity entity = diagnosticPointRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        long imageId = userStorage.addNewFile(entity.getName(), inputStream);
        long oldId = entity.getImageId();
        entity.setImageId(imageId);
        DiagnosticPointEntity saved = diagnosticPointRepository.save(entity);
        userStorage.deleteFile(oldId);
        return beanConverter.convert(saved);
    }

    public List<DiagnosticPointBean> getAllDiagnosticPoints(){
        Iterable<DiagnosticPointEntity> all = diagnosticPointRepository.findAll();
        List<DiagnosticPointBean> allBeans = new ArrayList<>();
        for(DiagnosticPointEntity entity: all){
            allBeans.add(beanConverter.convert(entity));
        }
        saveImageUrls(allBeans);
        return allBeans;
    }

    private void saveImageUrls(List<DiagnosticPointBean> beans){
        beans.forEach(bean -> saveImageUrl(bean));
    }

    private void saveImageUrl(DiagnosticPointBean bean){
        String filePath = userStorage.getFilePath(bean.getImageId());
        bean.setImageUrl(filePath);
    }
}

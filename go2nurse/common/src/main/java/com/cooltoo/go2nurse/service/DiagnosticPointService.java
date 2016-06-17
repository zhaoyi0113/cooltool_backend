package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.DiagnosticEnumerationBean;
import com.cooltoo.go2nurse.beans.DiagnosticPointBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.DiagnosticEnumerationBeanConverter;
import com.cooltoo.go2nurse.converter.DiagnosticPointBeanConverter;
import com.cooltoo.go2nurse.entities.DiagnosticPointEntity;
import com.cooltoo.go2nurse.repository.DiagnosticPointRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticPointService.class);

    @Autowired private DiagnosticPointRepository diagnosticPointRepository;
    @Autowired private UserGo2NurseFileStorageService userStorage;
    @Autowired private DiagnosticPointBeanConverter beanConverter;
    @Autowired private DiagnosticEnumerationBeanConverter enumerationBeanConverter;

    //========================================================================
    //            use enumeration for first time
    //========================================================================
    public boolean exitsDiagnostic(long diagnosticId) {
        boolean exist = DiagnosticEnumeration.exists((int)diagnosticId);
        logger.info("exist diagnostic={}, exist={}", diagnosticId, exist);
        return exist;
    }

    public List<DiagnosticEnumerationBean> getAllDiagnostic() {
        logger.info("get all diagnostic");
        List<DiagnosticEnumerationBean> beans = new ArrayList<>();
        List<DiagnosticEnumeration> diagnostics = DiagnosticEnumeration.getAllDiagnostic();
        for (DiagnosticEnumeration enume : diagnostics) {
            DiagnosticEnumerationBean bean = enumerationBeanConverter.convert(enume);
            beans.add(bean);
        }
        logger.info("diagnostic is {}", beans);
        return beans;
    }

    public List<DiagnosticEnumerationBean> getDiagnosticByIds(List<Long> diagnosticIds) {
        logger.info("get diagnostic by diagnostic ids ={} ", diagnosticIds);
        List<DiagnosticEnumerationBean> beans = new ArrayList<>();
        List<DiagnosticEnumeration> diagnostics = DiagnosticEnumeration.getDiagnosticByTypes(diagnosticIds);
        for (DiagnosticEnumeration de : diagnostics) {
            DiagnosticEnumerationBean bean = enumerationBeanConverter.convert(de);
            beans.add(bean);
        }
        logger.info("diagnostic is {}", beans);
        return beans;
    }

    //========================================================================
    //            use table for first time
    //========================================================================

    @Transactional
    public DiagnosticPointBean createDiagnosticPoint(String name, InputStream inputStream) {
        DiagnosticPointEntity entity = new DiagnosticPointEntity();
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTimeCreated(Calendar.getInstance().getTime());
        entity.setName(name);
        long id = userStorage.addNewFile(name, inputStream);
        entity.setImageId(id);
        entity.setDorder((int)diagnosticPointRepository.count()+1);
        DiagnosticPointEntity saved = diagnosticPointRepository.save(entity);
        return beanConverter.convert(saved);
    }

    public boolean exists(long diagnosticId) {
        return diagnosticPointRepository.exists(diagnosticId);
    }

    public DiagnosticPointBean getDiagnosticPoint(long id){
        DiagnosticPointEntity entity = getDiagnosticPointEntity(id);
        DiagnosticPointBean bean = beanConverter.convert(entity);
        saveImageUrl(bean);
        return bean;
    }

    @Transactional
    public DiagnosticPointBean editDiagnosticPointName(long id, String name) {
        DiagnosticPointEntity entity = getDiagnosticPointEntity(id);
        if (name != null) {
            entity.setName(name);
            DiagnosticPointEntity saved = diagnosticPointRepository.save(entity);
            return beanConverter.convert(saved);
        }
        throw new BadRequestException(ErrorCode.UNKNOWN);
    }

    @Transactional
    public DiagnosticPointBean editDiagnosticPointStatus(long id, String s){
        DiagnosticPointEntity entity = getDiagnosticPointEntity(id);
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
        DiagnosticPointEntity entity = getDiagnosticPointEntity(id);
        long imageId = userStorage.addNewFile(entity.getName(), inputStream);
        long oldId = entity.getImageId();
        entity.setImageId(imageId);
        DiagnosticPointEntity saved = diagnosticPointRepository.save(entity);
        userStorage.deleteFile(oldId);
        return beanConverter.convert(saved);
    }

    public DiagnosticPointEntity getDiagnosticPointEntity(long id) {
        DiagnosticPointEntity entity = diagnosticPointRepository.findOne(id);
        if (entity == null) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return entity;
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

    @Transactional
    public void moveDiagnosticPointUp(long id){
        DiagnosticPointEntity entity = getDiagnosticPointEntity(id);

        List<DiagnosticPointEntity> entities = diagnosticPointRepository.findByDorderLessThanOrderByDorder(entity.getDorder());
        if(entities.size() > 0){
            DiagnosticPointEntity last = entities.get(entities.size() - 1);
            int order = last.getDorder();
            last.setDorder(entity.getDorder());
            entity.setDorder(order);
        }
    }

    @Transactional
    public void moveDiagnosticPointDown(long id){
        DiagnosticPointEntity entity = getDiagnosticPointEntity(id);
        List<DiagnosticPointEntity> entities = diagnosticPointRepository.findByDorderGreaterThanOrderByDorder(entity.getDorder());
        if(entities.size()>0){
            int order = entities.get(0).getDorder();
            entities.get(0).setDorder(entity.getDorder());
            entity.setDorder(order);
        }
    }

    private void saveImageUrls(List<DiagnosticPointBean> beans){
        beans.forEach(bean -> saveImageUrl(bean));
    }

    private void saveImageUrl(DiagnosticPointBean bean){
        String fileUrl = userStorage.getFileURL(bean.getImageId());
        bean.setImageUrl(fileUrl);
    }
}

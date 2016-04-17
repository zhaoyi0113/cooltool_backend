package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.converter.NurseSpeakConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by yzzhao on 3/15/16.
 */
@Service("NurseSpeakService")
public class NurseSpeakService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakService.class.getName());

    @Autowired private NurseRepository nurseRepository;
    @Autowired private NurseSpeakRepository speakRepository;
    @Autowired private NurseSpeakConverter speakConverter;
    @Autowired private NurseSpeakCommentService speakCommentService;
    @Autowired private NurseSpeakThumbsUpService thumbsUpService;
    @Autowired private SpeakTypeService speakTypeService;
    @Autowired private ImagesInSpeakService speakImageService;
    @Autowired @Qualifier("StorageService") private StorageService storageService;

    //===============================================================
    //             get
    //===============================================================

    public long countByUserId(long userId){
        logger.info("get nurse {} speak count", userId);
        long count = speakRepository.countByUserId(userId);
        logger.info("get nurse {} speak count {}", userId, count);
        return count;
    }

    public Map<Long, Long> countByUserIds(String strUserIds){
        logger.info("get nurse {} speak count", strUserIds);
        if (VerifyUtil.isIds(strUserIds)) {
            String[]   strIds  = strUserIds.split(",");
            List<Long> userIds = new ArrayList<>();
            for (String id : strIds) {
                long tmpId = Long.parseLong(id);
                userIds.add(tmpId);
            }
            return countByUserIds(userIds);
        }
        return new HashMap<>();
    }

    public Map<Long, Long> countByUserIds(List<Long> userIds){
        if (null==userIds || userIds.isEmpty()) {
            return new HashMap<>();
        }
        logger.info("get nurse {} speak count", userIds);
        List<Object[]>  count  = speakRepository.countByUserIdIn(userIds);
        Map<Long, Long> id2num = new HashMap<>();
        for(int i=0; i<count.size(); i++) {
            Object[] tmp = count.get(i);
            logger.info("index {} array is {}--{}", i, tmp[0], tmp[1]);
            id2num.put((Long)tmp[0], (Long)tmp[1]);
        }
        logger.info("get nurse {} speak count {}", userIds, count);
        return id2num;
    }

    public long countBySpeakType(long userId, String speakType) {
        logger.info("get nurse speak ("+speakType+") count");
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null==speaktype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        SpeakTypeBean speakTypeBean = speakTypeService.getSpeakTypeByType(speaktype);
        if (null==speakTypeBean) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = speakRepository.countByUserIdAndSpeakType(userId, speakTypeBean.getId());
        return count;
    }

    public List<NurseSpeakBean> getNurseSpeak(long userId, int index, int number) {
        logger.info("get nurse speak at "+index+" number="+number);

        // get speaks
        PageRequest request = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks = speakRepository.findByUserId(userId, request);

        // move to cache
        List<NurseSpeakEntity> speakEntities = new ArrayList<NurseSpeakEntity>();
        for (NurseSpeakEntity entity : speaks) {
            speakEntities.add(entity);
        }

        // parse entities to bean
        List<NurseSpeakBean> beans = entitiesToBeans(speakEntities);
        fillOtherProperties(userId, beans);
        return beans;
    }

    public List<NurseSpeakBean> getSpeakByType(long userId, String speakType, int index, int number) {
        logger.info("get user={} all speak ({}) at {} number={}", userId, speakType, index, number);
        SpeakTypeBean speakTypeBean = null;
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null!=speaktype) {
            speakTypeBean = speakTypeService.getSpeakTypeByType(speaktype);
        }

        // get speaks
        PageRequest pagable = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks = null;
        if (null!=speakTypeBean) {
            logger.info("get speak type's speaks. type is {}", speakTypeBean);
            speaks = speakRepository.findBySpeakType(speakTypeBean.getId(), pagable);
        }
        else {
            logger.info("get all speak type's speaks");
            speaks = speakRepository.findAll(pagable);
        }

        // move to cache
        List<NurseSpeakEntity> speakEntities = new ArrayList<NurseSpeakEntity>();
        for (NurseSpeakEntity entity : speaks) {
            speakEntities.add(entity);
        }

        // parse entities to bean
        List<NurseSpeakBean> beans = entitiesToBeans(speakEntities);
        fillOtherProperties(userId, beans);
        return beans;
    }

    @Transactional
    public List<NurseSpeakBean> getSpeakByUserIdAndType(long userId, String speakType, int index, int number) {
        logger.info("get nurse speak ("+speakType+") at "+index+" number="+number);
        SpeakType speaktype = SpeakType.parseString(speakType);
        if (null==speaktype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        SpeakTypeBean speakTypeBean = speakTypeService.getSpeakTypeByType(speaktype);
        if (null==speakTypeBean) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // get speaks
        PageRequest pagable = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> speaks = speakRepository.findByUserIdAndSpeakType(userId, speakTypeBean.getId(), pagable);

        // move to cache
        List<NurseSpeakEntity> speakEntities = new ArrayList<NurseSpeakEntity>();
        for (NurseSpeakEntity entity : speaks) {
            speakEntities.add(entity);
        }

        // parse entities to bean
        List<NurseSpeakBean> beans = entitiesToBeans(speakEntities);
        fillOtherProperties(userId, beans);
        return beans;
    }

    public NurseSpeakBean getNurseSpeak(long userId, long speakId) {
        NurseSpeakEntity resultSet = speakRepository.findOne(speakId);
        NurseSpeakBean   speak     = speakConverter.convert(resultSet);
        speak.setImages(speakImageService.getImagesInSpeak(speak.getId()));
        List<NurseSpeakCommentBean> comments = speakCommentService.getSpeakCommentsByNurseSpeakId(speak.getId());
        for (NurseSpeakCommentBean tmp : comments) {
            tmp.setIsCurrentUserMade(userId==tmp.getCommentMakerId());
        }
        speak.setComments(comments);
        speak.setCommentsCount(comments.size());
        List<NurseSpeakThumbsUpBean> thumbsUps = thumbsUpService.getSpeakThumbsUpByNurseSpeakId(speakId);
        speak.setThumbsUps(thumbsUps);
        speak.setThumbsUpsCount(thumbsUps.size());
        return speak;
    }

    private List<NurseSpeakBean> entitiesToBeans(List<NurseSpeakEntity> entities) {
        if (null==entities || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakBean> beans = new ArrayList<>();
        for(NurseSpeakEntity tmp : entities) {
            NurseSpeakBean bean = speakConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(long userId, List<NurseSpeakBean> beans) {
        if (null==beans || beans.isEmpty()) {
            return;
        }

        // speak ids/file ids cache
        List<Long> speakIds = new ArrayList<Long>();
        List<Long> userIds = new ArrayList<Long>();
        List<Long> fileIds = new ArrayList<Long>();

        // get username
        Map<Long, String> userId2Name   = new HashMap<Long, String>();
        Map<Long, Long>   userId2FileId = new HashMap<Long, Long>();
        userIds.add(userId);
        for(NurseSpeakBean tmp : beans){
            if (userIds.contains(tmp.getUserId())) {
                continue;
            }
            userIds.add(tmp.getUserId());
        }

        Iterable<NurseEntity> users = nurseRepository.findByIdIn(userIds);
        for (NurseEntity tmp : users) {
            userId2Name.put(tmp.getId(), tmp.getName());
            userId2FileId.put(tmp.getId(), tmp.getProfilePhotoId());
            fileIds.add(tmp.getProfilePhotoId());
        }

        // convert to bean
        Map<Long, NurseSpeakBean> speakIdToBeanMap = new HashMap<Long, NurseSpeakBean>();
        for (NurseSpeakBean tmp : beans) {
            String nurseName = userId2Name.get(tmp.getUserId());
            tmp.setUserName(nurseName);
            speakIdToBeanMap.put(tmp.getId(), tmp);
            speakIds.add(tmp.getId());
        }

        NurseSpeakBean speakBean = null;

        // get comment of speak
        List<NurseSpeakCommentBean> comments = speakCommentService.getSpeakCommentsByNurseSpeakIds(speakIds);
        for (NurseSpeakCommentBean tmp : comments) {
            long speakId = tmp.getNurseSpeakId();
            speakBean = speakIdToBeanMap.get(speakId);

            List<NurseSpeakCommentBean> mapValue = speakBean.getComments();
            if (null==mapValue) {
                mapValue = new ArrayList<>();
                speakBean.setComments(mapValue);
            }
            // is current user made
            tmp.setIsCurrentUserMade((userId>0 && tmp.getCommentMakerId()==userId));

            mapValue.add(tmp);
        }

        // get thumbsUp of speak
        List<NurseSpeakThumbsUpBean> thumbsUps = thumbsUpService.getSpeakThumbsUpByNurseSpeakIds(speakIds);
        for (NurseSpeakThumbsUpBean tmp : thumbsUps) {
            long speakId = tmp.getNurseSpeakId();
            speakBean = speakIdToBeanMap.get(speakId);

            List<NurseSpeakThumbsUpBean> mapValue = speakBean.getThumbsUps();
            if (null==mapValue) {
                mapValue = new ArrayList<>();
                speakBean.setThumbsUps(mapValue);
            }

            mapValue.add(tmp);
        }

        // get image url of speak
        Map<Long, List<ImagesInSpeakBean>> idToImage = speakImageService.getImagesInSpeak(speakIds);
        for (NurseSpeakBean tmp : beans) {
            long                    speakId = tmp.getId();
            List<ImagesInSpeakBean> images  = idToImage.get(speakId);
            speakBean = speakIdToBeanMap.get(speakId);
            speakBean.setImages(images);
        }

        // get user profile photo url
        Map<Long, String> idToPath = storageService.getFilePath(fileIds);
        for (NurseSpeakBean entity : beans) {
            long   speakId    = entity.getId();
            long   tmpUserId  = entity.getUserId();
            long   userProfId = userId2FileId.get(tmpUserId);
            String userProUrl = idToPath.get(userProfId);
            speakBean = speakIdToBeanMap.get(speakId);
            speakBean.setUserProfilePhotoUrl(userProUrl);
        }

        // construct return values
        List<Object> countTarget = null;
        for (NurseSpeakBean tmp : beans) {
            long speakId = tmp.getId();
            speakBean    = speakIdToBeanMap.get(speakId);
            countTarget  = (List)speakBean.getComments();
            speakBean.setCommentsCount(null == countTarget ? 0 : countTarget.size());
            countTarget  = (List)speakBean.getThumbsUps();
            speakBean.setThumbsUpsCount(null == countTarget ? 0 : countTarget.size());
        }
        return;
    }


    //===============================================================
    //             add
    //===============================================================

    public NurseSpeakBean addSmug(long userId, String content, String fileName, InputStream fileInputStream) {
        logger.info("add SMUG : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.SMUG.name(), fileName, fileInputStream);
    }

    public NurseSpeakBean addCathart(long userId, String content, String fileName, InputStream fileInputStream) {
        logger.info("add CATHART : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.CATHART.name(), fileName, fileInputStream);
    }

    public NurseSpeakBean addAskQuestion(long userId, String content, String fileName, InputStream fileInputStream) {
        logger.info("add ASK_QUESTION : userId=" + userId + " content="+content+" fileName="+fileName);
        return addNurseSpeak(userId, content, SpeakType.ASK_QUESTION.name(), fileName, fileInputStream);
    }

    @Transactional
    private NurseSpeakBean addNurseSpeak(long userId, String content, String strSpeakType, String imageName, InputStream image) {
        logger.info("add nurse speak with userId={} speakType={} content={} imageName={} image={}", userId, strSpeakType, content, imageName, (null!=image));
        boolean hasImage = false;

        // check speak type
        SpeakType     speakType     = SpeakType.parseString(strSpeakType);
        SpeakTypeBean speakTypeBean = speakTypeService.getSpeakTypeByType(speakType);
        if (null==speakTypeBean) {
            logger.error("speak type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        NurseSpeakEntity entity = new NurseSpeakEntity();
        // check speak content
        if (null==content || "".equals(content)) {
            logger.error("speak content is empty");
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_IS_EMPTY);
        }

        entity.setUserId(userId);
        entity.setContent(content);
        entity.setSpeakType(speakTypeBean.getId());
        entity.setTime(new Date());
        entity = speakRepository.save(entity);


        if (null==imageName||"".equals(imageName) || null==image) {
            logger.info("there is no image file need to insert!");
        }
        else {
            addImage(userId, entity.getId(), imageName, image);
        }

        NurseSpeakBean bean = speakConverter.convert(entity);
        if (hasImage) {
            List<ImagesInSpeakBean> images = speakImageService.getImagesInSpeak(entity.getId());
            bean.setImages(images);
        }

        return bean;
    }

    public ImagesInSpeakBean addImage(long userId, long speakId, String imageName, InputStream image) {
        logger.info("user {} add image to speak={} image name={} image={}", userId, speakId, imageName, (null!=image));

        // check speak
        NurseSpeakEntity speakEntity   = speakRepository.findOne(speakId);
        if (null==speakEntity) {
            logger.error("nurse speak is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId!=speakEntity.getUserId()) {
            logger.error("user can not modify other's speak");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // check speak type
        SpeakTypeBean    speakTypeBean = speakTypeService.getSpeakType(speakEntity.getSpeakType());
        if (null==speakTypeBean) {
            logger.error("speak type is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = speakImageService.countImagesInSpeak(speakId);
        if (count>=1 && SpeakType.CATHART.equals(speakTypeBean.getType())) {
            logger.warn("the cathart speak do not need a image");
            return new ImagesInSpeakBean();
        }
        else if (count>=1 && SpeakType.SMUG.equals(speakTypeBean.getType())) {
            logger.warn("the smug speak do not need image more than one");
            return new ImagesInSpeakBean();
        }
        else if (count>=9 && SpeakType.ASK_QUESTION.equals(speakTypeBean.getType())) {
            logger.warn("the ask_question speak do not need image more than nine");
            return new ImagesInSpeakBean();
        }

        ImagesInSpeakBean imageInSpeakBean = speakImageService.addImage(speakId, imageName, image);
        return imageInSpeakBean;
    }

    //===============================================================
    //             delete
    //===============================================================

    @Transactional
    public List<NurseSpeakBean> deleteByIds(long speakMakerId, String strSpeakIds) {
        logger.info("delete nurse speak by speak ids {}.", strSpeakIds);
        if (!VerifyUtil.isIds(strSpeakIds)) {
            logger.warn("speak ids are invalid");
            return new ArrayList<>();
        }

        List<Long> ids       = new ArrayList<>();
        String[]   strArrIds = strSpeakIds.split(",");
        for (String tmp : strArrIds) {
            Long id = Long.parseLong(tmp);
            ids.add(id);
        }

        return deleteByIds(speakMakerId, ids);
    }

    @Transactional
    public List<NurseSpeakBean> deleteByIds(long speakMakerId, List<Long> speakIds) {
        logger.info("delete nurse speak by speak ids {}.", speakIds);
        if (null==speakIds || speakIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakEntity> speaks = speakRepository.findByIdIn(speakIds);
        if (null==speaks || speaks.isEmpty()) {
            logger.info("delete nothing");
        }

        for (NurseSpeakEntity speak : speaks) {
            if (speak.getUserId()==speakMakerId) {
                continue;
            }
            logger.warn("can not delete the comment {} not making by yourself {}", speak, speakMakerId);
            return new ArrayList<>();
        }

        speakRepository.delete(speaks);
        speakCommentService.deleteBySpeakIds(speakIds);
        thumbsUpService.deleteBySpeakIds(speakIds);
        speakImageService.deleteBySpeakIds(speakIds);

        List<NurseSpeakBean> retValue = new ArrayList<>();
        for (NurseSpeakEntity tmp : speaks) {
            NurseSpeakBean comment = speakConverter.convert(tmp);
            retValue.add(comment);
        }
        return retValue;
    }

    public ImagesInSpeakBean deleteImagesInSpeak(long imagesInSpeakId) {
        return speakImageService.deleteById(imagesInSpeakId);
    }

    //=======================================================
    //          Comment service
    //=======================================================

    public NurseSpeakCommentBean addSpeakComment(long speakId, long commentMakerId, long commentReceiverId, String comment) {
        NurseSpeakCommentBean commentBean = speakCommentService.addSpeakComment(speakId, commentMakerId, commentReceiverId, comment);
        //TODO need to transfer the comment to the relative person
        //... ...
        return commentBean;
    }

    public List<NurseSpeakCommentBean> deleteSpeakComment(long userId, String strCommentIds) {
        List<NurseSpeakCommentBean> comments = speakCommentService.getCommentByIds(strCommentIds);

        if (null==comments || comments.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> speakIds = new ArrayList<>();
        List<Long> makerIds = new ArrayList<>();
        for (NurseSpeakCommentBean comment : comments) {
            long speakId = comment.getNurseSpeakId();
            long makerId = comment.getCommentMakerId();
            if (!speakIds.contains(speakId)) {
                speakIds.add(speakId);
            }
            if (!makerIds.contains(makerId)) {
                makerIds.add(makerId);
            }
        }

        // is comment maker
        if (makerIds.size()==1 && userId==makerIds.get(0)) {
            logger.warn("delete by comment maker");
            comments = speakCommentService.deleteByIds(strCommentIds);
            return comments;
        }

        // is speak not exist
        List<NurseSpeakEntity> speaks = speakRepository.findByIdIn(speakIds);
        if (null==speaks || speakIds.isEmpty()) {
            logger.warn("delete as speak not exist");
            comments = speakCommentService.deleteByIds(strCommentIds);
            return comments;
        }

        // is speak maker
        boolean isSpeakMaker = true;
        for (NurseSpeakEntity tmp : speaks) {
            if (tmp.getUserId()==userId) {
                continue;
            }
            isSpeakMaker = false;
        }
        if (isSpeakMaker) {
            logger.warn("delete by speak maker");
            comments = speakCommentService.deleteByIds(strCommentIds);
            return comments;
        }
        logger.warn("can not delete comment");

        return new ArrayList<>();
    }


    //=======================================================
    //          Thumbs up service
    //=======================================================

    @Transactional
    public NurseSpeakThumbsUpBean setNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        NurseSpeakEntity speakEntity = speakRepository.findOne(nurseSpeakId);
        if (null==speakEntity) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
        }

        // you could not add thumbs_up for yourself
        if (thumbsUpUserId==speakEntity.getUserId()) {
            throw new BadRequestException(ErrorCode.SPEAK_THUMBS_UP_CAN_NOT_FOR_SELF);
        }

        NurseSpeakThumbsUpBean thumbsUpBean = thumbsUpService.setSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
        //TODO need to transfer this thumbs_up to the relative person

        return thumbsUpBean;
    }

    public NurseSpeakThumbsUpBean getNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId) {
        return thumbsUpService.findNurseSpeakThumbsUp(nurseSpeakId, thumbsUpUserId);
    }
}

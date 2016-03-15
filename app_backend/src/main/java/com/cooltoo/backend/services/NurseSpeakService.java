package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.converter.NurseSpeakConverter;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 3/15/16.
 */
@Service("NurseSpeakService")
public class NurseSpeakService {

    private static final Logger logger = Logger.getLogger(NurseSpeakService.class.getName());

    @Autowired
    private NurseSpeakRepository speakRepository;

    @Autowired
    private NurseSpeakConverter speakConverter;

    @Autowired
    private StorageService storageService;

    public List<NurseSpeakBean> getNurseSpeak(long userId, int index, int number) {
        logger.info("get nurse speak at "+index+" number="+number);
        PageRequest request = new PageRequest(index, number, Sort.Direction.DESC, "time");
        Page<NurseSpeakEntity> entities = speakRepository.findNurseSpeakByUserId(userId, request);
        List<NurseSpeakBean> speaks = new ArrayList<NurseSpeakBean>();
        for (NurseSpeakEntity entity : entities) {
            NurseSpeakBean speak = speakConverter.convert(entity);
            String fileUrl = storageService.getFileUrl(entity.getId());
            speak.setImageUrl(fileUrl);
            speaks.add(speak);
        }
        return speaks;
    }
}

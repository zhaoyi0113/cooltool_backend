package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.services.NurseFriendsService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yzzhao on 3/25/16.
 */
@Service

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
public class NurseSpeakDataCreator {

    private static final int threadNumber = 100;

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakDataCreator.class);

    @Autowired
    private NurseSpeakService nurseSpeakService;

    @Autowired
    private NurseFriendsService nurseFriendsService;

    @Autowired
    private NurseRepository nurseRepository;

    @Test
    public void testAddNurseSpeak(){
        addNurseSpeak(nurseRepository.findNurseByNameContaining(MockDataCreator.NURSE_NAME_PREFIX));
    }

    private void addNurseSpeak(List<NurseEntity> nurseEntities) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        final CountDownLatch latch = new CountDownLatch(nurseEntities.size());
        for (final NurseEntity entity : nurseEntities) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int number = (int) MockDataCreator.getRandomInt(10, 100);
                    for(int i=0; i<number; i++) {
                        String fileName = null;
                        InputStream inputStream = null;
                        SpeakType speakType = SpeakType.values()[(int) MockDataCreator.getRandomInt(0, 3)];
                        if (speakType.equals(SpeakType.CATHART)) {
                            fileName = "choumei" + MockDataCreator.getRandomInt(0, 17) + ".jpg";
                            inputStream = getResourceAsStream("/com/cooltoo/data/choumei/" + fileName);

                        }
                        logger.info("add nurse speak ");
                        NurseSpeakBean nurseSpeakBean = nurseSpeakService.addNurseSpeak(entity.getId(), MockDataCreator.getRandomString(100),
                                speakType.name(), fileName, inputStream);
                        addSpeakCommentsAndThumbUp(nurseSpeakBean);
                    }
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private InputStream getResourceAsStream(String name) {
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        if(inputStream == null){
            logger.error("can't find resource "+name);
            throw new BadRequestException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return inputStream;
    }
    private void addSpeakCommentsAndThumbUp(NurseSpeakBean entity){
        NurseEntity nurse = nurseRepository.findOne(entity.getUserId());
        List<NurseFriendsBean> friendList =
                nurseFriendsService.getFriendList(nurse.getId());
        int number = (int) MockDataCreator.getRandomInt(5, 10);
        logger.info("add comments and thumb up "+nurse.getName());
        for(int i=0; i<number; i++) {
            if(friendList.size() >= i && friendList.get(i) == null){
                continue;
            }
            nurseSpeakService.addSpeakComment(entity.getId(), friendList.get(i).getId(), nurse.getId(),
                    MockDataCreator.getRandomString(40));
            nurseSpeakService.addNurseSpeakThumbsUp(entity.getId(), friendList.get(i).getId());
        }

    }
}

package com.cooltoo.data;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.Application;
import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.services.NurseFriendsService;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.GenderType;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yzzhao on 3/20/16.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
public class MockDataCreator extends AbstractCooltooTest {

    private static final int nurseNumber = 100;

    private static final int threadNumber = 100;

    private static final Logger logger = LoggerFactory.getLogger(MockDataCreator.class);

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private NurseFriendsService nurseFriendsService;

    @Autowired
    private NurseService nurseService;

    @Autowired
    private NurseSpeakService nurseSpeakService;

    @Test
    public void testCreateNurse() {
        List<NurseEntity> nurse = createNurse();
        createFriendRelation(nurse);
        addNurseSpeak(nurse);
    }

    private List<NurseEntity> createNurse() {
        final  String name = "护士";
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        final CountDownLatch latch = new CountDownLatch(nurseNumber);
        final List<NurseEntity> allNurse = new ArrayList<NurseEntity>();
        for (int i = 0; i < nurseNumber; i++) {
            final int index = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    NurseEntity entity = new NurseEntity();
                    entity.setName(name + index);
                    logger.info("create nurse "+entity.getName());
                    entity.setAge((int) getRandomInt(18, 50));
                    entity.setGender(GenderType.parseInt((int) getRandomInt(0, 2)));
                    entity.setMobile(String.valueOf(getRandomInt(13900000000l, 13999999999l)));
                    entity.setPassword("123456");
                    NurseEntity b = saveNurse(entity);
                    if (b != null) {
                        allNurse.add(entity);
                        String fileName = "background" + getRandomInt(0, 10) + ".jpg";
                        InputStream inputStream = getResourceAsStream("/com/cooltoo/data/background/" + fileName);

                        nurseService.addBackgroundImage(b.getId(), fileName, inputStream);
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fileName = "head" + getRandomInt(0, 11) + ".jpg";
                        inputStream = getResourceAsStream("/com/cooltoo/data/heads/" + fileName);
                        nurseService.addHeadPhoto(b.getId(), fileName, inputStream);
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        return allNurse;
    }

    private InputStream getResourceAsStream(String name) {
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        if(inputStream == null){
            logger.error("can't find resource "+name);
            throw new BadRequestException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return inputStream;
    }

    private void createFriendRelation(final List<NurseEntity> nurseEntities) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        final CountDownLatch latch = new CountDownLatch(nurseEntities.size());
        for (int i = 0; i < nurseEntities.size(); i++) {
            final int ii = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int number = (int)getRandomInt(10,100);
                    for(int j=0; j<number; j++){
                        int index = (int)getRandomInt(0, nurseEntities.size());
                        NurseEntity user = nurseEntities.get(ii);
                        NurseEntity friend = nurseEntities.get(index);
                        logger.info("add friend between "+user.getName()+", "+friend.getName());
                        nurseFriendsService.addFriend(user.getId(), friend.getId());
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

    private NurseEntity saveNurse(NurseEntity entity) {
        List<NurseEntity> found = nurseRepository.findNurseByMobile(entity.getMobile());
        if (found.isEmpty()) {
            logger.info("create nurse " + entity.getMobile());
            return nurseRepository.save(entity);
        } else {
            logger.info("nurse " + entity.getMobile() + " alread existed.");
            return null;
        }
    }

    private void addNurseSpeak(List<NurseEntity> nurseEntities) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        final CountDownLatch latch = new CountDownLatch(nurseEntities.size());
        for (final NurseEntity entity : nurseEntities) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int number = (int) getRandomInt(10, 100);
                    for(int i=0; i<number; i++) {
                        String fileName = null;
                        InputStream inputStream = null;
                        SpeakType speakType = SpeakType.values()[(int) getRandomInt(0, 3)];
                        if (speakType.equals(SpeakType.CATHART)) {
                            fileName = "choumei" + getRandomInt(0, 17) + ".jpg";
                            inputStream = getResourceAsStream("/com/cooltoo/data/choumei/" + fileName);

                        }
                        logger.info("add nurse speak ");
                        NurseSpeakBean nurseSpeakBean = nurseSpeakService.addNurseSpeak(entity.getId(), getRandomString(100),
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

    private void addSpeakCommentsAndThumbUp(NurseSpeakBean entity){
        NurseEntity nurse = nurseRepository.findOne(entity.getUserId());
        List<NurseFriendsBean> friendList =
                nurseFriendsService.getFriendList(nurse.getId());
        int number = (int) getRandomInt(5, 10);
        logger.info("add comments and thumb up "+nurse.getName());
        for(int i=0; i<number; i++) {
            nurseSpeakService.addSpeakComment(entity.getId(), friendList.get(i).getId(), nurse.getId(),
                    getRandomString(40));
            nurseSpeakService.addNurseSpeakThumbsUp(entity.getId(), friendList.get(i).getId());
        }

    }

    private static long getRandomInt(long minimum, long maximum) {
        return minimum + (long) (Math.random() * maximum);
    }

    private static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}

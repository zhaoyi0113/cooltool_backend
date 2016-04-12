package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.TokenAccessEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.TokenAccessRepository;
import com.cooltoo.backend.services.NurseFriendsService;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserType;
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
import java.util.Date;
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
public class MockDataCreator  {

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

    @Autowired
    private TokenAccessRepository tokenRepository;

    public static final String NURSE_NAME_PREFIX = "护士";

    @Test
    public void testCreateNurse() {
        List<NurseEntity> nurse = createNurse();
    }

    private List<NurseEntity> createNurse() {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        final CountDownLatch latch = new CountDownLatch(nurseNumber);
        final List<NurseEntity> allNurse = new ArrayList<NurseEntity>();
        for (int i = 0; i < nurseNumber; i++) {
            final int index = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    NurseEntity entity = new NurseEntity();
                    entity.setName(NURSE_NAME_PREFIX + index);
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

                        nurseService.updateBackgroundImage(b.getId(), fileName, inputStream);
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fileName = "head" + getRandomInt(0, 11) + ".jpg";
                        inputStream = getResourceAsStream("/com/cooltoo/data/heads/" + fileName);
                        nurseService.updateHeadPhoto(b.getId(), fileName, inputStream);
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


    private NurseEntity saveNurse(NurseEntity entity) {
        List<NurseEntity> found = nurseRepository.findByMobile(entity.getMobile());
        List<NurseEntity> nurseByName = nurseRepository.findByName(entity.getName());
        if (found.isEmpty() && nurseByName.isEmpty()) {
            logger.info("create nurse " + entity.getMobile());
            entity = nurseRepository.save(entity);
            addNurseTonken(entity);
            return entity;
        } else {
            logger.info("nurse " + entity.getMobile() + " alread existed.");
            return null;
        }
    }

    public static long getRandomInt(long minimum, long maximum) {
        return minimum + (long) (Math.random() * maximum);
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private void addNurseTonken(NurseEntity nurse) {
        TokenAccessEntity entity = new TokenAccessEntity();
        entity.setUserId(nurse.getId());
        entity.setType(UserType.NURSE);
        entity.setTimeCreated(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setToken(nurse.getMobile()+System.nanoTime());
        List<TokenAccessEntity> all = tokenRepository.findTokenAccessByToken(entity.getToken());
        if (all.isEmpty()) {
            tokenRepository.save(entity);
        }
        else {
            tokenRepository.delete(all.get(0));
        }
    }
}

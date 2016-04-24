package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.services.NurseFriendsService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yzzhao on 4/4/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@Ignore
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
public class FriendRelationDataCreator {

    private static final Logger logger = LoggerFactory.getLogger(FriendRelationDataCreator.class);

    @Autowired
    private NurseFriendsService nurseFriendsService;
    private int threadNumber = 100;

    @Autowired
    private NurseRepository nurseRepository;

    @Test
    public void addFriendRelation(){
        Iterable<NurseEntity> nurses = nurseRepository.findAll();
        List<NurseEntity> entities = new ArrayList<NurseEntity>();
        for(NurseEntity entity : nurses){
            entities.add(entity);
        }
        createFriendRelation(entities);
    }

    private void createFriendRelation(final List<NurseEntity> nurseEntities) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        final CountDownLatch latch = new CountDownLatch(nurseEntities.size());
        for (int i = 0; i < nurseEntities.size(); i++) {
            final int ii = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int number = (int) MockDataCreator.getRandomInt(10, 100);
                    for (int j = 0; j < number; j++) {
                        int index = (int) MockDataCreator.getRandomInt(0, nurseEntities.size());
                        NurseEntity user = nurseEntities.get(ii);
                        NurseEntity friend = nurseEntities.get(index);
                        logger.info("add friend between " + user.getName() + ", " + friend.getName());
                        nurseFriendsService.setFriendship(user.getId(), friend.getId());
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

}

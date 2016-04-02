package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.backend.entities.NurseFriendsEntity;
import com.cooltoo.backend.repository.NurseFriendsRepository;
import com.cooltoo.backend.repository.NurseRepository;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
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

import java.util.List;

/**
 * Created by yzzhao on 4/2/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
public class DataCorrector {

    private static final Logger logger = LoggerFactory.getLogger(DataCorrector.class);

    @Autowired
    private NurseFriendsRepository friendsRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Test
    public void removeNonExistedUsers(){
        List<NurseFriendsEntity> friends = friendsRepository.findAll();
        for(NurseFriendsEntity entity : friends){
            deleteNonExistedNurse(entity.getId(), entity.getUserId());
            deleteNonExistedNurse(entity.getId(), entity.getFriendId());
        }
    }

    private void deleteNonExistedNurse(long id, long userId){
        if(!nurseRepository.exists(userId) && friendsRepository.exists(id)){
            logger.info("delete nurse "+userId);
            friendsRepository.delete(id);
        }
    }
}

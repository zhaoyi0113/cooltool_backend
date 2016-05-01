package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.services.ActivityService;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;

/**
 * Created by yzzhao on 5/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@Ignore
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
public class ActivitiesCreator {

    @Autowired
    private ActivityService activityService;

    @Test
    public void createActivities(){
        for(int i=0; i<10000;i++) {
            String title = MockDataCreator.getRandomString(10);
            String subTitle = MockDataCreator.getRandomString(20);
            String description = MockDataCreator.getRandomString(140);
            String place = MockDataCreator.getRandomString(20);
            long price = MockDataCreator.getRandomInt(0, 1000);
            String enrollUrl = "http://" + MockDataCreator.getRandomString(30);
            activityService.createActivity(title, subTitle, description, Calendar.getInstance().getTime().toString(), place, String.valueOf(price), enrollUrl);
        }
    }
}

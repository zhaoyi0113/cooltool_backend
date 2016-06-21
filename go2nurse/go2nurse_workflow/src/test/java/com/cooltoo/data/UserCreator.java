package com.cooltoo.data;

import com.cooltoo.Application;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.UserType;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 3/20/16.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class UserCreator {

    private static final int userNumber = 100;


    @Autowired private UserRepository userRepository;

    public static final String NURSE_NAME_PREFIX = "用户";
    public static final long year = 1000*60*60*24*365;

    @Test
    public void testCreateUser() {
        List<UserEntity> users = createUser();
        users = userRepository.save(users);
        System.out.println(users);
    }

    private List<UserEntity> createUser() {
        List<UserEntity> allUser = new ArrayList<>();
        for (int index = 0; index<userNumber; index ++) {
            UserEntity entity = new UserEntity();
            entity.setName(NURSE_NAME_PREFIX + index);
            entity.setBirthday(new Date(System.currentTimeMillis() - year * ((long) getRandomInt(18, 50))));
            entity.setGender(GenderType.parseInt((int) getRandomInt(0, 2)));
            entity.setMobile(String.valueOf(getRandomInt(13900000000l, 13999999999l)));
            entity.setPassword("123456");
            entity.setAuthority(UserAuthority.AGREE_ALL);
            entity.setType(UserType.NORMAL_USER);
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
            allUser.add(entity);
        }
        return allUser;
    }

    public static long getRandomInt(long minimum, long maximum) {
        return minimum + (long) (Math.random() * maximum);
    }

}

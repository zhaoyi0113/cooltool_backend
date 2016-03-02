package com.cooltoo.serivces;

import com.cooltoo.entities.TokenAccessEntity;
import com.cooltoo.repository.TokenAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by yzzhao on 3/2/16.
 */
@Service("NurseLoginService")
public class NurseLoginService {

    @Autowired
    private TokenAccessRepository tokenAccessRepository;


    public void login(String mobile, String password){
        TokenAccessEntity entity = new TokenAccessEntity();

        entity.setTimeCreated(Calendar.getInstance().getTime());

    }
}

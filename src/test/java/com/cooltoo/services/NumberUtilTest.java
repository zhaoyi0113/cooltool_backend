package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.util.NumberUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Transactional
public class NumberUtilTest extends AbstractCooltooTest {

    @Test
    public void testIsMobileValid() {
        boolean valid = NumberUtil.isMobileValid("3143214321");
        Assert.assertFalse(valid);
        valid = NumberUtil.isMobileValid("13812341232");
        Assert.assertTrue(valid);
    }
}

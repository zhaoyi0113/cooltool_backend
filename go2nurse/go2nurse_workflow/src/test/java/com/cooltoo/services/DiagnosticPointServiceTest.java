package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.DiagnosticPointBean;
import com.cooltoo.go2nurse.service.DiagnosticPointService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.glassfish.jersey.internal.util.collection.ByteBufferInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by yzzhao on 6/10/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/diagnostic_point.xml"),
})
public class DiagnosticPointServiceTest extends AbstractCooltooTest {

    @Autowired
    private DiagnosticPointService service;

    @Test
    public void testDiagnosticPointService(){
        ByteArrayInputStream inputStream = new ByteArrayInputStream("aaa".getBytes());
        DiagnosticPointBean dp = service.createDiagnosticPoint("aaa", inputStream);
        Assert.assertNotNull(dp);
        dp = service.getDiagnosticPoint(dp.getId());
        Assert.assertEquals("aaa",dp.getName());
        Assert.assertEquals(7, dp.getDorder());

        List<DiagnosticPointBean> allDiagnosticPoints = service.getAllDiagnosticPoints();
        Assert.assertEquals(7, allDiagnosticPoints.size());

        service.editDiagnosticPointStatus(dp.getId(), CommonStatus.DISABLED.name());
        dp = service.getDiagnosticPoint(dp.getId());
        Assert.assertEquals(CommonStatus.DISABLED, dp.getStatus());

    }

    @Test
    public void testDiagnosticPointMove(){
        service.moveDiagnosticPointDown(3);
        DiagnosticPointBean diagnosticPoint3 = service.getDiagnosticPoint(3);
        DiagnosticPointBean diagnosticPoint4 = service.getDiagnosticPoint(4);
        Assert.assertEquals(4, diagnosticPoint3.getDorder());
        Assert.assertEquals(3, diagnosticPoint4.getDorder());
    }
}

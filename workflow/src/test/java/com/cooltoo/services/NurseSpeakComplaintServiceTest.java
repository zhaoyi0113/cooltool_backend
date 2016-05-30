package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseSpeakComplaintBean;
import com.cooltoo.backend.services.NurseSpeakComplaintService;
import com.cooltoo.constants.SuggestionStatus;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hp on 2016/5/30.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_complaint_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/images_in_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_thumbs_up_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_speak_comment_service_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml"),
})
public class NurseSpeakComplaintServiceTest extends AbstractCooltooTest {

    @Autowired private NurseSpeakComplaintService complaintService;

    @Test
    public void testGetAllType() {
        List<String> complaintTypes = complaintService.getAllStatus();
        Assert.assertEquals(3, complaintTypes.size());
    }
    @Test
    public void testCountByStatus() {
        String status = "all";
        long count = complaintService.countByStatus(status);
        Assert.assertEquals(6, count);

        status = SuggestionStatus.UNREAD.name();
        count = complaintService.countByStatus(status);
        Assert.assertEquals(2, count);

        status = SuggestionStatus.READ.name();
        count = complaintService.countByStatus(status);
        Assert.assertEquals(4, count);

        long speakId = 1;
        count = complaintService.countBySpeakId(speakId);
        Assert.assertEquals(1, count);

        speakId = 2;
        count = complaintService.countBySpeakId(speakId);
        Assert.assertEquals(3, count);

        speakId = 3;
        count = complaintService.countBySpeakId(speakId);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testGetByStatus() {
        String status = "All";
        List<NurseSpeakComplaintBean> complaints = complaintService.getComplaintByStatus(status, 0, 4);
        Assert.assertEquals(4, complaints.size());
        Assert.assertEquals(3, complaints.get(3).getId());

        complaints = complaintService.getComplaintByStatus(status, 1, 4);
        Assert.assertEquals(2, complaints.size());
        Assert.assertEquals(2, complaints.get(0).getId());

        status = SuggestionStatus.READ.name();
        complaints = complaintService.getComplaintByStatus(status, 0, 5);
        Assert.assertEquals(4, complaints.size());
        Assert.assertEquals(6, complaints.get(0).getId());
        Assert.assertEquals(3, complaints.get(3).getId());
    }

    @Test
    public void testGetBySpeakId() {
        long speakId = 1;
        List<NurseSpeakComplaintBean> complaints = complaintService.getComplaintBySpeakId(speakId, 0, 4);
        Assert.assertEquals(1, complaints.size());
        Assert.assertEquals(1, complaints.get(0).getId());

        speakId = 2;
        complaints = complaintService.getComplaintBySpeakId(speakId, 0, 4);
        Assert.assertEquals(3, complaints.size());
        Assert.assertEquals(2, complaints.get(2).getId());
        Assert.assertEquals(4, complaints.get(0).getId());
    }

    @Test
    public void testAddComplaint() {
        long informantId = 0;
        long speakId = 4;
        String reason = "reason test aaaa";
        NurseSpeakComplaintBean bean = complaintService.addComplaint(informantId, speakId, reason);
        Assert.assertNotNull(bean);
        Assert.assertEquals(0, bean.getInformantId());
        Assert.assertEquals(4, bean.getSpeakId());
        Assert.assertEquals(reason, bean.getReason());
        Assert.assertEquals(SuggestionStatus.UNREAD, bean.getStatus());

        speakId = 200;
        Throwable thr = null;
        try {
            complaintService.addComplaint(informantId, speakId, reason);
        }
        catch (Exception ex) {
            thr = ex;
        }
        Assert.assertNotNull(thr);
    }

    @Test
    public void testUpdateComplaint() {
        long complaintId = 1;
        String reason = "reason test aaaa";
        String status = "read";
        NurseSpeakComplaintBean complaint = complaintService.updateCompliant(complaintId, reason, status);
        Assert.assertNotNull(complaint);
        Assert.assertEquals(1, complaint.getId());
        Assert.assertEquals(reason, complaint.getReason());
        Assert.assertEquals(SuggestionStatus.READ, complaint.getStatus());
    }
}

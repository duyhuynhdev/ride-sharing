package com.bikiegang.ridesharing;

import com.bikiegang.ridesharing.dao.BroadcastDao;
import com.bikiegang.ridesharing.pojo.Broadcast;
import com.bikiegang.ridesharing.utilities.BroadcastCenterUtil;
import org.junit.Test;

/**
 * Created by hpduy17 on 7/30/15.
 */
public class GCMTesting extends com.bikiegang.ridesharing.Test {
    @Test
    public void sendGCM(){
        BroadcastCenterUtil broadcast = new BroadcastCenterUtil();
        Broadcast br = new Broadcast();
        br.setOs(Broadcast.ANDROID);
        br.setUserId("fb_796315037151408");
        br.setRegId("dPTptl3wDvo:APA91bH4XwoWMgD5eddKu4raa0OEictnjRLDbfeotk_sVjLIhhmMhB5OdBAz8Nk5D2SupaMbVrJtIyH3U-nwB1oZC6g3-uRt_J3kxn_wTI_FPwZsVUXyoGcJMfF-omC5l1acSrzykhyL");
        br.setDeviceId("abc");
        br.setId(br.getDeviceId() + "#" + br.getUserId());
        new BroadcastDao().insert(br);
        broadcast.pushNotification("Test", "fb_796315037151408", BroadcastCenterUtil.CLOUD_BIKE_SENDER_ID);
        Thread thread = new Thread(broadcast);
        thread.start();
        while (true){

        }
    }
}

package com.bikiegang.ridesharing.geocoding;

import com.bikiegang.ridesharing.annn.framework.common.LogUtil;
import com.bikiegang.ridesharing.pojo.LatLng;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by hpduy17 on 7/17/15.
 */
public class GoogleDirectionAPIProcess {

    Logger logger = LogUtil.getLogger(this.getClass());
    private String url = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s%s";
    private String wayPointInUrl = "&waypoints=%s";
    private String viaInWayPoint = "via:%s";

    public JSONObject direction(LatLng[] latLngs) throws IOException {
        LatLng src = latLngs[0];
        LatLng des = latLngs[latLngs.length - 1];
        String wayPoints = "";
        if (latLngs.length >= 2) {
            String via = "";
            for (int i = 1; i <= latLngs.length - 2; i++) {
                via += String.format(viaInWayPoint, latLngs[i].toGoogleParameter());
                if (i < latLngs.length - 2) {
                    via += "|";
                }
            }
            wayPoints = String.format(wayPointInUrl, via);
        }
        String urlString = String.format(url, src.toGoogleParameter(), des.toGoogleParameter(), wayPoints);
        return queryGoogle(urlString);
    }

    public JSONObject queryGoogle(String urlString) throws IOException {
        URL url = new URL(urlString);
        InputStreamReader input = new InputStreamReader(url.openStream(), "UTF-8");
        try {
            return new JSONObject(new JSONTokener(input));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        } finally {
            input.close();
        }
        return null;
    }
}

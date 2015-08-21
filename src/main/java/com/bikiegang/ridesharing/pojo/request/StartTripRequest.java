package com.bikiegang.ridesharing.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hpduy17 on 7/15/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartTripRequest {
    private String userId = "";
    private long plannedTripId;


    public String getUserId() {
        return userId;
    }

    public long getPlannedTripId() {
        return plannedTripId;
    }


}

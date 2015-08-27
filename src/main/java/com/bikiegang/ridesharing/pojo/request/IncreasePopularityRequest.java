package com.bikiegang.ridesharing.pojo.request;

import com.bikiegang.ridesharing.annn.framework.util.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hpduy17 on 7/15/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncreasePopularityRequest {
    private String userId;
    private long popularLocationId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getPopularLocationId() {
        return popularLocationId;
    }

    public void setPopularLocationId(long popularLocationId) {
        this.popularLocationId = popularLocationId;
    }

    public IncreasePopularityRequest() {
    }

    public IncreasePopularityRequest(String userId, long popularLocationId) {
        this.userId = userId;
        this.popularLocationId = popularLocationId;
    }
    
    @Override
    public String toString() {
        return JSONUtil.Serialize(this);
    }
}

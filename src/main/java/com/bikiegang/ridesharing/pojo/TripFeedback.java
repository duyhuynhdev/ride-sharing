package com.bikiegang.ridesharing.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by hpduy17 on 6/25/15.
 */
public class TripFeedback {
    private long id;
    private long tripId;
    private String userFeedBackId;
    private String userBeFeedbackId;
    private int value;
    private long createdTime;
    private String comment;
    //final field
    @JsonIgnore
    public static final int POSITIVE = 1;
    @JsonIgnore
    public static final int NEUTRAL = 0;
    @JsonIgnore
    public static final int NEGATIVE = -1;

    public TripFeedback() {
    }

    public TripFeedback(long id, long tripId, int value, long createdTime, String comment, String userBeFeedbackId, String userFeedBackId) {
        this.id = id;
        this.tripId = tripId;
        this.value = value;
        this.createdTime = createdTime;
        this.comment = comment;
        this.userFeedBackId = userFeedBackId;
        this.userBeFeedbackId = userBeFeedbackId;
    }
    public TripFeedback(TripFeedback that) {
        this.id = that.id;
        this.tripId = that.tripId;
        this.value = that.value;
        this.createdTime = that.createdTime;
        this.comment = that.comment;
        this.userFeedBackId = that.userFeedBackId;
        this.userBeFeedbackId = that.userBeFeedbackId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserFeedBackId() {
        return userFeedBackId;
    }

    public void setUserFeedBackId(String userFeedBackId) {
        this.userFeedBackId = userFeedBackId;
    }

    public String getUserBeFeedbackId() {
        return userBeFeedbackId;
    }

    public void setUserBeFeedbackId(String userBeFeedbackId) {
        this.userBeFeedbackId = userBeFeedbackId;
    }
}

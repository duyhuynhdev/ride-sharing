package com.bikiegang.ridesharing.controller;

import com.bikiegang.ridesharing.dao.FeedDao;
import com.bikiegang.ridesharing.database.Database;
import com.bikiegang.ridesharing.database.IdGenerator;
import com.bikiegang.ridesharing.parsing.Parser;
import com.bikiegang.ridesharing.pojo.Feed;
import com.bikiegang.ridesharing.pojo.User;
import com.bikiegang.ridesharing.pojo.request.GetFeedsRequest;
import com.bikiegang.ridesharing.pojo.response.*;
import com.bikiegang.ridesharing.utilities.MessageMappingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hpduy17 on 8/19/15.
 */
public class FeedController {
    private FeedDao dao = new FeedDao();
    private Database database = Database.getInstance();

    public boolean postNewFeed(long refId, int type) {
        if (refId <= 0 || (type != Feed.PLANNED_TRIP && type != Feed.SOCIAL_TRIP))
            return false;
        Feed feed = new Feed();
        feed.setId(IdGenerator.getFeedId());
        feed.setRefId(refId);
        feed.setType(type);
        return dao.insert(feed);
    }

    public String getFeeds(GetFeedsRequest request) throws IOException {
        if (null == request.getUserId() || request.getUserId().equals("")) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_not_found, "'userId'");
        }
        if (request.getNumberOfFeed() <= 0) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_invalid, "'numberOfFeed'");
        }
        if (request.getStartIdx() < 0 || request.getStartIdx() >= database.getFeedHashMap().size()) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_invalid, "'startIdx'");
        }
        int fromIdx = 0;
        int toIdx = 0;
        switch (request.getGetWay()) {
            case GetFeedsRequest.NEW_FEED:
                fromIdx = request.getStartIdx();
                toIdx = database.getFeedHashMap().size();
                break;
            case GetFeedsRequest.HISTORY_FEED:
                fromIdx = request.getStartIdx() - request.getNumberOfFeed();
                if (fromIdx < 0)
                    fromIdx = 0;
                toIdx = request.getStartIdx();
                break;
            default:
                return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_invalid, "'getWay'");
        }
        List<Feed> feeds = new ArrayList<>(database.getFeedHashMap().values()).subList(fromIdx, toIdx);
        List<FeedResponse> responses = new ArrayList<>();
        for (Feed feed : feeds) {
            FeedResponse response = new FeedResponse();
            TripInFeed tif = null;
            ExPartnerInfoResponse partnerInfoResponse = null;
            UserShortDetailResponse userShortDetailResponse = null;
            switch (feed.getType()) {
                case Feed.PLANNED_TRIP:
                    tif = new PlannedTripShortDetailResponse(database.getPlannedTripHashMap().get(feed.getRefId()), request.getUserId());
                    break;
                case Feed.SOCIAL_TRIP:
                    tif = new SocialTripResponse(database.getSocialTripHashMap().get(feed.getRefId()));
                    break;
            }
            if (tif != null) {
                User user = database.getUserHashMap().get(tif.getCreatorId());
                if (user != null) {
                    userShortDetailResponse = new UserShortDetailResponse(user);
                    partnerInfoResponse = new UserController().getExPartners(user.getId());
                    response.setPartnerInfo(partnerInfoResponse);
                    response.setUserDetail(userShortDetailResponse);
                    response.setTripDetail(tif);
                    responses.add(response);
                }
            }
        }
        GetFeedsResponse feedsResponse = new GetFeedsResponse();
        feedsResponse.setFeeds(responses.toArray(new FeedResponse[responses.size()]));
        return Parser.FeedsToJSon(true,MessageMappingUtil.Successfully,feedsResponse);
    }
}
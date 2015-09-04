package com.bikiegang.ridesharing.controller;

import com.bikiegang.ridesharing.dao.PopularLocationDao;
import com.bikiegang.ridesharing.database.Database;
import com.bikiegang.ridesharing.database.IdGenerator;
import com.bikiegang.ridesharing.parsing.Parser;
import com.bikiegang.ridesharing.pojo.PopularLocation;
import com.bikiegang.ridesharing.pojo.request.AddPopularLocationRequest;
import com.bikiegang.ridesharing.pojo.request.IncreasePopularityRequest;
import com.bikiegang.ridesharing.pojo.response.GetPopularLocationResponse;
import com.bikiegang.ridesharing.utilities.MessageMappingUtil;
import com.bikiegang.ridesharing.utilities.daytime.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hpduy17 on 6/29/15.
 */
public class PopularLocationController {
    private PopularLocationDao dao = new PopularLocationDao();
    private Database database = Database.getInstance();

    // image processing
    public static final int bgImageWidth = 344*3;
    public static final int bgImageHeight = 136*3;


    //----------------
    public String increasePopularity(IncreasePopularityRequest request) throws JsonProcessingException {
        if (null == request.getUserId() || request.getUserId().equals("")) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_not_found, "'userId'");
        }

        if (request.getPopularLocationId() <= 0) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_invalid, "'popularLocationId'");
        }

        PopularLocation location = database.getPopularLocationHashMap().get(request.getPopularLocationId());
        if (null == location) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Object_is_not_found, "Location");
        }
        location.getSearcher().add(request.getUserId());

        if (dao.update(location)) {
            //sort
            int currentIdx = database.getOrderedPopularLocation().indexOf(location.getId());
            int realIdx = currentIdx;
            for (int i = currentIdx + 1; i < database.getOrderedPopularLocation().size(); i++) {
                long id = database.getOrderedPopularLocation().get(i);
                if (location.getSearcher().size() < database.getPopularLocationHashMap().get(id).getSearcher().size())
                    break;
                realIdx++;
            }
            if (realIdx != currentIdx) {
                database.getOrderedPopularLocation().remove(location.getId());
                database.getOrderedPopularLocation().add(realIdx, location.getId());
            }
            return Parser.ObjectToJSon(true, MessageMappingUtil.Successfully);
        }
        return Parser.ObjectToJSon(false, MessageMappingUtil.Interactive_with_database_fail);
    }

    public String addPopularLocation(AddPopularLocationRequest request) throws JsonProcessingException {
        if (null == request.getUserId() || request.getUserId().equals("")) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_not_found,"'userId'");
        }
        if (request.getLat() <= 0 && request.getLng() <= 0) {
            return Parser.ObjectToJSon(false, MessageMappingUtil.Element_is_invalid,"'lat' and 'lng'");
        }
        PopularLocation location = new PopularLocation();
        location.setId(IdGenerator.getPopularLocationId());
        location.setLat(request.getLat());
        location.setLng(request.getLng());
        location.setTime(DateTimeUtil.now());
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.getSearcher().add(request.getUserId());
        location.setBackgroundImageLink(request.getImagePath());
        if (dao.insert(location)) {
            return Parser.ObjectToJSon(true, MessageMappingUtil.Successfully);
        }
        return Parser.ObjectToJSon(false, MessageMappingUtil.Interactive_with_database_fail);
    }

    public String getPopularLocationList() throws JsonProcessingException {
        List<PopularLocation> locations = new ArrayList<>(database.getPopularLocationHashMap().values());
        Collections.shuffle(locations);
        return Parser.ObjectToJSon(true, MessageMappingUtil.Successfully, new GetPopularLocationResponse(locations));
    }




}

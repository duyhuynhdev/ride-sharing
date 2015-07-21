package com.bikiegang.ridesharing.controller;

import com.bikiegang.ridesharing.dao.PlannedTripDao;
import com.bikiegang.ridesharing.database.Database;
import com.bikiegang.ridesharing.database.IdGenerator;
import com.bikiegang.ridesharing.geocoding.FetchingDataFromGoogleRouting;
import com.bikiegang.ridesharing.geocoding.GoogleRoutingObject.GoogleRoute;
import com.bikiegang.ridesharing.geocoding.Pairing;
import com.bikiegang.ridesharing.parsing.Parser;
import com.bikiegang.ridesharing.pojo.LinkedLocation;
import com.bikiegang.ridesharing.pojo.PlannedTrip;
import com.bikiegang.ridesharing.pojo.User;
import com.bikiegang.ridesharing.pojo.request.AutoSearchParingRequest;
import com.bikiegang.ridesharing.pojo.request.CreatePlannedTripRequest;
import com.bikiegang.ridesharing.pojo.request.GetPlannedTripDetailRequest;
import com.bikiegang.ridesharing.pojo.response.*;
import com.bikiegang.ridesharing.utilities.DateTimeUtil;
import com.bikiegang.ridesharing.utilities.FakeGroup.FakePlannedTrip;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hpduy17 on 6/29/15.
 */
public class PlannedTripController {
    private PlannedTripDao dao = new PlannedTripDao();
    private Database database = Database.getInstance();
    public static final double DEFAULT_PRICE = 3; //3000 vnd/km -> 3 vnd/m
    public static boolean isCreatingFake = false;
    public PlannedTripSortDetailResponse[] getListPlannedTripSortDetail(List<Long> routeIds) throws IOException {
        PlannedTripSortDetailResponse[] plannedTripSortDetailResponses = new PlannedTripSortDetailResponse[routeIds.size()];
        for (int i = 0; i < routeIds.size(); i++) {
            long id = routeIds.get(i);
            PlannedTrip r = database.getPlannedTripHashMap().get(id);
            if (null != r) {
                plannedTripSortDetailResponses[i] = getPlannedTripSortDetail(r);
            }
        }
        return plannedTripSortDetailResponses;
    }

    public PlannedTripSortDetailResponse[] getListPlannedTripSortDetailFromObject(List<PlannedTrip> plannedTrips) throws IOException {
        PlannedTripSortDetailResponse[] plannedTripSortDetailResponses = new PlannedTripSortDetailResponse[plannedTrips.size()];
        for (int i = 0; i < plannedTrips.size(); i++) {
            plannedTripSortDetailResponses[i] = getPlannedTripSortDetail(plannedTrips.get(i));
        }
        return plannedTripSortDetailResponses;
    }

    public PlannedTripSortDetailResponse getPlannedTripSortDetail(PlannedTrip route) throws IOException {
        GoogleRoute googleRoute = new FetchingDataFromGoogleRouting().getRouteFromRoutingResult(route);
        PlannedTripSortDetailResponse plannedTripSortDetailResponse = new PlannedTripSortDetailResponse();
        plannedTripSortDetailResponse.setId(route.getId());
        plannedTripSortDetailResponse.setRole(route.getRole());
        plannedTripSortDetailResponse.setUnitPrice(route.getOwnerPrice());
        plannedTripSortDetailResponse.setStartAddress(googleRoute.getLegs()[0].getStart_address());
        plannedTripSortDetailResponse.setEndAddress(googleRoute.getLegs()[0].getEnd_address());
        int numberOfRequest = 0;
        try {
            numberOfRequest = database.getReceiverRequestsBox().get(route.getCreatorId()).get(route.getId()).size();
        } catch (Exception ignored) {

        }
        plannedTripSortDetailResponse.setNumberOfRequest(numberOfRequest);
        return plannedTripSortDetailResponse;
    }

    public String getPlannedTripDetail(GetPlannedTripDetailRequest request) throws IOException {
        if (request.getPlannedTripId() <= 0) {
            return Parser.ObjectToJSon(false, "'plannedTripId' is invalid");
        }
        PlannedTrip plannedTrip = database.getPlannedTripHashMap().get(request.getPlannedTripId());
        if (plannedTrip == null) {
            return Parser.ObjectToJSon(false, "Planned Trip is not found");
        }
        PlannedTripDetailResponse routeDetailResponse = new PlannedTripDetailResponse(getPlannedTripSortDetail(plannedTrip), plannedTrip.getRawRoutingResult().toString());
        return Parser.ObjectToJSon(true, "Get Planned Trip detail successfully", routeDetailResponse);
    }

    public String autoSearchParing(AutoSearchParingRequest request) throws Exception {

        AutoSearchParingResponse response = new AutoSearchParingResponse();
        PlannedTripSortDetailResponse[] route = null;
        if (null == request.getCreatorId() || request.getCreatorId().equals("")) {
            return Parser.ObjectToJSon(false, "'creatorId' is not found");
        }
        if (null == request.getGoogleRoutingResult() || request.getGoogleRoutingResult().equals("")) {
            return Parser.ObjectToJSon(false, "'googleRoutingResult' is invalid");
        }
        // create fake planned trip
        PlannedTrip fakePlannedTrip = new PlannedTrip();
        fakePlannedTrip.setGoTime(DateTimeUtil.now());
        fakePlannedTrip.setRawRoutingResult(new JSONObject(request.getGoogleRoutingResult()));
        fakePlannedTrip.setRole(0);
        fakePlannedTrip.setCreatorId(request.getCreatorId());
        fakePlannedTrip.setType(PlannedTrip.INSTANT);
        List<LinkedLocation> fakeLocation = new FetchingDataFromGoogleRouting().fetch(fakePlannedTrip);
        //TODO fake trip
        if(Database.databaseStatus == Database.TESTING && !isCreatingFake){
            isCreatingFake = true;
            new FakePlannedTrip().fakePlannedTrip(fakePlannedTrip);
        }
        //TODO end fake
        if (fakeLocation != null) {
            HashMap<Integer, List<PlannedTrip>> plannedTrips = new Pairing().pair(fakePlannedTrip, fakeLocation);
            for (int key : plannedTrips.keySet()) {
                List<PlannedTrip> plannedTripList = plannedTrips.get(key);
                List<UserDetailWithPlannedTripDetailResponse> details = new ArrayList<>();
                for (PlannedTrip r : plannedTripList) {
                    User creator = database.getUserHashMap().get(r.getCreatorId());
                    if (null != creator) {
                        PlannedTripDetailResponse plannedTripDetailResponse = new PlannedTripDetailResponse(getPlannedTripSortDetail(r), r.getRawRoutingResult().toString());
                        UserDetailWithPlannedTripDetailResponse userPlannedTripDetail = new UserDetailWithPlannedTripDetailResponse(creator, plannedTripDetailResponse);
                        details.add(userPlannedTripDetail);
                    }
                }
                if (key == User.DRIVER) {
                    response.setDrivers(details.toArray(new UserDetailWithPlannedTripDetailResponse[details.size()]));
                } else {
                    response.setPassengers(details.toArray(new UserDetailWithPlannedTripDetailResponse[details.size()]));
                }
            }
        }
        return Parser.ObjectToJSon(true, "Paring successfully", response);
    }

    public String createPlannedTrip(CreatePlannedTripRequest request) throws Exception {
        CreatePlannedTripResponse response = new CreatePlannedTripResponse();
        PlannedTripSortDetailResponse[] plannedTrips = null;
        if (null == request.getCreatorId() || request.getCreatorId().equals("")) {
            return Parser.ObjectToJSon(false, "'creatorId' is not found");
        }
        if (null == request.getGoogleRoutingResult() || request.getGoogleRoutingResult().equals("")) {
            return Parser.ObjectToJSon(false, "'googleRoutingResult' is invalid");
        }
        PlannedTrip plannedTrip = new PlannedTrip();
        plannedTrip.setId(IdGenerator.getPlannedTripId());
        plannedTrip.setRole(request.getRole());
        if (request.getGoTime() > DateTimeUtil.now()) {
            plannedTrip.setGoTime(request.getGoTime());
            plannedTrip.setType(PlannedTrip.SINGLE_FUTURE);
        } else {
            plannedTrip.setGoTime(DateTimeUtil.now());
            plannedTrip.setType(PlannedTrip.INSTANT);
        }
        plannedTrip.setCreatorId(request.getCreatorId());
        plannedTrip.setRawRoutingResult(new JSONObject(request.getGoogleRoutingResult()));
        //fetch data
        List<LinkedLocation> locations = new FetchingDataFromGoogleRouting().fetch(plannedTrip);
        //TODO fake trip
        if(Database.databaseStatus == Database.TESTING && !isCreatingFake){
            isCreatingFake = true;
            new FakePlannedTrip().fakePlannedTrip(plannedTrip);
        }
        //TODO end fake
        if (null != locations) {
            LinkedLocationController controller = new LinkedLocationController();
            for (LinkedLocation location : locations) {
                controller.insertLinkLocation(location, plannedTrip.getRole());
            }
        }
        // check price
        if (request.getPrice() < 0) {
            plannedTrip.setOwnerPrice(DEFAULT_PRICE);
        } else {
            plannedTrip.setOwnerPrice(request.getPrice() / plannedTrip.getSumDistance());
        }
        if (dao.insert(plannedTrip)) {
            PlannedTripSortDetailResponse yourPlannedTripDetail = getPlannedTripSortDetail(plannedTrip);
            if (request.isParing()) {
                HashMap<Integer, List<PlannedTrip>> paringResults = new Pairing().pair(plannedTrip);
                List<PlannedTrip> result;
                if (plannedTrip.getRole() == User.PASSENGER) {
                    result = paringResults.get(User.DRIVER);
                } else {
                    result = paringResults.get(User.PASSENGER);
                }
                plannedTrips = getListPlannedTripSortDetailFromObject(result);

            }
            response.setYourPlannedTrip(yourPlannedTripDetail);
            response.setPairedPlannedTripsResult(plannedTrips);
            return Parser.ObjectToJSon(true, "Create planned trip successfully", response);
        }
        return Parser.ObjectToJSon(true, "Cannot create planned trip");
    }

}

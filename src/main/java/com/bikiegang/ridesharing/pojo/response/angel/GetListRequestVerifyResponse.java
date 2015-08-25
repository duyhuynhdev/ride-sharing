package com.bikiegang.ridesharing.pojo.response.angel;

import java.util.List;

/**
 * Created by hpduy17 on 8/24/15.
 */
public class GetListRequestVerifyResponse {
    RequestVerifySortDetailResponse[] response;

    public GetListRequestVerifyResponse(RequestVerifySortDetailResponse[] requests) {
        this.response = requests;
    }

    public GetListRequestVerifyResponse(List<RequestVerifySortDetailResponse> requests) {
        this.response = requests.toArray(new RequestVerifySortDetailResponse[requests.size()]);
    }

    public RequestVerifySortDetailResponse[] getRequests() {
        return response;
    }

    public void setRequests(RequestVerifySortDetailResponse[] requests) {
        this.response = requests;
    }
}
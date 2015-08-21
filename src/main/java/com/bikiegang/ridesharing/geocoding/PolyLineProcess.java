package com.bikiegang.ridesharing.geocoding;

import com.bikiegang.ridesharing.pojo.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hpduy17 on 7/14/15.
 */
public class PolyLineProcess {
    public static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }

    public static double getDistanceFromPolyLine(String encoded) {

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        double distance = 0;
        LatLng previousLocation = null;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            if(previousLocation !=  null){
                distance += previousLocation.distanceInMetres(p);
            }
            previousLocation = p;
        }
        return distance;
    }
    public static void main(String... args) {
        String poly = "ao|`Aeo{iSw@e@IIAM@Of@sAFOAMg@e@c@Sc@M{@OaAOq@C@c@PgEJoC?cBV}G`@oMPg@PmE\\\\\\\\_JhAoY^eK`@uNNaGNqFXiFJcBP{GZqINaC~@sV`@yMXiHJcCkDB\\";
        List<LatLng> latLngs = decodePoly(poly);
        String line = "";
        for (LatLng ll : latLngs) {
            line += "(" + ll.getLat() + "," + ll.getLng() + ") ";
        }
        System.out.print(line);
    }
}

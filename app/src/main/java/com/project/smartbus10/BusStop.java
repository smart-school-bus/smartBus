package com.project.smartbus10;

public class BusStop {
    private String BusStopID;
    private String BusStopName;
    private String BusStopOrder;
    private String BusStopLatitude;
    private String BusStopLongitude;

    public void setBusStopID(String busStopID) {
        BusStopID = busStopID;
    }

    public void setBusStopName(String busStopName) {
        BusStopName = busStopName;
    }

    public void setBusStopOrder(String busStopOrder) {
        BusStopOrder = busStopOrder;
    }

    public void setBusStopLatitude(String busStopLatitude) {
        BusStopLatitude = busStopLatitude;
    }

    public void setBusStopLongitude(String busStopLongitude) {
        BusStopLongitude = busStopLongitude;
    }

    public String getBusStopID() {
        return BusStopID;
    }

    public String getBusStopName() {
        return BusStopName;
    }

    public String getBusStopOrder() {
        return BusStopOrder;
    }

    public String getBusStopLatitude() {
        return BusStopLatitude;
    }

    public String getBusStopLongitude() {
        return BusStopLongitude;
    }
}

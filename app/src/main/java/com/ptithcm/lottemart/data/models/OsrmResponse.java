package com.ptithcm.lottemart.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OsrmResponse {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public static class Route {
        @SerializedName("geometry")
        private String geometry; // Polyline encoded string

        @SerializedName("distance")
        private double distance;

        @SerializedName("duration")
        private double duration;

        public String getGeometry() {
            return geometry;
        }

        public double getDistance() {
            return distance;
        }

        public double getDuration() {
            return duration;
        }
    }
}

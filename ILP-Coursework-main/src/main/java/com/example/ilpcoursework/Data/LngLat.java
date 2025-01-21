package com.example.ilpcoursework.Data;

import java.util.Objects;

public class LngLat {
    private Double lng;
    private Double lat;



    // Default constructor needed by Jackson
    public LngLat() {
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LngLat lngLat = (LngLat) obj;
        return Double.compare(lngLat.lng, lng) == 0 && Double.compare(lngLat.lat, lat) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lng, lat);
    }


    // Constructor
    public LngLat(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    // Getters and setters
    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}

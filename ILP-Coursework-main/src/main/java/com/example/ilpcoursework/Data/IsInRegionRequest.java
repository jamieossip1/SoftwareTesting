package com.example.ilpcoursework.Data;

public class IsInRegionRequest {

    private LngLat position;
    private Region region;

    public IsInRegionRequest(LngLat position, Region region) {
        this.position = position;
        this.region = region;
    }
    public LngLat getPosition() {
        return position;
    }
    public void setPosition(LngLat position) {
        this.position = position;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
}

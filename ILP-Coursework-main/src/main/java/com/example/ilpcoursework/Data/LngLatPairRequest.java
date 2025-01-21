package com.example.ilpcoursework.Data;

public class LngLatPairRequest {
    private LngLat position1;
    private LngLat position2;


    public LngLatPairRequest(LngLat position1, LngLat position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    public LngLat getPosition1() {
        return position1;
    }
    public void setPosition1(LngLat position1) {
        this.position1 = position1;
    }
    public LngLat getPosition2() {
        return position2;
    }
    public void setPosition2(LngLat position2) {
        this.position2 = position2;
    }
}

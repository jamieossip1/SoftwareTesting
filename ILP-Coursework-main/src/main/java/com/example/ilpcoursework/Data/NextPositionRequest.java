package com.example.ilpcoursework.Data;

public class NextPositionRequest {
    private LngLat start;
    private Double angle;

    // Getters and setters
    public LngLat getStart() {
        return start;
    }

    public void setStart(LngLat start) {
        this.start = start;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }
}

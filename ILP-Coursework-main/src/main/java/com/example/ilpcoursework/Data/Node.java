package com.example.ilpcoursework.Data;

public class Node {
    LngLat point;
    Node parent;
    double angle;
    Double gScore;
    Double hScore;


    public Node(LngLat point, Node parent, Double angle, Double gScore, Double hScore) {
    this.point = point;
    this.parent = parent;
    this.angle = angle;
    this.gScore = gScore;
    this.hScore = hScore;



    }

    public LngLat getPoint() {
        return point;
    }
    public void setPoint(LngLat point) {
        this.point = point;
    }
    public Node getParent() {
        return parent;
    }
    public void setParent(Node parent) {
        this.parent = parent;

    }
    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Double getGScore() {
        return gScore;
    }
    public void setGScore(Double gScore) {
        this.gScore = gScore;
    }
    public Double getHScore() {
        return hScore;
    }
    public void setHScore(Double hScore) {
        this.hScore = hScore;
    }

}

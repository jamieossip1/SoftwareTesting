package com.example.ilpcoursework.RestClientData;

import com.example.ilpcoursework.Data.LngLat;

import java.util.List;

public class noFlyZones {

    private String name;
    private List<LngLat> vertices;

    public List<LngLat> getVertices() {
        return vertices;
    }

    public void setVertices(List<LngLat> vertices) {
        this.vertices = vertices;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

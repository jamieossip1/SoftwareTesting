package com.example.ilpcoursework.RestClientData;

import com.example.ilpcoursework.Data.LngLat;

import java.util.List;

public class CentralArea {
    public String name;
    public List<LngLat> vertices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LngLat> getVertices() {
        return vertices;
    }

    public void setVertices(List<LngLat> vertices) {
        this.vertices = vertices;
    }
}

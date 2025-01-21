package com.example.ilpcoursework.Data;

import java.util.List;

public class Region {
    private String name;
    private List<LngLat> vertices;

    public Region(String name, List<LngLat> vertices) {
        this.name = name;
        this.vertices = vertices;
    }
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

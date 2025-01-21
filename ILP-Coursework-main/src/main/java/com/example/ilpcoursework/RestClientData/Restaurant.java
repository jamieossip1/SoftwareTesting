package com.example.ilpcoursework.RestClientData;

import com.example.ilpcoursework.Data.LngLat;

import java.awt.*;
import java.util.List;

public class Restaurant {

    private String name;
    private LngLat location;
    private List<String> openingDays;
    private List<MenuItem> menu;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LngLat getLocation() {
        return location;
    }

    public void setLocation(LngLat location) {
        this.location = location;
    }
    public List<String> getOpeningDays() {
        return openingDays;
    }
    public void setOpeningDays(List<String> openingDays) {
        this.openingDays = openingDays;
    }
    public List<MenuItem> getMenu() {
        return menu;
    }
    public void setMenu(List<MenuItem> menu) {
        this.menu = menu;
    }
}

package com.movieticketapp.models;

import java.util.HashMap;
import java.util.Map;

public class Theater {
    private String theaterId;
    private String name;
    private String address;
    private String city;
    private boolean active;

    public Theater() {
    }

    public Theater(String theaterId, String name, String address, String city, boolean active) {
        this.theaterId = theaterId;
        this.name = name;
        this.address = address;
        this.city = city;
        this.active = active;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("theaterId", theaterId);
        map.put("name", name);
        map.put("address", address);
        map.put("city", city);
        map.put("active", active);
        return map;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

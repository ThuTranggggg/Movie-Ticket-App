package com.movieticketapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Showtime {
    private String showtimeId;
    private String movieId;
    private String theaterId;
    private String startTime;
    private String endTime;
    private String roomName;
    private double price;
    private List<String> availableSeats;
    private List<String> bookedSeats;

    public Showtime() {
    }

    public Showtime(String showtimeId, String movieId, String theaterId, String startTime, String endTime, String roomName, double price, List<String> availableSeats, List<String> bookedSeats) {
        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.price = price;
        this.availableSeats = availableSeats;
        this.bookedSeats = bookedSeats;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("showtimeId", showtimeId);
        map.put("movieId", movieId);
        map.put("theaterId", theaterId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("roomName", roomName);
        map.put("price", price);
        map.put("availableSeats", availableSeats == null ? new ArrayList<>() : availableSeats);
        map.put("bookedSeats", bookedSeats == null ? new ArrayList<>() : bookedSeats);
        return map;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }
}

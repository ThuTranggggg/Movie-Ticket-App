package com.movieticketapp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ticket {
    private String ticketId;
    private String userId;
    private String movieId;
    private String theaterId;
    private String showtimeId;
    private List<String> seatNumbers;
    private int quantity;
    private double totalPrice;
    private String bookingTime;
    private String status;
    private String movieTitle;
    private String theaterName;
    private String showtimeLabel;
    private String showtimeStartTime;
    private String posterUrl;

    public Ticket() {
    }

    public Ticket(String ticketId, String userId, String movieId, String theaterId, String showtimeId, List<String> seatNumbers, int quantity, double totalPrice, String bookingTime, String status, String movieTitle, String theaterName, String showtimeLabel, String showtimeStartTime, String posterUrl) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.showtimeId = showtimeId;
        this.seatNumbers = seatNumbers;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.bookingTime = bookingTime;
        this.status = status;
        this.movieTitle = movieTitle;
        this.theaterName = theaterName;
        this.showtimeLabel = showtimeLabel;
        this.showtimeStartTime = showtimeStartTime;
        this.posterUrl = posterUrl;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("ticketId", ticketId);
        map.put("userId", userId);
        map.put("movieId", movieId);
        map.put("theaterId", theaterId);
        map.put("showtimeId", showtimeId);
        map.put("seatNumbers", seatNumbers == null ? new ArrayList<>() : seatNumbers);
        map.put("quantity", quantity);
        map.put("totalPrice", totalPrice);
        map.put("bookingTime", bookingTime);
        map.put("status", status);
        map.put("movieTitle", movieTitle);
        map.put("theaterName", theaterName);
        map.put("showtimeLabel", showtimeLabel);
        map.put("showtimeStartTime", showtimeStartTime);
        map.put("posterUrl", posterUrl);
        return map;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getShowtimeLabel() {
        return showtimeLabel;
    }

    public void setShowtimeLabel(String showtimeLabel) {
        this.showtimeLabel = showtimeLabel;
    }

    public String getShowtimeStartTime() {
        return showtimeStartTime;
    }

    public void setShowtimeStartTime(String showtimeStartTime) {
        this.showtimeStartTime = showtimeStartTime;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}

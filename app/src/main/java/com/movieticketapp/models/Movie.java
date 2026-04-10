package com.movieticketapp.models;

import java.util.HashMap;
import java.util.Map;

public class Movie {
    private String movieId;
    private String title;
    private String genre;
    private String duration;
    private String rating;
    private String description;
    private String posterUrl;
    private String releaseDate;
    private boolean active;

    public Movie() {
    }

    public Movie(String movieId, String title, String genre, String duration, String rating, String description, String posterUrl, String releaseDate, boolean active) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.active = active;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("movieId", movieId);
        map.put("title", title);
        map.put("genre", genre);
        map.put("duration", duration);
        map.put("rating", rating);
        map.put("description", description);
        map.put("posterUrl", posterUrl);
        map.put("releaseDate", releaseDate);
        map.put("active", active);
        return map;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

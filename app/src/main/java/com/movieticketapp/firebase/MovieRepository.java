package com.movieticketapp.firebase;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.movieticketapp.models.Movie;
import com.movieticketapp.models.Showtime;
import com.movieticketapp.models.Theater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MovieRepository {

    public void getActiveMovies(DataCallback<List<Movie>> callback) {
        FirebaseManager.getFirestore()
                .collection("movies")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Movie> movies = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        Movie movie = document.toObject(Movie.class);
                        if (movie.getMovieId() == null) {
                            movie.setMovieId(document.getId());
                        }
                        if (movie.isActive() || movie.getTitle() != null) {
                            movies.add(movie);
                        }
                    }
                    Collections.sort(movies, Comparator.comparing(Movie::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
                    callback.onSuccess(movies);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unable to load movies." : e.getMessage()));
    }

    public void getMovieById(String movieId, DataCallback<Movie> callback) {
        FirebaseManager.getFirestore()
                .collection("movies")
                .document(movieId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Movie movie = snapshot.toObject(Movie.class);
                    if (movie != null && movie.getMovieId() == null) {
                        movie.setMovieId(snapshot.getId());
                    }
                    callback.onSuccess(movie);
                })
                .addOnFailureListener(e -> callback.onError("Unable to load movie details."));
    }

    public void getActiveTheaters(DataCallback<List<Theater>> callback) {
        FirebaseManager.getFirestore()
                .collection("theaters")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Theater> theaters = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        Theater theater = document.toObject(Theater.class);
                        if (theater.getTheaterId() == null) {
                            theater.setTheaterId(document.getId());
                        }
                        if (theater.isActive() || theater.getName() != null) {
                            theaters.add(theater);
                        }
                    }
                    Collections.sort(theaters, Comparator.comparing(Theater::getName, Comparator.nullsLast(String::compareToIgnoreCase)));
                    callback.onSuccess(theaters);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unable to load theaters." : e.getMessage()));
    }

    public void getShowtimesByMovie(String movieId, DataCallback<List<Showtime>> callback) {
        FirebaseManager.getFirestore()
                .collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Showtime> showtimes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        Showtime showtime = document.toObject(Showtime.class);
                        if (showtime.getShowtimeId() == null) {
                            showtime.setShowtimeId(document.getId());
                        }
                        showtimes.add(showtime);
                    }
                    sortShowtimes(showtimes);
                    callback.onSuccess(showtimes);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unable to load showtimes." : e.getMessage()));
    }

    public void getShowtimesByMovieAndTheater(String movieId, String theaterId, DataCallback<List<Showtime>> callback) {
        FirebaseManager.getFirestore()
                .collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("theaterId", theaterId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Showtime> showtimes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        Showtime showtime = document.toObject(Showtime.class);
                        if (showtime.getShowtimeId() == null) {
                            showtime.setShowtimeId(document.getId());
                        }
                        showtimes.add(showtime);
                    }
                    sortShowtimes(showtimes);
                    callback.onSuccess(showtimes);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unable to load showtimes." : e.getMessage()));
    }

    public List<Showtime> filterShowtimesByDate(List<Showtime> source, String dateKey) {
        List<Showtime> filtered = new ArrayList<>();
        if (source == null) {
            return filtered;
        }
        for (Showtime showtime : source) {
            if (com.movieticketapp.utils.DateTimeUtils.toDateKey(showtime.getStartTime()).equals(dateKey)) {
                filtered.add(showtime);
            }
        }
        sortShowtimes(filtered);
        return filtered;
    }

    private void sortShowtimes(List<Showtime> showtimes) {
        Collections.sort(showtimes, Comparator.comparing(Showtime::getStartTime, Comparator.nullsLast(String::compareTo)));
    }
}

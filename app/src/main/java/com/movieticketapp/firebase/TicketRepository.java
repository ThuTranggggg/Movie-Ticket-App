package com.movieticketapp.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.movieticketapp.models.Movie;
import com.movieticketapp.models.Showtime;
import com.movieticketapp.models.Theater;
import com.movieticketapp.models.Ticket;
import com.movieticketapp.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TicketRepository {
    private final FirebaseFirestore firestore = FirebaseManager.getFirestore();

    public void bookTicket(String userId, Movie movie, Theater theater, Showtime showtime, List<String> selectedSeats, DataCallback<String> callback) {
        DocumentReference showtimeRef = firestore.collection("showtimes").document(showtime.getShowtimeId());
        DocumentReference ticketRef = firestore.collection("tickets").document();
        DocumentReference userTicketRef = firestore.collection("users").document(userId).collection("tickets").document(ticketRef.getId());

        firestore.runTransaction((Transaction.Function<Void>) transaction -> {
            Showtime liveShowtime = transaction.get(showtimeRef).toObject(Showtime.class);
            if (liveShowtime == null) {
                throw new RuntimeException("Selected showtime no longer exists.");
            }
            List<String> bookedSeats = liveShowtime.getBookedSeats() == null ? new ArrayList<>() : new ArrayList<>(liveShowtime.getBookedSeats());
            for (String seat : selectedSeats) {
                if (bookedSeats.contains(seat)) {
                    throw new RuntimeException("Ghế này đã được đặt, vui lòng chọn ghế khác");
                }
            }

            bookedSeats.addAll(selectedSeats);
            List<String> availableSeats = liveShowtime.getAvailableSeats() == null ? new ArrayList<>() : new ArrayList<>(liveShowtime.getAvailableSeats());
            availableSeats.removeAll(selectedSeats);

            Ticket ticket = new Ticket(
                    ticketRef.getId(),
                    userId,
                    movie.getMovieId(),
                    theater.getTheaterId(),
                    showtime.getShowtimeId(),
                    selectedSeats,
                    selectedSeats.size(),
                    selectedSeats.size() * showtime.getPrice(),
                    DateTimeUtils.toIsoString(new Date()),
                    "Confirmed",
                    movie.getTitle(),
                    theater.getName(),
                    DateTimeUtils.formatDateTime(showtime.getStartTime()),
                    showtime.getStartTime(),
                    movie.getPosterUrl()
            );

            transaction.update(showtimeRef, "bookedSeats", bookedSeats, "availableSeats", availableSeats);
            transaction.set(ticketRef, ticket.toMap());
            transaction.set(userTicketRef, ticket.toMap());
            return null;
        }).addOnSuccessListener(unused -> callback.onSuccess(ticketRef.getId()))
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unable to book ticket." : e.getMessage()));
    }

    public void getTicketsForUser(String userId, DataCallback<List<Ticket>> callback) {
        firestore.collection("tickets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Ticket> tickets = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshot) {
                        Ticket ticket = document.toObject(Ticket.class);
                        if (ticket.getTicketId() == null) {
                            ticket.setTicketId(document.getId());
                        }
                        tickets.add(ticket);
                    }
                    Collections.sort(tickets, Comparator.comparing(Ticket::getBookingTime, Comparator.nullsLast(String::compareTo)).reversed());
                    callback.onSuccess(tickets);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage() == null ? "Unable to load tickets." : e.getMessage()));
    }

    public void getTicketById(String ticketId, DataCallback<Ticket> callback) {
        firestore.collection("tickets")
                .document(ticketId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Ticket ticket = snapshot.toObject(Ticket.class);
                    if (ticket != null && ticket.getTicketId() == null) {
                        ticket.setTicketId(snapshot.getId());
                    }
                    callback.onSuccess(ticket);
                })
                .addOnFailureListener(e -> callback.onError("Unable to load ticket details."));
    }

    @NonNull
    public static String statusLabel(String status) {
        return status == null ? "Pending" : status;
    }
}

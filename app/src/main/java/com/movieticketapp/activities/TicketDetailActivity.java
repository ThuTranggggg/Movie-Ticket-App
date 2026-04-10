package com.movieticketapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.movieticketapp.R;
import com.movieticketapp.firebase.DataCallback;
import com.movieticketapp.firebase.TicketRepository;
import com.movieticketapp.models.Ticket;
import com.movieticketapp.utils.DateTimeUtils;
import com.movieticketapp.utils.IntentKeys;
import com.movieticketapp.utils.PriceUtils;
import com.movieticketapp.utils.UiUtils;

public class TicketDetailActivity extends AppCompatActivity {
    private final TicketRepository ticketRepository = new TicketRepository();
    private boolean showSuccessBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        showSuccessBanner = getIntent().getBooleanExtra(IntentKeys.EXTRA_SHOW_SUCCESS, false);
        findViewById(R.id.layoutSuccessBanner).setVisibility(showSuccessBanner ? android.view.View.VISIBLE : android.view.View.GONE);

        findViewById(R.id.btnMyTickets).setOnClickListener(v -> startActivity(new Intent(this, MyTicketsActivity.class)));
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        });

        String ticketId = getIntent().getStringExtra(IntentKeys.EXTRA_TICKET_ID);
        if (ticketId == null || ticketId.trim().isEmpty()) {
            UiUtils.showSnack(findViewById(android.R.id.content), getString(R.string.error_generic));
            finish();
            return;
        }

        loadTicket(ticketId);
    }

    private void loadTicket(String ticketId) {
        ticketRepository.getTicketById(ticketId, new DataCallback<>() {
            @Override
            public void onSuccess(Ticket data) {
                if (data == null) {
                    UiUtils.showSnack(findViewById(android.R.id.content), getString(R.string.error_generic));
                    finish();
                    return;
                }
                bindTicket(data);
            }

            @Override
            public void onError(String message) {
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void bindTicket(Ticket ticket) {
        ((TextView) findViewById(R.id.txtMovieTitle)).setText(ticket.getMovieTitle());
        ((TextView) findViewById(R.id.txtTicketStatus)).setText(getString(R.string.ticket_status_format, ticket.getStatus()));
        ((TextView) findViewById(R.id.txtBookingId)).setText(getString(R.string.booking_id) + ": " + ticket.getTicketId());
        ((TextView) findViewById(R.id.txtTheater)).setText(getString(R.string.theater) + ": " + ticket.getTheaterName());
        ((TextView) findViewById(R.id.txtShowtime)).setText(getString(R.string.showtime) + ": " + ticket.getShowtimeLabel());
        ((TextView) findViewById(R.id.txtSeats)).setText(getString(R.string.seats) + ": " + TextUtils.join(", ", ticket.getSeatNumbers()));
        ((TextView) findViewById(R.id.txtQuantity)).setText(getString(R.string.quantity) + ": " + ticket.getQuantity());
        ((TextView) findViewById(R.id.txtTotalPrice)).setText(getString(R.string.price_value, PriceUtils.formatPrice(ticket.getTotalPrice())));

        if (showSuccessBanner) {
            String formattedShowtime = DateTimeUtils.formatDateTime(ticket.getShowtimeStartTime());
            if (formattedShowtime == null || formattedShowtime.trim().isEmpty()) {
                formattedShowtime = ticket.getShowtimeLabel();
            }
            ((TextView) findViewById(R.id.txtSuccessDescription)).setText(
                    getString(R.string.success_description_with_showtime, formattedShowtime)
            );
        }
    }
}

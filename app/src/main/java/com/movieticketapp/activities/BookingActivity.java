package com.movieticketapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.movieticketapp.R;
import com.movieticketapp.adapters.DateOptionAdapter;
import com.movieticketapp.adapters.SeatAdapter;
import com.movieticketapp.adapters.ShowtimeAdapter;
import com.movieticketapp.firebase.AuthRepository;
import com.movieticketapp.firebase.DataCallback;
import com.movieticketapp.firebase.MovieRepository;
import com.movieticketapp.firebase.TicketRepository;
import com.movieticketapp.models.DateOption;
import com.movieticketapp.models.Movie;
import com.movieticketapp.models.Showtime;
import com.movieticketapp.models.Theater;
import com.movieticketapp.notifications.NotificationHelper;
import com.movieticketapp.utils.DateTimeUtils;
import com.movieticketapp.utils.IntentKeys;
import com.movieticketapp.utils.PriceUtils;
import com.movieticketapp.utils.UiUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BookingActivity extends AppCompatActivity {
    private final MovieRepository movieRepository = new MovieRepository();
    private final TicketRepository ticketRepository = new TicketRepository();
    private final AuthRepository authRepository = new AuthRepository();
    private final List<Theater> theaters = new ArrayList<>();
    private final List<Showtime> allMovieShowtimes = new ArrayList<>();
    private final List<DateOption> dateOptions = new ArrayList<>();
    private Movie selectedMovie;
    private Theater selectedTheater;
    private Showtime selectedShowtime;
    private String selectedDateKey;
    private SeatAdapter seatAdapter;
    private ShowtimeAdapter showtimeAdapter;
    private DateOptionAdapter dateOptionAdapter;
    private View loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        loadingOverlay = findViewById(R.id.loadingOverlay);

        setupDateList();
        setupShowtimeList();
        setupSeatGrid();
        findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> confirmBooking());
        setupTheaterDropdown();
        loadData();
    }

    private void setupDateList() {
        androidx.recyclerview.widget.RecyclerView recyclerDates = findViewById(R.id.recyclerDates);
        recyclerDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateOptionAdapter = new DateOptionAdapter(option -> {
            selectedDateKey = option.getDateKey();
            dateOptionAdapter.setSelectedDateKey(selectedDateKey);
            ((TextView) findViewById(R.id.edtSelectedDate)).setText(selectedDateKey);
            refreshShowtimesForFilters();
        });
        recyclerDates.setAdapter(dateOptionAdapter);
    }

    private void setupShowtimeList() {
        androidx.recyclerview.widget.RecyclerView recyclerShowtimes = findViewById(R.id.recyclerShowtimes);
        recyclerShowtimes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        showtimeAdapter = new ShowtimeAdapter(showtime -> {
            selectedShowtime = showtime;
            showtimeAdapter.setSelectedShowtimeId(showtime.getShowtimeId());
            seatAdapter.submitData(showtime.getAvailableSeats(), showtime.getBookedSeats());
            findViewById(R.id.recyclerSeats).requestLayout();
            updateSummary();
        });
        recyclerShowtimes.setAdapter(showtimeAdapter);
    }

    private void setupSeatGrid() {
        androidx.recyclerview.widget.RecyclerView recyclerSeats = findViewById(R.id.recyclerSeats);
        recyclerSeats.setLayoutManager(new GridLayoutManager(this, 6));
        recyclerSeats.setNestedScrollingEnabled(false);
        seatAdapter = new SeatAdapter(new SeatAdapter.OnSeatChangeListener() {
            @Override
            public void onSelectionChanged(List<String> selectedSeats) {
                updateSummary();
            }

            @Override
            public void onSeatUnavailable() {
                UiUtils.showSnack(findViewById(android.R.id.content), getString(R.string.validation_seat_taken));
            }
        });
        recyclerSeats.setAdapter(seatAdapter);
    }

    private void setupTheaterDropdown() {
        AutoCompleteTextView autoTheater = findViewById(R.id.autoTheater);
        autoTheater.setOnItemClickListener((parent, view, position, id) -> {
            selectedTheater = theaters.get(position);
            ((TextInputLayout) findViewById(R.id.layoutTheater)).setError(null);
            refreshShowtimesForFilters();
        });
    }

    private void loadData() {
        String movieId = getIntent().getStringExtra(IntentKeys.EXTRA_MOVIE_ID);
        selectedDateKey = getIntent().getStringExtra(IntentKeys.EXTRA_SELECTED_DATE);
        setLoading(true);
        movieRepository.getMovieById(movieId, new DataCallback<>() {
            @Override
            public void onSuccess(Movie data) {
                if (data == null) {
                    setLoading(false);
                    UiUtils.showSnack(findViewById(android.R.id.content), getString(R.string.error_generic));
                    finish();
                    return;
                }
                selectedMovie = data;
                bindMovie(data);
                loadTheatersAndShowtimes();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void bindMovie(Movie movie) {
        ((TextView) findViewById(R.id.txtMovieTitle)).setText(movie.getTitle());
        ((TextView) findViewById(R.id.txtMovieMeta)).setText(getString(R.string.genre_runtime, movie.getGenre(), movie.getDuration() + " min"));
        ((TextView) findViewById(R.id.txtMovieRating)).setText(getString(R.string.rating_value, movie.getRating()));
        Glide.with(this)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.bg_poster_placeholder)
                .error(R.drawable.bg_poster_placeholder)
                .into((ImageView) findViewById(R.id.imgMoviePoster));
    }

    private void loadTheatersAndShowtimes() {
        movieRepository.getActiveTheaters(new DataCallback<>() {
            @Override
            public void onSuccess(List<Theater> theaterData) {
                theaters.clear();
                theaters.addAll(theaterData);
                bindTheaters();
                movieRepository.getShowtimesByMovie(selectedMovie.getMovieId(), new DataCallback<>() {
                    @Override
                    public void onSuccess(List<Showtime> showtimeData) {
                        setLoading(false);
                        allMovieShowtimes.clear();
                        allMovieShowtimes.addAll(showtimeData);
                        buildDateOptions();
                        if (!theaters.isEmpty()) {
                            selectTheater(0);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        setLoading(false);
                        UiUtils.showSnack(findViewById(android.R.id.content), message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void bindTheaters() {
        List<String> labels = new ArrayList<>();
        for (Theater theater : theaters) {
            labels.add(theater.getName() + " • " + theater.getCity());
        }
        AutoCompleteTextView autoTheater = findViewById(R.id.autoTheater);
        autoTheater.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, labels));
    }

    private void buildDateOptions() {
        Set<String> availableDates = new LinkedHashSet<>();
        for (Showtime showtime : allMovieShowtimes) {
            availableDates.add(DateTimeUtils.toDateKey(showtime.getStartTime()));
        }

        dateOptions.clear();
        for (java.util.Date date : DateTimeUtils.getNextSevenDays()) {
            String dateKey = DateTimeUtils.toDateKey(date);
            dateOptions.add(new DateOption(dateKey, DateTimeUtils.formatDayLabel(date), DateTimeUtils.formatDayNumber(date), availableDates.contains(dateKey)));
        }

        boolean found = false;
        for (DateOption option : dateOptions) {
            if (option.getDateKey().equals(selectedDateKey) && option.isEnabled()) {
                found = true;
                break;
            }
        }
        if (!found) {
            for (DateOption option : dateOptions) {
                if (option.isEnabled()) {
                    selectedDateKey = option.getDateKey();
                    break;
                }
            }
        }

        dateOptionAdapter.submitList(dateOptions, selectedDateKey);
        ((TextView) findViewById(R.id.edtSelectedDate)).setText(selectedDateKey);
    }

    private void selectTheater(int index) {
        selectedTheater = theaters.get(index);
        AutoCompleteTextView autoTheater = findViewById(R.id.autoTheater);
        autoTheater.setText(autoTheater.getAdapter().getItem(index).toString(), false);
        refreshShowtimesForFilters();
    }

    private void refreshShowtimesForFilters() {
        selectedShowtime = null;
        showtimeAdapter.setSelectedShowtimeId(null);
        seatAdapter.submitData(new ArrayList<>(), new ArrayList<>());

        List<Showtime> filtered = new ArrayList<>();
        for (Showtime showtime : allMovieShowtimes) {
            if (selectedTheater != null
                    && selectedTheater.getTheaterId().equals(showtime.getTheaterId())
                    && DateTimeUtils.toDateKey(showtime.getStartTime()).equals(selectedDateKey)) {
                filtered.add(showtime);
            }
        }

        showtimeAdapter.submitList(filtered);
        ((TextView) findViewById(R.id.txtShowtimeState)).setText(filtered.isEmpty()
                ? getString(R.string.showtimes_unavailable_label)
                : getString(R.string.showtimes_available_label, filtered.size()));

        if (!filtered.isEmpty()) {
            selectedShowtime = filtered.get(0);
            showtimeAdapter.setSelectedShowtimeId(selectedShowtime.getShowtimeId());
            seatAdapter.submitData(selectedShowtime.getAvailableSeats(), selectedShowtime.getBookedSeats());
            findViewById(R.id.recyclerSeats).requestLayout();
        }
        updateSummary();
    }

    private void updateSummary() {
        List<String> selectedSeats = seatAdapter.getSelectedSeats();
        ((TextView) findViewById(R.id.txtSelectedShowtime)).setText(
                selectedShowtime == null
                        ? getString(R.string.showtime) + ": " + getString(R.string.unknown_value)
                        : getString(R.string.showtime) + ": " + DateTimeUtils.formatDateTime(selectedShowtime.getStartTime())
        );
        ((TextView) findViewById(R.id.txtSelectedSeats)).setText(getString(R.string.selected_seats_label,
                selectedSeats.isEmpty() ? getString(R.string.unknown_value) : TextUtils.join(", ", selectedSeats)));
        ((TextView) findViewById(R.id.txtQuantity)).setText(getString(R.string.quantity_value, selectedSeats.size()));
        double total = selectedShowtime == null ? 0 : selectedShowtime.getPrice() * selectedSeats.size();
        ((TextView) findViewById(R.id.txtTotalPrice)).setText(getString(R.string.price_value, PriceUtils.formatPrice(total)));
    }

    private void confirmBooking() {
        TextInputLayout layoutTheater = findViewById(R.id.layoutTheater);
        layoutTheater.setError(null);
        if (selectedTheater == null) {
            layoutTheater.setError(getString(R.string.validation_theater_required));
            return;
        }
        if (selectedShowtime == null) {
            UiUtils.showSnack(findViewById(android.R.id.content), getString(R.string.validation_showtime_required));
            return;
        }
        List<String> selectedSeats = seatAdapter.getSelectedSeats();
        if (selectedSeats.isEmpty()) {
            UiUtils.showSnack(findViewById(android.R.id.content), getString(R.string.validation_seat_required));
            return;
        }
        if (authRepository.getCurrentFirebaseUser() == null) {
            UiUtils.showSnack(findViewById(android.R.id.content), getString(R.string.error_generic));
            return;
        }

        setLoading(true);
        ticketRepository.bookTicket(authRepository.getCurrentFirebaseUser().getUid(), selectedMovie, selectedTheater, selectedShowtime, selectedSeats, new DataCallback<>() {
            @Override
            public void onSuccess(String ticketId) {
                setLoading(false);
                NotificationHelper.scheduleReminder(BookingActivity.this, ticketId, selectedMovie.getTitle(), selectedShowtime.getStartTime());
                UiUtils.showToast(BookingActivity.this, getString(R.string.booking_success_message));
                android.content.Intent intent = new android.content.Intent(BookingActivity.this, TicketDetailActivity.class);
                intent.putExtra(IntentKeys.EXTRA_TICKET_ID, ticketId);
                intent.putExtra(IntentKeys.EXTRA_SHOW_SUCCESS, true);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void setLoading(boolean loading) {
        loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnConfirmBooking).setEnabled(!loading);
    }
}

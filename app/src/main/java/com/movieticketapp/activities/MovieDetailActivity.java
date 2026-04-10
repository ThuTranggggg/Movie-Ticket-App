package com.movieticketapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.movieticketapp.R;
import com.movieticketapp.adapters.DateOptionAdapter;
import com.movieticketapp.firebase.DataCallback;
import com.movieticketapp.firebase.MovieRepository;
import com.movieticketapp.models.DateOption;
import com.movieticketapp.models.Movie;
import com.movieticketapp.models.Showtime;
import com.movieticketapp.utils.DateTimeUtils;
import com.movieticketapp.utils.IntentKeys;
import com.movieticketapp.utils.UiUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MovieDetailActivity extends AppCompatActivity {
    private final MovieRepository movieRepository = new MovieRepository();
    private String movieId;
    private View loadingOverlay;
    private DateOptionAdapter dateOptionAdapter;
    private String selectedDateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieId = getIntent().getStringExtra(IntentKeys.EXTRA_MOVIE_ID);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        androidx.recyclerview.widget.RecyclerView recyclerDates = findViewById(R.id.recyclerDates);
        recyclerDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateOptionAdapter = new DateOptionAdapter(option -> {
            selectedDateKey = option.getDateKey();
            dateOptionAdapter.setSelectedDateKey(selectedDateKey);
            ((TextView) findViewById(R.id.txtDateSelectionHint)).setText(getString(R.string.selected_date_label, option.getDateKey()));
        });
        recyclerDates.setAdapter(dateOptionAdapter);

        findViewById(R.id.btnBookNow).setOnClickListener(v -> openBooking());

        loadMovieDetails();
    }

    private void loadMovieDetails() {
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
                bindMovie(data);
                loadAvailableDates();
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
        ((TextView) findViewById(R.id.txtGenreDuration)).setText(getString(R.string.genre_runtime, movie.getGenre(), movie.getDuration() + " min"));
        ((TextView) findViewById(R.id.txtRating)).setText(getString(R.string.rating_value, movie.getRating()));
        ((TextView) findViewById(R.id.txtReleaseDate)).setText(getString(R.string.release_date, DateTimeUtils.formatDate(movie.getReleaseDate())));
        ((TextView) findViewById(R.id.txtDescription)).setText(movie.getDescription());
        Glide.with(this)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.bg_poster_placeholder)
                .error(R.drawable.bg_poster_placeholder)
                .into((ImageView) findViewById(R.id.imgPoster));
    }

    private void loadAvailableDates() {
        movieRepository.getShowtimesByMovie(movieId, new DataCallback<>() {
            @Override
            public void onSuccess(List<Showtime> data) {
                setLoading(false);
                buildDateOptions(data);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void buildDateOptions(List<Showtime> showtimes) {
        Set<String> availableDates = new LinkedHashSet<>();
        for (Showtime showtime : showtimes) {
            availableDates.add(DateTimeUtils.toDateKey(showtime.getStartTime()));
        }

        List<DateOption> options = new ArrayList<>();
        for (java.util.Date date : DateTimeUtils.getNextSevenDays()) {
            String dateKey = DateTimeUtils.toDateKey(date);
            options.add(new DateOption(dateKey, DateTimeUtils.formatDayLabel(date), DateTimeUtils.formatDayNumber(date), availableDates.contains(dateKey)));
        }

        for (DateOption option : options) {
            if (option.isEnabled()) {
                selectedDateKey = option.getDateKey();
                break;
            }
        }
        if (selectedDateKey == null && !options.isEmpty()) {
            selectedDateKey = options.get(0).getDateKey();
        }
        dateOptionAdapter.submitList(options, selectedDateKey);
        ((TextView) findViewById(R.id.txtDateSelectionHint)).setText(getString(R.string.selected_date_label, selectedDateKey));
    }

    private void openBooking() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra(IntentKeys.EXTRA_MOVIE_ID, movieId);
        intent.putExtra(IntentKeys.EXTRA_SELECTED_DATE, selectedDateKey);
        startActivity(intent);
    }

    private void setLoading(boolean loading) {
        loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}

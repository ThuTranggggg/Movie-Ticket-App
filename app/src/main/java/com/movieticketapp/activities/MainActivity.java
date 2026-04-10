package com.movieticketapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.movieticketapp.R;
import com.movieticketapp.adapters.MovieAdapter;
import com.movieticketapp.firebase.AuthRepository;
import com.movieticketapp.firebase.DataCallback;
import com.movieticketapp.firebase.FirestoreSeeder;
import com.movieticketapp.firebase.MovieRepository;
import com.movieticketapp.models.Movie;
import com.movieticketapp.models.User;
import com.movieticketapp.notifications.NotificationHelper;
import com.movieticketapp.utils.IntentKeys;
import com.movieticketapp.utils.UiUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {
    private final MovieRepository movieRepository = new MovieRepository();
    private final AuthRepository authRepository = new AuthRepository();
    private final FirestoreSeeder firestoreSeeder = new FirestoreSeeder();
    private MovieAdapter movieAdapter;
    private View loadingOverlay;
    private View layoutEmpty;
    private View txtStateMessage;
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private boolean attemptedAutoSeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        loadingOverlay = findViewById(R.id.loadingOverlay);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        txtStateMessage = findViewById(R.id.txtStateMessage);

        androidx.recyclerview.widget.RecyclerView recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerMovies.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this);
        recyclerMovies.setAdapter(movieAdapter);

        ((androidx.swiperefreshlayout.widget.SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setOnRefreshListener(this::loadMovies);

        com.google.android.material.textfield.TextInputEditText edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                movieAdapter.filter(String.valueOf(s));
                updateEmptyState(movieAdapter.getFilteredCount() == 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        notificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        });

        loadUserGreeting();
        requestNotificationPermissionIfNeeded();
        loadMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetSearchAndShowAll();
    }

    private void loadUserGreeting() {
        authRepository.getCurrentUserProfile(new DataCallback<>() {
            @Override
            public void onSuccess(User data) {
                String name = data != null && data.getFullName() != null && !data.getFullName().trim().isEmpty()
                        ? data.getFullName()
                        : getString(R.string.anonymous_user);
                ((android.widget.TextView) findViewById(R.id.txtGreeting)).setText(getString(R.string.greeting_user, name));
            }

            @Override
            public void onError(String message) {
                ((android.widget.TextView) findViewById(R.id.txtGreeting)).setText(getString(R.string.greeting_user, getString(R.string.anonymous_user)));
            }
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        NotificationHelper.createNotificationChannel(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void loadMovies() {
        setLoading(true);
        movieRepository.getActiveMovies(new DataCallback<>() {
            @Override
            public void onSuccess(List<Movie> data) {
                setLoading(false);
                ((androidx.swiperefreshlayout.widget.SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
                if (data.size() < 6 && !attemptedAutoSeed) {
                    attemptedAutoSeed = true;
                    autoSeedAndReload();
                    return;
                }
                movieAdapter.submitList(data);
                resetSearchAndShowAll();
                ((android.widget.TextView) txtStateMessage).setText(data.isEmpty()
                        ? getString(R.string.empty_movies_message)
                        : getString(R.string.movies_available_label, movieAdapter.getFilteredCount()));
                updateEmptyState(movieAdapter.getFilteredCount() == 0);
                findViewById(R.id.recyclerMovies).requestLayout();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                ((androidx.swiperefreshlayout.widget.SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout)).setRefreshing(false);
                ((android.widget.TextView) txtStateMessage).setText(getString(R.string.error_load_movies));
                updateEmptyState(true);
                UiUtils.showSnack(findViewById(android.R.id.content), message);
            }
        });
    }

    private void updateEmptyState(boolean empty) {
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void resetSearchAndShowAll() {
        com.google.android.material.textfield.TextInputEditText edtSearch = findViewById(R.id.edtSearch);
        if (edtSearch.getText() != null && edtSearch.getText().length() > 0) {
            edtSearch.setText("");
        }
        movieAdapter.filter("");
    }

    private void autoSeedAndReload() {
        ((android.widget.TextView) txtStateMessage).setText("Syncing full movie catalog...");
        setLoading(true);
        firestoreSeeder.seedDemoData(new DataCallback<>() {
            @Override
            public void onSuccess(String data) {
                loadMovies();
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
    }

    private boolean onMenuItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_tickets) {
            startActivity(new Intent(this, MyTicketsActivity.class));
            return true;
        }
        if (itemId == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (itemId == R.id.action_logout) {
            authRepository.logout();
            UiUtils.showToast(this, getString(R.string.logout_success));
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
            return true;
        }
        return false;
    }

    @Override
    public void onMovieClicked(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(IntentKeys.EXTRA_MOVIE_ID, movie.getMovieId());
        startActivity(intent);
    }

    @Override
    public void onBookClicked(Movie movie) {
        onMovieClicked(movie);
    }
}

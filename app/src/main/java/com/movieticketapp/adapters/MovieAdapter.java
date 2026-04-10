package com.movieticketapp.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.movieticketapp.R;
import com.movieticketapp.models.Movie;
import com.movieticketapp.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final List<Movie> originalList = new ArrayList<>();
    private final List<Movie> filteredList = new ArrayList<>();
    private final OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClicked(Movie movie);

        void onBookClicked(Movie movie);
    }

    public MovieAdapter(OnMovieClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Movie> movies) {
        originalList.clear();
        filteredList.clear();
        if (movies != null) {
            originalList.addAll(movies);
            filteredList.addAll(movies);
        }
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(originalList);
        } else {
            String keyword = query.toLowerCase(Locale.getDefault()).trim();
            for (Movie movie : originalList) {
                String haystack = (movie.getTitle() + " " + movie.getGenre()).toLowerCase(Locale.getDefault());
                if (haystack.contains(keyword)) {
                    filteredList.add(movie);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getFilteredCount() {
        return filteredList.size();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = filteredList.get(position);
        holder.txtTitle.setText(movie.getTitle());
        holder.txtGenreDuration.setText(holder.itemView.getContext().getString(R.string.genre_runtime, movie.getGenre(), movie.getDuration() + " min"));
        holder.txtRating.setText(holder.itemView.getContext().getString(R.string.rating_value, movie.getRating()));
        holder.txtDescription.setText(movie.getDescription());

        Glide.with(holder.imgPoster.getContext())
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.bg_poster_placeholder)
                .error(R.drawable.bg_poster_placeholder)
                .into(holder.imgPoster);

        holder.itemView.setOnClickListener(v -> listener.onMovieClicked(movie));
        holder.btnBook.setOnClickListener(v -> listener.onBookClicked(movie));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtTitle;
        TextView txtGenreDuration;
        TextView txtRating;
        TextView txtDescription;
        View btnBook;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtGenreDuration = itemView.findViewById(R.id.txtGenreDuration);
            txtRating = itemView.findViewById(R.id.txtRating);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}

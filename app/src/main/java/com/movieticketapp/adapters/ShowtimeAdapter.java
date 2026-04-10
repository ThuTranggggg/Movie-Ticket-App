package com.movieticketapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.movieticketapp.R;
import com.movieticketapp.models.Showtime;
import com.movieticketapp.utils.DateTimeUtils;
import com.movieticketapp.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {
    private final List<Showtime> items = new ArrayList<>();
    private final OnShowtimeClickListener listener;
    private String selectedShowtimeId;

    public interface OnShowtimeClickListener {
        void onShowtimeSelected(Showtime showtime);
    }

    public ShowtimeAdapter(OnShowtimeClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Showtime> showtimes) {
        items.clear();
        if (showtimes != null) {
            items.addAll(showtimes);
        }
        notifyDataSetChanged();
    }

    public void setSelectedShowtimeId(String selectedShowtimeId) {
        this.selectedShowtimeId = selectedShowtimeId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShowtimeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = items.get(position);
        holder.txtTime.setText(DateTimeUtils.formatTime(showtime.getStartTime()));
        holder.txtRoom.setText(showtime.getRoomName());
        holder.txtPrice.setText(holder.itemView.getContext().getString(R.string.price_value, PriceUtils.formatPrice(showtime.getPrice())));

        boolean selected = showtime.getShowtimeId() != null && showtime.getShowtimeId().equals(selectedShowtimeId);
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), selected ? R.color.color_primary : R.color.color_surface_alt));
        holder.cardView.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), selected ? R.color.color_secondary : R.color.color_stroke));
        int textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.color_text_primary);
        int secondaryColor = ContextCompat.getColor(holder.itemView.getContext(), selected ? R.color.color_white : R.color.color_text_secondary);
        holder.txtTime.setTextColor(textColor);
        holder.txtRoom.setTextColor(secondaryColor);
        holder.txtPrice.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), selected ? R.color.color_white : R.color.color_secondary));

        holder.itemView.setOnClickListener(v -> {
            selectedShowtimeId = showtime.getShowtimeId();
            notifyDataSetChanged();
            listener.onShowtimeSelected(showtime);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView txtTime;
        TextView txtRoom;
        TextView txtPrice;

        ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            txtTime = itemView.findViewById(R.id.txtTime);
            txtRoom = itemView.findViewById(R.id.txtRoom);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}

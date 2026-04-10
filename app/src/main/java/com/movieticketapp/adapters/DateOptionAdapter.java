package com.movieticketapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.movieticketapp.R;
import com.movieticketapp.models.DateOption;

import java.util.ArrayList;
import java.util.List;

public class DateOptionAdapter extends RecyclerView.Adapter<DateOptionAdapter.DateViewHolder> {
    private final List<DateOption> items = new ArrayList<>();
    private final OnDateClickListener listener;
    private String selectedDateKey;

    public interface OnDateClickListener {
        void onDateSelected(DateOption option);
    }

    public DateOptionAdapter(OnDateClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<DateOption> data, String selectedKey) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        this.selectedDateKey = selectedKey;
        notifyDataSetChanged();
    }

    public void setSelectedDateKey(String selectedDateKey) {
        this.selectedDateKey = selectedDateKey;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_option, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateOption option = items.get(position);
        boolean selected = option.getDateKey().equals(selectedDateKey);
        boolean enabled = option.isEnabled();

        holder.txtDayLabel.setText(option.getDayLabel());
        holder.txtDayNumber.setText(option.getDayNumber());
        holder.itemView.setAlpha(enabled ? 1f : 0.45f);
        holder.itemView.setEnabled(enabled);

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                selected ? R.color.color_primary : R.color.color_surface_alt));
        holder.cardView.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(),
                selected ? R.color.color_secondary : R.color.color_stroke));
        holder.txtDayLabel.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                selected ? R.color.color_white : R.color.color_text_secondary));
        holder.txtDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                selected ? R.color.color_white : R.color.color_text_primary));

        holder.itemView.setOnClickListener(v -> {
            if (!enabled) {
                return;
            }
            selectedDateKey = option.getDateKey();
            notifyDataSetChanged();
            listener.onDateSelected(option);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView txtDayLabel;
        TextView txtDayNumber;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            txtDayLabel = itemView.findViewById(R.id.txtDayLabel);
            txtDayNumber = itemView.findViewById(R.id.txtDayNumber);
        }
    }
}

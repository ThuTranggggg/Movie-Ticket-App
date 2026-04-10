package com.movieticketapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movieticketapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {
    private final List<String> seats = new ArrayList<>();
    private final Set<String> bookedSeats = new HashSet<>();
    private final Set<String> selectedSeats = new HashSet<>();
    private final OnSeatChangeListener listener;

    public interface OnSeatChangeListener {
        void onSelectionChanged(List<String> selectedSeats);

        void onSeatUnavailable();
    }

    public SeatAdapter(OnSeatChangeListener listener) {
        this.listener = listener;
    }

    public void submitData(List<String> availableSeats, List<String> booked) {
        seats.clear();
        bookedSeats.clear();
        selectedSeats.clear();
        if (availableSeats != null) {
            seats.addAll(availableSeats);
        }
        if (booked != null) {
            bookedSeats.addAll(booked);
            for (String seat : booked) {
                if (!seats.contains(seat)) {
                    seats.add(seat);
                }
            }
        }
        notifyDataSetChanged();
        listener.onSelectionChanged(new ArrayList<>(selectedSeats));
    }

    public List<String> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        String seat = seats.get(position);
        holder.txtSeat.setText(seat);

        boolean booked = bookedSeats.contains(seat);
        boolean selected = selectedSeats.contains(seat);
        holder.txtSeat.setBackgroundResource(booked ? R.drawable.bg_seat_booked : selected ? R.drawable.bg_seat_selected : R.drawable.bg_seat_available);

        holder.itemView.setOnClickListener(v -> {
            if (bookedSeats.contains(seat)) {
                listener.onSeatUnavailable();
                return;
            }
            if (selectedSeats.contains(seat)) {
                selectedSeats.remove(seat);
            } else {
                selectedSeats.add(seat);
            }
            notifyItemChanged(position);
            listener.onSelectionChanged(new ArrayList<>(selectedSeats));
        });
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView txtSeat;

        SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSeat = itemView.findViewById(R.id.txtSeat);
        }
    }
}

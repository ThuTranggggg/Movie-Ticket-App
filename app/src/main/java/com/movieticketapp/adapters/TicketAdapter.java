package com.movieticketapp.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movieticketapp.R;
import com.movieticketapp.models.Ticket;
import com.movieticketapp.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private final List<Ticket> tickets = new ArrayList<>();
    private final OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    public TicketAdapter(OnTicketClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Ticket> data) {
        tickets.clear();
        if (data != null) {
            tickets.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TicketViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.txtMovieTitle.setText(ticket.getMovieTitle());
        holder.txtStatus.setText(holder.itemView.getContext().getString(R.string.ticket_status_format, ticket.getStatus()));
        holder.txtTheater.setText(holder.itemView.getContext().getString(R.string.theater) + ": " + ticket.getTheaterName());
        holder.txtShowtime.setText(holder.itemView.getContext().getString(R.string.showtime) + ": " + ticket.getShowtimeLabel());
        holder.txtSeats.setText(holder.itemView.getContext().getString(R.string.seats) + ": " + TextUtils.join(", ", ticket.getSeatNumbers()));
        holder.txtBookingCode.setText(holder.itemView.getContext().getString(R.string.booking_id) + ": " + ticket.getTicketId());
        holder.txtTotalPrice.setText(holder.itemView.getContext().getString(R.string.price_value, PriceUtils.formatPrice(ticket.getTotalPrice())));
        holder.itemView.setOnClickListener(v -> listener.onTicketClick(ticket));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView txtMovieTitle;
        TextView txtStatus;
        TextView txtTheater;
        TextView txtShowtime;
        TextView txtSeats;
        TextView txtBookingCode;
        TextView txtTotalPrice;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMovieTitle = itemView.findViewById(R.id.txtMovieTitle);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTheater = itemView.findViewById(R.id.txtTheater);
            txtShowtime = itemView.findViewById(R.id.txtShowtime);
            txtSeats = itemView.findViewById(R.id.txtSeats);
            txtBookingCode = itemView.findViewById(R.id.txtBookingCode);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
        }
    }
}

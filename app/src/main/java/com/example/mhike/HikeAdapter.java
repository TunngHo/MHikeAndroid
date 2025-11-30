package com.example.mhike;

import android.content.Intent; // <–
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeVH> {

    private List<Hike> hikes = new ArrayList<>();
    private OnItemActionListener listener;

    public interface OnItemClickListener {
        void onItemClicked(Hike h);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    public interface OnItemActionListener {
        void onEditClicked(Hike h);
        void onDeleteClicked(Hike h);
    }

    public void setListener(OnItemActionListener l) { this.listener = l; }

    public void setHikes(List<Hike> list) {
        this.hikes = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HikeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hike, parent, false);
        return new HikeVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HikeVH holder, int position) {
        Hike h = hikes.get(position);
        holder.txtName.setText(h.getName());
        holder.txtDate.setText(h.getDate());
        holder.txtLocation.setText(h.getLocation());

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClicked(h);
            }
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClicked(h);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClicked(h);
        });

        holder.btnObservations.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ObservationListActivity.class);
            i.putExtra("HIKE_ID", h.getId());
            v.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return hikes.size();
    }

    static class HikeVH extends RecyclerView.ViewHolder {
        TextView txtName, txtDate, txtLocation;
        ImageButton btnEdit, btnDelete, btnObservations;

        HikeVH(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.itemName);
            txtDate = itemView.findViewById(R.id.itemDate);
            txtLocation = itemView.findViewById(R.id.itemLocation);
            btnEdit = itemView.findViewById(R.id.itemEdit);
            btnDelete = itemView.findViewById(R.id.itemDelete);
            btnObservations = itemView.findViewById(R.id.itemObservations);
        }
    }
}

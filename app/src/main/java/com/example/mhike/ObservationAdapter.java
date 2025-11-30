package com.example.mhike;

import android.content.Context; // Thêm import này
import android.content.Intent;   // Thêm import này
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
import android.view.View;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObsVH> {

    private List<Observation> observations = new ArrayList<>();
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEditClicked(Observation o);
        void onDeleteClicked(Observation o);
    }

    public void setListener(OnItemActionListener l) { this.listener = l; }
    public void setObservations(List<Observation> list) { this.observations = list; notifyDataSetChanged(); }

    @NonNull
    @Override
    public ObsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_observation, parent, false);
        return new ObsVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ObsVH holder, int position) {
        Observation o = observations.get(position);
        holder.txtObservation.setText(o.getObservation());
        holder.txtTime.setText(o.getTime());
        holder.txtComments.setText(o.getComments());
        holder.btnEdit.setOnClickListener(v -> { if (listener!=null) listener.onEditClicked(o); });
        holder.btnDelete.setOnClickListener(v -> { if (listener!=null) listener.onDeleteClicked(o); });

        String uriString = o.getImageUri();
        if (uriString != null && !uriString.isEmpty()) {
            Uri imageUri = Uri.parse(uriString);
            holder.imgPreview.setImageURI(imageUri);
            holder.imgPreview.setVisibility(View.VISIBLE);

            holder.imgPreview.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(imageUri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    holder.itemView.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "No app to view images.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            holder.imgPreview.setVisibility(View.GONE);
            holder.imgPreview.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() { return observations.size(); }

    static class ObsVH extends RecyclerView.ViewHolder {
        TextView txtObservation, txtTime, txtComments;
        ImageButton btnEdit, btnDelete;
        ImageView imgPreview;
        ObsVH(@NonNull View itemView) {
            super(itemView);
            txtObservation = itemView.findViewById(R.id.itemObsText);
            txtTime = itemView.findViewById(R.id.itemObsTime);
            txtComments = itemView.findViewById(R.id.itemObsComments);
            btnEdit = itemView.findViewById(R.id.itemObsEdit);
            btnDelete = itemView.findViewById(R.id.itemObsDelete);
            imgPreview = itemView.findViewById(R.id.itemObsImage);
        }
    }
}

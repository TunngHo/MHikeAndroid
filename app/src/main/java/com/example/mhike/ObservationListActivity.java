package com.example.mhike;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class ObservationListActivity extends AppCompatActivity implements ObservationAdapter.OnItemActionListener {

    private RecyclerView rvObs;
    private ObservationAdapter adapter;
    private HikeDbHelper db;
    private long hikeId;
    private long oId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_list);

        rvObs = findViewById(R.id.recyclerObservations);
        rvObs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObservationAdapter();
        adapter.setListener(this);
        rvObs.setAdapter(adapter);

        db = new HikeDbHelper(this);
        hikeId = getIntent().getLongExtra("HIKE_ID", -1);
        oId = getIntent().getLongExtra("OBS_ID", -1);

        Button btnAdd = findViewById(R.id.btnAddObs);
        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddObservationActivity.class);
            i.putExtra("HIKE_ID", hikeId);
            startActivity(i);
        });

        Button btnBack = findViewById(R.id.btnBackToHikeList);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        loadData();
    }

    private void loadData() {
        List<Observation> list = db.getObservationsByHike(hikeId);
        adapter.setObservations(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onEditClicked(Observation o) {
        Intent i = new Intent(this, AddObservationActivity.class);
        i.putExtra("HIKE_ID", hikeId);
        i.putExtra("OBS_ID", o.getId());
        startActivity(i);
    }

    @Override
    public void onDeleteClicked(Observation o) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Observation")
                .setMessage("Delete this observation?")
                .setPositiveButton("Yes", (d, w) -> {
                    db.deleteObservation(o.getId());
                    loadData();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

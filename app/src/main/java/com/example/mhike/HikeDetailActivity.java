package com.example.mhike;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem; // Thêm import này
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Thêm import này
import androidx.appcompat.app.AppCompatActivity;

public class HikeDetailActivity extends AppCompatActivity {

    private TextView txtName, txtLocation, txtDate, txtParking, txtLength, txtDifficulty, txtWeather, txtEquipment, txtDescription;
    private Button btnViewObservations;
    private Button btnEditHike;
    private Button btnBackToList;
    private HikeDbHelper dbHelper;
    private long hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);

        dbHelper = new HikeDbHelper(this);
        findViews();

        Intent intent = getIntent();
        hikeId = intent.getLongExtra("HIKE_ID", -1);

        if (hikeId == -1) {
            Toast.makeText(this, "Error: Hike not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadHikeDetails();

        btnViewObservations.setOnClickListener(v -> {
            Intent obsIntent = new Intent(HikeDetailActivity.this, ObservationListActivity.class);
            obsIntent.putExtra("hike_id", hikeId);
            startActivity(obsIntent);
        });

        btnEditHike.setOnClickListener(v -> {
            Intent editIntent = new Intent(HikeDetailActivity.this, AddHikeActivity.class);
            editIntent.putExtra("edit_id", hikeId);
            startActivity(editIntent);
        });

        btnBackToList.setOnClickListener(v -> {
            finish();
        });

    }

    private void findViews() {
        txtName = findViewById(R.id.detailTxtName);
        txtLocation = findViewById(R.id.detailTxtLocation);
        txtDate = findViewById(R.id.detailTxtDate);
        txtParking = findViewById(R.id.detailTxtParking);
        txtLength = findViewById(R.id.detailTxtLength);
        txtDifficulty = findViewById(R.id.detailTxtDifficulty);
        txtWeather = findViewById(R.id.detailTxtWeather);
        txtEquipment = findViewById(R.id.detailTxtEquipment);
        txtDescription = findViewById(R.id.detailTxtDescription);
        btnViewObservations = findViewById(R.id.btnViewObservations);
        btnEditHike = findViewById(R.id.btnEditHike);
        btnBackToList = findViewById(R.id.btnBackToList);
    }

    private void loadHikeDetails() {
        Hike hike = dbHelper.getHike(hikeId);

        if (hike != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(hike.getName());
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            txtName.setText(hike.getName());
            txtLocation.setText(hike.getLocation());
            txtDate.setText("Date: " + hike.getDate());
            txtParking.setText("Parking Available: " + hike.getParking());
            txtLength.setText("Length: " + hike.getLengthKm() + " km");
            txtDifficulty.setText("Difficulty: " + hike.getDifficulty());
            txtWeather.setText("Expected Weather: " + hike.getWeather());
            txtEquipment.setText("Recommended Equipment: " + hike.getEquipment());
            txtDescription.setText(hike.getDescription());
        } else {
            Toast.makeText(this, "Could not load hike details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

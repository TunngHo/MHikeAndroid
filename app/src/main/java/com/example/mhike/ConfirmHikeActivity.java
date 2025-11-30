package com.example.mhike;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

public class ConfirmHikeActivity extends AppCompatActivity {

    private TextView txtSummary;
    private Button btnBack, btnSave;

    private Hike currentHike;
    private long editId = -1;
    private long currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hike);

        SharedPreferences prefs = getSharedPreferences("MHIKE_PREFS", MODE_PRIVATE);
        currentUserId = prefs.getLong("LOGGED_IN_USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User session not found. Cannot save hike.", Toast.LENGTH_LONG).show();
            Intent errorIntent = new Intent(this, HikeListActivity.class);
            startActivity(errorIntent);
            finish();
            return;
        }

        txtSummary = findViewById(R.id.txtSummary);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        editId = intent.getLongExtra("edit_id", -1);
        String name = intent.getStringExtra("name");
        String location = intent.getStringExtra("location");
        String date = intent.getStringExtra("date");
        String length = intent.getStringExtra("length");
        String parking = intent.getStringExtra("parking");
        String difficulty = intent.getStringExtra("difficulty");
        String description = intent.getStringExtra("description");
        String weather = intent.getStringExtra("weather");
        String equipment = intent.getStringExtra("equipment");

        String summary = "Confirm Hike Details:\n\n" +
                "Name: " + name + "\n" +
                "Location: " + location + "\n" +
                "Date: " + date + "\n" +
                "Length: " + length + " km\n" +
                "Parking: " + parking + "\n" +
                "Difficulty: " + difficulty + "\n" +
                "Description: " + description + "\n" +
                "Expected Weather: " + weather + "\n" +
                "Recommended Equipment: " + equipment;

        txtSummary.setText(summary);

        currentHike = new Hike();
        currentHike.setName(name);
        currentHike.setLocation(location);
        currentHike.setDate(date);
        currentHike.setLengthKm(length);
        currentHike.setParking(parking);
        currentHike.setDifficulty(difficulty);
        currentHike.setDescription(description);
        currentHike.setWeather(weather);
        currentHike.setEquipment(equipment);
        if (editId != -1) currentHike.setId(editId);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveHike());
    }

    private void saveHike() {
        HikeDbHelper dbHelper = new HikeDbHelper(this);
        if (editId != -1) {
            int rows = dbHelper.updateHike(currentHike);
            Toast.makeText(this, "Hike updated (" + rows + " row)", Toast.LENGTH_SHORT).show();
        } else {
            long id = dbHelper.addHike(currentHike, currentUserId);
            Toast.makeText(this, "Hike saved (ID: " + id + ")", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, HikeListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

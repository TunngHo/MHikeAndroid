package com.example.mhike;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import java.util.Calendar;

public class AddHikeActivity extends AppCompatActivity {

    private EditText edtName, edtLocation, edtLength, edtDescription, edtWeather, edtEquipment;
    private TextView txtDate;
    private Spinner spnDifficulty, spnParking;
    private Button btnSelectDate, btnSubmit, btnCancel;
    private String selectedDate = "";
    private long editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hike);

        edtName = findViewById(R.id.edtName);
        edtLocation = findViewById(R.id.edtLocation);
        edtLength = findViewById(R.id.edtLength);
        edtDescription = findViewById(R.id.edtDescription);
        edtWeather = findViewById(R.id.edtWeather);
        edtEquipment = findViewById(R.id.edtEquipment);
        txtDate = findViewById(R.id.txtDate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        spnDifficulty = findViewById(R.id.spnDifficulty);
        spnParking = findViewById(R.id.spnParking);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        ArrayAdapter<CharSequence> diffAdapter = ArrayAdapter.createFromResource(
                this, R.array.difficulty_levels, android.R.layout.simple_spinner_item);
        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDifficulty.setAdapter(diffAdapter);

        ArrayAdapter<CharSequence> parkAdapter = ArrayAdapter.createFromResource(
                this, R.array.parking_options, android.R.layout.simple_spinner_item);
        parkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnParking.setAdapter(parkAdapter);

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        HikeDbHelper db = new HikeDbHelper(this);
        editId = getIntent().getLongExtra("edit_id", -1);
        if (editId != -1) {
            Hike existing = db.getHike(editId);
            if (existing != null) {
                edtName.setText(existing.getName());
                edtLocation.setText(existing.getLocation());
                selectedDate = existing.getDate();
                txtDate.setText(selectedDate);
                edtLength.setText(existing.getLengthKm());
                edtDescription.setText(existing.getDescription());
                edtWeather.setText(existing.getWeather());
                edtEquipment.setText(existing.getEquipment());
                setSpinnerSelectionByValue(spnParking, existing.getParking());
                setSpinnerSelectionByValue(spnDifficulty, existing.getDifficulty());
                btnSubmit.setText("Confirm");
            }
        }

        btnSubmit.setOnClickListener(v -> validateAndSubmit());
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    txtDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void validateAndSubmit() {
        String name = edtName.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String length = edtLength.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String weather = edtWeather.getText().toString().trim();
        String equipment = edtEquipment.getText().toString().trim();
        String parking = spnParking.getSelectedItem().toString();
        String difficulty = spnDifficulty.getSelectedItem().toString();

        if (name.isEmpty() || location.isEmpty() || selectedDate.isEmpty()
                || length.isEmpty() || parking.isEmpty() || difficulty.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ConfirmHikeActivity.class);
        intent.putExtra("edit_id", editId);
        intent.putExtra("name", name);
        intent.putExtra("location", location);
        intent.putExtra("date", selectedDate);
        intent.putExtra("length", length);
        intent.putExtra("parking", parking);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("description", description);
        intent.putExtra("weather", weather);
        intent.putExtra("equipment", equipment);
        startActivity(intent);
    }

    private void setSpinnerSelectionByValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}

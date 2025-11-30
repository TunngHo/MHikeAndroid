package com.example.mhike;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class AddObservationActivity extends AppCompatActivity {

    private EditText edtObservation, edtComments;
    private TextView txtTime;
    private Button btnPickTime, btnSave, btnCancel;
    private Button btnPickImage;
    private ImageView imgObsPreview;

    private Uri imageUri;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    private long hikeId, oId = -1;
    private String selectedTime;

    private void registerImagePicker() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                try {
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                this.imageUri = uri;
                imgObsPreview.setImageURI(uri);
                imgObsPreview.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        registerImagePicker();


        edtObservation = findViewById(R.id.edtObservation);
        edtComments = findViewById(R.id.edtComments);
        txtTime = findViewById(R.id.txtTime);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnSave = findViewById(R.id.btnSave);
        btnPickImage = findViewById(R.id.btnPickImage);
        imgObsPreview = findViewById(R.id.imgObsPreview);
        btnCancel = findViewById(R.id.btnCancel);

        hikeId = getIntent().getLongExtra("HIKE_ID", -1);
        oId = getIntent().getLongExtra("OBS_ID", -1);

        if (oId != -1) {
            setTitle("Edit Observation");
            loadDataForEdit();
        } else {
            setTitle("Add Observation");
            setDefaultTime();
        }

        btnPickTime.setOnClickListener(v -> showTimePickerDialog());
        btnSave.setOnClickListener(v -> saveObservation());
        btnPickImage.setOnClickListener(v -> pickImage());

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }
        imgObsPreview.setOnClickListener(v -> {
            if (imageUri != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(imageUri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "No application can handle this request.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void setDefaultTime() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        txtTime.setText(selectedTime);
    }

    private void loadDataForEdit() {
        HikeDbHelper db = new HikeDbHelper(this);
        Observation o = db.getObservation(oId);
        if (o != null) {
            edtObservation.setText(o.getObservation());
            edtComments.setText(o.getComments());

            selectedTime = o.getTime();
            txtTime.setText(selectedTime);

            String uriString = o.getImageUri();
            if (uriString != null && !uriString.isEmpty()) {
                this.imageUri = Uri.parse(uriString);
                imgObsPreview.setImageURI(this.imageUri);
                imgObsPreview.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, "Observation not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    txtTime.setText(selectedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void pickImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void saveObservation() {
        String oText = edtObservation.getText().toString().trim();
        String comments = edtComments.getText().toString().trim();
        String imageUriString = (imageUri != null) ? imageUri.toString() : null;

        if (oText.isEmpty()) {
            Toast.makeText(this, "Observation field is required!", Toast.LENGTH_SHORT).show();
            edtObservation.requestFocus();
            return;
        }

        if (hikeId == -1) {
            Toast.makeText(this, "Error: Hike ID not found. Cannot save observation.", Toast.LENGTH_LONG).show();
            return;
        }

        HikeDbHelper db = new HikeDbHelper(this);
        Observation o = new Observation(oId, hikeId, oText, selectedTime, comments, imageUriString);


        if (oId != -1) {
            int rowsAffected = db.updateObservation(o);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Observation updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update observation.", Toast.LENGTH_SHORT).show();
            }
        } else {
            long newId = db.addObservation(
                    o.getHikeId(),
                    o.getObservation(),
                    o.getTime(),
                    o.getComments(),
                    o.getImageUri()
            );
            if (newId != -1) {
                Toast.makeText(this, "Observation saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save observation.", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}

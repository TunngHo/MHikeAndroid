package com.example.mhike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences; // BƯỚC 1: Thêm import
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

public class SearchHikeActivity extends AppCompatActivity implements HikeAdapter.OnItemActionListener {

    private EditText edtName, edtLocation, edtLength, edtDate;
    private Button btnSearch, btnBack;
    private RecyclerView recyclerSearch;
    private HikeAdapter adapter;
    private HikeDbHelper db;
    private long currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hike);

        SharedPreferences prefs = getSharedPreferences("MHIKE_PREFS", MODE_PRIVATE);
        currentUserId = prefs.getLong("LOGGED_IN_USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User session not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db = new HikeDbHelper(this);

        edtName = findViewById(R.id.edtSearchName);
        edtLocation = findViewById(R.id.edtSearchLocation);
        edtLength = findViewById(R.id.edtSearchLength);
        edtDate = findViewById(R.id.edtSearchDate);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerSearch = findViewById(R.id.recyclerSearchResults);
        btnBack = findViewById(R.id.btnBackFromSearch);

        setupRecyclerView();

        btnSearch.setOnClickListener(v -> performSearch());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new HikeAdapter();
        adapter.setListener(this);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerSearch.setAdapter(adapter);
    }

    private void performSearch() {
        String name = edtName.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String length = edtLength.getText().toString().trim();
        String date = edtDate.getText().toString().trim();

        List<Hike> results = db.advancedSearch(name, location, length, date, currentUserId);

        adapter.setHikes(results);

        if (results.isEmpty()) {
            Toast.makeText(this, "No hikes found matching criteria for this user", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onEditClicked(Hike h) {
        Intent i = new Intent(this, AddHikeActivity.class);
        i.putExtra("edit_id", h.getId());
        startActivity(i);
    }

    @Override
    public void onDeleteClicked(Hike h) {
        db.deleteHike(h.getId());
        performSearch();
        Toast.makeText(this, "Hike '" + h.getName() + "' deleted", Toast.LENGTH_SHORT).show();
    }
}

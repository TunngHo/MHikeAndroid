package com.example.mhike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences; // BƯỚC 1: Thêm import SharedPreferences
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class HikeListActivity extends AppCompatActivity
        implements HikeAdapter.OnItemActionListener, HikeAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private HikeAdapter adapter;
    private HikeDbHelper db;
    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_list);

        SharedPreferences prefs = getSharedPreferences("MHIKE_PREFS", MODE_PRIVATE);
        currentUserId = prefs.getLong("LOGGED_IN_USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not logged in. Redirecting to login.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerHikes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HikeAdapter();
        adapter.setListener(this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        db = new HikeDbHelper(this);

        loadHikes();
    }

    @Override
    public void onItemClicked(Hike h) {
        Intent intent = new Intent(this, HikeDetailActivity.class);
        intent.putExtra("HIKE_ID", h.getId());
        startActivity(intent);
    }

    private void loadHikes() {
        List<Hike> list = db.getAllHikes(currentUserId);
        adapter.setHikes(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != -1) {
            loadHikes();
        }
    }

    @Override
    public void onEditClicked(Hike h) {
        Intent i = new Intent(this, AddHikeActivity.class);
        i.putExtra("edit_id", h.getId());
        startActivity(i);
    }

    @Override
    public void onDeleteClicked(Hike h) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Hike")
                .setMessage("Are you sure you want to delete this hike?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteHike(h.getId());
                    loadHikes();
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_hike_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent i = new Intent(this, AddHikeActivity.class);
            startActivity(i);
            return true;

        } else if (id == R.id.action_search) {
            Intent i = new Intent(this, SearchHikeActivity.class);
            startActivity(i);
            return true;

        } else if (id == R.id.action_reset) {
            new AlertDialog.Builder(this)
                    .setTitle("Reset Database")
                    .setMessage("This will delete ALL hikes and observations for the current user. Continue?")
                    .setPositiveButton("Yes", (d, w) -> {
                        db.deleteAllHikes(currentUserId);
                        Toast.makeText(this, "All your hikes have been deleted", Toast.LENGTH_SHORT).show();
                        loadHikes();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;

        } else if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences prefs = getSharedPreferences("MHIKE_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("LOGGED_IN_USER_ID");
                        editor.apply();

                        Intent intent = new Intent(HikeListActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;

        } else if (id == R.id.action_exit) {
            finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

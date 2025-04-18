package com.example.androidgameproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ScoreboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the activity and go back to the previous screen
            }
        });

        // Current username TextView
        TextView tvCurrentUsername = findViewById(R.id.tvCurrentUsername);

        // Get the logged-in user
        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference("users").child(GlobalVariables.email).child("username");
        if (GlobalVariables.email != "") {
            tvCurrentUsername.setText("Logged in as: " + usernameRef.get());
        }
        else {
            tvCurrentUsername.setText("Not logged in");
        }

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // List to hold scores
        List<ScoreEntry> scores = new ArrayList<>();
        ScoreAdapter adapter = new ScoreAdapter(scores);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firestores
        fetchScores(scores, adapter);
    }

    private void fetchScores(List<ScoreEntry> scores, ScoreAdapter adapter) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        TextView tvNoScores = findViewById(R.id.tvNoScores);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        scores.clear(); // Clear any existing data

        // Use a query to get all users sorted by 'best_score' in descending order
        usersRef.orderByChild("best_score").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Show "No scores available" message
                            tvNoScores.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            // Iterate through the data and get the top scores
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String username = snapshot.child("username").getValue(String.class);
                                Integer score = snapshot.child("best_score").getValue(Integer.class);
                                if (username != null && score != null) {
                                    scores.add(new ScoreEntry(username, score));
                                }

                                //add not logged user
                                if(Objects.equals(snapshot.getKey(), "best_score")){
                                    scores.add(new ScoreEntry("Anonymous - not logged in", snapshot.getValue(Integer.class)));
                                }
                            }

                            // Sort the scores in descending order (if needed)
                            scores.sort((score1, score2) -> Integer.compare(score2.getScore(), score1.getScore()));

                            // Update UI
                            tvNoScores.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged(); // Notify adapter of data changes
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors (e.g., log or show a message to the user)
                        tvNoScores.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
        });
    }
}
package com.example.androidgameproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class EndScreenActivity extends AppCompatActivity {
    private MusicManager musicManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        musicManager = new MusicManager();
        musicManager.startMusic(2);

        // Get the score from the Intent
        int score = getIntent().getIntExtra("final_score", 0);

        // Display the score
        TextView scoreText = findViewById(R.id.score_text);
        scoreText.setText("Your Score: " + score);
        saveBestScore(score);

        // check m_auth to be null
        // change save best score to save using the displayed name
        //bestScoreTextView.setText("Can't present highest score, user not connected");

        // Try Again Button
        findViewById(R.id.try_again_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Restart the game (FrameActivity)
                musicManager.stopMusic();
                Intent intent = new Intent(getApplicationContext(), FrameActivity.class);
                startActivity(intent);
               // finish();
            }
        });

        // Scoreboards Button
        findViewById(R.id.scoreboards_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ScoreboardsActivity
                Intent intent = new Intent(EndScreenActivity.this, ScoreboardActivity.class);
                startActivity(intent);
            }
        });

        // Exit Button
        findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit the app
                finishAffinity();
            }
        });
    }

    private void saveBestScore(int score) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userScoreRef = database.getReference("users").child(GlobalVariables.email).child("best_score");

        // Retrieve the current best score
        userScoreRef.get().addOnSuccessListener(snapshot -> {
            Integer bestScore = snapshot.getValue(Integer.class);
            TextView bestScoreTextView = findViewById(R.id.best_score);
            if (bestScore == null || score > bestScore) {
                // Update the best score in Firebase
                userScoreRef.setValue(score);
                bestScoreTextView.setText("Best score: " + score);
            }
            else{
                bestScoreTextView.setText("Best score: " + bestScore);
            }
        });
    }

    // to do - fix colors in - button ripple. endgame screen. scoreboard screen.
}

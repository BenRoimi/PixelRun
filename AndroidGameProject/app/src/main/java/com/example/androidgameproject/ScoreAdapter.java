package com.example.androidgameproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private List<ScoreEntry> scores;

    // Constructor to initialize the dataset
    public ScoreAdapter(List<ScoreEntry> scores) {
        this.scores = scores;
    }

    // ViewHolder class to hold references to the item layout views
    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView username, score;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            score = itemView.findViewById(R.id.tvScore);
        }
    }

    // Inflates the item layout and creates a ViewHolder
    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    // Binds data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreEntry score = scores.get(position);
        holder.username.setText(score.getUsername());
        holder.score.setText(String.valueOf(score.getScore()));
    }

    // Returns the total number of items in the dataset
    @Override
    public int getItemCount() {
        return scores.size();
    }
}

package com.example.androidgameproject;

import android.content.Context;
import android.content.Intent;

public class MusicManager {
    private static Intent musicServiceIntent;
    private static boolean backroundMusicOn;
    private static Context context;
    private static int playingSound; // 1 - backround, 2 - losing sound

    // Constructor that accepts a Context
    public MusicManager(Context cnt) {
        this.context = cnt.getApplicationContext(); // Use application context to avoid memory leaks
        this.musicServiceIntent = new Intent(context, MyMusicService.class);
        this.backroundMusicOn = false;
    }

    public MusicManager() {

    }

    public int getPlayingSound() {
        return playingSound;
    }

    public void setPlayingSound(int playingSoundPar) {
        playingSound = playingSoundPar;
    }

    // Method to start the music service
    public void startMusic(int playingSoundPar) {
        if (context != null && musicServiceIntent != null) {
            playingSound = playingSoundPar;
            context.startService(musicServiceIntent);

            if(playingSound == 1){
                backroundMusicOn = true;
            }
        }
    }

    // Method to stop the music service
    public void stopMusic() {
        if (context != null && musicServiceIntent != null) {
            context.stopService(musicServiceIntent);

            if(playingSound == 1){
                backroundMusicOn = false;
            }
        }
    }

    // Method to check if the music is on
    public boolean isBackroundMusicOn() {
        return backroundMusicOn;
    }
}

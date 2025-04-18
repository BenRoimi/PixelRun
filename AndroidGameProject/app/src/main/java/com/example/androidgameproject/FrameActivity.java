package com.example.androidgameproject;

import android.app.Dialog;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class
FrameActivity extends AppCompatActivity {
    private FrameLayout frame;
    private int scrHeight;
    private int scrWidth;
    private GameManager myGameManager;
    private MusicManager musicManager;
    private float mInitialY;
    private static final float SWIPE_THRESHOLD = 70f;
    private Dialog pauseDialog;
    private boolean resumed = false;
    private boolean wasMusicOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        frame = findViewById(R.id.gameFrame);

        checkPermission();

        musicManager = new MusicManager(this);
        musicManager.startMusic(1); // backround music
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Record the starting Y position when the user touches the screen
                mInitialY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                // Calculate the movement delta
                float deltaY = mInitialY - event.getY();

                // Check if the movement is an upward swipe
                if (deltaY > SWIPE_THRESHOLD) {
                    if(myGameManager.isNotJumping()){
                        myGameManager.jump();
                    }
                }

                if (deltaY < (SWIPE_THRESHOLD * -1)) {
                    if(myGameManager.isNotJumping()){
                        myGameManager.slide();
                    }
                }

                // Additional check for the region where toggleMusic() is triggered
                if ((50 <= event.getX() && event.getX() <= 235) &&
                        (370 <= event.getY() && event.getY() <= 520)) {
                    toggleMusic();
                }

                // Log touch position for debugging
                Log.d("Touch", "x=" + event.getX() + " y=" + event.getY());
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && !resumed) {
            scrHeight=frame.getHeight();
            scrWidth=frame.getWidth();
            myGameManager = new GameManager(this, scrWidth,scrHeight);
            frame.addView(myGameManager);
        }
    }

    public void checkPermission(){
        CallReceiver myReceiver= new CallReceiver();
        int permissionCheck = this.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        this.registerReceiver(myReceiver, intentFilter);
    }

    private void toggleMusic() {
        if (musicManager.isBackroundMusicOn()) {
            musicManager.stopMusic();
            myGameManager.changeToMuteImage();
        } else {
            musicManager.startMusic(1); // backround music
            myGameManager.changeToSpeakerImage();
        }
    }

    private void saveMusicStatusAndTurnOff(){
        wasMusicOn = musicManager.isBackroundMusicOn();
        if (wasMusicOn) {
            musicManager.stopMusic();
            myGameManager.changeToMuteImage();
        }
    }

    private void setMusicBackToSavedStatus(){
        if(wasMusicOn){
            musicManager.startMusic(1); // backround music
            myGameManager.changeToSpeakerImage();
        }
    }

    // Function to pause the game and show the popup
    public void pauseGame() {
        saveMusicStatusAndTurnOff();
        myGameManager.stand();
        // Create a custom Dialog as a popup
        pauseDialog = new Dialog(this);
        pauseDialog.setContentView(R.layout.popup_game_paused);
        pauseDialog.setCancelable(false);  // Don't dismiss by tapping outside

        // Show the dialog
        pauseDialog.show();
    }

    // Function to resume the game when the user touches the screen
    public void resumeGame() {
        // Dismiss the pause dialog
        if (pauseDialog != null && pauseDialog.isShowing()) {
            pauseDialog.dismiss();
        }

        setMusicBackToSavedStatus();
        myGameManager.stopStanding();
        // Resume game logic here (e.g., restart game loop, resume animations, etc.)
        // For example, if you use a game loop, you can resume it here
        resumed=true;
    }
}
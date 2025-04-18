package com.example.androidgameproject;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MyMusicService extends Service {

    MediaPlayer backroundMusicPlayer;
    MediaPlayer losingSoundPlayer;
    MusicManager mm;

    public MyMusicService() {
        mm = new MusicManager();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(mm.getPlayingSound() == 1 ) {
            backroundMusicPlayer = MediaPlayer.create(this, R.raw.backroundmusic);
            backroundMusicPlayer.setLooping(true); // Set looping
            backroundMusicPlayer.setVolume(1000,1000);
            backroundMusicPlayer.start();
        }
        else if (mm.getPlayingSound() == 2) {
            losingSoundPlayer = MediaPlayer.create(this, R.raw.game_over_sound);
            losingSoundPlayer.setLooping(false);
            losingSoundPlayer.setVolume(1000, 1000);
            losingSoundPlayer.start();
            losingSoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("end play", "here");
                    //MusicManager mm= new MusicManager();
                    //mm.stopMusic();

                }
            });
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mm.getPlayingSound() == 1){
            backroundMusicPlayer.stop();
            backroundMusicPlayer.release();
        }
        if(mm.getPlayingSound() == 2){
            losingSoundPlayer.stop();
            losingSoundPlayer.release();
        }
    }
}

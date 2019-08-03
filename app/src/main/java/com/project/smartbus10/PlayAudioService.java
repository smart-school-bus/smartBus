package com.project.smartbus10;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class PlayAudioService extends Service implements MediaPlayer.OnPreparedListener {
    private String audio;
    private SharedPreferences sp;
    MediaPlayer mediaPlayer = null;

    public PlayAudioService( ) {

    }
    public void onCreate() {

        super.onCreate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        audio= (String) intent.getSerializableExtra("audio");
        //location= (Location) intent.getSerializableExtra("location");

        Log.d("geoLocate", ""+audio );
        try {
            mediaPlayer  = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audio);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
           // mediaPlayer.start();
            Log.d("geoLocate", "machLocationCATC");
        } catch (Exception e) {
            // TODO: handle exception
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null)
        { mediaPlayer.stop();}
        try {
            mediaPlayer.release();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.start();}
        catch (Exception e) {
            // TODO: handle exception
        }

    }
}


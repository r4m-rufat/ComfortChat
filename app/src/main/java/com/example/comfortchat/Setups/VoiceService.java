package com.example.comfortchat.Setups;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class VoiceService {

    private Context context;
    private MediaPlayer topMediPlayer;

    public VoiceService(Context context) {
        this.context = context;
    }

    public void playAudioFromFirebaseUrl(String url, final onPlayCallBack onPlayCallBack){

        if (topMediPlayer != null){

            topMediPlayer.release();

        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            topMediPlayer = mediaPlayer;

        }catch (IOException e){
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                mp.release();
                onPlayCallBack.onFinished();

            }
        });

    }

    public interface onPlayCallBack{

        void onFinished();

    }

}

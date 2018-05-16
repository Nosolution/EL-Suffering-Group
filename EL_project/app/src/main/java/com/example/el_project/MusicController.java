package com.example.el_project;


/*
* 这个类是用于管理音乐播放的，
* 尽管目前很简单，但未来可能会大量拓展。
*
* 如何使用：
*   实例化一个此类的对象，构造时传入context作为参数，直接对此对象start, pause, stop, restart
*   播放的音乐直接由此对象选择，未来可能会修改音乐，增加可播放音乐数量，均对此类操作
*
* 注意：
*   当当前页面退出或此音乐不再需要播放等情况，请即时在相应的OnDestroy等生命周期管理函数中调用此对象的release方法
*
* 未来可能加入：
*   多支音乐循环，下一首等
*               by NA 2018/4/18
*
* */


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class MusicController {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MusicCollection collection = new MusicCollection();
    private Context context;
    private int musicPlaying;

    MusicController(Context context){
        this.context = context;
        initMusicPlayer();
    }

    private void initMusicPlayer(){
        musicPlaying = collection.getRandomMusic();
        mediaPlayer = MediaPlayer.create(context, musicPlaying);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("TEST", "onCompletion: MusicComplete");
                randomSwitch();
            }
        });
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    //随机切歌
    public void randomSwitch(){
        musicPlaying = collection.getRandomMusicEx(musicPlaying);
        release();
        mediaPlayer = MediaPlayer.create(context, musicPlaying);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("TEST", "onCompletion: MusicComplete");
                randomSwitch();
            }
        });
        mediaPlayer.start();
    }

    //音乐播放开始，也是恢复暂停
    public void start(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void resume(){
        start();
    }

    public void pause(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void stop(){
        mediaPlayer.reset();
        initMusicPlayer();
    }

    public void restart(){
        stop();
        start();
    }

    public void release(){
        if (mediaPlayer != null){
            stop();
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

}

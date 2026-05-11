package com.example.aircraftwar.application;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.aircraftwar.R;

/*音效播放类*/
public class PlayMusic {
    private final MainActivity mainActivity;
    private final SoundPool soundPool;
    private final HashMap<Integer,Integer>map;
    private boolean[] isOpen = new boolean[5];
    public PlayMusic(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        AudioAttributes attributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_GAME).build();
        soundPool = new SoundPool.Builder().setMaxStreams(10).setAudioAttributes(attributes).build();
        map = new HashMap<Integer,Integer>();
        for(int i=0;i<5;i++){
            isOpen[i] = true;
        }
    }
    //初始化系统音效
    public void initSound(){
        map.put(1, soundPool.load(mainActivity, R.raw.bomb_explosion, 1));
        map.put(2, soundPool.load(mainActivity, R.raw.bullet, 1));
        map.put(3, soundPool.load(mainActivity, R.raw.bullet_hit, 1));
        map.put(4, soundPool.load(mainActivity, R.raw.game_over, 1));
        map.put(5, soundPool.load(mainActivity, R.raw.get_supply, 1));
    }
    //播放音效
    public void playSound(int sound,int loop){
        if(isOpen[sound-1]){
            AudioManager am = (AudioManager)mainActivity.getSystemService(Context.AUDIO_SERVICE);
            float stramVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            float stramMaxVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            float volume = stramVolumeCurrent/stramMaxVolumeCurrent;
            soundPool.play(map.get(sound), volume, volume, 1, loop, 1.0f);
        }
    }
    //释放资源
    public void release(){
        if(soundPool != null)
            soundPool.release();
    }
    //停止播放音效

    public boolean isOpen(int id) {
        return isOpen[id-1];
    }

    public void setOpen(int id, boolean isOpen) {
        this.isOpen[id-1] = isOpen;
    }
}
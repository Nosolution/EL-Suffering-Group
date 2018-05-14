package com.example.el_project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicCollection {

    private List<Integer> musicCollection;
    private Random random;
    private int count;

    MusicCollection(){
        musicCollection = new ArrayList<Integer>();
        init();
    }

    private void init(){
        musicCollection.add(R.raw.music_choosen_for_app_1);
        musicCollection.add(R.raw.music_choosen_for_app_2);
        musicCollection.add(R.raw.music_choosen_for_app_3);
        musicCollection.add(R.raw.music_choosen_for_app_4);
        musicCollection.add(R.raw.music_choosen_for_app_5);
        musicCollection.add(R.raw.music_choosen_for_app_6);
        musicCollection.add(R.raw.music_choosen_for_app_7);
        musicCollection.add(R.raw.music_choosen_for_app_8);
        musicCollection.add(R.raw.music_choosen_for_app_9);
        musicCollection.add(R.raw.music_choosen_for_app_10);

        count = musicCollection.size();
    }

    //得到一首除外传入歌曲后的歌
    public int getRandomMusicEx(int Ex){
        random = new Random();
        int result = 0;
        do {
            int index = random.nextInt(count);
            result = musicCollection.get(index);
        }while (result == Ex);
        return result;
    }

    //得到一首随机歌
    public int getRandomMusic(){
        random = new Random();
        int result = 0;
        int index = random.nextInt(count);
        result = musicCollection.get(index);
        return result;
    }

}

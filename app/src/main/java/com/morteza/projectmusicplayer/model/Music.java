package com.morteza.projectmusicplayer.model;

import com.morteza.projectmusicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Music {
    private int    id;
    private String name;
    private String artist;
    private int    coverResId;
    private int    artistResId;
    private int    musicFileResId;

    public Music(String name, String artist, int coverResId, int artistResId, int musicFileResId) {
        this.name           = name;
        this.artist         = artist;
        this.coverResId     = coverResId;
        this.artistResId    = artistResId;
        this.musicFileResId = musicFileResId;
    }

    public static List<Music> getList() {
        List<Music> musicList = new ArrayList<>();
        musicList.add(new Music("Chehel Gis", "Evan Band", R.drawable.music_1_cover, R.drawable.music_1_artist,
                R.raw.music_1));
        musicList.add(new Music("Tanha tarin", "Reza Sadeghi", R.drawable.music_2_cover, R.drawable.music_2_artist,
                R.raw.music_2));
        musicList.add(new Music("Hich", "Reza Bahram", R.drawable.music_3_cover, R.drawable.music_3_artist, R.raw.music_3));
        return musicList;
    }

    public static String convertMillisToString(long duration) {
        long sec = (duration / 1000) % 60;
        long min = (duration / (1000 * 60)) % 60;
        return String.format(Locale.US, "%02d:%02d", min, sec);
    }


    public int getMusicFileResId()                    { return musicFileResId; }

    public void setMusicFileResId(int musicFileResId) { this.musicFileResId = musicFileResId; }

    public int getId()                                { return id; }

    public void setId(int id)                         { this.id = id; }

    public String getName()                           { return name; }

    public void setName(String name)                  { this.name = name; }

    public String getArtist()                         { return artist; }

    public void setArtist(String artist)              { this.artist = artist; }

    public int getCoverResId()                        { return coverResId; }

    public void setCoverResId(int coverResId)         { this.coverResId = coverResId; }

    public int getArtistResId()                       { return artistResId; }

    public void setArtistResId(int artistResId)       { this.artistResId = artistResId; }
}

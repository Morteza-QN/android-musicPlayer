package com.morteza.projectmusicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.slider.Slider;
import com.morteza.projectmusicplayer.databinding.ActivityMainBinding;
import com.morteza.projectmusicplayer.model.Music;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MusicAdapter.ICallbackListener {
    // TODO: 7/24/2020 find music in phone
    // TODO: 7/24/2020 play random music

    private ActivityMainBinding binding;
    private List<Music>         musicList    = Music.getList();
    private MediaPlayer         mediaPlayer;
    private MusicState          musicState   = MusicState.STOPPED;
    private Timer               timer;
    private boolean             isDragging;
    private int                 currentMusic = 0;
    private MusicAdapter        adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new MusicAdapter(musicList, this);
        binding.rvMainPlaylist.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.rvMainPlaylist.setAdapter(adapter);

        onMusicChange(musicList.get(currentMusic));

        binding.btnMainPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LottieAnimationView animationView = findViewById(R.id.animationView);
                switch (musicState) {
                    case PLAYING:
                        mediaPlayer.pause();
                        musicState = MusicState.PAUSED;
                        animationView.pauseAnimation();
                        binding.btnMainPlay.setImageResource(R.drawable.ic_play_32dp);
                        break;
                    case PAUSED:
                    case STOPPED:
                        mediaPlayer.start();
                        musicState = MusicState.PLAYING;
                        animationView.playAnimation();
                        binding.btnMainPlay.setImageResource(R.drawable.ic_pause_24dp);
                        break;
                }
            }
        });
        //move slider in music move
        binding.sliderMain.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) { binding.tvMainTimeCurrent.setText(Music.convertMillisToString((long) value)); }
            }
        });
        binding.sliderMain.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                isDragging = true;
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                isDragging = false;
                mediaPlayer.seekTo((int) slider.getValue()); // move into music
            }
        });
        binding.btnMainForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextMusic();
            }
        });
        binding.btnMainBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPreviousMusic();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    private void onMusicChange(final Music music) {
        adapter.notifyMusicChange(music);
        binding.sliderMain.setValue(0);
        binding.tvMainArtist.setText(music.getArtist());
        binding.ivMainArtist.setActualImageResource(music.getArtistResId());
        binding.ivMainCover.setActualImageResource(music.getCoverResId());
        binding.tvMainMusicName.setText(music.getName());
        mediaPlayer = MediaPlayer.create(this, music.getMusicFileResId());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                musicState = MusicState.PLAYING;
                binding.btnMainPlay.setImageResource(R.drawable.ic_pause_24dp);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //this run not thread main -> no access UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //main thread access UI
                                if (!isDragging) {
                                    binding.tvMainTimeCurrent
                                            .setText(Music.convertMillisToString(mediaPlayer.getCurrentPosition()));
                                    binding.sliderMain.setValue(mediaPlayer.getCurrentPosition());
                                }
                            }
                        });
                    }
                }, 1000, 1000);
                binding.tvMainTimeEnd.setText(Music.convertMillisToString(mediaPlayer.getDuration()));
                binding.sliderMain.setValueTo(mediaPlayer.getDuration());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        goNextMusic();
                    }
                });
            }
        });
    }

    private void goNextMusic() {
        timer.cancel();
        timer.purge();
        mediaPlayer.release();
        currentMusic = currentMusic < musicList.size() - 1 ? currentMusic + 1 : 0;
        onMusicChange(musicList.get(currentMusic));
    }

    private void goPreviousMusic() {
        timer.cancel();
        timer.purge();
        mediaPlayer.release();
        currentMusic = currentMusic == 0 ? musicList.size() - 1 : currentMusic - 1;
        onMusicChange(musicList.get(currentMusic));
    }

    @Override
    public void onClickItem(Music music, int position) {
        timer.cancel();
        timer.purge();
        mediaPlayer.release();
        onMusicChange(musicList.get(position));
    }

    private enum MusicState {PLAYING, PAUSED, STOPPED}
}
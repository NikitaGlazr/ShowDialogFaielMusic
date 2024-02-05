package com.example.showdialogfaiel;
import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mPlayer;
    SeekBar seekBar;
    boolean isSeeking = false;
    Button playButton, pauseButton, stopButton, nextButton, previousButton;
    SeekBar volumeControl;
    AudioManager audioManager;

    int[] musicFiles = {R.raw.musicmoroxz, R.raw.music2, R.raw.sharik, R.raw.music4,
            R.raw.dimok,R.raw.doktorlivsi,
            R.raw.samyjjdorogojjchelovek, R.raw.vladimirskijjcentral, };
    int currentSongIndex = 0;

    ListView songListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayer = MediaPlayer.create(this, R.raw.musicmoroxz);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
            }
        });

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        nextButton = findViewById(R.id.nextTrackButton);
        previousButton = findViewById(R.id.previousTrackButton2);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeControl = findViewById(R.id.volumeControl);
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curValue);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(mPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
            }
        });

        initializeListView();
    }

    private void initializeListView() {
        songListView = findViewById(R.id.songListView);
        songList = new ArrayList<>();
        songList.add("Song moroz 1");
        songList.add("Song 80-ex 2");
        songList.add("Song sharik 3");
        songList.add("Song volkonaft 4");
        songList.add("Song mem 5");
        songList.add("Song Dockor Livsi 6");
        songList.add("Song samyjj dorogojj chelovek 7");
        songList.add("Song vladimirskijj central 8");
        songList.add("Song proverka lista 9");
        songList.add("Song proverka lista 10");
        songList.add("Song proverka lista 11");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == currentSongIndex) {
                    view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                } else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
                return view;
            }
        };

        songListView.setAdapter(adapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSongIndex = position;
                playNewTrack();
                adapter.notifyDataSetChanged();
            }
        });
        final Handler handler = new Handler();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null) {
                    int mCurrentPosition = mPlayer.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000); // обновлять каждую секунду
            }
        });
    }


    public void play(View view){
        mPlayer.start();
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }
    public void pause(View view){

        mPlayer.pause();
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }
    public void stop(View view){
        stopPlay();
    }

    public void nextTrack(View view) {
        currentSongIndex = (currentSongIndex + 1) % musicFiles.length;
        playNewTrack();
    }

    public void previousTrack(View view) {
        currentSongIndex = (currentSongIndex - 1 + musicFiles.length) % musicFiles.length;
        playNewTrack();
    }

    private void playNewTrack() {
        mPlayer.stop();
        mPlayer.release();

        mPlayer = MediaPlayer.create(this, musicFiles[currentSongIndex]);
        mPlayer.setOnCompletionListener(mp -> stopPlay());
        mPlayer.start();

        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    private void stopPlay() {
        mPlayer.stop();
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            playButton.setEnabled(true);
        } catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
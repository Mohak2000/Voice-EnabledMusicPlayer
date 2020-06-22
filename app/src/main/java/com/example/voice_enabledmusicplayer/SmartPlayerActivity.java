package com.example.voice_enabledmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayerActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper="";
    private String mode="ON";
    private int temp;

    private ImageView pausePlaybtn,nextBtn,prevbtn;
    private TextView songNameTxt;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledButton;

    private MediaPlayer mymediaPlayer;
    private int position;
    private ArrayList<File> mysongs;
    private String mSongsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);

        checkVoiceCommandPermission();
        pausePlaybtn=findViewById(R.id.play_pause_button);
        nextBtn=findViewById(R.id.next_button);
        prevbtn=findViewById(R.id.previous_btn);
        imageView=findViewById(R.id.logo);
        lowerRelativeLayout=findViewById(R.id.lower);
        voiceEnabledButton=findViewById(R.id.voice_enabled_btn);
        songNameTxt=findViewById(R.id.songname);


        parentRelativeLayout=findViewById(R.id.parentRelativeLayout);
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        validateREcvalndstrtplaying();
        imageView.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {


            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle)
            {
                ArrayList<String> matchesground=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchesground!=null)
                {
                    if(mode.equals("ON")) {
                        keeper = matchesground.get(0);

                        if (keeper.equals("pause the song")) {

                            Onlypausemusic();
                            Toast.makeText(SmartPlayerActivity.this, "Command :-" + keeper, Toast.LENGTH_LONG).show();

                        } else if (keeper.equals("play the song")) {


                                Onlyplaymusic();
                                Toast.makeText(SmartPlayerActivity.this, "Command :-" + keeper, Toast.LENGTH_LONG).show();


                        }
                        else if (keeper.equals("play next song")) {


                            playNextsong();
                            Toast.makeText(SmartPlayerActivity.this, "Command :-" + keeper, Toast.LENGTH_LONG).show();


                        }
                        else if (keeper.equals("play previous song")) {


                            playPrevSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command :-" + keeper, Toast.LENGTH_LONG).show();


                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });

        voiceEnabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(mode.equals("ON"))
                {
                    mode="OFF";
                    voiceEnabledButton.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);

                }
                else
                {
                    mode="ON";
                    voiceEnabledButton.setText("Voice Enabled Mode  ON");
                    lowerRelativeLayout.setVisibility(View.GONE);

                }

            }
        });

        pausePlaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                playpauseSong();

            }
        });

        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mymediaPlayer.getCurrentPosition()>0)
                {
                    playPrevSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mymediaPlayer.getCurrentPosition()>0)
                {
                    playNextsong();
                }
            }
        });
    }


    private void validateREcvalndstrtplaying()
    {
        if(mymediaPlayer!=null)
        {
            mymediaPlayer.stop();
        mymediaPlayer.release();
        }

        Intent intent=getIntent();
        Bundle bundle= intent.getExtras();

        mysongs=(ArrayList) bundle.getParcelableArrayList("song");
        mSongsName=mysongs.get(position).getName();
        String songName=intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position=bundle.getInt("position",0);

        Uri uri =Uri.parse(mysongs.get(position).toString());

        mymediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);
        mymediaPlayer.start();
    }

    private void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent= new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void Onlyplaymusic()
    {
        imageView.setBackgroundResource(R.drawable.five);
        mymediaPlayer.start();
        pausePlaybtn.setImageResource(R.drawable.pause);
    }
    private void Onlypausemusic()
    {
        imageView.setBackgroundResource(R.drawable.four);
        mymediaPlayer.pause();
        pausePlaybtn.setImageResource(R.drawable.play);
    }

    private void playpauseSong()
    {
        imageView.setBackgroundResource(R.drawable.four);

        if(mymediaPlayer.isPlaying())
        {
                pausePlaybtn.setImageResource(R.drawable.play);
                mymediaPlayer.pause();
        }
        else
        {
                pausePlaybtn.setImageResource(R.drawable.pause);
                mymediaPlayer.start();
                 imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void playNextsong()
    {
        mymediaPlayer.pause();
        mymediaPlayer.stop();
        mymediaPlayer.release();

        position=((position+1)%mysongs.size());

        Uri uri =Uri.parse(mysongs.get(position).toString());
        mymediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);
        mSongsName=mysongs.get(position).toString();
        songNameTxt.setText(mSongsName);
        mymediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);

        if(mymediaPlayer.isPlaying())
        {
            pausePlaybtn.setImageResource(R.drawable.pause);

        }
        else
        {
            pausePlaybtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void playPrevSong()
    {
        mymediaPlayer.pause();
        mymediaPlayer.stop();
        mymediaPlayer.release();

        position=((position-1<0)?(mysongs.size()-1):(position-1));

        Uri uri =Uri.parse(mysongs.get(position).toString());
        mymediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);
        mSongsName=mysongs.get(position).toString();
        songNameTxt.setText(mSongsName);
        mymediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);
        if(mymediaPlayer.isPlaying())
        {
            pausePlaybtn.setImageResource(R.drawable.pause);

        }
        else
        {
            pausePlaybtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }
    }

}

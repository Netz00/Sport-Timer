package com.example.sporttimer;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {


    long odgoda=3000;
    boolean odgoda_1=true;

    long trajanjeRunde=6000;
    long trajanjePauze=3000;
    int brojRundi=4;
    boolean rundaPauza=true;
    boolean kraj=false;

    int runda=1;
    TextView timerTextView;
    TextView roundTextView;
    Button b;
    long startTime = 0;


    long passedTime = 0;
    long delayIspisa=200;
    long millis;


    private SoundPool soundPool;
    private int soundID1,soundID2;
    boolean loaded = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;
    int counter;



    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            millis = passedTime+(System.currentTimeMillis() - startTime);

            do{
                ispis(racun(millis));
            }while(racun(millis)==-1);//da ponovi za jedanput u slucaju 5og uvjeta

            if(!kraj)timerHandler.postDelayed(this, delayIspisa);//ponovo izvrši sam sebe za 500ms
            else{
                kraj=false;
                runda=1;
                rundaPauza=true;

            }

        }

        public long racun(long millis2) {


            if(millis2<odgoda && odgoda_1)//ODGODA POCETKA MECA
            {
                return odgoda-millis2;
            }
            else if(odgoda_1){
                playSound(soundID1);
                odgoda_1=false;
            }


            if(millis2<(trajanjeRunde*runda+trajanjePauze*(runda-1)+odgoda) && rundaPauza)//RUNDA
            {
                return (trajanjeRunde*runda+trajanjePauze*(runda-1)+odgoda)-millis2;
            }
            else if (rundaPauza){//KRAJ RUNDE
                playSound(soundID1);
                rundaPauza=false;
            }

            if(runda==brojRundi){//KRAJ MECA
                kraj=true;
                playSound(soundID2);
                return 0;
            }

            if(millis2<((trajanjePauze+trajanjeRunde)*runda+odgoda) && !rundaPauza)//PAUZA
            {
                return ((trajanjePauze+trajanjeRunde)*runda+odgoda)-millis2;
            }
            else if (!rundaPauza){//KRAJ PAUZE
                runda++;
                rundaPauza=true;
                playSound(soundID1);
            }

            return -1;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            brojRundi=settings.getInt("br_rundi", 4);

            trajanjeRunde=settings.getLong("vrime_runde", 6000);

            trajanjePauze=settings.getLong("vrime_pauze", 3000);

            odgoda=settings.getLong("delay", 3000);




        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;



        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // the counter will help us recognize the stream id of the sound played  now
        counter = 0;

        // Load the sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID1 = soundPool.load(this, R.raw.short_ring, 1);
        soundID2 = soundPool.load(this, R.raw.long_ring, 1);




        timerTextView = (TextView) findViewById(R.id.prikazVremena);
        roundTextView = (TextView) findViewById(R.id.textView);

        ispis(trajanjeRunde);

        b = (Button) findViewById(R.id.button);//start-pause-continue
        b.setText("start");
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                Button r = (Button) findViewById(R.id.button1);
                r.setText("reset");
                if (b.getText().equals("pause")) {

                    passedTime=millis;
                    timerHandler.removeCallbacks(timerRunnable);//izbrisi sve zadatke od timerRunnable na cekanju
                    b.setText("continue");
                }

                else {
                    startTime = System.currentTimeMillis();
                    kraj=false;
                    timerHandler.post(timerRunnable);//zapocni jedno mjerenje vremena
                    b.setText("pause");


                }

            }
        });


        Button r = (Button) findViewById(R.id.button1);//reset
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button r = (Button) v;

                if (r.getText().equals("settings")) {//otvori postavke vremena i rundi
                    settings();
                }

                b = (Button) findViewById(R.id.button);
                timerHandler.removeCallbacks(timerRunnable);//izbrisi sve zadatke od timerRunnable na cekanju
                startTime=0;
                passedTime=0;
                runda=1;
                rundaPauza=true;
                kraj=false;
                odgoda_1=true;
                ispis(trajanjeRunde);
                roundTextView.setText(String.valueOf(runda));
                b.setText("start");
                r.setText("settings");



            }
        });

    }

    public void playSound(int soundID) {
        // Is the sound loaded does it already play?


        if(actVolume!=(float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        {
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = actVolume / maxVolume;
        }
        // AudioManager audio settings for adjusting the volume



        if (loaded ) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            counter = counter++;
        }
    }


    public void settings() {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    public void ispis(long millis3) {
        int seconds = (int) (millis3 / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        roundTextView.setText(String.valueOf(runda));
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
        if(kraj)b.setText("restart");
    }

    @Override
    public void onStop() {
        passedTime = millis;
        timerHandler.removeCallbacks(timerRunnable);
        b.setText("continue");
        super.onStop();
    }

    @Override
    public void onRestart() {

        startTime = System.currentTimeMillis();
        kraj=false;
        timerHandler.post(timerRunnable);//zapocni jedno mjerenje vremena
        b.setText("pause");

        super.onRestart();
    }

    @Override
    public void onDestroy() {
        passedTime = 0;
        timerHandler.removeCallbacks(timerRunnable);
        b.setText("start");
        super.onDestroy();
    }


}
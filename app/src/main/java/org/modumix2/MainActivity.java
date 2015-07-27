package org.modumix2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.media.audiofx.Equalizer;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    MediaPlayer pp;
    Button play;
    Button pause;
    Button cue;
    Equalizer eq;
    LinearLayout eqLayout;

    int po= 0; // controlar la posición
    int corte =0;

    //int MAX_VOLUME = 101;
    float volumen=100;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play=(Button)findViewById(R.id.buttonPlay);
        pause=(Button)findViewById(R.id.buttonPause);
        cue=(Button)findViewById(R.id.buttonCue);

        pp = MediaPlayer.create(this, R.raw.vection);
        pp.start();

        eq=new Equalizer(0, pp.getAudioSessionId());
        eq.setEnabled(true);
        setupEq();

/*
        eq=new Equalizer(0, 0);
        eq.setEnabled(true);
        setupEq();*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()&&pp!=null){
            eq.release();
            pp.release();
            pp=null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void limpiar(){

        if(pp!=null){
            pp.release();
        }

    }

    public void metodoPlay (View v){

        if (pp != null){
            pp.start();
            return;
        }
        limpiar();
        pp = MediaPlayer.create(this, R.raw.vection);
        pp.start();
        eq=new Equalizer(0, pp.getAudioSessionId());
        eq.setEnabled(true);
        setupEq();
        //eq=new Equalizer(0, pp.getAudioSessionId());
       // eq.setEnabled(true);


    }

    public void metodoPause (View v) {

        if (pp != null && pp.isPlaying()) {
            po = pp.getCurrentPosition();
            pp.pause();


        }
    }


    public void metodoCue (View v){

        if(pp!=null){
            if(!pp.isPlaying()) {
                corte=po;
            }
            }
            pp.pause();
            pp.seekTo(corte);

            po=corte;
        }

    public void volumenMas (View v){
        if(volumen!=100)
        {
            volumen=volumen+10.0f;
        }
        //float volume = (float) (1 - (Math.log(MAX_VOLUME - volumen) / Math.log(MAX_VOLUME)));
        if(pp!=null){
        pp.setVolume(volumen/100, volumen/100);}
    }

    public void volumenMenos (View v){
        if(volumen!=0){
            volumen=volumen-10.0f;
        }

        //float volume = (float) (1 - (Math.log(MAX_VOLUME - volumen) / Math.log(MAX_VOLUME)));
        if(pp!=null){
        pp.setVolume(volumen/100.0f, volumen/100.0f);}
    }

    public void browse() {
        Intent intent = new Intent();
        intent.setType("*/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent = Intent.createChooser(intent, "Select file");
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String[] mimetypes = { "audio/*", "video/*" };
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        //startActivityForResult(intent, Constants.REQUEST_BROWSE);
    }

    public void setupEq() {
        eqLayout=(LinearLayout)findViewById(R.id.eqLayout);
        short numBands=eq.getNumberOfBands();
        Log.d(TAG,"BANDS: "+numBands);
        final short lowerEqBand=eq.getBandLevelRange()[0];
        final short upperEqBand=eq.getBandLevelRange()[1];

        //short i= (short) (numBands-1);
        for(short i=(short) (numBands-1);i>=0;i--) {

            final short eqBandIndex=i;
            TextView freqHeader = new TextView(this);
            freqHeader.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            freqHeader.setGravity(Gravity.CENTER_HORIZONTAL);
            freqHeader.setText((eq.getCenterFreq(eqBandIndex)) / 1000 + "Hz");
            Log.d(TAG, "Hz: " + eq.getCenterFreq(eqBandIndex));

            eqLayout.addView(freqHeader);

            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView lowerEqBandTextView = new TextView(this);
            lowerEqBandTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            lowerEqBandTextView.setText((lowerEqBand / 100) + "dB");
            Log.d(TAG, "LowBandEq: " + lowerEqBand);
            TextView upperEqBandTextView = new TextView(this);
            upperEqBandTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            upperEqBandTextView.setText((upperEqBand / 100) + "dB");
            Log.d(TAG, "UpBandEq: " + lowerEqBand);

            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight=1;

            SeekBar seekBar=new SeekBar(this);
            seekBar.setId(i);//¿?
            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqBand - lowerEqBand);
            seekBar.setProgress(eq.getBandLevel(eqBandIndex));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    eq.setBandLevel(eqBandIndex, (short) (progress + lowerEqBand));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            seekBarRowLayout.addView(lowerEqBandTextView);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqBandTextView);
            eqLayout.addView(seekBarRowLayout);

        }







    }
}

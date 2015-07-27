package org.modumix2;

import android.content.Intent;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.media.audiofx.Equalizer;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class Deck extends Fragment {
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private static final int REQUEST_BROWSE = 9500;
    private OnFragmentInteractionListener mListener;
    Visualizer visualizer;
    private LinearLayout visualizerLayout;
    private Wave wave;

    MediaPlayer pp;
    Button play;
    Button pause;
    Button cue;
    Button browse;
    Button bendMenos;
    Button bendMas;
    Equalizer eq;
    SeekBar seekBarVolumen;
    SeekBar seekBarCue;
    ScrollView scrollViewEq;
    CheckBox extMixer;

    Uri soundUri;

    Handler mHandler;

    int milis=500;

    int seekBackwardTime=10;
    int seekForwardTime=10;


    int po= 0; // controlar la posicion
    int corte =0;

    //int MAX_VOLUME = 101;
    float volume=0;
    float volumeCue=0;

    private static final String TAG = "Fragment";

    public Deck() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_deck, container, false);
        bendMenos=(Button)rootView.findViewById(R.id.bendMenos);
        bendMas=(Button)rootView.findViewById(R.id.bendMas);
        seekBarVolumen=(SeekBar)rootView.findViewById(R.id.seekBarVolumen);
        seekBarCue=(SeekBar)rootView.findViewById(R.id.seekBarCue);
        play=(Button)rootView.findViewById(R.id.buttonPlay);
        cue=(Button)rootView.findViewById(R.id.buttonCue);
        browse=(Button)rootView.findViewById(R.id.buttonBrowse);
        extMixer=(CheckBox)rootView.findViewById(R.id.extMixer);


        scrollViewEq =(ScrollView)rootView.findViewById(R.id.scrollViewEq);
        visualizerLayout=(LinearLayout)rootView.findViewById(R.id.visualizerLayout);

        seekBarVolumen.setMax(100);
        seekBarVolumen.setProgress(0);
        seekBarVolumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volume = (float) progress;
                if (pp != null) {
                    pp.setVolume(volumeCue / 100, volume / 100);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarCue.setMax(100);
        seekBarCue.setProgress(0);
        seekBarCue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //cambiar volumen
                volumeCue = (float) progress;
                if (pp != null) {
                    pp.setVolume(volumeCue / 100, volume / 100);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bendMenos.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, milis);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (pp != null) {
                        int currentPosition = pp.getCurrentPosition();
                        if (currentPosition - seekBackwardTime >= 0) {
                            pp.seekTo(currentPosition - seekBackwardTime);
                        } else {
                            pp.seekTo(0);
                        }
                    }
                    mHandler.postDelayed(this, milis);
                }
            };

        });


        bendMas.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, milis);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    if (pp != null) {
                        int currentPosition = pp.getCurrentPosition();
                        if (currentPosition + seekForwardTime <= pp.getDuration()) {
                            pp.seekTo(currentPosition + seekBackwardTime);
                        } else {
                            pp.seekTo(pp.getDuration());
                        }
                    }
                    mHandler.postDelayed(this, milis);
                }
            };

        });


        play.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (pp != null && pp.isPlaying()) {
                    po = pp.getCurrentPosition();
                    pp.pause();
                    play.setText("Play");
                    return;
                }
                if (pp != null) {
                    pp.start();
                    play.setText("Pause");
                    return;
                }
                limpiar();
                play.setText("Pause");

                if(soundUri!=null){
                    pp=new MediaPlayer();


                    try {
                        pp.setDataSource(getActivity().getApplicationContext(), soundUri);
                        pp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    pp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if(extMixer.isChecked()){
                                if(getId()==R.id.fragment2)
                                {
                                    Log.d(TAG, "Entro");
                                mp.setVolume((float)1,(float)0);
                                } else
                                {
                                    Log.d(TAG, "Entro");
                                mp.setVolume((float)0,(float)1);}
                                seekBarCue.setEnabled(false);
                                seekBarVolumen.setEnabled(false);
                            }else {
                                seekBarCue.setEnabled(true);
                                seekBarVolumen.setEnabled(true);
                                mp.setVolume((float) seekBarCue.getProgress() / 100, (float) seekBarVolumen.getProgress() / 100);

                                eq = new Equalizer(0, mp.getAudioSessionId());
                                eq.setEnabled(true);
                                //setupVisualizer(pp);
                                setupEq();
                            }
                            mp.start();
                        }
                    });
                }
            }

        });


        cue.setOnClickListener(new View.OnClickListener() {

                public void onClick (View view){

                    if (pp != null) {
                        if (!pp.isPlaying()) {
                            corte = po;
                            return;
                        }
                        pp.pause();
                        pp.seekTo(corte);
                        po = corte;
                        play.setText("Play");
                    }


                }
        });

        browse.setOnClickListener(new View.OnClickListener() {

            public void onClick (View view){
                Intent intent = new Intent();
                intent.setType("*/*");
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent = Intent.createChooser(intent, "Select file");
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    String[] mimetypes = { "audio/*" };
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                }
                startActivityForResult(intent, REQUEST_BROWSE);
            }});

        return  rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BROWSE
                && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                soundUri=uri;
                Log.d(TAG,soundUri.toString());
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // ODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }*/

    public void limpiar(){

        if(pp!=null){
            pp.release();
        }

    }


    public void setupEq() {
       // eqLayout=(LinearLayout)getActivity().findViewById(R.id.eqLayout);
        short numBands=eq.getNumberOfBands();
        Log.d(TAG,"BANDS: "+numBands);
        final short lowerEqBand=eq.getBandLevelRange()[0];
        final short upperEqBand=eq.getBandLevelRange()[1];

        LinearLayout eqLayout = new LinearLayout(getActivity().getApplicationContext());
        eqLayout.setOrientation(LinearLayout.VERTICAL);


        for(short i=(short) (numBands-1);i>=0;i--) {

            final short eqBandIndex=i;
            TextView freqHeader = new TextView(getActivity().getApplicationContext());
            freqHeader.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            freqHeader.setGravity(Gravity.CENTER_HORIZONTAL);
            freqHeader.setText((eq.getCenterFreq(eqBandIndex)) / 1000 + "Hz");
            Log.d(TAG, "Hz: " + eq.getCenterFreq(eqBandIndex));

            eqLayout.addView(freqHeader);//?????

            LinearLayout seekBarRowLayout = new LinearLayout(getActivity().getApplicationContext());
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight=1;

            SeekBar seekBar=new SeekBar(getActivity().getApplicationContext());
            seekBar.setId(i);
            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqBand - lowerEqBand);
            seekBar.setProgress((upperEqBand - lowerEqBand)/2);

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

            seekBarRowLayout.addView(seekBar);
            eqLayout.addView(seekBarRowLayout);

        }

        scrollViewEq.addView(eqLayout);

    }

    private void setupVisualizer(MediaPlayer pp) {
        //visualizerLayout = (LinearLayout) getActivity().findViewById(R.id.visualizerLayout);
        // Create a VisualizerView to display the audio waveform for the current settings
        wave = new Wave(getActivity().getApplicationContext());
        wave.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        visualizerLayout.addView(wave);

        // Create the Visualizer object and attach it to our media player.
        if(pp!=null){
            visualizer = new Visualizer(pp.getAudioSessionId());
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                  int samplingRate) {
                    wave.updateVisualizer(bytes);
                }

                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);

        }


    }

}

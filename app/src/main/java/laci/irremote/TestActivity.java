package laci.irremote;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Handlers.Signal.SignalDecoder;
import laci.irremote.Handlers.Signal.SignalGenerator;
import laci.irremote.Handlers.Signal.SignalRecorder;
import laci.irremote.Views.SignalGraph;

public class TestActivity extends AppCompatActivity {
/*
    SignalGenerator SG;
    SignalRecorder SR;
    EditText horoffset;
    EditText pulseoff;
    Button generate;
    Button record;
    Integer[] Signal;
    AudioManager mAudioManager;
    SignalDecoder signalDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        SG = new SignalGenerator(38000/2);
        SR = new SignalRecorder();
        signalDecoder = new SignalDecoder();
        horoffset = (EditText) findViewById(R.id.hor_offset);
        pulseoff = (EditText) findViewById(R.id.pulse_offset);
        generate = (Button) findViewById(R.id.generate_btn);
        record = (Button) findViewById(R.id.record_btn);
        horoffset.setText(100 + "");
        pulseoff.setText(10+"");
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setWiredHeadsetOn(true);
    }

    public void GenerateFrequency(View v){
        //double Signal[] = {24,6,6,6.5,11,7,5.5,6,6,6,13,6,6,6,6,6,6,6.5,6,6,6,6,6,6,12,6,12,6,6,6,6,6};
        //double Signal[] = {30000};
        if(Signal == null) return;
        if(Signal.length == 0){
            Log.i("SIGNAL", "nothing recorded");
            return;
        }
        Log.i("SIGNAL", "lenght of signal: "  + Signal.length);
        String sig = "{";
        for(Integer s : Signal) sig += s + ",";
        Log.i("SIGNAL", sig);
        SG.SetHorOff(Integer.parseInt(horoffset.getText().toString()));
        SG.SetPulseOff(Integer.parseInt(pulseoff.getText().toString()));
        SG.Generate(Signal);

        SG.Emit();

        /*
        Thread find = new Thread(new Runnable() {
            public void run() {
                for(int i = 80; i <= 190; i += 10){
                    for(int j = 0; j <= 30; j +=10){
                        //horoffset.setText(i + "");
                        //pulseoff.setText(j + "");
                        SG.SetHorOff(i);
                        SG.SetPulseOff(j);
                        SG.Generate(Signal);
                        SG.Emit();
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "find Thread");
        find.start();


    }

    public void RecordSignal(View v) {
        if(record.getText().equals("RECORD")){
            generate.setEnabled(false);

            SR.startRecording(mAudioManager);

            record.setText("STOP");
        }else if(record.getText().equals("STOP")){
            SR.stopRecording();
            ArrayList<Short> data = SR.getRecorderData();
            Signal = signalDecoder.cutDecodedSignal(signalDecoder.decodeRawSignal(data));

            record.setText("RECORD");
            generate.setEnabled(true);
        }


    }
    */


    private FrameLayout SignalGraphHolder;
    private SignalGraph signal_graph;
    private Button record;
    private EditText NameEdit;
    private int SignalID;
    private laci.irremote.Handlers.Database.DataStructures.Signal signalInfo;

    private MVC_Controller Controller;
    private SignalRecorder SR;
    private Integer[] Signal;
    private AudioManager mAudioManager;
    private SignalDecoder signalDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_decoding);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Signal = new Integer[]{0};

        SR = new SignalRecorder();
        signalDecoder = new SignalDecoder();
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setWiredHeadsetOn(true);

        SignalGraphHolder = (FrameLayout) findViewById(R.id.signal_graph_holder);
        record = (Button) findViewById(R.id.record_btn);
        signal_graph = new SignalGraph(this, Signal);

        SignalGraphHolder.addView(signal_graph);


    }


    public void onBackBtnClick(View view){ finish(); }


    //TODO CONTROLLER
    public void RecordSignal(View v) {
        if(record.getText().equals("RECORD")){
            SR.startRecording(mAudioManager);

            record.setText("STOP");
        }else if(record.getText().equals("STOP")){
            SR.stopRecording();
            ArrayList<Short> data = SR.getRecorderData();
            //Signal = signalDecoder.decodeRawSignal(data);
            Signal = signalDecoder.cutDecodedSignal(signalDecoder.decodeRawSignal(data));
            if(Signal != null){
                signal_graph.UpdateSignal(Signal);
                signal_graph.invalidate();
            }else{
                Toast.makeText(TestActivity.this, "Recording Unsuccessful", Toast.LENGTH_LONG).show();
            }

            record.setText("RECORD");
        }


    }


}

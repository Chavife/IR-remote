package laci.irremote;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.StringReader;
import java.util.ArrayList;

import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Handlers.Signal.SignalDecoder;
import laci.irremote.Handlers.Signal.SignalRecorder;
import laci.irremote.Views.RawSignalGraph;
import laci.irremote.Views.SignalGraph;

public class GlobalSettingActivity extends AppCompatActivity {

    private Button record;
    private FrameLayout RawSignalGraphHolder;
    private RawSignalGraph raw_signal_graph;
    private FrameLayout SignalGraphHolder;
    private SignalGraph signal_graph;
    private FrameLayout CutSignalGraphHolder;
    private SignalGraph cut_signal_graph;

    private EditText UpThreshEdit;
    private EditText BotThreshEdit;
    private EditText MinLengthEdit;


    private MVC_Controller Controller;
    private SignalRecorder SR;
    private ArrayList<Short> RawSignal = new ArrayList<>();
    private Integer[] Signal = {0};
    private AudioManager mAudioManager;
    private SignalDecoder signalDecoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Controller = new MVC_Controller(this);

        SR = new SignalRecorder();
        signalDecoder = new SignalDecoder(Controller.getDecodeUpperThreshold(), Controller.getDecodeBottomThreshold(), Controller.getDecodeMinLength());
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setWiredHeadsetOn(true);

        record = (Button) findViewById(R.id.record_btn);

        UpThreshEdit = (EditText) findViewById(R.id.up_thresh_edit);
        BotThreshEdit = (EditText) findViewById(R.id.bot_thresh_edit);
        MinLengthEdit = (EditText) findViewById(R.id.min_length_edit);

        UpThreshEdit.setText(Controller.getDecodeUpperThreshold()+"");
        BotThreshEdit.setText(Controller.getDecodeBottomThreshold()+"");
        MinLengthEdit.setText(Controller.getDecodeMinLength()+"");

        UpThreshEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double value = 0;
                if(!s.toString().isEmpty()) value = Double.parseDouble(s.toString());
                signalDecoder.setUpperThreshold(value);
                Controller.setDecodeUpperThreshold(value);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        BotThreshEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double value = 0;
                if(!s.toString().isEmpty()) value = Double.parseDouble(s.toString());
                signalDecoder.setBottomThreshold(value);
                Controller.setDecodeBottomThreshold(value);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        MinLengthEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = 0;
                if(!s.toString().isEmpty()) value = Integer.parseInt(s.toString());
                signalDecoder.setMinLength(value);
                Controller.setDecodeMinLength(value);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        RawSignalGraphHolder = (FrameLayout) findViewById(R.id.raw_signal_graph_holder);
        SignalGraphHolder = (FrameLayout) findViewById(R.id.signal_graph_holder);
        CutSignalGraphHolder = (FrameLayout) findViewById(R.id.cut_signal_graph_holder);

        raw_signal_graph = new RawSignalGraph(this, RawSignal);
        signal_graph = new SignalGraph(this, Signal);
        cut_signal_graph = new SignalGraph(this, Signal);

        RawSignalGraphHolder.addView(raw_signal_graph);
        SignalGraphHolder.addView(signal_graph);
        CutSignalGraphHolder.addView(cut_signal_graph);
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

            raw_signal_graph.UpdateSignal(signalDecoder.cutSilenceRawSignal(data));
            raw_signal_graph.invalidate();

            signal_graph.UpdateSignal(signalDecoder.decodeRawSignal(data));
            signal_graph.invalidate();


            Signal = signalDecoder.cutDecodedSignal(signalDecoder.decodeRawSignal(data));
            if(Signal != null){
                cut_signal_graph.UpdateSignal(Signal);
                cut_signal_graph.invalidate();
            }else{
                Toast.makeText(GlobalSettingActivity.this, "Recording Unsuccessful", Toast.LENGTH_SHORT).show();
            }

            record.setText("RECORD");
        }


    }
}

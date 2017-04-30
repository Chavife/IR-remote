package laci.irremote;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.DeviceSetting;
import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Handlers.Signal.SignalDecoder;
import laci.irremote.Handlers.Signal.SignalRecorder;
import laci.irremote.Views.SignalGraph;

public class SignalDecodingActivity extends AppCompatActivity {

    private FrameLayout SignalGraphHolder;
    private SignalGraph signal_graph;
    private Button record;
    private EditText NameEdit;
    private EditText Repeat;
    private Spinner Setting;
    private int SignalID;
    private ArrayList<DeviceSetting> Devices;
    private Signal signalInfo;

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

        Controller = new MVC_Controller(this);
        NameEdit = (EditText) findViewById(R.id.name_edit);
        Repeat = (EditText) findViewById(R.id.repeat_edit);
        Setting = (Spinner) findViewById(R.id.device_spinner);

        final Bundle SignalInfo = getIntent().getExtras();
        if(SignalInfo == null) return;

        SignalID = SignalInfo.getInt("ID");
        if(SignalID == -1){
            SignalID = Controller.createNewSignal();
        }
        signalInfo  = Controller.getSignal(SignalID);
        Signal = signalInfo.getSignal();
        NameEdit.setText(signalInfo.getName());
        Repeat.setText(signalInfo.getRepeat()+"");


        Devices = Controller.getAllDevices();
        ArrayList<String> Settings = new ArrayList<>();
        int current_device = 0;
        for(int i = 0 ; i < Devices.size(); i++){
            if(signalInfo.getSetting_id() == Devices.get(i).getDeviceID()) current_device = i;
            Settings.add(Devices.get(i).getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Settings);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Setting.setAdapter(adapter);

        Setting.setSelection(current_device);



        Setting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Controller.setSignalsDevice(SignalID, Devices.get(position).getDeviceID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        NameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setSignalName(SignalID,s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        Repeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setSignalRepeat(SignalID,s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        SR = new SignalRecorder();
        signalDecoder = new SignalDecoder(Controller.getDecodeUpperThreshold(), Controller.getDecodeBottomThreshold(), Controller.getDecodeMinLength());
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
                Controller.setSignalSignal(SignalID,Signal);
            }else{
                Toast.makeText(SignalDecodingActivity.this, "Recording Unsuccessful", Toast.LENGTH_SHORT).show();
            }

            record.setText("RECORD");
        }


    }

}

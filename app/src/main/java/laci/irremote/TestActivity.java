package laci.irremote;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import laci.irremote.Handlers.Signal.SignalGenerator;
import laci.irremote.Handlers.Signal.SignalRecorder;

public class TestActivity extends AppCompatActivity {

    SignalGenerator SG;
    SignalRecorder SR;
    EditText horoffset;
    EditText pulseoff;
    Button generate;
    Button record;
    Integer[] Signal;
    AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        SG = new SignalGenerator(38000/2);
        SR = new SignalRecorder();
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

        */

    }

    public void RecordSignal(View v) {
        if(record.getText().equals("RECORD")){
            generate.setEnabled(false);

            SR.startRecording(mAudioManager);

            record.setText("STOP");
        }else if(record.getText().equals("STOP")){
            SR.stopRecording();
            Signal = SR.returnDecodedData();

            record.setText("RECORD");
            generate.setEnabled(true);
        }


    }
}

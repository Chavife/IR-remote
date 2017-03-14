package laci.irremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import laci.irremote.Handlers.SignalGenerator;

public class TestActivity extends AppCompatActivity {

    SignalGenerator FG;
    EditText horoffset;
    EditText pulseoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        FG = new SignalGenerator(18000);
        horoffset = (EditText) findViewById(R.id.hor_offset);
        pulseoff = (EditText) findViewById(R.id.pulse_offset);
        horoffset.setText(90 + "");
        pulseoff.setText(25+"");
    }

    public void GenerateFrequency(View v) throws InterruptedException {
        //ToneGenerator TG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //TG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        double Signal[] = {24,6,6,6.5,11,7,5.5,6,6,6,13,6,6,6,6,6,6,6.5,6,6,6,6,6,6,12,6,12,6,6,6,6,6};
        //double Signal[] = {30000};
        FG.SetHorOff(Integer.parseInt(horoffset.getText().toString()));
        FG.SetPulseOff(Integer.parseInt(pulseoff.getText().toString()));
        FG.Generate(Signal);
        FG.Emit();
    }
}

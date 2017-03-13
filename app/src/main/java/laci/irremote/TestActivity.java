package laci.irremote;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void GenerateFrequency(View v){
        ToneGenerator TG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        TG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
    }
}

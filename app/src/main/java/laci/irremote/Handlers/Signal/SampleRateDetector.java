package laci.irremote.Handlers.Signal;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

/**
 * Simple class for Extension of classes where wee need the maximal Devices Sample Rate
 */

public class SampleRateDetector {

    public int DEVICE_MAX_SAMPLE_RATE = get_max_sample_rate();

    private int get_max_sample_rate(){
        int MAX_SAMPLE_RATE = 8000;
        for (int rate : new int[] {192000,176400,96000,88200,48000,44100,32000,22050,16000}) {  // add the rates you wish to check against
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                Log.i("SAMPLE", "" + bufferSize);
                MAX_SAMPLE_RATE = rate;
                break;
            }
        }
        return MAX_SAMPLE_RATE;
    }
}

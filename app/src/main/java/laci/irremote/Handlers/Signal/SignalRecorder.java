package laci.irremote.Handlers.Signal;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;


/**
 * This Class handles all the recording of an Signal and if wanted returns the raw recorded data
 * for further handling. Usually the SignalDecoder class gets it.
 */

public class SignalRecorder extends SampleRateDetector {
    private final int RECORDER_SAMPLERATE = DEVICE_MAX_SAMPLE_RATE;
    //private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    ArrayList<Short> Data = null;

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format


    public SignalRecorder() {
    }

    public void startRecording(AudioManager am) {

        Data = new ArrayList<>();

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            am.setBluetoothScoOn(false);
            am.setSpeakerphoneOn(false);
            am.setMicrophoneMute(false);
            am.setWiredHeadsetOn(true);

        }

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioData();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    /**Writes the recording array to the raw datastructure of this class */
    private void writeAudioData() {

        short sData[] = new short[BufferElements2Rec];

        while (isRecording) {
            recorder.read(sData, 0, BufferElements2Rec);
            for(short value : sData) Data.add(value);
        }

    }

    /**Stops the recording thread*/
    public void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }

    }

    /**Returns the raw recorded Data*/
    public ArrayList<Short> getRecorderData(){
        return Data;
    }

}
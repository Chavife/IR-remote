package laci.irremote.Handlers.Signal;


import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

import laci.irremote.Handlers.Signal.SampleRateDetector;

import static junit.framework.Assert.assertNotNull;


/**
 * Created by laci on 17.3.2017.
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

            AudioDeviceInfo[] device = am.getDevices(am.GET_DEVICES_INPUTS);


            Log.i("devices count" , device.length + "");

            for(int i = 0 ; i < device.length; i++){
                String shit = device[i].getId() + "";
                Log.i("DEVICE", " at " + i + "  this device: " + shit);
            }

           // recorder.setPreferredDevice(device[4]);
        }

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile() {

        short sData[] = new short[BufferElements2Rec];

        while (isRecording) {
            recorder.read(sData, 0, BufferElements2Rec);

            for(short value : sData) Data.add(value);
        }

    }

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

    public Short[] returnData(){
        return Data.toArray(new Short[Data.size()]);
    }

    public Integer[] returnDecodedData(){
        boolean signalStarted = false;
        boolean switched = true;
        double threshold = 0.1;
        LinkedList<Integer> DecodedSignal = new LinkedList<>();
        DecodedSignal.add(new Integer(-70));
        for(short value : Data){

            double amp = value/32765.0; //amplitude value in range of 0-1

            if(amp < -threshold && !signalStarted) signalStarted = true; //we cut the silence from the beggining

            if(signalStarted){
                if(switched && amp < -threshold && DecodedSignal.getLast() <= -30){
                    DecodedSignal.add(new Integer(0));
                    switched = false;
                }

                if(!switched && amp > threshold && DecodedSignal.getLast() >= 50){
                    DecodedSignal.add(new Integer(0));
                    switched = true;
                }

                if(!switched){
                    DecodedSignal.set(DecodedSignal.size()-1,DecodedSignal.getLast()+1);
                }else if (switched){
                    DecodedSignal.set(DecodedSignal.size()-1,DecodedSignal.getLast()-1);
                }
            }
        }
        return DecodedSignal.toArray(new Integer[DecodedSignal.size()]);
    }

}



























/*
    //convert short to byte
    private byte[] short2byte(short[] sData) {

        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }
    */

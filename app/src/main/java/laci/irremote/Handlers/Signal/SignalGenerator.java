package laci.irremote.Handlers.Signal;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * This file contains Handlers for generating specific
 * frequencies, horizontal alignments, frequency length etc.
 */

public class SignalGenerator extends SampleRateDetector{


    private double duration = 0; // seconds
    private final int sampleRate = DEVICE_MAX_SAMPLE_RATE;
    //private final int sampleRate = 44100;
    private int numSamples = (int) (duration * sampleRate);
    private double sample[];
    private double Frequency = 18000; // Hz
    private double HorizontalOffset = 90.0; // Degree
    private double PulseOffsetPerc = 0.0; // Percent/100

    private byte MonoGeneratedSnd[];
    private byte StereoGeneratedSnd[];

    /**
     * @param Frequency
     *
     * Constructor for Frequency Generator
     * Frequency parameter gets an Int value of the desired Frequency to generate in Hz
     */
    public SignalGenerator(int Frequency) {
        this.Frequency = Frequency;
    }

    public void Emit(){
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples*4,
                AudioTrack.MODE_STATIC);
        audioTrack.setStereoVolume(1f, 1f);
        audioTrack.write(StereoGeneratedSnd, 0, StereoGeneratedSnd.length);
        audioTrack.play();
    }

    public void SetHorOff(double offset){
        HorizontalOffset = offset;
    }

    public void SetPulseOff(double OffsetinPercent){
        PulseOffsetPerc = OffsetinPercent/100.0;
    }



    public void Generate(Integer SignalinSampleLength[]){


        numSamples = 0;

        for(int i = 0; i < SignalinSampleLength.length; i++){
            numSamples += Math.abs(SignalinSampleLength[i]);
        }

        duration = (numSamples * 1.0)/sampleRate;
        sample = new double[numSamples];
        MonoGeneratedSnd = new byte[2 * numSamples];
        StereoGeneratedSnd = new byte[4 * numSamples];


        double Period = (sampleRate/ Frequency);
        double HalfPeriodCounter = Period/2.0;
        double PulseOffset = (Period/2.0) * (-PulseOffsetPerc);
        boolean LogicalOne = true;
        int position = 0;
        for(int i = 0; i < SignalinSampleLength.length; i++){

            for(int j = 0; j< Math.abs(SignalinSampleLength[i]); j++){

                if(SignalinSampleLength[i] > 0){ //generate carrier frequency
                    if(position < HalfPeriodCounter + PulseOffset && LogicalOne) sample[position] = 1;
                    else if (position < HalfPeriodCounter - PulseOffset && !LogicalOne) sample[position] = -1;
                    else{
                        sample[position] = sample[position-1];
                        LogicalOne = !LogicalOne;
                        HalfPeriodCounter += Period/2.0;
                    }
                }else{ //don`t generate
                    sample[position] = 0;
                }
                position++;
            }
            if(SignalinSampleLength[i] < 0) HalfPeriodCounter += Math.abs(SignalinSampleLength[i]);
        }


        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (double dVal : sample) {
            short val = (short) (dVal * 32767);
            MonoGeneratedSnd[idx++] = (byte) (val & 0x00ff);
            MonoGeneratedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        int HorizontalOffset = (int)((this.HorizontalOffset /360.0) * (sampleRate/ Frequency) * 2.0);

        int j = 0;
        for(int i = HorizontalOffset; i < MonoGeneratedSnd.length-2; i+=2){
            StereoGeneratedSnd[j++] = MonoGeneratedSnd[i];
            StereoGeneratedSnd[j++] = MonoGeneratedSnd[i+1];
            StereoGeneratedSnd[j++] = MonoGeneratedSnd[(i - HorizontalOffset)];
            StereoGeneratedSnd[j++] = MonoGeneratedSnd[(i+1) - HorizontalOffset];
        }

    }

}

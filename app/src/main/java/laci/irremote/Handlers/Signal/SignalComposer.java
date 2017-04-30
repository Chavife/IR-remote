package laci.irremote.Handlers.Signal;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DBHandler;
import laci.irremote.Handlers.Database.DataStructures.DeviceSetting;
import laci.irremote.Handlers.Database.DataStructures.Signal;

/**
 * Created by laci on 23.4.2017.
 */

public class SignalComposer extends SampleRateDetector{
    private SignalGenerator SG;
    private int signalPause = -(int) (0.025 * DEVICE_MAX_SAMPLE_RATE);

    private DBHandler DB;
    byte[] StereoStream;
    int TotalLength = 0;

    public SignalComposer(Context c) {
        SG = new SignalGenerator(38000/2, 90, 0);
        DB = new DBHandler(c,null);
    }

    public void Compose(ArrayList<Signal> Signals){
        if(Signals.size() == 0){
            StereoStream = null;
            return;
        }

        TotalLength = 0;

        for(Signal s : Signals){
            for(int i = 0; i < s.getSignal().length; i++){
                TotalLength += Math.abs(s.getSignal()[i]) * s.getRepeat();
            }
            TotalLength += s.getRepeat() * Math.abs(signalPause);
        }

        StereoStream = new byte[TotalLength*4];
        int streamindex = 0;

        Integer[] Sample;

        for(Signal s : Signals){
            Sample = new Integer[(s.getSignal().length +1) * s.getRepeat()];
            DeviceSetting SignalsDevice = DB.getDevice(s.getSetting_id());
            SG.SetFrequency(SignalsDevice.getFrequency());
            SG.SetHorOff(SignalsDevice.getHor_off());
            SG.SetPulseOff(SignalsDevice.getVer_off());

            for(int j = 0 ; j < s.getRepeat(); j++){
                for (int i = 0 ; i <= s.getSignal().length ; i++){
                    if(i == s.getSignal().length){
                        Sample[i + (j*(s.getSignal().length+1))] = signalPause;
                    }else{
                        Sample[i + (j*(s.getSignal().length+1))] = s.getSignal()[i];
                    }
                }
            }

            byte[] generated = SG.Generate(Sample);

            for(byte g : generated){
                StereoStream[streamindex] = g;
                streamindex++;
            }
        }
    }

    public int getLengthInms(){
        return (int) Math.floor((TotalLength/(DEVICE_MAX_SAMPLE_RATE * 1.0)) * 1000);
    }


    public void Compose(Signal signal, int freq, int hor, int pulse){
        if(signal == null){
            StereoStream = null;
            return;
        }
        TotalLength = 0;

        for(int i = 0; i < signal.getSignal().length; i++){
            TotalLength += Math.abs(signal.getSignal()[i]) * signal.getRepeat();
        }
        TotalLength += signal.getRepeat() * Math.abs(signalPause);

        Integer[] Sample;

        Sample = new Integer[(signal.getSignal().length +1) * signal.getRepeat()];
        SG.SetFrequency(freq);
        SG.SetHorOff(hor);
        SG.SetPulseOff(pulse);

        for(int j = 0 ; j < signal.getRepeat(); j++){
            for (int i = 0 ; i <= signal.getSignal().length ; i++){
                if(i == signal.getSignal().length){
                    Sample[i + (j*(signal.getSignal().length+1))] = signalPause;
                }else{
                    Sample[i + (j*(signal.getSignal().length+1))] = signal.getSignal()[i];
                }
            }
        }

        StereoStream = SG.Generate(Sample);
    }

    public void Play(){
        if(StereoStream == null) return;
        SG.Emit(StereoStream);
    }

}

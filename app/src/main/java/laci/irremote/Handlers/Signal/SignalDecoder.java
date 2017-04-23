package laci.irremote.Handlers.Signal;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by laci on 22.4.2017.
 */

public class SignalDecoder extends SampleRateDetector {

    private double threshold = 0.4;
    private final int sampleRate = DEVICE_MAX_SAMPLE_RATE;
    private final int pauselength = (int) Math.round(sampleRate*0.005);


    public Integer[] decodeRawSignal(ArrayList<Short> Data){
        boolean signalStarted = false;
        boolean switched = true;

        LinkedList<Integer> DecodedSignal = new LinkedList<>();
        DecodedSignal.add(new Integer(-pauselength));
        for(short value : Data){

            double amp = value/32765.0; //amplitude value in range of 0-1

            if(amp < -threshold && !signalStarted) signalStarted = true; //we cut the silence from the beggining

            if(signalStarted){
                if(switched && amp < -threshold && DecodedSignal.getLast() <= -100){
                    DecodedSignal.add(new Integer(0));
                    switched = false;
                }

                if(!switched && amp > threshold && DecodedSignal.getLast() >= 100){
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
        DecodedSignal.remove(DecodedSignal.size()-1);
        return DecodedSignal.toArray(new Integer[DecodedSignal.size()]);
    }

    public Integer[] cutDecodedSignal(Integer[] decodedSignal){
        if(decodedSignal.length < 2) return null;
        ArrayList<ArrayList<Integer>> cutSignals = new ArrayList<>();

        for(Integer a : decodedSignal){
            if(a <= -pauselength){
                cutSignals.add(new ArrayList<Integer>());
            }else{
                cutSignals.get(cutSignals.size()-1).add(a);
            }
        }

        int oneSigLength = cutSignals.get(0).size();
        boolean allsame = false;
        for(int i = 1 ; i < cutSignals.size(); i++){
            if(oneSigLength == cutSignals.get(i).size()){
                allsame = true;
            }else{
                allsame = false;
                break;
            }
        }

        if(!allsame){
            Log.i("DECODING", "Signals are not the same");
            for(int i = 0 ; i < cutSignals.size(); i++){
                Log.i("DECODING", String.valueOf(cutSignals.get(i).size()));
            }
            return null;
        }

        Integer[] cutSignal = new Integer[oneSigLength];
        for(int i = 0; i < oneSigLength; i++){
            int avg = 0;
            for(int j = 0; j < cutSignals.size(); j++){
                avg += cutSignals.get(j).get(i);
                if(j == cutSignals.size() - 1){
                    avg /= cutSignals.size();
                    cutSignal[i] = avg;
                }
            }
        }
        Log.i("DECODING", "Signals decoding successful");
        return  cutSignal;
    }
}

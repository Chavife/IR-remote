package laci.irremote.Handlers.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by laci on 29.4.2017.
 */

public class SPHandler {

    SharedPreferences SP;
    SharedPreferences.Editor editor;

    public SPHandler(Context context) {
        SP = context.getSharedPreferences("Settings", context.MODE_PRIVATE);
        editor = SP.edit();
    }

    public void setUpperThreshold(double threshold){
        editor.putString("DecoderUpperThreshold", threshold + "");
        editor.commit();
    }

    public double getUpperThreshold(){
        return Double.parseDouble(SP.getString("DecoderUpperThreshold", 0.4 + ""));
    }

    public void setBottomThreshold(double threshold){
        editor.putString("DecoderBottomThreshold", threshold + "");
        editor.commit();
    }

    public double getBottomThreshold(){
        return Double.parseDouble(SP.getString("DecoderBottomThreshold", 0.4 + ""));
    }

    public void setMinLength(int minLength){
        editor.putInt("DecoderMinLength", minLength);
        editor.commit();
    }

    public int getMinLength(){
        return SP.getInt("DecoderMinLength", 52);
    }
}

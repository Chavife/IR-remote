package laci.irremote.Handlers.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/** This Class handles all the communication the the Shared Preferences of the Android device
 * other variables of storage should be programed here.
 */

public class SPHandler {

    SharedPreferences SP;
    SharedPreferences.Editor editor;

    /**Constructor*/
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

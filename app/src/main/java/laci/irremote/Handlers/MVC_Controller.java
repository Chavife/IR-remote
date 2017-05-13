package laci.irremote.Handlers;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DBHandler;
import laci.irremote.Handlers.Database.DataStructures.DeviceSetting;
import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.Handlers.Database.SPHandler;

/**
 * Controller for the MVC (Model-View-Controller) Pattern
 *
 * Handles the communication with the DB and SP handlers between the Activities
 * and also some minor calculations.
 */

public class MVC_Controller extends Application {

    DBHandler DB = null;
    SPHandler SP = null;
    int columns = 5;
    int rows = 0;

    public MVC_Controller(Context c) {
        DB = new DBHandler(c,null);
        SP = new SPHandler(c);
    }

    public void DBinit(int screen_width, int screen_height){
        int button_size = (int) Math.floor(screen_width/columns);
        rows = (int) Math.floor(screen_height/(button_size-10));

        DB.initDB(columns,rows,38000,90,0);

    }

    public int getRows(){
        return rows;
    }
    public int getColumns(){
        return columns;
    }

    public RemoteButton[][] getButtons(){ return DB.getButtons(columns,rows); }

    public RemoteButton getButtonInfo(int ID){
        return DB.getButtonInfo(ID);
    }

    public ArrayList<Signal> getButtonSignals(int buttonID){ return DB.getSignalsForButton(buttonID);}

    public ArrayList<Signal> getComplementaryButtonSignals(int buttonID){ return DB.getComplementarySignalsForButton(buttonID);}

    public void removeSignalFromButton(int ButtonID, int SignalID){ DB.removeSignalFromButton(ButtonID, SignalID); }

    public ArrayList<Signal> getAllSignals(){return DB.getAllSignals();}

    public ArrayList<Signal> getSignalsForDevice(int DeviceID){return DB.getSignalsForDevice(DeviceID);}

    public Signal getSignal(int SignalID){ return DB.getSignal(SignalID);}

    public void removeSignal(int SignalID){ DB.removeSignal(SignalID);}

    public void setSignalName(int SignalID, String Text){ DB.setSignalName(SignalID, Text);}

    public void setSignalRepeat(int SignalID, String Text){ DB.setSignalRepeat(SignalID, Text);}

    public void setSignalSignal(int SignalID, Integer[] signal){
        String Signal = "";

        for(Integer s: signal){
            Signal += s + " ";
        }

        Signal = Signal.substring(0,Signal.length()-1);

        DB.setSignalSignal(SignalID, Signal);
    }

    public void addSignalsToButton(int ButtonID, ArrayList<Signal> selectedSignals){
        for(Signal s : selectedSignals){
            DB.addSignalToButton(ButtonID,s.getSignalID());
        }
    }

    public void setSignalsDevice(int SignalID, int DeviceID){DB.setSignalsDevice(SignalID,DeviceID);}

    public int createNewSignal(){ return  DB.createNewSignal();}

    public void setButtonEnabled(int ID, boolean enabled){
        DB.setButtonEnabled(ID, enabled);
    }

    public void setButtonColor(int ID, int Color){
        DB.setButtonColor(ID, Color);
    }

    public void setButtonName(int ID, String Text){
        DB.setButtonName(ID, Text);
    }

    public int createNewDevice(){ return  DB.createNewDevice();}

    public ArrayList<DeviceSetting> getAllDevices(){return DB.getAllDevices();}

    public DeviceSetting getDevice(int DeviceID){ return DB.getDevice(DeviceID);}

    public void removeDevice(int DeviceID){ DB.removeDevice(DeviceID);}

    public void setDeviceName(int DeviceID, String Text){ DB.setDeviceName(DeviceID, Text);}

    public void setDeviceFrequency(int DeviceID, String Text){ DB.setDeviceFrequency(DeviceID, Text);}

    public void setDeviceHorOff(int DeviceID, String Text){ DB.setDeviceHor(DeviceID, Text);}

    public void setDeviceVerOff(int DeviceID, String Text){ DB.setDeviceVer(DeviceID, Text);}


    public void setDecodeUpperThreshold(double threshold){ SP.setUpperThreshold(threshold);}

    public double getDecodeUpperThreshold(){ return SP.getUpperThreshold();}

    public void setDecodeBottomThreshold(double threshold){ SP.setBottomThreshold(threshold);}

    public double getDecodeBottomThreshold(){ return SP.getBottomThreshold();}

    public void setDecodeMinLength(int minLength){ SP.setMinLength(minLength);}

    public int getDecodeMinLength(){ return SP.getMinLength(); }
}

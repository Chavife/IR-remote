package laci.irremote.Handlers;

import android.app.Application;
import android.content.Context;
import android.widget.Button;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DBHandler;
import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.Database.DataStructures.Signal;

/**
 * Controller for the MVC (Model-View-Controller) Pattern
 */

public class MVC_Controller extends Application {

    DBHandler DB = null;
    int columns = 5;
    int rows = 0;

    public MVC_Controller(Context c) {
        DB = new DBHandler(c,null);
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

}

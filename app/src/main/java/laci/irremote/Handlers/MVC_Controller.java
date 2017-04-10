package laci.irremote.Handlers;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.transition.CircularPropagation;
import android.util.Pair;
import android.view.Display;

import laci.irremote.Handlers.Database.DBHandler;
import laci.irremote.Handlers.Database.DataStructures.RemoteButton;

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

    public RemoteButton[][] getButtons(){
        return DB.getButtons(columns,rows);
    }

    public RemoteButton getButtonInfo(int ID){
        return DB.getButtonInfo(ID);
    }

    public void setButtonEnabled(int ID, boolean enabled){
        DB.setButtonEnabled(ID, enabled);
    }

    public void setButtonColor(int ID, int Color){
        DB.setButtonColor(ID, Color);
    }

    public void setButtonText(int ID, String Text){
        DB.setButtonText(ID, Text);
    }

}

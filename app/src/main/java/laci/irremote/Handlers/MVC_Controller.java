package laci.irremote.Handlers;

import android.app.Application;
import android.content.Context;
import android.util.Pair;

import laci.irremote.Handlers.Database.DBHandler;
import laci.irremote.Handlers.Database.DataStructures.RemoteButton;

/**
 * Controller for the MVC (Model-View-Controller) Pattern
 */

public class MVC_Controller extends Application {

    DBHandler DB = null;
    int columns = 5;
    int rows = 0;

    public void init(Context c,int view_height, int view_width){

        DB = new DBHandler(c,null);

        int button_size = (int) Math.floor(view_width/columns);
        rows = (int) Math.floor(view_height/(button_size-10));

        DB.initDB(columns,rows,38000,90,0);

    }

    public RemoteButton[][] getButtons(){
        return DB.getButtons(columns,rows);
    }

    public Pair<Integer,Integer> getButtonPosition(int ID){
        return DB.getButtonPosition(ID);
    }

    public void setButtonEnabled(int x, int y, boolean enabled){
        DB.setButtonEnabled(x, y,enabled);
    }

}

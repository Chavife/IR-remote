package laci.irremote.Handlers.Database.DataStructures;

import android.app.Application;
import android.content.Context;
import android.widget.Button;

/**
 * This Class is a DataStructure of an Button where we can easily store all the variables
 * from the database which we could need for later use
 */

public class RemoteButton extends Application{
    private int ID; /**Database ID of the button*/
    private String Name; /**User defined name which is showed in the RemoteControlActivity*/
    private boolean Enabled; /**Flag if the RemoteButton is enabled or disabled*/



    /**Constructor*/
    public RemoteButton(int ID, String name, boolean enabled) {
        this.ID = ID;
        Name = name;
        Enabled = enabled;
    }

    /*Getters*/
    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    /*This Getter returns an actual Android Button version based on the variables*/
    public Button getAndroidButton(Context context){
        Button btn = new Button(context);
        btn.setEnabled(Enabled);
        btn.setId(ID);
        if(!Enabled){
            btn.setAlpha(0);
        }
        btn.setText(Name);
        //btn.setText("" + ID);
        return btn;
    }

    /*Setters*/
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }


}

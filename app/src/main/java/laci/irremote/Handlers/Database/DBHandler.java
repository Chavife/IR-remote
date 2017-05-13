package laci.irremote.Handlers.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.DeviceSetting;
import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.Database.DataStructures.Signal;

/**
 * This class is the Database Handler for this application,
 * here you can find the creation and communication of the database.
 *
 * This application uses only SQLite data structure for storing data.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ir_remote.db";

    /** Database table of Buttons,
     * where all the Button information is stored about and actual button
     * The RemoteButton data structure class highly depend on this table.*/
    private static final String TABLE_BUTTONS = "Buttons";
    private static final String BUTTON_ID = "button_ID";
    private static final String BUTTON_NAME = "button_name"; /**Name which is Showed in The RemoteControllerActivity*/
    private static final String BUTTON_X = "x_pos";
    private static final String BUTTON_Y = "y_pos";
    private static final String BUTTON_COLOR = "color";
    private static final String BUTTON_ENABLED = "enabled";

    /** Database table of Signals.
     * This is where are all the Signals Stored
     * signal uses a Device setting for optimal function*/
    private static final String TABLE_SIGNALS = "Signals";
    private static final String SIGNAL_ID = "signal_ID";
    private static final String SIGNAL_NAME = "signal_name"; /**Name of an actual Button on the original remote*/
    private static final String SIGNAL_SIGNAL = "signal";
    private static final String SIGNAL_REPEAT = "repeat";
    private static final String SIGNAL_SETTING = "setting_id";

    /** Database table which connects Buttons and Signals together.
     * One Button can emit more signals at once which provides an macro feature*/
    private static final String TABLE_BUTTON_SIGNAL = "Button_Signal";
    private static final String BUTTON_SIGNAL_BUTTON = "button_id";
    private static final String BUTTON_SIGNAL_SIGNAL = "signal_id";

    /** Database of Devices
     * User can define a Device which will hold all the configuration needed
     * for optimal function.
     *
     * All the buttons connected to the Same device can use the same configuration*/
    private static final String TABLE_DEVICES = "Devices";
    private static final String DEVICE_ID = "device_ID";
    private static final String DEVICE_NAME = "device_name"; /**Name of the device*/
    private static final String DEVICE_FREQUENCY = "frequency";
    private static final String DEVICE_HOR_OFF = "hor_offset";
    private static final String DEVICE_VER_OFF = "ver_offset";


    /**Constructor*/
    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    /** This function si called when the Constructor is first ever created
     * Builds the Database described at the Variables*/
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_BUTTONS = "CREATE TABLE IF NOT EXISTS " + TABLE_BUTTONS + "(" +
                BUTTON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BUTTON_NAME + " TEXT, " +
                BUTTON_X + " INTEGER, " +
                BUTTON_Y + " INTEGER, " +
                BUTTON_COLOR + " INTEGER, " +
                BUTTON_ENABLED + " INTEGER" +
                ");";

        String CREATE_TABLE_SIGNALS = "CREATE TABLE IF NOT EXISTS " + TABLE_SIGNALS + "(" +
                SIGNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SIGNAL_NAME + " TEXT, " +
                SIGNAL_SIGNAL + " TEXT, " +
                SIGNAL_REPEAT + " INTEGER, " +
                SIGNAL_SETTING + " INTEGER" +
                ");";

        String CREATE_TABLE_BUTTON_SIGNAL = "CREATE TABLE IF NOT EXISTS " + TABLE_BUTTON_SIGNAL + "(" +
                BUTTON_SIGNAL_BUTTON + " INTEGER, " +
                BUTTON_SIGNAL_SIGNAL + " INTEGER" +
                ");";

        String CREATE_TABLE_SETTINGS = "CREATE TABLE IF NOT EXISTS " + TABLE_DEVICES + "(" +
                DEVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DEVICE_NAME + " TEXT, " +
                DEVICE_FREQUENCY + " INTEGER, " +
                DEVICE_HOR_OFF + " INTEGER, " +
                DEVICE_VER_OFF + " INTEGER" +
                ");";

        db.execSQL(CREATE_TABLE_BUTTONS);
        db.execSQL(CREATE_TABLE_SIGNALS);
        db.execSQL(CREATE_TABLE_BUTTON_SIGNAL);
        db.execSQL(CREATE_TABLE_SETTINGS);
    }

    /**When we update the database version then we drop all the tables and recreate the DB with new rows*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_ALL_TABLES = "DROP TABLE IF EXISTS " + TABLE_BUTTONS + ", " + TABLE_SIGNALS + ", " +
                TABLE_BUTTON_SIGNAL + ", " + TABLE_DEVICES + ";";
        db.execSQL(DROP_ALL_TABLES);
        onCreate(db);
    }


    /** We call this function at the first start of the application
     * we create every button based on the screen size (bigger screen can accept more buttons)
     * and we create and Default Setting of an universal device (this may not work for every device)*/
    public void initDB(int num_of_coll, int num_of_rows, int default_freq, int default_hor_off, int default_ver_off){

        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_BUTTONS + ";";
        Cursor c = db.rawQuery(checkQuery,null);

        c.moveToFirst();

        if(c.isAfterLast()){ //There is nothing in the DB, must Initialize
            ContentValues values;
            db = getWritableDatabase();

            for(int x = 0 ; x < num_of_coll; x++){
                for (int y = 0 ; y < num_of_rows; y++){
                    if(x == num_of_coll-1 && y == num_of_rows-1) break; //bottom right corner is saved for settings Button
                    values = new ContentValues();
                    values.put(BUTTON_ENABLED, 0);
                    values.put(BUTTON_NAME, "");
                    values.put(BUTTON_X, x);
                    values.put(BUTTON_Y, y);
                    values.put(BUTTON_COLOR , -12303292); //Default DARK GRAY Color
                    db.insert(TABLE_BUTTONS,null,values);
                }
            }

            values = new ContentValues();
            values.put(DEVICE_NAME,"DEFAULT");
            values.put(DEVICE_FREQUENCY,default_freq);
            values.put(DEVICE_HOR_OFF,default_hor_off);
            values.put(DEVICE_VER_OFF,default_ver_off);
            db.insert(TABLE_DEVICES,null,values);

        }
        db.close();

    }

    /** returns the x and y position in the GridLayout of the button with the given ID*/
    public RemoteButton getButtonInfo(int ID){
        SQLiteDatabase db = getWritableDatabase();

        String q = "SELECT * FROM " + TABLE_BUTTONS + " WHERE " + BUTTON_ID + " = " + ID + ";";
        Cursor c = db.rawQuery(q,null);

        c.moveToFirst();

        RemoteButton btn = null;
        if(!c.isAfterLast()){
            btn = new RemoteButton(c.getInt(c.getColumnIndex(BUTTON_ID)),
                    c.getString(c.getColumnIndex((BUTTON_NAME))),
                    c.getInt(c.getColumnIndex(BUTTON_ENABLED)) > 0,
                    c.getInt(c.getColumnIndex(BUTTON_COLOR)));
        }

        return btn;
    }

    /**this sets the Button with an given ID to enable ar disable*/
    public void setButtonEnabled(int ID, boolean enabled){
        String query =  "UPDATE " + TABLE_BUTTONS +
                " SET " + BUTTON_ENABLED + " = " + ((enabled) ? 1 : 0 )+
                " WHERE " + BUTTON_ID + " = " + ID + ";"
                ;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(query);
    }

    public void setButtonColor(int ID, int Color){
        String query =  "UPDATE " + TABLE_BUTTONS +
                " SET " + BUTTON_COLOR + " = " + Color+
                " WHERE " + BUTTON_ID + " = " + ID + ";"
                ;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(query);
    }

    public void setButtonName(int ID, String Name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_BUTTONS +
                                                                " SET " + BUTTON_NAME + " = ?" +
                                                                " WHERE " + BUTTON_ID + " = " + ID + ";");
        stmt.bindString(1, Name);
        stmt.execute();
    }


    /**Gives an two dimensional array of RemoteButton structures
     * (see more information in that particular class file)*/
    public RemoteButton[][] getButtons(int columns, int rows){

        RemoteButton[][] Buttons = new RemoteButton[columns][rows];

        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_BUTTONS + ";";
        Cursor c = db.rawQuery(checkQuery,null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            int x = c.getInt(c.getColumnIndex(BUTTON_X));
            int y = c.getInt(c.getColumnIndex(BUTTON_Y));
            Buttons[x][y] = new RemoteButton(c.getInt(c.getColumnIndex(BUTTON_ID)),
                                             c.getString(c.getColumnIndex((BUTTON_NAME))),
                                             c.getInt(c.getColumnIndex(BUTTON_ENABLED)) > 0,
                                             c.getInt(c.getColumnIndex(BUTTON_COLOR)));
            c.moveToNext();
        }

        return Buttons;
    }

    /**This method creates a new signal and returns its ID in the DB*/
    public int createNewSignal(){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SIGNAL_NAME, "");
        values.put(SIGNAL_SIGNAL, "0");
        values.put(SIGNAL_REPEAT, 3); //3 is most common so we make it as default value
        values.put(SIGNAL_SETTING, 1);
        db.insert(TABLE_SIGNALS,null,values);

        String Query = "SELECT MAX(" + SIGNAL_ID + ")"+
                        " FROM " + TABLE_SIGNALS + ";";

        Cursor c = db.rawQuery(Query,null);

        c.moveToFirst();
        int ID = c.getInt(c.getColumnIndex("MAX(" + SIGNAL_ID + ")"));
        return ID;
    }

    /**This method returns all the signals which are assigned to the button whit an given ID*/
    public ArrayList<Signal> getSignalsForButton(int ButtonID){
        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_SIGNALS + " AS si, " + TABLE_DEVICES + " AS se" +
                            " WHERE si." + SIGNAL_ID + " IN" +
                            " (SELECT " + BUTTON_SIGNAL_SIGNAL +
                            " FROM " + TABLE_BUTTON_SIGNAL +
                            " WHERE " + BUTTON_SIGNAL_BUTTON + "=" + ButtonID + ") " + "AND" +
                            " si." + SIGNAL_SETTING + " = se." + DEVICE_ID +";";
        Cursor c = db.rawQuery(checkQuery,null);

        ArrayList<Signal> Signals = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()){
            Signals.add(new Signal(c.getInt(c.getColumnIndex(SIGNAL_ID)),
                                        c.getString(c.getColumnIndex(SIGNAL_NAME)),
                                        c.getString(c.getColumnIndex(SIGNAL_SIGNAL)),
                                        c.getInt(c.getColumnIndex(SIGNAL_REPEAT)),
                                        c.getInt(c.getColumnIndex(SIGNAL_SETTING)),
                                        c.getString(c.getColumnIndex(DEVICE_NAME))));
            c.moveToNext();
        }

        return Signals;
    }

    /**This method returns all the not assigned signals of a button*/
    public ArrayList<Signal> getComplementarySignalsForButton(int ButtonID){
        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_SIGNALS + " AS si, " + TABLE_DEVICES + " AS se" +
                " WHERE si." + SIGNAL_ID + " NOT IN" +
                " (SELECT " + BUTTON_SIGNAL_SIGNAL +
                " FROM " + TABLE_BUTTON_SIGNAL +
                " WHERE " + BUTTON_SIGNAL_BUTTON + "=" + ButtonID + ") " + "AND" +
                " si." + SIGNAL_SETTING + " = se." + DEVICE_ID +";";
        Cursor c = db.rawQuery(checkQuery,null);

        ArrayList<Signal> Signals = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()){
            Signals.add(new Signal(c.getInt(c.getColumnIndex(SIGNAL_ID)),
                    c.getString(c.getColumnIndex(SIGNAL_NAME)),
                    c.getString(c.getColumnIndex(SIGNAL_SIGNAL)),
                    c.getInt(c.getColumnIndex(SIGNAL_REPEAT)),
                    c.getInt(c.getColumnIndex(SIGNAL_SETTING)),
                    c.getString(c.getColumnIndex(DEVICE_NAME))));
            c.moveToNext();
        }

        return Signals;
    }

    /**This method adds signal with and ID to an button with an ID*/
    public void addSignalToButton(int ButtonID, int SignalID){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BUTTON_SIGNAL_BUTTON, ButtonID);
        values.put(BUTTON_SIGNAL_SIGNAL, SignalID);
        db.insert(TABLE_BUTTON_SIGNAL,null,values);
        Log.i("DB" , ButtonID + " "  + SignalID);
    }

    /**This method removes one signal with and ID from the button with an given ID*/
    public void removeSignalFromButton(int ButtonId, int SignalID){
        String query = "DELETE FROM " + TABLE_BUTTON_SIGNAL +
                       " WHERE " + BUTTON_SIGNAL_BUTTON + "=" + ButtonId +
                       " AND " + BUTTON_SIGNAL_SIGNAL + "=" + SignalID + ";";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    /**The method removes a signal from the database completely and also
     * handles all the dependencies of this removed signal
     * thus if an button had this signal, it will be removed from its list*/
    public void removeSignal(int SignalID){
        String query1 = "DELETE FROM " + TABLE_BUTTON_SIGNAL +
                " WHERE " + BUTTON_SIGNAL_SIGNAL + "=" + SignalID + ";";
        String query2 = "DELETE FROM " + TABLE_SIGNALS +
                " WHERE " + SIGNAL_ID + "=" + SignalID + ";";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query1);
        db.execSQL(query2);
    }

    /**This method return all the Signals which exists in the DB*/
    public ArrayList<Signal> getAllSignals(){
        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT *" +
                " FROM " + TABLE_SIGNALS + " AS si, " + TABLE_DEVICES + " AS se" +
                " WHERE si." + SIGNAL_SETTING + " = se." + DEVICE_ID +";";
        Cursor c = db.rawQuery(checkQuery,null);

        ArrayList<Signal> Signals = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()){
            Signals.add(new Signal(c.getInt(c.getColumnIndex(SIGNAL_ID)),
                    c.getString(c.getColumnIndex(SIGNAL_NAME)),
                    c.getString(c.getColumnIndex(SIGNAL_SIGNAL)),
                    c.getInt(c.getColumnIndex(SIGNAL_REPEAT)),
                    c.getInt(c.getColumnIndex(SIGNAL_SETTING)),
                    c.getString(c.getColumnIndex(DEVICE_NAME))));
            c.moveToNext();
        }

        return Signals;
    }

    /**This method returns all the signals which are assigned to an give device*/
    public ArrayList<Signal> getSignalsForDevice(int DeviceID){
        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT *" +
                " FROM " + TABLE_SIGNALS + " AS si, " + TABLE_DEVICES + " AS se" +
                " WHERE si." + SIGNAL_SETTING + " = se." + DEVICE_ID +
                " AND si." + SIGNAL_SETTING + "=" + DeviceID + ";";
        Cursor c = db.rawQuery(checkQuery,null);

        ArrayList<Signal> Signals = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()){
            Signals.add(new Signal(c.getInt(c.getColumnIndex(SIGNAL_ID)),
                    c.getString(c.getColumnIndex(SIGNAL_NAME)),
                    c.getString(c.getColumnIndex(SIGNAL_SIGNAL)),
                    c.getInt(c.getColumnIndex(SIGNAL_REPEAT)),
                    c.getInt(c.getColumnIndex(SIGNAL_SETTING)),
                    c.getString(c.getColumnIndex(DEVICE_NAME))));
            c.moveToNext();
        }

        return Signals;
    }

    /**Returns all the parameters of an signal with an give ID*/
    public Signal getSignal(int SignalID){
        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_SIGNALS + " AS si, " + TABLE_DEVICES + " AS se" +
                " WHERE si." + SIGNAL_SETTING + " = se." + DEVICE_ID +" AND" +
                " si." + SIGNAL_ID + " = " + SignalID + ";";
        Cursor c = db.rawQuery(checkQuery,null);

        Signal s = null;

        c.moveToFirst();
        if(!c.isAfterLast()){
            s = new Signal(c.getInt(c.getColumnIndex(SIGNAL_ID)),
                    c.getString(c.getColumnIndex(SIGNAL_NAME)),
                    c.getString(c.getColumnIndex(SIGNAL_SIGNAL)),
                    c.getInt(c.getColumnIndex(SIGNAL_REPEAT)),
                    c.getInt(c.getColumnIndex(SIGNAL_SETTING)),
                    c.getString(c.getColumnIndex(DEVICE_NAME)));
        }

        return s;
    }

    /**Sets the name of an Signal*/
    public void setSignalName(int ID, String Name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_SIGNALS +
                " SET " + SIGNAL_NAME + " = ?" +
                " WHERE " + SIGNAL_ID + " = " + ID + ";");
        stmt.bindString(1, Name);
        stmt.execute();
    }

    /**Sets the repetition of an signal*/
    public void setSignalRepeat(int ID, String Repeat){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_SIGNALS +
                " SET " + SIGNAL_REPEAT + " = ?" +
                " WHERE " + SIGNAL_ID + " = " + ID + ";");
        stmt.bindString(1, Repeat);
        stmt.execute();
    }

    /**Sets the Decoded signal in String of an signal with an gived ID*/
    public void setSignalSignal(int ID, String Signal){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_SIGNALS +
                " SET " + SIGNAL_SIGNAL + " = ?" +
                " WHERE " + SIGNAL_ID + " = " + ID + ";");
        stmt.bindString(1, Signal);
        stmt.execute();
    }

    /**This method sets the chosen device for the signal*/
    public void setSignalsDevice(int SignalID, int DeviceID){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_SIGNALS +
                " SET " + SIGNAL_SETTING + " = ?" +
                " WHERE " + SIGNAL_ID + " = " + SignalID + ";");
        stmt.bindString(1, DeviceID + "");
        stmt.execute();
    }

    /**This method creates a new device and returns its ID*/
    public int createNewDevice(){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DEVICE_NAME, "");
        values.put(DEVICE_FREQUENCY, 38000);
        values.put(DEVICE_HOR_OFF, 90); //3 is most common so we make it as default value
        values.put(DEVICE_VER_OFF, 0);
        db.insert(TABLE_DEVICES,null,values);

        String Query = "SELECT MAX(" + DEVICE_ID + ")"+
                " FROM " + TABLE_DEVICES + ";";

        Cursor c = db.rawQuery(Query,null);

        c.moveToFirst();
        int ID = -1;
        if(!c.isAfterLast()){
            ID = c.getInt(c.getColumnIndex("MAX(" + DEVICE_ID + ")"));
        }

        return ID;
    }

    /**returns all the existing devices in an array of the Devices datastructure*/
    public ArrayList<DeviceSetting> getAllDevices(){
        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT *" +
                " FROM " + TABLE_DEVICES + ";";
        Cursor c = db.rawQuery(checkQuery,null);

        ArrayList<DeviceSetting> Devices = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()){
            Devices.add(new DeviceSetting(c.getInt(c.getColumnIndex(DEVICE_ID)),
                    c.getString(c.getColumnIndex(DEVICE_NAME)),
                    c.getInt(c.getColumnIndex(DEVICE_FREQUENCY)),
                    c.getInt(c.getColumnIndex(DEVICE_HOR_OFF)),
                    c.getInt(c.getColumnIndex(DEVICE_VER_OFF))));

            c.moveToNext();
        }

        return Devices;
    }

    /**returns a single device which has the given ID*/
    public DeviceSetting getDevice(int DeviceID){
        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT *" +
                " FROM " + TABLE_DEVICES +
                " WHERE " + DEVICE_ID + "=" + DeviceID + ";";
        Cursor c = db.rawQuery(checkQuery,null);

        DeviceSetting d = null;

        c.moveToFirst();
        if(!c.isAfterLast()){
            d = new DeviceSetting(c.getInt(c.getColumnIndex(DEVICE_ID)),
                    c.getString(c.getColumnIndex(DEVICE_NAME)),
                    c.getInt(c.getColumnIndex(DEVICE_FREQUENCY)),
                    c.getInt(c.getColumnIndex(DEVICE_HOR_OFF)),
                    c.getInt(c.getColumnIndex(DEVICE_VER_OFF)));
        }

        return d;
    }

    /**Sets the devices name*/
    public void setDeviceName(int DeviceID, String Name){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_DEVICES +
                " SET " + DEVICE_NAME + " = ?" +
                " WHERE " + DEVICE_ID + " = " + DeviceID + ";");
        stmt.bindString(1, Name);
        stmt.execute();
    }

    /**Sets the devices frequency*/
    public void setDeviceFrequency(int DeviceID, String Frequency){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_DEVICES +
                " SET " + DEVICE_FREQUENCY + " = ?" +
                " WHERE " + DEVICE_ID + " = " + DeviceID + ";");
        stmt.bindString(1, Frequency);
        stmt.execute();
    }

    /**Sets the devices Horizontal offset*/
    public void setDeviceHor(int DeviceID, String Hor_off){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_DEVICES +
                " SET " + DEVICE_HOR_OFF + " = ?" +
                " WHERE " + DEVICE_ID + " = " + DeviceID + ";");
        stmt.bindString(1, Hor_off);
        stmt.execute();
    }

    /**Sets the devices Vertical offset*/
    public void setDeviceVer(int DeviceID, String Ver_off){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_DEVICES +
                " SET " + DEVICE_VER_OFF + " = ?" +
                " WHERE " + DEVICE_ID + " = " + DeviceID + ";");
        stmt.bindString(1, Ver_off);
        stmt.execute();
    }

    /**Removes the Device completely and handles all the dependencies of this removal
     * thus it sets the DEFAULT device to all signals which were set to this device*/
    public void removeDevice(int DeviceID){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        SQLiteStatement stmt = sqLiteDatabase.compileStatement("UPDATE " + TABLE_SIGNALS +
                " SET " + SIGNAL_SETTING + " = ?" +
                " WHERE " + SIGNAL_SETTING + " = " + DeviceID + ";");
        stmt.bindString(1, "1");
        stmt.execute();
        String query2 = "DELETE FROM " + TABLE_DEVICES +
                " WHERE " + DEVICE_ID + "=" + DeviceID + ";";
        sqLiteDatabase.execSQL(query2);
    }
}

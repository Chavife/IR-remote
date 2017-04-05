package laci.irremote.Handlers.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.Pair;

import laci.irremote.Handlers.Database.DataStructures.RemoteButton;

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
    private static final String BUTTON_ID = "ID";
    private static final String BUTTON_NAME = "name"; /**Name which is Showed in The RemoteControllerActivity*/
    private static final String BUTTON_X = "x_pos";
    private static final String BUTTON_Y = "y_pos";
    private static final String BUTTON_ENABLED = "enabled";

    /** Database table of Signals.
     * This is where are all the Signals Stored
     * signal uses a Device setting for optimal function*/
    private static final String TABLE_SIGNALS = "Signals";
    private static final String SIGNAL_ID = "ID";
    private static final String SIGNAL_NAME = "name"; /**Name of an actual Button on the original remote*/
    private static final String SIGNAL_SIGNAL = "signal";
    private static final String SIGNAL_SETTING = "setting_id";

    /** Database table which connects Buttons and Signals together.
     * One Button can emit more signals at once which provides an macro feature*/
    private static final String TABLE_BUTTON_SIGNAL = "Button_Signal";
    private static final String BUTTON_SIGNAL_BUTTON = "button_id";
    private static final String BUTTON_SIGNAL_SIGNAL = "signal_id";

    /** Database od Settings
     * User can define a Device which will hold all the configuration needed
     * for optimal function.
     *
     * All the buttons connected to the Same device can use the same configuration*/
    private static final String TABLE_SETTINGS = "Settings";
    private static final String SETTING_ID = "ID";
    private static final String SETTING_NAME = "name"; /**Name of the device*/
    private static final String SETTING_FREQUENCY = "frequency";
    private static final String SETTING_HOR_OFF = "hor_offset";
    private static final String SETTING_VER_OFF = "ver_offset";


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
                BUTTON_ENABLED + " INTEGER" +
                ");";

        String CREATE_TABLE_SIGNALS = "CREATE TABLE IF NOT EXISTS " + TABLE_SIGNALS + "(" +
                SIGNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SIGNAL_NAME + " TEXT, " +
                SIGNAL_SIGNAL + " TEXT, " +
                SIGNAL_SETTING + " INTEGER" +
                ");";

        String CREATE_TABLE_BUTTON_SIGNAL = "CREATE TABLE IF NOT EXISTS " + TABLE_BUTTON_SIGNAL + "(" +
                BUTTON_SIGNAL_BUTTON + " INTEGER, " +
                BUTTON_SIGNAL_SIGNAL + " INTEGER" +
                ");";

        String CREATE_TABLE_SETTINGS = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS+ "(" +
                SETTING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SETTING_NAME + " TEXT, " +
                SETTING_FREQUENCY + " INTEGER, " +
                SETTING_HOR_OFF + " INTEGER, " +
                SETTING_VER_OFF + " INTEGER" +
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
                TABLE_BUTTON_SIGNAL + ", " + TABLE_SETTINGS + ";";
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
                    db.insert(TABLE_BUTTONS,null,values);
                }
            }

            values = new ContentValues();
            values.put(SETTING_NAME,"DEFAULT");
            values.put(SETTING_FREQUENCY,default_freq);
            values.put(SETTING_HOR_OFF,default_hor_off);
            values.put(SETTING_VER_OFF,default_ver_off);
            db.insert(TABLE_SETTINGS,null,values);

        }
        db.close();

    }

    /** returns the x and y position in the GridLayout of the button with the given ID*/
    public Pair<Integer,Integer> getButtonPosition(int ID){
        SQLiteDatabase db = getWritableDatabase();

        String q = "SELECT * FROM " + TABLE_BUTTONS + " WHERE " + BUTTON_ID + " = " + ID + ";";
        Cursor c = db.rawQuery(q,null);

        c.moveToFirst();

        int x,y;
        if(!c.isAfterLast()){
            x = c.getInt(c.getColumnIndex(BUTTON_X));
            y = c.getInt(c.getColumnIndex(BUTTON_Y));
        }else{ //in case of invalid ID
            x=-1;
            y=-1;
        }

        return new Pair<>(new Integer(x), new Integer(y));
    }

    /**this sets the Button with an given x and y position to enable ar disable*/
    public void setButtonEnabled(int x, int y, boolean enabled){
        String query =  "UPDATE " + TABLE_BUTTONS +
                " SET " + BUTTON_ENABLED + " = " + ((enabled) ? 1 : 0 )+
                " WHERE " + BUTTON_X + " = " + x +
                " AND " + BUTTON_Y + " = " + y + ";"
                ;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(query);
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
                                             c.getInt(c.getColumnIndex(BUTTON_ENABLED)) > 0);
            c.moveToNext();
        }

        return Buttons;
    }

}

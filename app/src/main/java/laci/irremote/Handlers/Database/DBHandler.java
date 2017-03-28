package laci.irremote.Handlers.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by laci on 27.3.2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ir_remote.db";

    private static final String TABLE_BUTTONS = "Buttons";
    private static final String BUTTON_ID = "ID";
    private static final String BUTTON_NAME = "name";
    private static final String BUTTON_X = "x_pos";
    private static final String BUTTON_Y = "y_pos";
    private static final String BUTTON_ENABLED = "enabled";

    private static final String TABLE_SIGNALS = "Signals";
    private static final String SIGNAL_ID = "ID";
    private static final String SIGNAL_NAME = "name";
    private static final String SIGNAL_SIGNAL = "signal";
    private static final String SIGNAL_SETTING = "setting_id";

    private static final String TABLE_BUTTON_SIGNAL = "Button_Signal";
    private static final String BUTTON_SIGNAL_BUTTON = "button_id";
    private static final String BUTTON_SIGNAL_SIGNAL = "signal_id";

    private static final String TABLE_SETTINGS = "Settings";
    private static final String SETTING_ID = "ID";
    private static final String SETTING_NAME = "name";
    private static final String SETTING_FREQUENCY = "frequency";
    private static final String SETTING_HOR_OFF = "hor_offset";
    private static final String SETTING_VER_OFF = "ver_offset";



    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

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



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_ALL_TABLES = "DROP TABLE IF EXISTS " + TABLE_BUTTONS + ", " + TABLE_SIGNALS + ", " +
                TABLE_BUTTON_SIGNAL + ", " + TABLE_SETTINGS + ";";
        db.execSQL(DROP_ALL_TABLES);
        onCreate(db);
    }


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

    public class IR_Button{
        public int ID;
        public String Name;
        public boolean Enabled;
    }

    public IR_Button[][] getButtons(int columns, int rows){

        IR_Button[][] Buttons = new IR_Button[columns][rows];

        SQLiteDatabase db = getReadableDatabase();
        String checkQuery = "SELECT * FROM " + TABLE_BUTTONS + ";";
        Cursor c = db.rawQuery(checkQuery,null);

        c.moveToFirst();

        while(!c.isAfterLast()){
            int x = c.getInt(c.getColumnIndex(BUTTON_X));
            int y = c.getInt(c.getColumnIndex(BUTTON_Y));
            Buttons[x][y] = new IR_Button();
            Buttons[x][y].ID = c.getInt(c.getColumnIndex(BUTTON_ID));
            Buttons[x][y].Name = c.getString(c.getColumnIndex((BUTTON_NAME)));
            Buttons[x][y].Enabled = c.getInt(c.getColumnIndex(BUTTON_ENABLED)) > 0;

            c.moveToNext();
        }

        return Buttons;
    }

}

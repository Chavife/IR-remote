package laci.irremote;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.ResourceBundle;

import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Handlers.Signal.SignalComposer;


/**
 * Main GUI Activity of the RemoteController
 * this activity contains all the Buttons_info in which you can control your devices with the module
 *
 * */

public class RemoteControllerActivity extends AppCompatActivity {


    private View Layout;
    private MVC_Controller Controller;
    private GridLayout ButtonsLayout;
    private Button SettingsBtn;
    private RemoteButton[][] Buttons_info;
    private Button[][] RemoteButtons;
    private int width = 0;
    private int height = 0;
    private int rows = 0;
    private int columns = 0;
    private boolean EDITING_FLAG = false;
    private SignalComposer SC;
    Thread Play = new Thread();

    /**Here We initialize every Object we are going to need*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ircontroller);
        Layout = findViewById(R.id.full_screen_layout);
        ButtonsLayout = (GridLayout) findViewById(R.id.buttons_layout);
        setFullscreen();

        /**Screen size for counting maximal buttons*/
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        Controller = new MVC_Controller(this);
        Controller.DBinit(width, height); //This Method initialize DB only at the first startup of the application
        SC = new SignalComposer(this);

        rows = Controller.getRows();
        columns = Controller.getColumns();

    }

    /**On Resume method is always called even if the Activity is not alive*/
    @Override
    protected void onResume() {
        super.onResume();
        setFullscreen();

        Buttons_info = Controller.getButtons();

        /*if we didn`t created the main view yet, do it*/
        if(ButtonsLayout.getChildCount() <= 5) { //5 is a good enough constant to check if we did

            RemoteButtons = new Button[columns][rows];

            ButtonsLayout.setRowCount(rows);
            ButtonsLayout.setColumnCount(columns);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (width / columns),
                    (height / rows));

            for (int x = 0; x < columns; x++) {
                for (int y = 0; y < rows; y++) {
                    if (Buttons_info[x][y] != null) {

                        final Button btn = Buttons_info[x][y].getAndroidButton(this);

                        btn.setLayoutParams(params);

                        btn.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(final View v, MotionEvent event) {
                                if(event.getAction() == MotionEvent.ACTION_DOWN){
                                    if (EDITING_FLAG) {
                                        Intent BtnConfig = new Intent(RemoteControllerActivity.this, ButtonConfigurationActivity.class);
                                        BtnConfig.putExtra("ID", v.getId());
                                        startActivity(BtnConfig);
                                    } else if(!EDITING_FLAG){
                                        v.setAlpha((float) 0.5);
                                        SC.Compose(Controller.getButtonSignals(v.getId()));
                                        if(Play != null && !Play.isAlive()){
                                            Play = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try{
                                                        for(int i = 0; i < 50; i++){
                                                            SC.Play();
                                                            Thread.sleep(SC.getLengthInms());
                                                        }
                                                    }catch (InterruptedException e){
                                                        return;
                                                    }
                                                }
                                            });
                                            Play.start();
                                        }
                                    }
                                }else if(event.getAction() == MotionEvent.ACTION_UP){
                                    if(Play != null && Play.isAlive()) Play.interrupt();
                                    v.setAlpha(1);
                                }
                                return true;
                            }
                        });

                        RemoteButtons[x][y] = btn;
                        ButtonsLayout.addView(btn);
                    }
                }
            }
            createSettingsBtn();
            ButtonsLayout.addView(SettingsBtn);
        }else { //if the Buttons are already made, only refresh Data of the Buttons
            for (int x = 0; x < columns; x++) {
                for (int y = 0; y < rows; y++) {
                    if (Buttons_info[x][y] != null) {
                        RemoteButtons[x][y].getBackground().setColorFilter(Buttons_info[x][y].getColor(), PorterDuff.Mode.SRC_ATOP);
                        RemoteButtons[x][y].setText(Buttons_info[x][y].getName());
                        if(EDITING_FLAG) {
                            if (Buttons_info[x][y].isEnabled()) {
                                RemoteButtons[x][y].setAlpha(1);
                            } else {
                                RemoteButtons[x][y].setAlpha((float) 0.20);
                            }
                        }
                    }
                }
            }
        }
    }


    /**This is the place when everything is already loaded and we can
     * check for the screen size
     * */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setFullscreen();
    }


    /**This is an kind of Constructor for the Settings button which sits in the right bottom
     * corner of this activity*/
    private void createSettingsBtn(){
        SettingsBtn = new Button(this);
        SettingsBtn.setBackground(getResources().getDrawable(android.R.drawable.ic_menu_preferences));
        SettingsBtn.setLayoutParams(new LinearLayout.LayoutParams((width / columns) - 10, (height / rows) - 10));

        SettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(RemoteControllerActivity.this, SettingsBtn);
                popup.getMenuInflater().inflate(R.menu.popup_remote_settings, popup.getMenu());

                MenuItem manage_btn = popup.getMenu().findItem(R.id.manage_remote_btns);
                if(EDITING_FLAG){
                    manage_btn.setTitle("Lock Buttons");
                }else{
                    manage_btn.setTitle("Manage Buttons");
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.manage_remote_btns:

                                /*Enable all the Buttons to Enable Editing of them*/
                                for(int x = 0 ; x < columns ; x++){
                                    for(int y = 0 ; y < rows; y++){
                                        if(RemoteButtons[x][y] != null){
                                            if(!RemoteButtons[x][y].isEnabled() && item.getTitle() == "Manage Buttons"){
                                                RemoteButtons[x][y].setEnabled(true);
                                                RemoteButtons[x][y].setAlpha((float) 0.20);
                                                EDITING_FLAG = true;
                                            }else if(!Buttons_info[x][y].isEnabled() && item.getTitle() == "Lock Buttons"){
                                                RemoteButtons[x][y].setEnabled(false);
                                                RemoteButtons[x][y].setAlpha(0);
                                                EDITING_FLAG = false;
                                            }
                                        }
                                    }
                                }
                                return true;
                            case R.id.manage_settings:
                                Intent devices = new Intent(RemoteControllerActivity.this, DevicesActivity.class);
                                startActivity(devices);
                                return true;
                            case R.id.manage_signals:
                                Intent signals = new Intent(RemoteControllerActivity.this, SignalsActivity.class);
                                startActivity(signals);
                                return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

    }

    /**Control function to hide all the bars for immersive full screen experience*/
    private void setFullscreen(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}

package laci.irremote;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.MVC_Controller;


/**
 * Main GUI Activity of the RemoteController
 * this activity contains all the Buttons in which you can control your devices with the module
 *
 * */

public class RemoteControllerActivity extends AppCompatActivity {


    private View Layout;
    private MVC_Controller Controller;
    private GridLayout ButtonsLayout;
    private Button SettingsBtn;
    private RemoteButton[][] Buttons;
    private Button[][] RemoteButtons;
    private int view_width = 0;
    private int view_height = 0;
    private boolean EDITING_FLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ircontroller);
        Layout = findViewById(R.id.full_screen_layout);
        ButtonsLayout = (GridLayout) findViewById(R.id.buttons_layout);
        setFullscreen();

        Controller = new MVC_Controller();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullscreen();
    }


    /**This is the place when everything is already loaded and we can
     * check for the screen size
     * */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setFullscreen();
        if(ButtonsLayout.getChildCount() <= 5) { //5 is a good enough constant
            view_width = Layout.getWidth();
            view_height = Layout.getHeight();

            Log.i("SIZES", view_width + " " + view_height);

            Controller.init(this, view_height, view_width);

            Buttons = Controller.getButtons();
            RemoteButtons = new Button[Buttons.length][Buttons[0].length];

            ButtonsLayout.setRowCount(Buttons[0].length);
            ButtonsLayout.setColumnCount(Buttons.length);


            for (int x = 0; x < Buttons.length; x++) {
                for (int y = 0; y < Buttons[x].length; y++) {
                    if (Buttons[x][y] != null) {
                        final Button btn = Buttons[x][y].getAndroidButton(getApplicationContext());
                        btn.setPadding(0, 0, 0, 0);
                        btn.setLayoutParams(new LinearLayout.LayoutParams(view_width / 5, view_height / Buttons[x].length));
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Pair<Integer,Integer> pos = Controller.getButtonPosition(btn.getId());
                                if (EDITING_FLAG) {
                                    if(!Buttons[pos.first][pos.second].isEnabled()){
                                        btn.setAlpha(1);
                                        Buttons[pos.first][pos.second].setEnabled(true);
                                        Controller.setButtonEnabled(pos.first,pos.second,true);
                                    }else{
                                        btn.setAlpha((float) 0.5);
                                        Buttons[pos.first][pos.second].setEnabled(false);
                                        Controller.setButtonEnabled(pos.first,pos.second,false);
                                    }

                                } else if(!EDITING_FLAG){

                                }
                            }
                        });
                        RemoteButtons[x][y] = btn;
                        ButtonsLayout.addView(btn);
                    }
                }
            }
            createSettingsBtn();
            ButtonsLayout.addView(SettingsBtn);
        }
    }


    /**This is an kind of Constructor for the Settings button which sits in the right bottom
     * corner of this activity*/
    private void createSettingsBtn(){
        SettingsBtn = new Button(getApplicationContext());
        SettingsBtn.setBackground(getResources().getDrawable(android.R.drawable.ic_menu_preferences));
        SettingsBtn.setLayoutParams(new LinearLayout.LayoutParams(view_width / 5, view_width / 5));

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
                                for(int x = 0 ; x < RemoteButtons.length ; x++){
                                    for(int y = 0 ; y < RemoteButtons[x].length; y++){
                                        if(RemoteButtons[x][y] != null){
                                            if(!RemoteButtons[x][y].isEnabled() && item.getTitle() == "Manage Buttons"){
                                                RemoteButtons[x][y].setEnabled(true);
                                                RemoteButtons[x][y].setAlpha((float) 0.5);
                                                EDITING_FLAG = true;
                                            }else if(!Buttons[x][y].isEnabled() && item.getTitle() == "Lock Buttons"){
                                                RemoteButtons[x][y].setEnabled(false);
                                                RemoteButtons[x][y].setAlpha(0);
                                                EDITING_FLAG = false;
                                            }
                                        }
                                    }
                                }
                                return true;
                            case R.id.manage_settings:
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

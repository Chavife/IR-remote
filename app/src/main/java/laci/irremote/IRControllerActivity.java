package laci.irremote;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import laci.irremote.Handlers.Database.DBHandler;
import laci.irremote.Handlers.MVC_Controller;


public class IRControllerActivity extends AppCompatActivity {


    private View Layout;
    private MVC_Controller Controller;
    private GridLayout ButtonsLayout;

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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int view_width = Layout.getWidth();
        int view_height = Layout.getHeight();

        Log.i("SIZES", view_width + " " + view_height);

        Controller.init(this,view_height,view_width);

        DBHandler.IR_Button[][] Buttons = Controller.getButtons();

        ButtonsLayout.setRowCount(Buttons[0].length);
        ButtonsLayout.setColumnCount(Buttons.length);

        for(int x = 0; x < Buttons.length ; x++){
            for(int y = 0; y < Buttons[x].length; y++){
                if(Buttons[x][y] != null){
                    final Button btn = new Button(getApplicationContext());
                    btn.setId(Buttons[x][y].ID);
                    btn.setText(Buttons[x][y].ID + "");
                    btn.setPadding(0,0,0,0);
                    btn.setLayoutParams(new LinearLayout.LayoutParams(view_width/5, view_height/Buttons[x].length));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(btn.getAlpha() == 0){
                                btn.setAlpha(1);
                            }else{
                                btn.setAlpha(0);
                            }
                        }
                    });


                    ButtonsLayout.addView(btn);
                }
            }
        }
    }

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

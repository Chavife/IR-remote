package laci.irremote;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.MVC_Controller;

public class ButtonConfigurationActivity extends AppCompatActivity {

    private EditText NameEdit;
    private Button ColorPick;
    private CheckBox EnabledChkBox;
    private int ButtonID;
    private RemoteButton btn_info;
    private MVC_Controller Controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_configuration);

        Controller = new MVC_Controller(this);

        NameEdit = (EditText) findViewById(R.id.name_edit);
        ColorPick = (Button) findViewById(R.id.color_pick_btn);
        EnabledChkBox = (CheckBox) findViewById(R.id.enabled_chkbox);

        final Bundle ButtonInfo = getIntent().getExtras();
        if(ButtonInfo == null) return;

        ButtonID = ButtonInfo.getInt("ID");
        btn_info = Controller.getButtonInfo(ButtonID);

        NameEdit.setText(btn_info.getName());

        NameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setButtonText(ButtonID,s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EnabledChkBox.setChecked(btn_info.isEnabled());
        ColorPick.setBackgroundColor(btn_info.getColor());



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void onBackBtnClick(View view){
        finish();
    }

    public void EnabledChanged(View view){
        Controller.setButtonEnabled(ButtonID,EnabledChkBox.isChecked());
    }

    public void PickColor(View view){
        HSVColorPickerDialog cpd = new HSVColorPickerDialog( ButtonConfigurationActivity.this, btn_info.getColor(), new HSVColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void colorSelected(Integer color) {
                // Do something with the selected color
                Controller.setButtonColor(ButtonID,color);
                ColorPick.setBackgroundColor(color);
            }
        });
        cpd.setTitle( "Pick a color" );
        cpd.show();
    }
}

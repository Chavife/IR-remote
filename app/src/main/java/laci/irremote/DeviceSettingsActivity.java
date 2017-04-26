package laci.irremote;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.DeviceSetting;
import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Handlers.Signal.AutoSettingRunnable;
import laci.irremote.Views.Dialogs.SignalForDeviceChoosingDialog;

public class DeviceSettingsActivity extends AppCompatActivity {

    private Button AutoSetBtn;
    private EditText NameEdit;
    private TextView FrequencyText;
    private EditText FrequencyEdit;
    private TextView HorText;
    private EditText HorEdit;
    private TextView VerText;
    private EditText VerEdit;
    private TextView InfoText;

    private boolean run = true;


    private MVC_Controller Controller;
    private DeviceSetting deviceSetting;
    private int DeviceID;
    Thread find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Controller = new MVC_Controller(this);
        NameEdit = (EditText) findViewById(R.id.name_edit);
        FrequencyText = (TextView) findViewById(R.id.frequency_text);
        FrequencyEdit = (EditText) findViewById(R.id.frequency_edit);
        VerText = (TextView) findViewById(R.id.ver_off_text);
        HorEdit = (EditText) findViewById(R.id.hor_off_edit);
        HorText = (TextView) findViewById(R.id.ver_off_text);
        VerEdit = (EditText) findViewById(R.id.ver_off_edit);
        AutoSetBtn = (Button) findViewById(R.id.auto_set_btn);
        InfoText = (TextView) findViewById(R.id.auto_set_info_text);

        final Bundle DeviceInfo = getIntent().getExtras();
        if(DeviceInfo == null) return;

        DeviceID = DeviceInfo.getInt("ID");
        if(DeviceID == -1){
            DeviceID = Controller.createNewDevice();
        }

        deviceSetting  = Controller.getDevice(DeviceID);
        NameEdit.setText(deviceSetting.getName());
        FrequencyEdit.setText(deviceSetting.getFrequency()+"");
        HorEdit.setText(deviceSetting.getHor_off()+"");
        VerEdit.setText(deviceSetting.getVer_off()+"");

        NameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setDeviceName(DeviceID,s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        FrequencyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setDeviceFrequency(DeviceID,s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        HorEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setDeviceHorOff(DeviceID,s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        VerEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setDeviceVerOff(DeviceID,s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(find != null && find.isAlive())find.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onBackBtnClick(View view){ finish(); }



    public void AutoSet(final View view){

        if(find != null && find.isAlive()){
            AutoSetBtn.setText("SET");
        }else{
            ArrayList<Signal> SignalsOfDevice = Controller.getSignalsForDevice(DeviceID);
            if(SignalsOfDevice.size() == 0){
                Toast.makeText(DeviceSettingsActivity.this, "No Signals assigned for this Device", Toast.LENGTH_SHORT).show();
            }else{
                new SignalForDeviceChoosingDialog(DeviceSettingsActivity.this, SignalsOfDevice, new SignalForDeviceChoosingDialog.OnSignalSelectedListener() {
                    @Override
                    public void SignalsSelected(Signal selectedSignal) {
                        if(selectedSignal.getSignal().length < 5){
                            Toast.makeText(DeviceSettingsActivity.this, "Selected Signal is not set", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        AutoSetBtn.setText("SET FREQ");
                        find = new Thread(new AutoSettingRunnable(
                                FrequencyText,
                                HorText,
                                VerText,
                                InfoText,
                                FrequencyEdit,
                                HorEdit,
                                VerEdit,
                                AutoSetBtn,
                                selectedSignal,
                                DeviceSettingsActivity.this
                        ));
                        find.start();
                    }
                });
            }
        }
    }
}

package laci.irremote;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Views.Dialogs.HSVColorPickerDialog;
import laci.irremote.Views.Adapters.SignalArrayAdapter;
import laci.irremote.Views.Dialogs.SignalChoosingDialog;

public class ButtonConfigurationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private EditText NameEdit;
    private TextView InfoText;
    private Button ColorPick;
    private CheckBox EnabledChkBox;
    private int ButtonID;
    private RemoteButton btn_info;
    private ListView SignalListView;
    private MVC_Controller Controller;
    private SignalArrayAdapter SignalAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_configuration);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Controller = new MVC_Controller(this);

        NameEdit = (EditText) findViewById(R.id.name_edit);
        ColorPick = (Button) findViewById(R.id.color_pick_btn);
        EnabledChkBox = (CheckBox) findViewById(R.id.enabled_chkbox);
        SignalListView = (ListView) findViewById(R.id.signals_list);
        InfoText = (TextView) findViewById(R.id.info_text);


        final Bundle ButtonInfo = getIntent().getExtras();
        if(ButtonInfo == null) return;

        ButtonID = ButtonInfo.getInt("ID");
        btn_info = Controller.getButtonInfo(ButtonID);

        NameEdit.setText(btn_info.getName());
        InfoText.setText("BUTTON CONFIGURATION");

        NameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Controller.setButtonName(ButtonID,s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EnabledChkBox.setChecked(btn_info.isEnabled());
        ColorPick.setBackgroundColor(btn_info.getColor());

    }


    @Override
    protected void onResume() {
        super.onResume();

        SignalAdapter = new SignalArrayAdapter(ButtonConfigurationActivity.this, R.layout.signal_list_item, Controller.getButtonSignals(ButtonID));
        SignalAdapter.add(new Signal(-1,"ADD SIGNAL FOR THIS BUTTON","0",0,-1,""));
        SignalListView.setAdapter(SignalAdapter);
        SignalListView.setOnItemClickListener(this);
        SignalListView.setOnItemLongClickListener(this);
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
                Controller.setButtonColor(ButtonID,color);
                ColorPick.setBackgroundColor(color);
            }
        });
        cpd.setTitle( "Pick a color" );
        cpd.show();
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(SignalAdapter.getItem(position).getSignalID() == -1){
            new SignalChoosingDialog(ButtonConfigurationActivity.this, Controller.getComplementaryButtonSignals(ButtonID), new SignalChoosingDialog.OnSignalsSelectedListener() {
                @Override
                public void SignalsSelected(ArrayList<Signal> selectedSignals) {
                    if(selectedSignals.size() > 0) Controller.addSignalsToButton(ButtonID, selectedSignals);
                    UpdateList();
                }
            });
        }else{
            Intent signal = new Intent(ButtonConfigurationActivity.this, SignalDecodingActivity.class);
            signal.putExtra("ID", SignalAdapter.getItem(position).getSignalID());
            startActivity(signal);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(SignalAdapter.getItem(position).getSignalID() != -1){
            PopupMenu popup = new PopupMenu(ButtonConfigurationActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.popup_item, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.remove_signal:
                            Controller.removeSignalFromButton(ButtonID, SignalAdapter.getItem(position).getSignalID());
                            UpdateList();
                            return true;
                    }
                    return false;
                }
            });
            popup.show();
            return true;
        }
        return false;
    }

    public void UpdateList(){
        SignalAdapter.updateSignals(Controller.getButtonSignals(ButtonID));
        SignalAdapter.add(new Signal(-1,"ADD SIGNAL FOR THIS BUTTON","0",0,-1,""));
    }
}

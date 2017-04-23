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

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.RemoteButton;
import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Views.HSVColorPickerDialog;
import laci.irremote.Views.SignalArrayAdapter;
import laci.irremote.Views.SignalChoosingDialog;

public class SignalsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ListView SignalListView;
    private MVC_Controller Controller;
    private ArrayList<Signal> Signals;
    private SignalArrayAdapter SignalAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signals);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Controller = new MVC_Controller(this);

        SignalListView = (ListView) findViewById(R.id.signals_list);



    }

    @Override
    protected void onResume() {
        super.onResume();
        Signals = Controller.getAllSignals();

        SignalAdapter = new SignalArrayAdapter(SignalsActivity.this, R.layout.signal_list_item, Signals);
        SignalAdapter.add(new Signal(-1,"ADD SIGNAL","0",0,-1,""));
        SignalListView.setAdapter(SignalAdapter);
        SignalListView.setOnItemClickListener(this);
        SignalListView.setOnItemLongClickListener(this);
    }

    public void onBackBtnClick(View view){
        finish();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent signal = new Intent(SignalsActivity.this, SignalDecodingActivity.class);
            signal.putExtra("ID", Signals.get(position).getSignalID());
            startActivity(signal);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(position != Signals.size()-1){
            PopupMenu popup = new PopupMenu(SignalsActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.popup_signal_item, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.remove_signal:
                            Controller.removeSignal(Signals.get(position).getSignalID());
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
        Signals = Controller.getAllSignals();
        SignalAdapter.updateSignals(Signals);
        SignalAdapter.add(new Signal(-1,"ADD SIGNAL","0",0,-1,""));
    }
}

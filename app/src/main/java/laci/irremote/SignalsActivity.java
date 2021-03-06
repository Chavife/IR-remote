package laci.irremote;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Views.Adapters.SignalArrayAdapter;

public class SignalsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ListView SignalListView;
    private MVC_Controller Controller;
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

        SignalAdapter = new SignalArrayAdapter(SignalsActivity.this, R.layout.signal_list_item, Controller.getAllSignals());
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
            signal.putExtra("ID", SignalAdapter.getItem(position).getSignalID());
            startActivity(signal);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(SignalAdapter.getItem(position).getSignalID() != -1){
            PopupMenu popup = new PopupMenu(SignalsActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.popup_item, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.remove_signal:
                            Controller.removeSignal(SignalAdapter.getItem(position).getSignalID());
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
        SignalAdapter.updateSignals(Controller.getAllSignals());
        SignalAdapter.add(new Signal(-1,"ADD SIGNAL","0",0,-1,""));
    }
}

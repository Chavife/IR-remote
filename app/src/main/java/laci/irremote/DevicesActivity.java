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

import laci.irremote.Handlers.Database.DataStructures.DeviceSetting;
import laci.irremote.Handlers.MVC_Controller;
import laci.irremote.Views.Adapters.DeviceArrayAdapter;

public class DevicesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ListView DeviceListView;
    private MVC_Controller Controller;
    private DeviceArrayAdapter DeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Controller = new MVC_Controller(this);

        DeviceListView = (ListView) findViewById(R.id.devices_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DeviceAdapter = new DeviceArrayAdapter(DevicesActivity.this, R.layout.device_list_item, Controller.getAllDevices());
        DeviceAdapter.add(new DeviceSetting(-1,"ADD DEVICE",-1,-1,-1));
        DeviceListView.setAdapter(DeviceAdapter);
        DeviceListView.setOnItemClickListener(this);
        DeviceListView.setOnItemLongClickListener(this);
    }

    public void onBackBtnClick(View view){
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent device = new Intent(DevicesActivity.this, DeviceSettingsActivity.class);
        device.putExtra("ID", DeviceAdapter.getItem(position).getDeviceID());
        startActivity(device);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if(DeviceAdapter.getItem(position).getDeviceID() != -1 && DeviceAdapter.getItem(position).getDeviceID() != 1){
            PopupMenu popup = new PopupMenu(DevicesActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.popup_item, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.remove_signal:
                            Controller.removeDevice(DeviceAdapter.getItem(position).getDeviceID());
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
        DeviceAdapter.updateDevices(Controller.getAllDevices());
        DeviceAdapter.add(new DeviceSetting(-1,"ADD DEVICE",-1,-1,-1));
    }
}

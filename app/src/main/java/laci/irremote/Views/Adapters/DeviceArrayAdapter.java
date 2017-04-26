package laci.irremote.Views.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.DeviceSetting;
import laci.irremote.R;

/**
 * Created by laci on 23.4.2017.
 */

public class DeviceArrayAdapter extends ArrayAdapter<DeviceSetting>{
    private ArrayList<DeviceSetting> Devices;

    public static class ViewHolder {
        public TextView device_name;
    }

    public DeviceArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.Devices = new ArrayList<>();
    }

    public DeviceArrayAdapter(Context context, int resource, ArrayList<DeviceSetting> dev) {
        super(context, resource);
        this.Devices = dev;
    }

    public void updateDevices(ArrayList<DeviceSetting> devices){
        this.Devices = devices;
    }

    @Override
    public void add(DeviceSetting object) {
        this.Devices.add(object);
        super.add(object);
    }

    @Override
    public int getCount(){ return this.Devices.size(); }

    @Nullable
    @Override
    public DeviceSetting getItem(int position) { return this.Devices.get(position); }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        SignalArrayAdapter.ViewHolder holder;
        try {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi = inflater.inflate(R.layout.device_list_item, null);

                holder = new SignalArrayAdapter.ViewHolder();
                holder.device_name = (TextView) vi.findViewById(R.id.device_name);

                vi.setTag(holder);
            }else{
                holder = (SignalArrayAdapter.ViewHolder) vi.getTag();
            }
            holder.device_name.setText(Devices.get(position).getName());
        } catch (Exception e){}
        return vi;
    }
}

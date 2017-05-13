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

import laci.irremote.Handlers.Database.DataStructures.Signal;
import laci.irremote.R;

/**
 * Adapter handling the ListView of the signals
 */

public class SignalArrayAdapter extends ArrayAdapter<Signal> {

    private ArrayList<Signal> Signals;

    public static class ViewHolder {
        public TextView signal_name;
        public TextView device_name;
    }

    public SignalArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.Signals = new ArrayList<>();
    }

    public SignalArrayAdapter(Context context, int resource, ArrayList<Signal> sig) {
        super(context, resource);
        this.Signals = sig;
    }

    public void updateSignals(ArrayList<Signal> signals){
        this.Signals = signals;
    }

    @Override
    public void add(Signal object) {
        this.Signals.add(object);
        super.add(object);
    }

    @Override
    public int getCount(){ return this.Signals.size(); }

    @Nullable
    @Override
    public Signal getItem(int position) { return this.Signals.get(position); }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        try {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi = inflater.inflate(R.layout.signal_list_item, null);

                holder = new ViewHolder();

                holder.signal_name = (TextView) vi.findViewById(R.id.signal_name);
                holder.device_name = (TextView) vi.findViewById(R.id.device_name);

                vi.setTag(holder);
            }else{
                holder = (ViewHolder) vi.getTag();
            }
            holder.signal_name.setText(Signals.get(position).getName());

            if(Signals.get(position).getSetting_id() == -1){
                holder.device_name.setText("");
            }else{
                holder.device_name.setText("Device: " + Signals.get(position).getSetting_name());
            }

        } catch (Exception e){}
        return vi;
    }
}

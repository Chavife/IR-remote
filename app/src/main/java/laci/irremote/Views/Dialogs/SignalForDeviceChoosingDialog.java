package laci.irremote.Views.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.Signal;

/**
 * Created by laci on 25.4.2017.
 */

public class SignalForDeviceChoosingDialog extends AlertDialog.Builder{

    private ArrayList<Signal> Signals;
    private Signal selectedSignal;

    private ArrayAdapter<String> SignalStringArray;

    private OnSignalSelectedListener Listener;

    public interface OnSignalSelectedListener {
        void SignalsSelected( Signal selectedSignal);
    }

    public SignalForDeviceChoosingDialog (Context context, ArrayList<Signal> signals, final OnSignalSelectedListener listener) {
        super(context);
        this.Listener = listener;
        Signals = signals;
        selectedSignal = null;

        SignalStringArray = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
        for(Signal s : Signals){
            SignalStringArray.add(s.getName());
        }

        setTitle("Pick Signal to test on");

        setAdapter(SignalStringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Listener.SignalsSelected(Signals.get(which));
            }
        });

        setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        create();
        show();
    }
}
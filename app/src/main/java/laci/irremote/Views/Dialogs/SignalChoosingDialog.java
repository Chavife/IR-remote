package laci.irremote.Views.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.ArrayList;

import laci.irremote.Handlers.Database.DataStructures.Signal;

/**
 * Created by laci on 21.4.2017.
 */

public class SignalChoosingDialog extends AlertDialog.Builder{

    private ArrayList<Signal> Signals;
    private ArrayList<Signal> selectedSignals;

    private String[] SignalStringArray;

    private OnSignalsSelectedListener Listener;

    public interface OnSignalsSelectedListener {
        void SignalsSelected( ArrayList<Signal> selectedSignals);
    }

    public SignalChoosingDialog(Context context, ArrayList<Signal> signals, final OnSignalsSelectedListener listener) {
        super(context);
        this.Listener = listener;
        Signals = signals;
        selectedSignals = new ArrayList<>();

        SignalStringArray = new String[Signals.size()];
        for(int i = 0; i < Signals.size(); i++){
            SignalStringArray[i] = Signals.get(i).getSetting_name() + " => " + Signals.get(i).getName();
        }

        setTitle("Pick Signals");

        setMultiChoiceItems(SignalStringArray, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedSignals.add(Signals.get(which));
                        } else if (selectedSignals.contains(Signals.get(which))) {
                            selectedSignals.remove(Signals.get(which));
                        }
                    }
                });

        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Listener.SignalsSelected(selectedSignals);
            }
        });

        setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
               //DISMISS
            }
        });

        create();
        show();
    }
}

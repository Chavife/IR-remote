package laci.irremote.Handlers.Signal;

import android.app.Activity;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import laci.irremote.Handlers.Database.DataStructures.Signal;

/**
 * This Class is the Runnable for semi-automatic setting o an device in this application
 * This handles all the stuff which happens during the setting in an different thread.
 */

public class AutoSettingRunnable implements Runnable{

    private SignalComposer SC;

    /**All the Views for visual representation*/
    private TextView FrequencyText;
    private TextView HorText;
    private TextView VerText;
    private TextView InfoText;

    private EditText FrequencyEdit;
    private EditText HorEdit;
    private EditText VerEdit;

    private Button Btn;

    private Signal Sig;

    private Activity Activity;

    /**Constructor*/
    public AutoSettingRunnable(TextView frequencyText,
                               TextView horText,
                               TextView verText,
                               TextView infoText,
                               EditText frequencyEdit,
                               EditText horEdit,
                               EditText verEdit,
                               Button btn,
                               Signal sig,
                               android.app.Activity activity) {
        FrequencyText = frequencyText;
        HorText = horText;
        VerText = verText;
        InfoText = infoText;
        FrequencyEdit = frequencyEdit;
        HorEdit = horEdit;
        VerEdit = verEdit;
        Btn = btn;
        Sig = sig;
        Activity = activity;

        SC = new SignalComposer(activity.getApplicationContext());
    }

    @Override
    public void run() {
        try{
            int freq = 36000;
            int horoff = 80;
            int veroff;

            Activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FrequencyEdit.setTextColor(Color.RED);
                }
            });

            frequencyLoop:
            for(freq = 36000; freq <= 42000; freq += 2000) {
                final int finalF = freq;
                Activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrequencyEdit.setText(finalF + "");
                    }
                });
                for (horoff = 80; horoff <= 190; horoff += 10) {
                    for (veroff = 0; veroff <= 30; veroff += 10) {
                        if (Btn.getText() == "SET") {
                            Activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Btn.setText("FREQUENCY SET");
                                }
                            });
                            Thread.sleep(1500);
                            break frequencyLoop;
                        }
                        SC.Compose(Sig,freq,horoff,veroff);
                        SC.Play();
                        Activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(InfoText.getText() == "TRYING FREQUENCIES..."){
                                   InfoText.setText("TRYING FREQUENCIES.");
                                }else if(InfoText.getText() == "TRYING FREQUENCIES."){
                                    InfoText.setText("TRYING FREQUENCIES..");
                                }else{
                                    InfoText.setText("TRYING FREQUENCIES...");
                                }
                            }
                        });
                        Thread.sleep(SC.getLengthInms() + 300);
                    }
                }
            }

            Activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FrequencyEdit.setTextColor(Color.DKGRAY);
                    HorEdit.setTextColor(Color.RED);
                    VerEdit.setTextColor(Color.RED);
                    Btn.setText("SET HOR VER OFFSET");
                }
            });

        horveroffLoop:
            for(int h = horoff-10; h <= horoff+10; h += 5){
                for(int v = 0; v <= 30; v +=5){
                    final int finalH = h;
                    final int finalV = v;
                    if (Btn.getText() == "SET") {
                        Activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Btn.setText("HOR VER OFFSET SET");
                            }
                        });
                        Thread.sleep(1500);
                        break horveroffLoop;
                    }

                    Activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HorEdit.setText(finalH +"");
                            VerEdit.setText(finalV +"");
                            if(InfoText.getText() == "SETTING HORIZONTAL AND VERTICAL OFFSET..."){
                                InfoText.setText("SETTING HORIZONTAL AND VERTICAL OFFSET.");
                            }else if(InfoText.getText() == "SETTING HORIZONTAL AND VERTICAL OFFSET."){
                                InfoText.setText("SETTING HORIZONTAL AND VERTICAL OFFSET..");
                            }else{
                                InfoText.setText("SETTING HORIZONTAL AND VERTICAL OFFSET...");
                            }
                        }
                    });

                    SC.Compose(Sig,freq,h,v);
                    SC.Play();



                    Thread.sleep(SC.getLengthInms() + 1000);
                }
            }
            Activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FrequencyEdit.setTextColor(Color.DKGRAY);
                    HorEdit.setTextColor(Color.DKGRAY);
                    VerEdit.setTextColor(Color.DKGRAY);
                    Btn.setText("SEMI-AUTOMATIC SETTING");
                    InfoText.setText("");
                }
            });
        }catch (InterruptedException e){
            return;
        }

    }
}

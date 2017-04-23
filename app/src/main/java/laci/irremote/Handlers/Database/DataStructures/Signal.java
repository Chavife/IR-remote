package laci.irremote.Handlers.Database.DataStructures;

/**
 * Created by laci on 21.4.2017.
 */

public class Signal {
    private int SignalID;
    private String Name;
    private Integer[] Signal;
    private int Repeat;
    private int setting_id;
    private String setting_name;

    public Signal(int SignalID, String name, String signal, int repeat, int setting_id, String setting_name) {
        this.SignalID = SignalID;
        Name = name;
        Signal = ParseSignalFromString(signal);
        Repeat = repeat;
        this.setting_id = setting_id;
        this.setting_name = setting_name;
    }

    public Integer[] ParseSignalFromString(String signal){
        String[] values = signal.split(" ");
        Integer[] parsedsignal = new Integer[values.length];

        for(int i = 0; i < values.length ; i++){
            parsedsignal[i] = Integer.parseInt(values[i]);
        }
        return parsedsignal;
    }

    public String getName() {
        return Name;
    }

    public Integer[] getSignal() {
        return Signal;
    }

    public int getSetting_id() {
        return setting_id;
    }

    public String getSetting_name() {
        return setting_name;
    }

    public int getSignalID() { return SignalID; }

    public int getRepeat() { return Repeat; }

    public void setName(String name) {
        Name = name;
    }

    public void setSignal(Integer[] signal) {
        Signal = signal;
    }

    public void setSetting_id(int setting_id) {
        this.setting_id = setting_id;
    }

    public void setSetting_name(String setting_name) {
        this.setting_name = setting_name;
    }

    public void setSignalID(int signalID) { SignalID = signalID; }

    public void setRepeat(int repeat) { Repeat = repeat; }
}

package laci.irremote.Handlers.Database.DataStructures;


/**
 * This Class is a DataStructure of an Signal where we can easily store all the variables
 * from the database which we could need for later use
 */

public class Signal {
    private int SignalID; /**ID of the Signal*/
    private String Name; /**Name of the signal for better recognition*/
    private Integer[] Signal; /**Data structure of the signal*/
    private int Repeat; /**Repetition of the signal at generating*/
    private int setting_id; /**ID of the used device*/
    private String setting_name; /**Name of the Device for ease of use*/

    /**Constructor*/
    public Signal(int SignalID, String name, String signal, int repeat, int setting_id, String setting_name) {
        this.SignalID = SignalID;
        Name = name;
        Signal = ParseSignalFromString(signal);
        Repeat = repeat;
        this.setting_id = setting_id;
        this.setting_name = setting_name;
    }

    /**Method which translates the String from the DB to an array of integers*/
    public Integer[] ParseSignalFromString(String signal){
        String[] values = signal.split(" ");
        Integer[] parsedsignal = new Integer[values.length];

        for(int i = 0; i < values.length ; i++){
            parsedsignal[i] = Integer.parseInt(values[i]);
        }
        return parsedsignal;
    }

    /*Getters*/
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


    /*Setters*/
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

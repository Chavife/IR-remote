package laci.irremote.Handlers.Database.DataStructures;

/**
 * This Class is a DataStructure of an Device where we can easily store all the variables
 * from the database which we could need for later use
 */

public class DeviceSetting {
    private int DeviceID; /**ID if the device*/
    private String Name; /**Name which is shown also in the list of devices*/
    private Integer Frequency; /**Frequency parameter*/
    private Integer Hor_off; /**Horizontal offset*/
    private Integer Ver_off; /**Vertical offset*/


    /**Constructor*/
    public DeviceSetting(int ID, String name, Integer frequency, Integer hor_off, Integer ver_off) {
        this.DeviceID = ID;
        Name = name;
        Frequency = frequency;
        Hor_off = hor_off;
        Ver_off = ver_off;
    }

    /*Getters*/
    public int getDeviceID() {
        return DeviceID;
    }

    public String getName() {
        return Name;
    }

    public Integer getFrequency() {
        return Frequency;
    }

    public Integer getHor_off() {
        return Hor_off;
    }

    public Integer getVer_off() {
        return Ver_off;
    }


    /*Setters*/
    public void setID(int ID) {
        this.DeviceID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setFrequency(Integer frequency) {
        Frequency = frequency;
    }

    public void setHor_off(Integer hor_off) {
        Hor_off = hor_off;
    }

    public void setVer_off(Integer ver_off) {
        Ver_off = ver_off;
    }
}

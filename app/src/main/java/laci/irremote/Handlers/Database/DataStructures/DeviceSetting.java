package laci.irremote.Handlers.Database.DataStructures;

/**
 * Created by laci on 23.4.2017.
 */

public class DeviceSetting {
    private int DeviceID;
    private String Name;
    private Integer Frequency;
    private Integer Hor_off;
    private Integer Ver_off;

    public DeviceSetting(int ID, String name, Integer frequency, Integer hor_off, Integer ver_off) {
        this.DeviceID = ID;
        Name = name;
        Frequency = frequency;
        Hor_off = hor_off;
        Ver_off = ver_off;
    }

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

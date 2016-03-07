package ch.ethz.coss.nervousnet.vm.model;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "CONFIG".
 */
public class Config {

    private Byte State;
    private String UUID;
    private String DeviceBrand;
    private String DeviceModel;
    private String DeviceOS;
    private String DeviceOSversion;
    private Long LastSyncTime;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Config() {
    }

    public Config(Byte State, String UUID, String DeviceBrand, String DeviceModel, String DeviceOS, String DeviceOSversion, Long LastSyncTime) {
        this.State = State;
        this.UUID = UUID;
        this.DeviceBrand = DeviceBrand;
        this.DeviceModel = DeviceModel;
        this.DeviceOS = DeviceOS;
        this.DeviceOSversion = DeviceOSversion;
        this.LastSyncTime = LastSyncTime;
    }

    public Byte getState() {
        return State;
    }

    public void setState(Byte State) {
        this.State = State;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getDeviceBrand() {
        return DeviceBrand;
    }

    public void setDeviceBrand(String DeviceBrand) {
        this.DeviceBrand = DeviceBrand;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String DeviceModel) {
        this.DeviceModel = DeviceModel;
    }

    public String getDeviceOS() {
        return DeviceOS;
    }

    public void setDeviceOS(String DeviceOS) {
        this.DeviceOS = DeviceOS;
    }

    public String getDeviceOSversion() {
        return DeviceOSversion;
    }

    public void setDeviceOSversion(String DeviceOSversion) {
        this.DeviceOSversion = DeviceOSversion;
    }

    public Long getLastSyncTime() {
        return LastSyncTime;
    }

    public void setLastSyncTime(Long LastSyncTime) {
        this.LastSyncTime = LastSyncTime;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}

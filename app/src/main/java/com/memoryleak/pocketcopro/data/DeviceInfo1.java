package com.memoryleak.pocketcopro.data;

public class DeviceInfo1 {
    private String name;
    private String value;

    public DeviceInfo1(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
/*
public class DeviceInfo {
    private String id;
    private String name;
    private String location;
    private String usage;
    private String status;
    private String craft;

    public DeviceInfo(String id, String name, String location, String usage, String status, String carft)
    {
        this.craft = carft;
        this.id = id;
        this.location = location;
        this.name = name;
        this.usage = usage;
        this.status = status;
    }
}*/
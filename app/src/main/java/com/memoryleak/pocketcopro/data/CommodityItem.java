package com.memoryleak.pocketcopro.data;

import java.time.Instant;

import lombok.Data;

@Data
public class CommodityItem {
    private String itemId;
    private String commodityId;
    private String category;
    private String name;
    private String iconId;
    private String description;
    private String itemDescription;
    private String modeName;
    private String modeDescription;
    private String billingModeName;
    private String billingModeDescription;
    private String address;
    private Integer status;
    private String thirdDescription;
    private Point location;
    private Double height;
    private Double distance;

    private Instant createTime;

    private Instant updateTime;
    public CommodityItem(String name, String iconid, Point p, Double h, Double d)
    {
        this.name = name;
        this.iconId = iconid;
        this.location = p;
        this.height = h;
        this.distance = d;
    }
}

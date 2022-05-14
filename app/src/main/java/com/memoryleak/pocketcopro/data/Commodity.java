package com.memoryleak.pocketcopro.data;

import com.memoryleak.pocketcopro.R;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class Commodity {
    private String commodityId;
    private String userId;
    private String category;
    private String name;
    private String description;
    private String iconId;
    private String modeName;
    private String modeDescription;
    private String billingModeName;
    private String billingModeDescription;
    private String thirdPartyApi;
    private Integer status;
    private Integer count;
    private Type commodityType;
    private Set<String> qualifications;
    private Instant createTime;
    private Instant updateTime;
    private Double distance;

    private List<CommodityItem> items;

    public enum Type {
        DELIVERY(R.string.delivery),
        OUTLET(R.string.outlet),
        SHARE(R.string.share);

        public final int title;

        Type(int title) {
            this.title = title;
        }
    }
}

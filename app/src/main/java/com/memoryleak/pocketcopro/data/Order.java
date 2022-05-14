package com.memoryleak.pocketcopro.data;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class Order {
    private String orderId;
    private String commodityName;
    private String modeName;
    private String modeDescription;
    private String billingModeName;
    private String billingModeDescription;
    private String status;
    private String iconId;
    private String deliveryId;
    private String deliveryCompany;
    private List<Action> nextActionList;
    private Integer totalCost;
    private Instant createTime;
    private Instant updateTime;
    private List<Event> orderEventList;

    @Data
    public static class Event {
        private Instant eventTime;
        private String status;
        private String description;
    }

    @Data
    public static class Action {
        private String actionId;
        private String userName;
        private String sellerName;
        private API apiName;
        private String roll;
    }

    public enum API {
        buy,
        deposit,
        deliver,
        confirmDelivery,
        returnProduct,
        confirmReturn,
        continueRent,
        unlock,
        end,
    }
}

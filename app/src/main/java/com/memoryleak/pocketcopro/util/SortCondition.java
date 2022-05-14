package com.memoryleak.pocketcopro.util;

import lombok.Data;

@Data
public class SortCondition {
    private String field;
    private Order order;

    public enum Order {
        ASCENDING,
        DESCENDING,
    }

    public SortCondition() {
    }

    public SortCondition(String field, Order order) {
        this.field = field;
        this.order = order;
    }

    @Deprecated
    public static final SortCondition ID_ASCENDING = new SortCondition(Constant.ID, Order.ASCENDING);
    public static final SortCondition ORDER_ID_ASCENDING = new SortCondition(Constant.ORDER_ID, Order.ASCENDING);
    public static final SortCondition CREATE_TIME_DESCENDING = new SortCondition(Constant.CREATE_TIME, SortCondition.Order.DESCENDING);

    public static final SortCondition DISTANCE_ASCENDING = new SortCondition(Constant.DISTANCE, Order.ASCENDING);
    public static final SortCondition COMMODITY_ID_ASCENDING = new SortCondition(Constant.COMMODITY_ID, Order.ASCENDING);
}

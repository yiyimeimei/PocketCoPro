package com.memoryleak.pocketcopro.util;

import lombok.Data;

@Data
public class Result<T> {
    private StatusCode statusCode;
    private T data;

    public enum StatusCode {
        SUCCESS,
        UNKNOWN_ERROR,
        IO_FAILURE,
        ILLEGAL_ARGUMENT,
        USER_NOT_VERIFIED,
        ORDER_ALREADY_CANCELED,
        ORDER_EXPIRED,
        COMMODITY_ITEM_USED,
        ORDERID_NOT_EXIST,
        SCHEDULER_FAULT,
        NULL_ARGUMENT,
        PARSE_ERROR,
        SERVICE_CONNECTION_ERROR,
        ORDER_INIT_FAILURE,
        WRONG_TASK,

        COMMODITY_INDEX_IO_ERROR,

        USER_NOT_EXIST,
        USER_PASSWORD_ERROR,
        USER_IDENTITY_ERROR,

        COMMODITY_NOT_FOUND,
        USER_BALANCE_NOT_ENOUGH,
        DELIVERY_NOT_FOUND
    }
}
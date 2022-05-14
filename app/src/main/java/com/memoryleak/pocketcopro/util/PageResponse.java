package com.memoryleak.pocketcopro.util;

import java.util.List;

import lombok.Data;

@Data
public class PageResponse<T> {
    private List<T> data;
    private Integer position;
    private Integer total;

    public PageResponse() {
    }

    public PageResponse(List<T> data, Integer position, Integer total) {
        this.data = data;
        this.position = position;
        this.total = total;
    }
}

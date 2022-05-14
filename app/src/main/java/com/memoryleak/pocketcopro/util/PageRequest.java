package com.memoryleak.pocketcopro.util;

import java.util.List;

import lombok.Data;

@Data
public class PageRequest<K, V> {
    private Integer size;
    private Direction direction;
    private List<SortCondition> conditions;
    private K filter;
    private V data;

    public enum Direction {
        BEFORE,
        AFTER
    }

    public PageRequest() {
        this(null, null, null, null, null);
    }

    public PageRequest(Integer size, List<SortCondition> conditions, V data) {
        this(size, Direction.AFTER, conditions, null, data);
    }

    public PageRequest(Integer size, Direction direction, List<SortCondition> conditions, K filter, V data) {
        this.size = size;
        this.direction = direction;
        this.conditions = conditions;
        this.filter = filter;
        this.data = data;
    }
}

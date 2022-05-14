package com.memoryleak.pocketcopro.data;

import lombok.Data;

@Data
public class Point {
    private Double x;
    private Double y;

    public Point() {
    }

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}

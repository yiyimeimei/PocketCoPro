package com.memoryleak.pocketcopro.data;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class RadarData {
    @SerializedName("label")
    private String title;
    @SerializedName("score")
    private double percent;

    public RadarData(String title, double percent) {
        this.title = title;
        this.percent = percent;
    }
}

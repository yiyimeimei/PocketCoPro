package com.memoryleak.pocketcopro.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class Credit {
    @SerializedName("dimensions")
    private List<RadarData> dimensions;
}

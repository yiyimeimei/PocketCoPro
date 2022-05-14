package com.memoryleak.pocketcopro.data;

import java.time.Instant;

import lombok.Data;

@Data
public class User {
    private String userId;
    private String username;
    private String nickname;
    private String address;
    private String phone;
    private String avatarId;
    private Instant createTime;
    private Instant updateTime;
    /*public User(String a, String b, String c)
    {
        this.username = a;
    }*/
    public User(String a)
    {
        this.username = a;
    }
}

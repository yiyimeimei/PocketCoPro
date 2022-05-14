package com.memoryleak.pocketcopro.data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class UserData {
    private String userId;
    private String username;
    private transient String password;
    private transient Integer identity;

    private String nickname;
    private String phone;
    private String address;

    private Double balance;

    private Set<String> qualifications = new HashSet<>();

    private Instant createTime;

    private Instant updateTime;

    public UserData() {
    }

    public UserData(String username, String password, Integer identity) {
        this.username = username;
        this.password = password;
        this.identity = identity;
    }
}
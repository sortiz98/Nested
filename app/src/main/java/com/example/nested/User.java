package com.example.nested;

import com.squareup.picasso.RequestCreator;

public class User {


    private String name;
    private String uid;
    private RequestCreator rc1;

    public User() {}

    public User(String uid, String name, RequestCreator rc1) {
        this.name = name;
        this.uid = uid;
        this.rc1 = rc1;
    }

    public String getUid() { return this.uid; }

    public String getName() {
        return this.name;
    }

    public RequestCreator getRC1() { return this.rc1; }

}

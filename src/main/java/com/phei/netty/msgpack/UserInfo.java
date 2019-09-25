package com.phei.netty.msgpack;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable {
    private String name;
    private int userID;
    private int age;


    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final int getUserID() {
        return userID;
    }

    public final void setUserID(int userID) {
        this.userID = userID;
    }

    public final int getAge() {
        return age;
    }

    public final void setAge(int age) {
        this.age = age;
    }
}

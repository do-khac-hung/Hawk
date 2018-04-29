package com.khachungbg97gmail.projectqr;

/**
 * Created by ASUS on 3/23/2018.
 */



import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASUS on 3/6/2018.
 */

public class Item {
    private  String username="";
    private  String code="";
    private  String location="";

    public Item() {
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("code", code);
        result.put("location", location);
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                ", username='" + username + '\'' +
                ", code='" + code + '\'' +
                ", location=" + location +
                '}';
    }
}

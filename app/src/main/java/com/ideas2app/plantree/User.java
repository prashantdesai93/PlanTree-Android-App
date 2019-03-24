package com.ideas2app.plantree;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 5/14/2018.
 */

public class User {

    public String id, email, fName, lName, university, birthDate, androidDeviceId, lat, lon;
    int isPushEnabled;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fName", fName);
        result.put("lName", lName);
        result.put("email", email);
        result.put("university", university);
        result.put("birthDate", birthDate);
        result.put("androidDeviceId", androidDeviceId);
        result.put("isPushEnabled", isPushEnabled);
        result.put("id", id);
        result.put("lat", lat);
        result.put("lon", lon);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", university='" + university + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", androidDeviceId='" + androidDeviceId + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", isPushEnabled=" + isPushEnabled +
                '}';
    }
}
package com.ideas2app.plantree;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Concern implements Serializable {

    public String cTitle, cDescription, cDate, cCategory, cLat, cLng, cUpVotes, cDownVotes, cId;

    public Concern() {
    }

    public Concern(String cTitle, String cDescription, String cDate, String cCategory, String cLat, String cLng, String cUpVotes, String cDownVotes, String cId) {
        this.cTitle = cTitle;
        this.cDescription = cDescription;
        this.cDate = cDate;
        this.cCategory = cCategory;
        this.cLat = cLat;
        this.cLng = cLng;
        this.cUpVotes = cUpVotes;
        this.cDownVotes = cDownVotes;
        this.cId = cId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cTitle", cTitle);
        result.put("cDescription", cDescription);
        result.put("cDate", cDate);
        result.put("cCategory", cCategory);
        result.put("cLat", cLat);
        result.put("cLng", cLng);
        result.put("cUpVotes", cUpVotes);
        result.put("cDownVotes", cDownVotes);
        result.put("cId", cId);
        return result;
    }

    @Override
    public String toString() {
        return "Concern{" +
                "cTitle='" + cTitle + '\'' +
                ", cDescription='" + cDescription + '\'' +
                ", cDate='" + cDate + '\'' +
                ", cCategory='" + cCategory + '\'' +
                ", cLat='" + cLat + '\'' +
                ", cLng='" + cLng + '\'' +
                ", cUpVotes='" + cUpVotes + '\'' +
                ", cDownVotes='" + cDownVotes + '\'' +
                ", cId='" + cId + '\'' +
                '}';
    }
}

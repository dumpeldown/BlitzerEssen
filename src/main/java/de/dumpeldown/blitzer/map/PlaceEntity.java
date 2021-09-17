package de.dumpeldown.blitzer.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;

public class PlaceEntity implements Serializable {
    private String displayName;
    private double latitude;
    private double longitude;
    private String type;
    private double[] boundingBox;

    public PlaceEntity(JSONObject object){
        this.displayName = object.getString("display_name");
        JSONArray array = object.getJSONArray("boundingbox");
        this.boundingBox = new double[array.length()];
        for(int i = 0; i <array.length(); i++){
            this.boundingBox[i] = array.getDouble(i);
        }
        this.latitude = object.getDouble("lat");
        this.longitude = object.getDouble("lon");
        try {
            this.type = object.getString("class");
        }catch(JSONException jsonException){
            this.type = "undefined";
        }
    }

    @Override
    public String toString() {
        return "Displayname: "+this.displayName
                +"\nType: "+this.type
                +"\nLat: "+this.latitude
                +"\nLon: "+this.longitude
                +"\nBoundingBox: "+ Arrays.toString(this.boundingBox);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }
}

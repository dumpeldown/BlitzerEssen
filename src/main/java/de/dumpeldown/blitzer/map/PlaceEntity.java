package de.dumpeldown.blitzer.map;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class PlaceEntity {
    String displayName;
    double latitude;
    double longitude;
    String type;
    double[] boundingBox;

    public PlaceEntity(JSONObject object){
        this.displayName = object.getString("display_name");
        JSONArray array = object.getJSONArray("boundingbox");
        this.boundingBox = new double[array.length()];
        for(int i = 0; i <array.length(); i++){
            this.boundingBox[i] = array.getDouble(i);
        }
        this.latitude = object.getDouble("lat");
        this.longitude = object.getDouble("lon");
        this.type = object.getString("class");
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
}

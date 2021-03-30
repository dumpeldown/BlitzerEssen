package de.dumpeldown.blitzer.main;

import de.dumpeldown.blitzer.map.MapManager;
import de.dumpeldown.blitzer.map.PlaceEntity;
import de.dumpeldown.blitzer.request.RequestManager;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        /*
        OCRManager.initTesseract();
        OCRManager.initImage();
        for (String s : OCRManager.pngToText().values()) {
            System.out.println(s + "\n-------------------------------------------");
        }
         */
        ArrayList<double[]> wayPoints = new ArrayList<>();

        /*
        Für jeden Eintrag (Straße) der aus dem Bild ausgelesen wird.
         */
        RequestManager requestManager = new RequestManager("hammerstraße essen");
        JSONObject jsonObject = requestManager.makeRequest();
        PlaceEntity entity = new PlaceEntity(jsonObject);
        System.out.println(entity.toString());

        wayPoints.add(new double[]{entity.getLatitude(), entity.getLongitude()});
        MapManager mapManager = new MapManager(wayPoints);
        mapManager.displayMap();
    }
}

package de.dumpeldown.blitzer.map;

import de.dumpeldown.blitzer.request.LocationRequestManager;
import me.tongfei.progressbar.ProgressBar;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class GeocodeManager {
    private static int errorCounter = 0;

    public GeocodeManager() {
    }

    public ArrayList<PlaceEntity> geocodeEntities(ArrayList<String> allStreets) {

        /*
        Hier wird das Geocoding aller Straßen durchgeführt.
         */
        ArrayList<PlaceEntity> allEntities = new ArrayList<>();
        ArrayList<String> errorStreets = new ArrayList<>();
        System.out.println("Größe inkl Duplikate: " + allStreets.size());
        ArrayList<String> allStreetsNoDuplicates = new ArrayList<>(new LinkedHashSet<>(allStreets));
        System.out.println("Größe ohne Duplikate: " + allStreetsNoDuplicates.size());
        for (String street : ProgressBar.wrap(allStreetsNoDuplicates, "Geocoding")) {
            System.out.println(street);
            LocationRequestManager locationRequestManager = new LocationRequestManager(street + " essen");
            JSONObject jsonObject = locationRequestManager.makeRequest();
            if (jsonObject == null) {
                System.out.println("Fehler beim Abrufen der Daten der 'forward-geocoding' " +
                        "API, versuche jetzt nächste Straße.");
                errorCounter++;
                if (errorCounter == 10) {
                    System.out.println("Aborting geocoding, getting a lot of errors.\n" +
                            "Maybe your api key is not set correctly?");
                    return null;
                }
                continue;
            }
            PlaceEntity entity = new PlaceEntity(jsonObject);
            if (entity.getType().equals("undefined")) {
                errorStreets.add(street);
            } else {
                allEntities.add(entity);
            }
            /*
             * Zwischen Request muss ein Timeout von 500ms liegen, da in meinem LocationIQ
             *  Plan 2 req/sec erlaubt sind. Mit 1000ms bin ich dann auf der sicheren Seite.
             */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (errorStreets.size() > 0) {
            System.out.println("Bei diesen Straßen ist ein Problem aufgetreten (Wahrscheinlich OCR Fehler):");
            for (String error : errorStreets) {
                System.out.println(error);
            }
            System.out.println("Willst du die Fehler bei den Straßen selber korrigieren? y/n");
            Scanner scanner = new Scanner(System.in);
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                allEntities.addAll(geocodeEntities(correctStreetNames(errorStreets)));
            }
        }
        return allEntities;
    }

    public ArrayList<String> correctStreetNames(ArrayList<String> errorStreets) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> corrected = new ArrayList<>();
        for (String s : errorStreets) {
            System.out.println("Ursprünglicher Name: " + s);
            System.out.println("Neuer Name: ");
            String cor = scanner.nextLine();
            corrected.add(cor);
        }
        return corrected;
    }

}

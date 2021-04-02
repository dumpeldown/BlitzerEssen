package de.dumpeldown.blitzer.main;

import de.dumpeldown.blitzer.map.MapManager;
import de.dumpeldown.blitzer.map.PlaceEntity;
import de.dumpeldown.blitzer.ocr.OCRManager;
import de.dumpeldown.blitzer.request.RequestManager;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static final String IMAGE_NAME = "1.png";
    public static void main(String[] args) {
        File out = new File("allEntities"+IMAGE_NAME);
        ArrayList<PlaceEntity> allEntities = new ArrayList<>();
        ArrayList<String> allStreets = new ArrayList<>();
        try {
            if (out.createNewFile() || out.length() == 0) {
                System.out.println("Die 'allEntities'-Datei existiert nicht oder ist leer.");

                OCRManager ocrManager = new OCRManager();
                ocrManager.init();
                /*
                Produziert 7 Strings, die die 7 Spalten des Bildes darstellen.
                Einer dieser Strings enthält alle Straßen, diese werden dann an den linebreaks in
                 einzelne Worte gesplittet. Leere Zeilen werden dann entfernt.
                 */
                for (String s : ocrManager.pngToText()) {
                    ArrayList<String> toRemove = new ArrayList<>();
                    List<String> strings =
                            Arrays.stream(s.split("\\r?\\n")).collect(Collectors.toList());
                    for (String str : strings) {
                        /*
                        Leere Zeilen und Feiertag rausfiltern.
                         */
                        if (str.isEmpty() || str.equals("Feiertag")) {
                            toRemove.add(str);
                        }
                    }
                    System.out.println("Insgesamt " + toRemove.size() + " leere zeilen entfernt " +
                            "aus einer Spalte entfernt.");
                    strings.removeAll(toRemove);
                    allStreets.addAll(strings);
                }
                /*
                Hier wird das Geocoding aller Straßen durchgeführt.
                 */
                int done = 1;
                int todo = allStreets.size();
                System.out.println("Starte 'forward-gecoding' für alle Straßen, erwartete Dauer " +
                        "circa " + todo +" Sekunden.");

                for (String street : allStreets) {
                    System.out.println(street);
                    done++;
                    if (done % 10 == 0) {
                        System.out.println("["+(todo/done)*100+"%]\t"
                                +done + " / " + todo + "Straßen bearbeitet.");
                    }
                    RequestManager requestManager = new RequestManager(street + " essen");
                    JSONObject jsonObject = requestManager.makeRequest();
                    if (jsonObject == null) {
                        System.out.println("Fehler beim Abrufen der Daten der 'forward-geocoding' " +
                                "API, versuche jetzt nächste Straße.");
                        continue;
                    }
                    PlaceEntity entity = new PlaceEntity(jsonObject);
                    allEntities.add(entity);
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
                serializeEntities(allEntities);
            } else {
                System.out.println("Benutze serialisierte Daten aus einem vorherigen Durchlauf.");
                allEntities = deserializeEntities();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        MapManager mapManager = new MapManager(allEntities);
        mapManager.displayMap();
    }


    private static void serializeEntities(ArrayList<PlaceEntity> allEntities) {
        /*
        serialize data nachdem das Geocoding abgeschlossen ist.
         */
        try {
            FileOutputStream fos = new FileOutputStream("allEntities"+IMAGE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allEntities);
            oos.close();
            fos.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Serialisieren der ArrayList mit allen Entities.");
            e.printStackTrace();
        }

    }

    private static ArrayList<PlaceEntity> deserializeEntities() {
        try {
            FileInputStream fis = new FileInputStream("allEntities"+IMAGE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<PlaceEntity> allEntities = (ArrayList<PlaceEntity>) ois.readObject();
            ois.close();
            fis.close();
            return allEntities;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
        return null;
    }
}
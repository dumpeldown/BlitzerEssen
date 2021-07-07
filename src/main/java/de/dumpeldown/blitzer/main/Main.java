package de.dumpeldown.blitzer.main;

import de.dumpeldown.blitzer.image.ImageManager;
import de.dumpeldown.blitzer.map.MapManager;
import de.dumpeldown.blitzer.map.PlaceEntity;
import de.dumpeldown.blitzer.ocr.OCRManager;
import de.dumpeldown.blitzer.request.LocationRequestManager;
import me.tongfei.progressbar.ProgressBar;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static String IMAGE_NAME;
    public static String SERIALIZE_NAME;
    private static int errorCounter = 0;

    public static void main(String[] args) {
        ArrayList<String> allStreets;
        ArrayList<PlaceEntity> allEntities = new ArrayList<>();
        boolean value;
        do {
            value = chooseFile();
        } while (!value);
        if (!new ImageManager(IMAGE_NAME).jfifToPng()) {
            System.out.println("Fehler beim Convertieren und PNG!");
            System.exit(-1);
        }
        File serializeFile = new File("./serializedData/" + SERIALIZE_NAME);
        try {
            if (serializeFile.createNewFile() || serializeFile.length() == 0) {
                System.out.println("Die serialisierte Datei existiert nicht oder ist leer.");

                OCRManager ocrManager = new OCRManager();
                if (!ocrManager.init()) {
                    System.out.println("Beende Program wegen Fehler.");
                    return;
                }
                allStreets = getStreetNames(ocrManager);
                allEntities = geocodeEntities(allStreets);
                if (allEntities != null) {
                    serializeEntities(allEntities);
                } else {
                    System.out.println("Exiting, please debug.");
                    return;
                }
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

    private static ArrayList<String> getStreetNames(OCRManager ocrManager) {
        /*
        Produziert 7 Strings, die die 7 Spalten des Bildes darstellen.
        Einer dieser Strings enthält alle Straßen, diese werden dann an den linebreaks in
         einzelne Worte gesplittet. Leere Zeilen werden dann entfernt.
         */
        ArrayList<String> allStreets = new ArrayList<>();
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
        return allStreets;
    }

    private static ArrayList<PlaceEntity> geocodeEntities(ArrayList<String> allStreets) {

        /*
        Hier wird das Geocoding aller Straßen durchgeführt.
         */
        ArrayList<PlaceEntity> allEntities = new ArrayList<>();
        ArrayList<String> errorStreets = new ArrayList<>();
        int todo = allStreets.size();
        System.out.println("Starte 'forward-gecoding' für alle Straßen, erwartete Dauer " +
                "circa " + todo + " Sekunden.");

        for (String street : ProgressBar.wrap(allStreets, "Geocoding")) {
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
        System.out.println("Bei diesen Straßen ist ein Problem aufgetreten (Wahrscheinlich OCR Fehler):");
        for (String error : errorStreets) {
            System.out.println(error);
        }
        return allEntities;
    }

    private static boolean chooseFile() {
        File folder = new File(".\\images");
        List<File> listOfFiles = Arrays.asList(folder.listFiles());
        ArrayList<File> filteredFiles = new ArrayList<>();

        for (File f : listOfFiles) {
            if (f.isFile() && f.getName().contains(".png")) {
                filteredFiles.add(f);
            }
        }
        int i = 0;
        for (File ff : filteredFiles) {
            System.out.println(i + 1 + ": " + ff.getName());
            i++;
        }
        System.out.println("Welche Datei willst du als Karte darstellen?");
        Scanner sc = new Scanner(System.in);
        int auswahl = sc.nextInt();
        if (auswahl <= filteredFiles.size() && auswahl > 0) {
            IMAGE_NAME = filteredFiles.get(auswahl - 1).getName();
            SERIALIZE_NAME = IMAGE_NAME.split("\\.")[0] + ".ser";
        } else {
            System.out.println("Dieser Dateiname wurde nicht gefunden.");
            return false;
        }
        System.out.println("IMAGE_NAME: " + IMAGE_NAME);
        System.out.println("SERIALIZE_NAME: " + SERIALIZE_NAME);
        return true;
    }


    private static void serializeEntities(ArrayList<PlaceEntity> allEntities) {
        /*
        serialize data nachdem das Geocoding abgeschlossen ist.
         */
        try {
            FileOutputStream fos = new FileOutputStream("./serializedData/" + SERIALIZE_NAME);
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
            FileInputStream fis = new FileInputStream("./serializedData/" + SERIALIZE_NAME);
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
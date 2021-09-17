package de.dumpeldown.blitzer.main;

import de.dumpeldown.blitzer.map.GeocodeManager;
import de.dumpeldown.blitzer.map.MapManager;
import de.dumpeldown.blitzer.map.PlaceEntity;
import de.dumpeldown.blitzer.ocr.OCRManager;
import de.dumpeldown.blitzer.serialize.SerializationHelper;
import de.dumpeldown.blitzer.threading.BlitzerTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

public class Main {

    private static final long RUNNER_DELAY = 10000000;
    private static final int NUMBER_OF_TWEETS = 50;
    public static void main(String[] args) {
        ArrayList<String> allStreets;
        ArrayList<PlaceEntity> allEntities = new ArrayList<>();
        String imageName = chooseMode();

        if (!imageName.isBlank()) {
            GeocodeManager geocodeManager = new GeocodeManager();
            String serializeName = imageName.split("\\.")[0] + ".ser";
            File serializeFile = new File("./serializedData/" + serializeName);
            try {
                if (serializeFile.createNewFile() || serializeFile.length() == 0) {
                    System.out.println("Für dieses Bild wird nun Texterkennung durchgeführt.");

                    OCRManager ocrManager = new OCRManager();
                    if (!ocrManager.init(imageName)) {
                        System.out.println("Beende Program wegen Fehler.");
                        return;
                    }
                    allStreets = ocrManager.getStreetNames();
                    allEntities = geocodeManager.geocodeEntities(allStreets);
                    if (allEntities != null) {
                        SerializationHelper.serializeEntities(allEntities, serializeName);
                    } else {
                        System.out.println("Exiting, please debug.");
                        return;
                    }
                } else {
                    System.out.println("Benutze serialisierte Daten aus einem vorherigen Durchlauf.");
                    allEntities = SerializationHelper.deserializeEntities(serializeName);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            MapManager mapManager = new MapManager(allEntities);
            mapManager.displayMap();
        }
    }


    private static String chooseMode() {
        Scanner sc = new Scanner(System.in);
        System.out.println("(1)Einzelnes Bild verwenden oder \n (2) Dauerläufer-Thread starten?");
        if (sc.nextInt() == 2) {
            Timer blitzerTimer = new Timer();
            blitzerTimer.schedule(new BlitzerTask(Main.NUMBER_OF_TWEETS), 0, RUNNER_DELAY);
            return "";
        }
        File folder = new File(".\\images");
        List<File> listOfFiles = List.of(folder.listFiles());
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

        int auswahl = sc.nextInt();
        if (!(auswahl <= filteredFiles.size() && auswahl > 0)) {
            System.out.println("Dieser Dateiname wurde nicht gefunden.");
            return "";

        }
        return filteredFiles.get(auswahl - 1).getName();
    }
}
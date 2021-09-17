package de.dumpeldown.blitzer.threading;

import de.dumpeldown.blitzer.image.ImageManager;
import de.dumpeldown.blitzer.map.GeocodeManager;
import de.dumpeldown.blitzer.map.PlaceEntity;
import de.dumpeldown.blitzer.ocr.OCRManager;
import de.dumpeldown.blitzer.serialize.SerializationHelper;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;

public class BlitzerThread extends Thread{
    private URL url;
    private ImageManager imageManager;
    private OCRManager ocrManager;
    private GeocodeManager geocodeManager;

    public BlitzerThread(URL url){
        this.url = url;
        this.imageManager = new ImageManager();
        this.ocrManager = new OCRManager();
        this.geocodeManager = new GeocodeManager();

    }
    @Override
    public void run() {
        String fileName;
        String serializeName;
        try {
            fileName = this.imageManager.handleTwitterImage(this.url);
            serializeName = fileName.split("\\.")[0] + ".ser";
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        if (!this.ocrManager.init(fileName)) {
            System.out.println("Beende Program wegen Fehler.");
            return;
        }
        ArrayList<String> allStreets = this.ocrManager.getStreetNames();
        ArrayList<PlaceEntity> allEntities = geocodeManager.geocodeEntities(allStreets);
        if (allEntities != null) {
            SerializationHelper.serializeEntities(allEntities, serializeName);
        } else {
            System.out.println("Exiting, please debug.");
        }
        System.out.println("Serialization finished. Map can now be displayed for this image.");
    }
}

package de.dumpeldown.blitzer.serialize;

import de.dumpeldown.blitzer.map.PlaceEntity;

import java.io.*;
import java.util.ArrayList;

public class SerializationHelper {
    public static void serializeEntities(ArrayList<PlaceEntity> allEntities, String serializeName) {
        /*
        serialize data nachdem das Geocoding abgeschlossen ist.
         */
        try {
            FileOutputStream fos = new FileOutputStream("./serializedData/" + serializeName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allEntities);
            oos.close();
            fos.close();
        } catch (IOException e) {
            System.out.println("Fehler beim Serialisieren der ArrayList mit allen Entities.");
            e.printStackTrace();
        }

    }

    public static ArrayList<PlaceEntity> deserializeEntities(String serializeName) {
        try {
            FileInputStream fis = new FileInputStream("./serializedData/" + serializeName);
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

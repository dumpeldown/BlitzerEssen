package de.dumpeldown.blitzer.ocr;

import me.tongfei.progressbar.ProgressBar;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OCRManager {
    private static Tesseract tesseract;
    private static BufferedImage bufferedImage;
    private static String IMAGE_PROPERTIES;

    public OCRManager() {
        tesseract = new Tesseract();
        File f = new File(".");
        String absolutPath = f.getAbsolutePath();
        absolutPath = absolutPath.substring(0, absolutPath.length() - 1);
        tesseract.setDatapath(absolutPath + "src\\main\\resources\\de\\dumpeldown\\blitzer\\ocr" +
                "\\tesseract_data");
        tesseract.setLanguage("deu");
    }

    public boolean init(String imageName) {
        String propName = imageName.split("\\.")[0] + ".properties";
        try {
            bufferedImage = ImageIO.read(new File("./images/" + imageName));
            byte[] propByte;
            try {
                propByte = FileUtils.readFileToByteArray(new File("./images/" + propName));
            } catch (NullPointerException e) {
                System.out.println("Prop Datei für die ausgewählte Datei nicht gefunden.");
                return false;
            }
            IMAGE_PROPERTIES = new String(propByte);
            System.out.println("IMAGE_PROPERTIES gelesen: " + IMAGE_PROPERTIES);
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<String> getStreetNames() {
        /*
        Produziert 7 Strings, die die 7 Spalten des Bildes darstellen.
        Einer dieser Strings enthält alle Straßen, diese werden dann an den linebreaks in
         einzelne Worte gesplittet. Leere Zeilen werden dann entfernt.
         */
        ArrayList<String> allStreets = new ArrayList<>();
        for (String s : pngToText()) {
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
            strings.removeAll(toRemove);
            allStreets.addAll(strings);
        }
        return allStreets;
    }

    public ArrayList<String> pngToText() {
        ProgressBar pb = new ProgressBar("OCR", getAmountOfDays());
        pb.setExtraMessage("Preparing OCR");
        double xStart = 0, yStart = 0;
        ArrayList<String> streets = new ArrayList<>();
        double width = bufferedImage.getWidth();
        double height = bufferedImage.getHeight();
        System.out.println("Gesamt Breite des Bildes: " + width);
        System.out.println("Gesamt Höhe des Bildes: " + height);
        try {
            xStart = getXStart();
            yStart = getYStart();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Malformed or misconfigured .properties file.");
        }
        System.out.println("xStart= " + xStart);
        System.out.println("yStart= " + yStart);
        //höhe ohne header
        // breite ohne ränder, jede spalte ist gleich breit
        height = height - yStart;
        width -= (xStart * 2);
        pb.setExtraMessage("");
        for (int spalte = 0; spalte < getAmountOfDays(); spalte++) {
            pb.step();
            try {
                streets.add(tesseract.doOCR(bufferedImage,
                        new Rectangle(
                                (int) (xStart + ((width / getAmountOfDays()) * spalte)),
                                (int) yStart,
                                (int) (width / getAmountOfDays()),
                                (int) height)));
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        }
        return streets;
    }

    private double getXStart() {
        double x = Double.parseDouble(IMAGE_PROPERTIES.split(";")[0].split("=")[1].trim());
        System.out.println("Found x in prop file: " + x);
        return x;
    }

    private double getYStart() {
        double y = Double.parseDouble(IMAGE_PROPERTIES.split(";")[1].split("=")[1].trim());
        System.out.println("Found y in prop file: " + y);
        return y;
    }

    private int getAmountOfDays() {
        try {
            return Integer.parseInt(IMAGE_PROPERTIES.split(";")[2].split("=")[1].trim());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Malformed Properties File.");
        }
        //default to 7 days.
        return 7;
    }
}
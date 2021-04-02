package de.dumpeldown.blitzer.ocr;

import de.dumpeldown.blitzer.main.Main;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class OCRManager {
    private static Tesseract tesseract;
    private static BufferedImage bufferedImage;
    private static String IMAGE_PROPERTIES;

    private static final String PROP_NAME = Main.IMAGE_NAME.split("\\.")[0]+".prop";

    public OCRManager() {
        tesseract = new Tesseract();
        File f = new File(".");
        String absolutPath = f.getAbsolutePath();
        absolutPath = absolutPath.substring(0, absolutPath.length() - 1);
        tesseract.setDatapath(absolutPath + "src\\main\\resources\\de\\dumpeldown\\blitzer\\ocr" +
                "\\tesseract_data");
        tesseract.setLanguage("deu");
    }

    public boolean init() {
        try {
            bufferedImage = ImageIO.read(getClass().getResource(Main.IMAGE_NAME));
            byte[] propByte;
            System.out.println(PROP_NAME);
            try {
                propByte =
                        getClass().getResource(PROP_NAME).openStream().readAllBytes();
            }catch(NullPointerException e){
                System.out.println("Prop Datei für die ausgewählte Datei nicht gefunden.");
                return false;
            }
            System.out.println(
                Arrays.toString(propByte)
            );
            IMAGE_PROPERTIES =
                    new String(
                        propByte
                    );
            System.out.println("IMAGE_PROPERTIES gelesen: " + IMAGE_PROPERTIES);
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<String> pngToText() {
        ArrayList<String> straßen = new ArrayList<>();
        double width = bufferedImage.getWidth();
        double height = bufferedImage.getHeight();
        System.out.println("Gesamt Breite des Bildes: " + width);
        System.out.println("Gesamt Höhe des Bildes: " + height);
        double xStart = getXStart();
        double yStart = getYStart();
        System.out.println("xStart= "+xStart);
        System.out.println("yStart= "+yStart);
        //höhe ohne header
        // breite ohne ränder, jede spalte ist gleich breit
        height = height - yStart;
        width -= (xStart * 2);
        System.out.println("Höhe des Bildes ohne 'Header': " + height);
        for (int spalte = 0; spalte < getAmountOfDays(); spalte++) {
            System.out.println("Aktuelle Spalte: " + spalte);
            System.out.println("Trying to read at coordinates: \n" +
                    "x = " + (xStart + ((width / 7) * spalte)) +
                    "\ny = " + yStart +
                    "\nwidth = " + width / getAmountOfDays() +
                    "\nheight = " + height);
            try {
                straßen.add(tesseract.doOCR(bufferedImage,
                        new Rectangle(
                                (int) (xStart + ((width / getAmountOfDays()) * spalte)),
                                (int) yStart,
                                (int) (width / getAmountOfDays()),
                                (int) height)));
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        }
        return straßen;
    }

    private double getYStart() {
        return Double.parseDouble(IMAGE_PROPERTIES.split(";")[1].split("=")[1].trim());
    }

    private double getXStart() {
        return Double.parseDouble(IMAGE_PROPERTIES.split(";")[0].split("=")[1].trim());
    }

    private int getAmountOfDays(){
        return Integer.parseInt(IMAGE_PROPERTIES.split(";")[2].split("=")[1].trim());
    }
}
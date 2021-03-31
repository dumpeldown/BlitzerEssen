package de.dumpeldown.blitzer.ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class OCRManager {
    private static Tesseract tesseract;
    private static BufferedImage bufferedImage;

    public static void initTesseract() {
        tesseract = new Tesseract();
        tesseract.setDatapath("C://Users//Jurek//Documents//programming-fun//Blitzer//src//main" +
                "//resources//tesseract_data");
        tesseract.setLanguage("deu");
    }

    public static void initImage() {
        try {
            bufferedImage = ImageIO.read(new File("C://Users//Jurek//Documents//programming-fun" +
                    "//Blitzer//src//main//resources" +
                    "//tesseract_data//3.png"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static ArrayList<String> pngToText() {
        ArrayList<String> straßen = new ArrayList<>();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        System.out.println("Gesamt Breite des Bildes: " + width);
        System.out.println("Gesamt Höhe des Bildes: " + height);
        int xStart = 30;
        int yStart = 115;
        //höhe ohne header
        // breite ohne ränder, jede spalte ist gleich breit
        height = height - yStart;
        width -= (xStart * 2);
        System.out.println("Höhe des Bildes ohne 'Header': " + height);
        for (int spalte = 0; spalte < 7; spalte++) {
            System.out.println("Aktuelle Spalte: " + spalte);
            System.out.println("Trying to read at coordinates: \n" +
                    "x = " + (xStart + ((width / 7) * spalte)) +
                    "\ny = " + yStart +
                    "\nwidth = " + width / 7 +
                    "\nheight = " + height);
            try {
                straßen.add(tesseract.doOCR(bufferedImage,
                        new Rectangle(
                                xStart + ((width / 7) * spalte),
                                yStart,
                                width / 7,
                                height)));
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        }
        return straßen;
    }
}

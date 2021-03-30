package de.dumpeldown.blitzer.ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class OCRManager {
    private static Tesseract tesseract;
    private static BufferedImage bufferedImage;

    public static void initTesseract() {
        tesseract = new Tesseract();
        tesseract.setDatapath("C://Users//Jurek//Documents//programming-fun//BlitzerEssen//tesseract_data");
        tesseract.setLanguage("deu");
    }

    public static void initImage(){
        try {
            bufferedImage = ImageIO.read(new File("C://Users//Jurek//Documents//programming-fun//BlitzerEssen" +
                    "//tesseract_data//data.png"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /*
    1. rec: x1 = 35, x2 = 380, y1 = 115, y2 = 2000
     */
    public static HashMap<Integer, String> pngToText() {
        HashMap<Integer, String> straßen = new HashMap<>();
        int width = 330;
        int height = 1990;
        int xStart = 35;
        int yStart = 115;
        for (int i = 0; i < 7; i++) {
            try {
                straßen.put(i,tesseract.doOCR(bufferedImage, new Rectangle(xStart+(width*i),
                        yStart,
                        width,
                        height)));
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        }
        return straßen;
    }
}

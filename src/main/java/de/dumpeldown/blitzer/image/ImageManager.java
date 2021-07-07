package de.dumpeldown.blitzer.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageManager {
    String imageName;
    String outputFile;

    public ImageManager(String imageName) {
        this.imageName = imageName;
        this.outputFile = imageName;
    }

    public boolean jfifToPng() {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(".\\images\\" + imageName));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // write the bufferedImage back to outputFile
        try {
            ImageIO.write(bufferedImage, "png", new File(".\\images\\" + imageName));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

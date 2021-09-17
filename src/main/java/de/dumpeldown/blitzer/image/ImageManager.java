package de.dumpeldown.blitzer.image;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageManager {

    public ImageManager() {
    }

    public boolean jfifToPng(String imageName) {
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

    public void generatePropFile() {

    }

    public String saveImageFromTwitter(URL url) throws FileNotFoundException {
        String fileName = url.toString().split("/")[4];
        String serializedFile = fileName.split("\\.")[0] + ".ser";

        if (Files.exists(Path.of("./serializedData/" + serializedFile))) {
            System.out.println("Image already was processed by OCR.");
            throw new FileNotFoundException("Image already OCR'ed.");
        }
        //incase there exists an old image that was not ocr'ed before, delete it (jpg, png and properties).
        try {
            Files.deleteIfExists(Paths.get("./images/" + fileName));
            Files.deleteIfExists(Paths.get("./images/" + fileName.split("\\.")[0] + ".png"));
            Files.deleteIfExists(Paths.get("./images/" + fileName.split("\\.")[0] + ".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get("./images/" + fileName));
        } catch (IOException e) {
            System.out.println("Image already downloaded.");
            throw new FileNotFoundException("Image already downloaded.");
        }
        System.out.println("Image successfully downloaded.");
        createPropFile(fileName);

        return resizeImageToUniformDimension(fileName);
    }

    private void createPropFile(String fileName) {
        System.out.println("Creating .properties-File for new image.");
        String defaultValues = "x = 10;y = 58;days = 7";
        String propFile = fileName.split("\\.")[0] + ".properties";
        Path path = Paths.get("./images/" + propFile);
        try {
            Files.createFile(path);
            Files.write(path, defaultValues.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create .properties-File.");
        }
    }

    public String handleTwitterImage(URL url) throws FileNotFoundException {
        return saveImageFromTwitter(url);
    }

    public String resizeImageToUniformDimension(String imageName) {
        System.out.println("Starting to resize Image to width 1000 and keep aspect ratio.");
        BufferedImage bufferedImage;
        String pngName = imageName.split("\\.")[0] + ".png";
        try {
            bufferedImage = ImageIO.read(new File(".\\images\\" + imageName));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        BufferedImage newBufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, 1500);

        // write the bufferedImage back to outputFile
        try {
            ImageIO.write(newBufferedImage, "png", new File(".\\images\\" + pngName));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        //delete the old jpg
        try {
            Files.deleteIfExists(Path.of(imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pngName;
    }
}

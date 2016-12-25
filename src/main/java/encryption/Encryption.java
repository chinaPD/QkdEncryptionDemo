package encryption;

import com.sun.deploy.util.ArrayUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Created by hadoop on 16-12-23.
 */
public class Encryption {

    private static final String ENCRYPTION_ALGORITHM = "AES"; //  AES/CBC/PKCS5Padding
    public static final String BIN_KEY_STR = "0101010101010101110000110011010101010101" +
            "010100101010101010101010100101";
    private static byte[] temp;

    public static byte[] encryptFile2Bytes(String fullFilePath) {
        return encryptFile2Bytes(fullFilePath, BIN_KEY_STR);
    }

    public static byte[] encryptFile2Bytes(String fullFilePath , String binKeyStr) {
        byte[] imageRawData = null;
        try {
            imageRawData = extractImage2Bytes(fullFilePath);
            System.out.println("Raw Image Size: " + imageRawData.length);
            imageRawData = encrypt(imageRawData, binKeyStr);
            System.out.println("Encrypted Image Size: " + imageRawData.length);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageRawData;
    }

    public static File decryptBytes2File(byte[] dataBytes) {
        return decryptBytes2File(dataBytes, BIN_KEY_STR);
    }

    public static File decryptBytes2File(byte[] dataBytes, String binKeyStr) {
        File file = null;
        try {
            byte[] imageRawData = null;
            System.out.println("Encrypted Image Size: " + dataBytes.length);
            imageRawData = decrypt(dataBytes, binKeyStr);
            System.out.println("Reproducted Image Size: " + imageRawData.length);
            file = compressBytes2Image(imageRawData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File encryptImageFileShowable(String imageFullPath) {
        File rawImageFile = new File(imageFullPath);
        BufferedImage bufferedImage = null;
        int imageWidth;
        int imageHeight;
        try {
            bufferedImage = ImageIO.read(rawImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        imageWidth = bufferedImage.getWidth();
        imageHeight = bufferedImage.getHeight();
        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
        byte[] imageBytes = data.getData();
        try {
            imageBytes = encrypt(imageBytes, BIN_KEY_STR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage newBufferImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
        for (int r = 0; r < imageHeight; r++) {
            for (int c = 0; c < imageWidth; c++) {
                int index = r * imageWidth + c;
                int red = imageBytes[index] & 0xFF;
                int green = imageBytes[index + 1] & 0xFF;
                int blue = imageBytes[index + 2] & 0xFF;
                int rgb = (red << 16) | (green << 8) | blue;
                bufferedImage.setRGB(c, r, rgb);
            }
        }

        File outputFile = new File(Encryption.class.getResource("../fileCache").getFile(),
                "encryptedImage.jpg");
        outputFile.setWritable(true);

        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            ImageIO.write(bufferedImage, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    public static File decryptImageFile(String imageFullPath) {
        File imageFile = null;
        try {
//            byte[] imageRawData = extractImage2Bytes(imageFullPath);
            byte[] imageRawData = temp;
            System.out.println("Encrypted Image Size: " + imageRawData.length);
            imageRawData = decrypt(imageRawData, BIN_KEY_STR);
            System.out.println("Reproducted Image Size: " + imageRawData.length);
            imageFile = compressBytes2Image(imageRawData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    public static byte[] binaryStr2Bytes(String key) {
        int strLen = key.length();
        int addBitsNum = strLen % 8;
        int byteNum = strLen / 8;
        if (addBitsNum > 0) byteNum += 1;
        addBitsNum = 8 - addBitsNum;
        String addStr = new String(new char[addBitsNum]).replace("\0", "1");
        key += addStr;
        System.out.println("AddBitsNum: " + addBitsNum + "\n"
                + "    String: " + addStr);
        byte[] keyBytes = new byte[byteNum];

        int baseIndex = 0;
        for (int i = 0; i < byteNum; i++) {
            baseIndex = i << 3;
            for (int j = 0; j < 8; j++) {
                keyBytes[i] <<= 1;
                if (key.charAt(baseIndex + j) == '0') {
                    keyBytes[i] += 0;
                } else {
                    keyBytes[i] += 1;
                }
            }
        }

        return keyBytes;
    }

    public static Key binaryStr2Key(String key) throws Exception {
        byte[] keyBytes = binaryStr2Bytes(key);
        byte[] fixedKeyBytes = new byte[16];

        for (int i = 0; i < 16; i++) {
            if (i < keyBytes.length) {
                fixedKeyBytes[i] = keyBytes[i];
            } else {
                fixedKeyBytes[i] = (byte) i;
            }
        }
  /*      char[] keyChars = bytesToCharsUTFCustom(keyBytes);
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(keyChars, salt, 65526, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] fixedKeyBytes = f.generateSecret(spec).getEncoded();

        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher c = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, fixedKeyBytes);*/
        SecretKey secretKey = new SecretKeySpec(fixedKeyBytes, ENCRYPTION_ALGORITHM);
        return secretKey;
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key secretKey = binaryStr2Key(key);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key secretKey = binaryStr2Key(key);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static int imageWidth = 0;
    private static int imageHeight = 0;
    private static boolean hasAlphaChannel = false;

    public static byte[] extractImage2Bytes(String imageFullPath) throws IOException {
        // open Image
/*         File imgFile = new File(imageFullPath);
       BufferedImage bufferedImage = ImageIO.read(imgFile);

        imageWidth = bufferedImage.getWidth();
        imageHeight = bufferedImage.getHeight();
        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
        hasAlphaChannel = (bufferedImage.getAlphaRaster() != null);

        return data.getData();*/
        Path path = Paths.get(imageFullPath);
        return Files.readAllBytes(path);
    }

    public static File createEncryptedFile(String rawFilePath) {
        File imageFile = null;
        try {
            byte[] imageRawData = extractImage2Bytes(rawFilePath);
            System.out.println("Raw Image Size: " + imageRawData.length);
            imageRawData = encrypt(imageRawData, BIN_KEY_STR);
            temp = imageRawData;
            System.out.println("Encrypted Image Size: " + imageRawData.length);
            imageFile = compressBytes2Image(imageRawData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    public static File createImageFileFromBytes(byte[] dataBytes) throws IOException {
        File outputFile = new File(Encryption.class.getResource("../fileCache").getFile(),
                "noise.jpg");
        outputFile.delete();
        outputFile.setWritable(true);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        int byteSize = dataBytes.length;
        double temp = (byteSize / 3.0 / 0.625);
        temp = Math.sqrt(temp);
        int imageWidth = (int)(temp);
        int imageHeight = (int)(temp * 0.625);
        System.out.println("Generated File Size:\n"
                    + "    Width: " + imageWidth + "\n"
                    + "    Height: " + imageHeight);

        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
        for (int r = 0; r < imageHeight; r++) {
            for (int c = 0; c < imageWidth; c++) {
                int index = r * imageWidth + c;
                if (index+2 > byteSize) break;
                int red = dataBytes[index] & 0xFF;
                int green = dataBytes[index + 1] & 0xFF;
                int blue = dataBytes[index + 2] & 0xFF;
                int rgb = (red << 16) | (green << 8) | blue;
                bufferedImage.setRGB(c, r, rgb);
            }
        }

        ImageIO.write(bufferedImage, "jpg", outputFile);
        return outputFile;
    }

    public static File compressBytes2Image(byte[] imageBytes) throws IOException {
        System.out.println("ByteSize: " + imageBytes.length + "\n");
        File outputFile = new File(Encryption.class.getResource("../fileCache").getFile(),
                "image.jpg");
        outputFile.delete();
        outputFile.setWritable(true);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        Path path = Files.write(Paths.get(outputFile.getAbsolutePath()), imageBytes);
        System.out.println("File path: " + path.toString());
     /*   FileOutputStream stream = new FileOutputStream(outputFile);
        try {
            stream.write(imageBytes);
        } finally {
            stream.close();
        }*/
        return outputFile;
       /* InputStream imageByteArray = new BufferedInputStream(new ByteArrayInputStream(imageBytes));
        BufferedImage bufferedImage = ImageIO.read(imageByteArray);
        if (bufferedImage == null) {
            System.out.println("Buffered Image is null!!!");
            return null;
        }*/
 /*       int[][] result = new int[imageHeight][imageWidth];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < imageBytes.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) imageBytes[pixel] & 0xff) << 24); // alpha
                argb += ((int) imageBytes[pixel + 1] & 0xff); // blue
                argb += (((int) imageBytes[pixel + 2] & 0xff) << 8); // green
                argb += (((int) imageBytes[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == imageWidth) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < imageBytes.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) imageBytes[pixel] & 0xff); // blue
                argb += (((int) imageBytes[pixel + 1] & 0xff) << 8); // green
                argb += (((int) imageBytes[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == imageWidth) {
                    col = 0;
                    row++;
                }
            }
        }*/

    /*    int byteSize = imageBytes.length;
        int imagePixelSize = imageWidth * imageHeight;
        System.out.println("ByteSize: " + byteSize + "\n"
                    + "    ImagePixelSize： " + imagePixelSize + "\n"
                    + "    Image Height: " + imageHeight + "\n"
                    + "    Image Width: " + imageWidth);

        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
        for (int r = 0; r < imageHeight; r++) {
            for (int c = 0; c < imageWidth; c++) {
                int index = r * imageWidth + c;
                int red = imageBytes[index] & 0xFF;
                int green = imageBytes[index + 1] & 0xFF;
                int blue = imageBytes[index + 2] & 0xFF;
                int rgb = (red << 16) | (green << 8) | blue;
                bufferedImage.setRGB(c, r, rgb);
            }
        }
        System.out.println("Image Raw Size: " + imageBytes.length + "\n"
                + "    Buffered Image Info: " + bufferedImage.toString());

        File outputFile = new File(Encryption.class.getResource("../fileCache").getFile(),
                "image.jpg");
        outputFile.setWritable(true);
//        new File("../fileCache")
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        ImageIO.write(bufferedImage, "jpg", outputFile);
        return outputFile;*/
    }

    public static char[] bytesToCharsUTFCustom(byte[] bytes) {
        char[] buffer = new char[bytes.length >> 1];
        for (int i = 0; i < buffer.length; i++) {
            int bpos = i << 1;
            char c = (char) (((bytes[bpos] & 0x00FF) << 8) + (bytes[bpos + 1] & 0x00FF));
            buffer[i] = c;
        }
        return buffer;
    }


}

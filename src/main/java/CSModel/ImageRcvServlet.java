package CSModel;

import controller.ServerCall;
import sun.misc.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Created by hadoop on 16-12-14.
 */

@WebServlet("/image")
@MultipartConfig
public class ImageRcvServlet extends HttpServlet {
    private static ServerCall mServerCall;

    public static void registerServerCall(ServerCall serverCall) {
        mServerCall = serverCall;
    }

    public static void receiveEncryptedFile(byte[] dataBytes) {
        if (mServerCall != null) {
            mServerCall.receiveEncryptedFile(dataBytes);
        }
    }

    public static void receiveNormalFile(File imageFile) {
        if (mServerCall != null) {
            mServerCall.receiveNormalFile(imageFile);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Come in!");
        String description = req.getParameter("description");   // Retrieves <input type="text" name="description">
        Part filePart = req.getPart("file");    // Retrieves <input type="file" name="file">
        String submittedName = filePart.getName();
        String contentType = filePart.getContentType();
        System.out.println(submittedName + "\n"
                + "    " + contentType);
        String fileName = Paths.get(submittedName).getFileName().toString();
        System.out.println(fileName);
        if (contentType.equals("image/jpeg")) {
            if (fileName == null || fileName.equals("")) fileName = "temp" + Math.round(1000 * Math.random());
            InputStream fileContent = filePart.getInputStream();
            File imageFile = new File(getClass().getResource(".").getFile(), ".." + File.pathSeparator
                    + "fileCache" + File.pathSeparator + fileName);
            if (!imageFile.exists()) {
                imageFile.createNewFile();
            }
            imageFile.setWritable(true);
            OutputStream fileOut = new FileOutputStream(imageFile);
            copy(fileContent, fileOut);

            receiveNormalFile(imageFile);
        } else {
            InputStream fileContent = filePart.getInputStream();
            long receivedFileSize = filePart.getSize();
            System.out.println("Http Server received file size: " + receivedFileSize);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[100000];

            while ((nRead = fileContent.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] dataBytes = buffer.toByteArray();

            receiveEncryptedFile(dataBytes);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        System.out.println("ImageReceiver: receive a GET request!");
    }

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;
    private void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
            //If needed, close streams.
        } finally {
            input.close();
            output.close();
        }
    }
}

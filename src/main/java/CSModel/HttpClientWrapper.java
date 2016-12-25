package CSModel;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hadoop on 16-12-14.
 */
public class HttpClientWrapper {

    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String BOUNDARY = "----------------adjnvouvmx924ws";
    private static final String LINE_FEED = "\r\n";

    private static String IP;
    private static int PORT;
    private static String HttpUrl;
    private static String HttpsUrl;

    private static String RouterIp;
    private static int RouterPort;

    public static void setRouterIpPort(String ip, int port){
        RouterIp = ip;
        RouterPort =  port;
    }

    public static void sendRouterInfo(final String jsonStr) {
        final String url = "http://" + RouterIp + ":" + RouterPort;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.connect();

                    OutputStream serverOutStream = conn.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(serverOutStream));
                    bw.write(jsonStr);
                    bw.flush();
                    serverOutStream.close();

                    int responseCode = 0;
                    String requestStr = null;
                    if ((responseCode = conn.getResponseCode()) == HttpURLConnection.HTTP_OK) {
                        InputStream in = conn.getInputStream();
                        requestStr = read(in).toString();

                    }

                    System.out.println("Router Info Send,  ResponseCode: " + responseCode + "\n"
                            + "    ResponseContent: " + requestStr);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    public HttpClientWrapper(String ip, int port) {
        IP = ip;
        PORT = port;
        HttpUrl = "http://" + IP + ":" + PORT;
        HttpsUrl = "https://" + IP + ":" + PORT;
    }

    public static void setRemoteServer(String ip, int port) {
        IP = ip;
        PORT = port;
        HttpUrl = "http://" + IP + ":" + PORT;
        HttpsUrl = "https://" + IP + ":" + PORT;
    }

    public static void postMultipartForm(final String fullFilePath) {
        postMultipartForm(HttpUrl + "/image", fullFilePath);
    }

    public static void postMultipartFormBytes(final byte[] dataBytes) {
        System.out.println("Http Sended data size: " + dataBytes.length);
        postMultipartForm(HttpUrl + "/image", dataBytes);
    }

    public static void postMultipartForm(final String url, final String fullFilePath) {
        System.out.println("request: " + url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    conn.connect();

                    OutputStream serverOutStream = conn.getOutputStream();

                 /*   File file = new File(fullFilePath);
                    String fileName = file.getName();
                    String mimeType = new MimetypesFileTypeMap().getContentType(file);

                    BufferedInputStream fileData = new BufferedInputStream(new FileInputStream(file));
                    StringBuilder sbBuf = new StringBuilder();
                    sbBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    sbBuf.append("Content-Disposition: form-data; name=\"" + fileName + "\";"
                            + "filename=\"" + fileName + "\";"
                            + "Content-Type:" + mimeType
                            + "\r\n\r\n");

                    OutputStream out = new DataOutputStream(conn.getOutputStream());

                    StringBuilder line = new StringBuilder();
                    line.append("--" + BOUNDARY + "\r\n");
                    out.write(sbBuf.toString().getBytes());
                    out.write(("--" + BOUNDARY).);

                    int bytes = 0;
                    byte[] buffer = new byte[1024 * 1024];
                    while ((bytes = fileData.read(buffer)) != -1) {
                        out.write(buffer, 0, bytes);
                    }

                    out.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());

                    fileData.close();
                    out.close();*/

                    addFormField(serverOutStream, "Encryption","None");
                    File file = new File(fullFilePath);
                    addFilePart(serverOutStream, "file", file);
                    addMultiPartFinishBoundary(serverOutStream);

                    int responseCode = 0;
                    String requestStr = null;
                    if ((responseCode = conn.getResponseCode()) == HttpURLConnection.HTTP_OK) {
                        InputStream in = conn.getInputStream();
                        requestStr = read(in).toString();

                    }

                    System.out.println("ResponseCode: " + responseCode + "\n"
                                + "    ResponseContent: " + requestStr);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();

    }

    public static void postMultipartForm(final String url, final byte[] imageBytes) {
        System.out.println("request: " + url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(3000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                    conn.connect();

                    OutputStream serverOutStream = conn.getOutputStream();

                    addFormField(serverOutStream, "Encryption","AES");

                    addFilePart(serverOutStream, "file", imageBytes);
                    addMultiPartFinishBoundary(serverOutStream);
                    serverOutStream.flush();
                    serverOutStream.close();

                    int responseCode = 0;
                    String requestStr = null;
                    if ((responseCode = conn.getResponseCode()) == HttpURLConnection.HTTP_OK) {
                        InputStream in = conn.getInputStream();
                        requestStr = read(in).toString();

                    }

                    System.out.println("ResponseCode: " + responseCode + "\n"
                            + "    ResponseContent: " + requestStr);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();

    }
    /**
     * 读取流中的数据
     */
    public static StringBuilder read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder;
    }

    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public static void addFormField(OutputStream toServerStream,String name, String value) {
        PrintWriter writer = new PrintWriter(toServerStream);
        writer.append("--" + BOUNDARY).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public static void addFilePart(OutputStream toServerStream, String fieldName, File uploadFile)
            throws IOException {
        PrintWriter writer = new PrintWriter(toServerStream);
        String fileName = uploadFile.getName();
        writer.append("--" + BOUNDARY).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + HttpURLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            toServerStream.write(buffer, 0, bytesRead);
        }
        toServerStream.flush();
        inputStream.close();

        writer.flush();
    }

    public static void addFilePart(OutputStream toServerStream, String fieldName, byte[] dataBytes)
            throws IOException {
        PrintWriter writer = new PrintWriter(toServerStream);

        writer.append("--" + BOUNDARY).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fieldName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + "Encryption")
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

//        FileInputStream inputStream = new FileInputStream(uploadFile);
//        InputStream inputStream = new ByteArrayInputStream(dataBytes);
//        byte[] buffer = new byte[4096];
//        int bytesRead = -1;
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            toServerStream.write(buffer, 0, bytesRead);
//        }
        toServerStream.write(dataBytes, 0, dataBytes.length);
        toServerStream.flush();
//        inputStream.close();

//        writer.append(LINE_FEED).append(LINE_FEED);
        writer.flush();
    }

    public static void addMultiPartFinishBoundary(OutputStream toServerStream) {
        PrintWriter writer = new PrintWriter(toServerStream);
        writer.append(LINE_FEED).flush();
        writer.append("--" + BOUNDARY + "--").append(LINE_FEED);
        writer.close();
    }

    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(OutputStream toServerStream,String name, String value) {
        PrintWriter writer = new PrintWriter(toServerStream);
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }
}

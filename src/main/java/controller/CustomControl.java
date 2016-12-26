package controller;

import CSModel.HttpClientWrapper;
import CSModel.ImageRcvServlet;
import CSModel.JettyServerWrapper;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import component.AutoCompleteTextField;
import component.TextFieldObserver;
import encryption.Encryption;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.NetCardMgr;
import model.NetInterfaceInfo;
import model.PropertyBean;
import model.RouterInfo;
import view.AutoClassMgr;
import view.AutoClose;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * Created by hadoop on 16-12-15.
 */
public class CustomControl extends GridPane implements AutoClose, ServerCall, HttpClientCall{

    private Stage mPrimaryStage;
    private PropertyBean mPropertyBean;
    private RouterInfo mRouterInfo = new RouterInfo();
    @FXML
    private GridPane root_grid_pane;
    @FXML
    private AutoCompleteTextField network_select;
    @FXML
    private AutoCompleteTextField local_ip;
    @FXML
    private TextField local_port;
    @FXML
    private AutoCompleteTextField remote_net_alias;
    @FXML
    private AutoCompleteTextField remote_ip;
    @FXML
    private TextField remote_port;
    @FXML
    private TextField image_path;
    @FXML
    private TextField key_file_path;
    @FXML
    private TextArea key_text_view;
    @FXML
    private ImageView image_view;

    /******************Router Info************/
    @FXML
    private TextField router_connector_field;
    @FXML
    private TextField quantum_optical_field;
    @FXML
    private TextField classic_optical_field;
    @FXML
    private TextField channel_num_field;
    @FXML
    private TextField sync_optical_field;
    @FXML
    private TextField router_ip_field;
    @FXML
    private TextField router_port_field;
    @FXML
    private TextField request_result_field;
    /******************Router Info************/

    List<NetInterfaceInfo> local_net_info_list;
    List<NetInterfaceInfo> remote_net_info_list;

    private JettyServerWrapper jetty;

    public CustomControl(Stage primaryStage, PropertyBean property) {
        mPrimaryStage = primaryStage;
        mPropertyBean = property;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/main_application.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bounds bounds = root_grid_pane.getBoundsInParent();
        image_view.setPickOnBounds(true);
        System.out.println(bounds.getMaxX() + " -- " + bounds.getMinX());
        System.out.println("ImageView Height:" + image_view.getFitHeight() + " Width:" + image_view.getFitWidth());
        Image image = new Image(getClass().getResource("../image/image_background.png").toExternalForm(),
                0, 0, false, false);

        image_view.setImage(image);
        network_select.registerChangeObserver(local_net_name_observer);
        local_ip.registerChangeObserver(local_net_ip_observer);
        remote_net_alias.registerChangeObserver(remote_net_alias_observer);
        remote_ip.registerChangeObserver(remoter_net_ip_observer);

        local_net_info_list = NetCardMgr.getNetCardInfoList(mPropertyBean.LocalIpList);
        if (local_net_info_list != null) {
            NetInterfaceInfo netInfo = local_net_info_list.get(0);
            jetty = new JettyServerWrapper(netInfo.IP, netInfo.PORT);
            network_select.setText(netInfo.ALIAS);
            local_ip.setText(netInfo.IP);
            local_port.setText(String.valueOf(netInfo.PORT));
        }
        for (NetInterfaceInfo netInfo : local_net_info_list) {
            network_select.getEntries().add(netInfo.ALIAS);
            local_ip.getEntries().add(netInfo.IP);

        }

        remote_net_info_list = mPropertyBean.RemoteIpList;
        boolean isFisrt = true;
        for (NetInterfaceInfo netInfo : remote_net_info_list) {
            if (isFisrt) {
                isFisrt = false;
                remote_net_alias.setText(netInfo.ALIAS);
                remote_ip.setText(netInfo.IP);
                remote_port.setText(String.valueOf(netInfo.PORT));
                HttpClientWrapper.setRemoteServer(netInfo.IP, netInfo.PORT);
            }
            remote_net_alias.getEntries().add(netInfo.ALIAS);
            remote_ip.getEntries().add(netInfo.IP);
        }


        AutoClassMgr.registerAutoClose(this);
        ImageRcvServlet.registerServerCall(this);
        HttpClientWrapper.registerHttpClientCall(this);

        if (mPropertyBean.RouterIp != null && mPropertyBean.RouterPort != 0) {
            HttpClientWrapper.setRouterIpPort(mPropertyBean.RouterIp, mPropertyBean.RouterPort);
            router_ip_field.setText(mPropertyBean.RouterIp);
            router_port_field.setText(String.valueOf(mPropertyBean.RouterPort));
        } else {
            HttpClientWrapper.setRouterIpPort("127.0.0.1", 6688);
            router_ip_field.setText("127.0.0.1");
            router_port_field.setText(String.valueOf(6688));
        }
        router_connector_field.setText(String.valueOf(mRouterInfo.FromFlag));
        quantum_optical_field.setText(String.valueOf(mRouterInfo.QuantumOptical));
        sync_optical_field.setText(String.valueOf(mRouterInfo.SynOptical));
        classic_optical_field.setText(String.valueOf(mRouterInfo.ClassicOptical));
        channel_num_field.setText(String.valueOf(mRouterInfo.UserNums));
    }

    public void handleRouterInfoSetting(ActionEvent actionEvent) {
        String connector_port = router_connector_field.getText().trim();
        String channel_num = channel_num_field.getText().trim();
        String quantum_optical = quantum_optical_field.getText().trim();
        String sync_optical = sync_optical_field.getText().trim();
        String classic_optical = classic_optical_field.getText().trim();
        String router_ip = router_ip_field.getText().trim();
        String router_port = router_port_field.getText().trim();

        int connector_num = Integer.valueOf(connector_port);
        if (connector_num > 1 || connector_num < 0) {
            connector_num = 0;
            router_connector_field.setText("0");
        }

        mRouterInfo.FromFlag = connector_num;
        mRouterInfo.UserNums = Integer.valueOf(channel_num);
        mRouterInfo.QuantumOptical = Double.valueOf(quantum_optical);
        mRouterInfo.SynOptical = Double.valueOf(sync_optical);
        mRouterInfo.ClassicOptical = Double.valueOf(classic_optical);

        mPropertyBean.RouterIp = router_ip;
        mPropertyBean.RouterPort = Integer.valueOf(router_port);
        HttpClientWrapper.setRouterIpPort(router_ip, Integer.valueOf(router_port));

    }

    public void handleLocalIpSetting(ActionEvent actionEvent) {
        System.out.println("Start Server!");
        String alias = network_select.getText().trim();
        String ip = local_ip.getText().trim();
        int port = Integer.valueOf(local_port.getText().trim());
        NetInterfaceInfo netInfo = NetCardMgr.findNetInfoByIp(local_net_info_list,ip);
        if (netInfo != null) {
            if (!alias.equals(netInfo.ALIAS)) {
                network_select.getEntries().remove(netInfo.ALIAS);
                network_select.getEntries().add(alias);
                local_ip.getEntries().remove(netInfo.IP);
                local_ip.getEntries().add(ip);
            }
            netInfo.ALIAS = alias;
            netInfo.PORT = port;
        } else {
            local_net_info_list.add(new NetInterfaceInfo(ip, port, alias));
            network_select.getEntries().add(alias);
            local_ip.getEntries().add(ip);
        }
        if (jetty != null) jetty.destroy();
        mRouterInfo.SourceIP = ip;
        jetty = new JettyServerWrapper(ip, port);
    }

    public void handleRemoteIpSetting(ActionEvent actionEvent) {
        String ip = remote_ip.getText().trim();
        String alias = remote_net_alias.getText().trim();
        int port = Integer.valueOf(remote_port.getText().trim());
        NetInterfaceInfo netInfo = NetCardMgr.findNetInfoByIp(remote_net_info_list, ip);
        if (netInfo != null) {
            if (!alias.equals(netInfo.ALIAS)) {
                remote_net_alias.getEntries().remove(netInfo.ALIAS);
                remote_net_alias.getEntries().add(alias);
                remote_ip.getEntries().remove(netInfo.IP);
                remote_ip.getEntries().add(ip);
            }
            netInfo.ALIAS = alias;
            netInfo.PORT = port;

        } else {
            remote_net_info_list.add(new NetInterfaceInfo(ip, port, alias));
            remote_net_alias.getEntries().add(alias);
            remote_ip.getEntries().add(ip);
        }
        HttpClientWrapper.setRemoteServer(ip, port);
        mRouterInfo.ClassicDesIP = ip;
    }

    public void handleKeyFileSeclect(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File initialPath = new File(".");
        if (mPropertyBean.QkdPath != null) {
            initialPath = new File(mPropertyBean.QkdPath);
            fileChooser.setInitialDirectory(initialPath);
        }
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("文本文件", "*.txt", "*.json", "*.xml", "*.java", "*.c")
        );
        File selectedFile = fileChooser.showOpenDialog(mPrimaryStage);
        if (selectedFile == null) {
            return;
        }
        String mimeType = new MimetypesFileTypeMap().getContentType(selectedFile);
        System.out.println("File Mime Type: " + mimeType);
        String fileFullPath = selectedFile.getAbsolutePath();
        key_file_path.setText(fileFullPath);
        mPropertyBean.QkdPath = fileFullPath.substring(0, fileFullPath.lastIndexOf(File.separatorChar));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile)))) {
            int charNum = 0;
            char[] charBuf = new char[1024];
            while ((charNum = br.read(charBuf)) != -1) {
                key_text_view.appendText(String.valueOf(charBuf));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleImageFileSeclect(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File initialPath;
        if (mPropertyBean.ImagePath != null) {
            initialPath = new File(mPropertyBean.ImagePath);
            fileChooser.setInitialDirectory(initialPath);
        } else {
            initialPath = new File("~");
        }
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("图片文件", "*.png", "*.jpg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(mPrimaryStage);
        if (selectedFile == null) {
            return;
        }
        String fileFullPath = selectedFile.getAbsolutePath();
        image_path.setText(fileFullPath);
        mPropertyBean.ImagePath = fileFullPath.substring(0, fileFullPath.lastIndexOf(File.separatorChar));
        String mimeType = new MimetypesFileTypeMap().getContentType(selectedFile);
        System.out.println("File Mime Type: " + mimeType);

        Image image = null;
        try {
            image = new Image(new FileInputStream(selectedFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image_view.setImage(image);
    }

    public void sendImageWithoutKey(ActionEvent actionEvent) {
        String imagePath = image_path.getText().trim();
        HttpClientWrapper.postMultipartForm(imagePath);
    }

    public void sendImageWithKey(ActionEvent actionEvent) {
        String imagePath = image_path.getText().trim();
        String keyFilePath = key_file_path.getText().trim();
        String binKeyStr = null;
        if (keyFilePath == null || keyFilePath.length() == 0) {
            key_file_path.setText("don't have a key file!!!");
        } else {
            binKeyStr = popFirstLineOfFile(keyFilePath);
        }

        byte[] dataBytes;
        if (binKeyStr == null || binKeyStr.length() == 0) {
            key_text_view.setText("无可用秘钥，将采用固定的默认秘钥加密！！！");
            dataBytes = Encryption.encryptFile2Bytes(imagePath);
        } else {
            dataBytes = Encryption.encryptFile2Bytes(imagePath, binKeyStr);
        }

        HttpClientWrapper.postMultipartFormBytes(dataBytes);
       /* File imageFile = Encryption.encryptImageFile(imagePath);

        if (imageFile == null) {
            imageFile = new File(
                    getClass().getResource("../image/image_background.png").getFile()
            );
        }

        Image image = null;
        try {
            image = new Image(new FileInputStream(imageFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image_view.setImage(image);*/
    }

    private boolean RouterConnected = false;
    public void handleRouterRequest(ActionEvent actionEvent) {
        if (!RouterConnected) {
            mRouterInfo.isConnected = 1;
            HttpClientWrapper.sendRouterInfo(mRouterInfo.toString());
        } else {

        }

       /* String keyFilePath = key_file_path.getText().trim();
        if (keyFilePath == null || keyFilePath.length() == 0) return;
        String line = popFirstLineOfFile(keyFilePath);*/
    }

    private byte[] receivedDataBytes;

    @Override
    public void receiveEncryptedFile(byte[] dataBytes) {
        receivedDataBytes = dataBytes;

        File imageFile = null;

//        imageFile = Encryption.encryptImageFileShowable(image_path.getText().trim());
        try {
            imageFile = Encryption.createImageFileFromBytes(dataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageFile == null) {
            imageFile = new File(
                    getClass().getResource("../image/image_background.png").getFile()
            );
        }
        Image image = null;
        try {
            image = new Image(new FileInputStream(imageFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image_view.setImage(image);
    }

    @Override
    public void receiveNormalFile(File imageFile) {
        Image image = null;
        try {
            image = new Image(new FileInputStream(imageFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image_view.setImage(image);
        image_path.setText("Image file comes for remote server!");
    }

    public void reproductEncryptedImage(ActionEvent actionEvent) {
        if (receivedDataBytes == null) return;
        String keyFilePath = key_file_path.getText().trim();
        String binKeyStr = null;
        if (keyFilePath == null || keyFilePath.length() == 0) {
            key_file_path.setText("don't have a key file!!!");
        } else {
            binKeyStr = popFirstLineOfFile(keyFilePath);
        }

        File imageFile = null;
        if (binKeyStr == null || binKeyStr.length() == 0) {
            key_text_view.setText("无可用秘钥，将采用固定的默认秘钥解密！！！");
            imageFile = Encryption.decryptBytes2File(receivedDataBytes);
        } else {
            imageFile = Encryption.decryptBytes2File(receivedDataBytes, binKeyStr);
        }

        if (imageFile == null) {
            imageFile = new File(
                    getClass().getResource("../image/image_background.png").getFile()
            );
        }

        String mimeType = new MimetypesFileTypeMap().getContentType(imageFile);
        String type = mimeType.split("/")[0];
        if (!type.equals("image")) {
            image_path.setText("解密失败，请确认双方秘钥是否一致！");
            imageFile = new File(
                    getClass().getResource("../image/image_background.png").getFile()
            );
        }

        Image image = null;
        try {
            image = new Image(new FileInputStream(imageFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image_view.setImage(image);
    }

    @Override
    public void autoClose() {
        if (jetty != null) {
            jetty.destroy();
        }

        mPropertyBean.LocalIpList = local_net_info_list;
        mPropertyBean.RemoteIpList = remote_net_info_list;
    }

    TextFieldObserver local_net_name_observer = new TextFieldObserver() {
        @Override
        public void textChanged(String text) {

        }

        @Override
        public void focusChanged(boolean isInFocus) {

        }

        @Override
        public void textSelected(String text) {
//            String alias = network_select.getText().trim();
            String alias = text;
            NetInterfaceInfo netInfo = NetCardMgr.findNetInfoByAlias(local_net_info_list, alias);
            local_ip.setText(netInfo.IP);
            local_port.setText(String.valueOf(netInfo.PORT));
        }
    };

    TextFieldObserver local_net_ip_observer = new TextFieldObserver() {
        @Override
        public void textChanged(String text) {

        }

        @Override
        public void focusChanged(boolean isInFocus) {

        }

        @Override
        public void textSelected(String text) {
            String ip = text;
            NetInterfaceInfo netInfo = NetCardMgr.findNetInfoByIp(local_net_info_list, ip);
            network_select.setText(netInfo.ALIAS);
            local_port.setText(String.valueOf(netInfo.PORT));
        }
    };

    TextFieldObserver remote_net_alias_observer = new TextFieldObserver() {
        @Override
        public void textChanged(String text) {

        }

        @Override
        public void focusChanged(boolean isInFocus) {

        }

        @Override
        public void textSelected(String text) {

        }
    };

    TextFieldObserver remoter_net_ip_observer = new TextFieldObserver() {
        @Override
        public void textChanged(String text) {

        }

        @Override
        public void focusChanged(boolean isInFocus) {

        }

        @Override
        public void textSelected(String text) {

        }
    };

    public String popFirstLineOfFile(String filePath) {
        return popTopNLineOfFile(filePath, 1);
    }

    public String popTopNLineOfFile(String filePath, int n) {
        if (filePath == null || filePath.length() == 0) return null;
        File file = new File(filePath);
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        while ((n-- > 0) && fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (line.equals("\n") || line.equals("\r\n")) {
                n++;
            } else {
                sb.append(line);
            }
        }

        key_text_view.clear();
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter out = new BufferedWriter(fileWriter);
            while (fileScanner.hasNextLine()) {
                String next = fileScanner.nextLine();
                key_text_view.appendText(next + "\n");
                if (next.equals("\n") || next.equals("\r\n")) {
                    out.newLine();
                } else {
                    out.write(next);
                    out.newLine();
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileScanner.close();
        return sb.toString();
    }

    @Override
    public void requestResult(String result) {
        request_result_field.setText(result);
    }
}

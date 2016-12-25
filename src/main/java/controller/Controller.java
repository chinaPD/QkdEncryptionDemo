package controller;

import javafx.fxml.FXML;

import javafx.event.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Controller {
    @FXML
    private TextField local_ip;
    @FXML
    private TextField local_port;
    @FXML
    private TextField remote_ip;
    @FXML
    private TextField remote_port;
    @FXML
    private ImageView image_view;

    public void initView() {
        Image image = new Image(getClass().getResource("../image/image_background.png").toExternalForm());
        image_view.setImage(image);
    }

    public void handleLocalIpSetting(ActionEvent actionEvent) {
        System.out.println("Button Test!");
    }

    public void handleRemoteIpSetting(ActionEvent actionEvent) {

    }
}

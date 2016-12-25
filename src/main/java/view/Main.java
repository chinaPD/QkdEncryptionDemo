package view;

import CSModel.JettyServerWrapper;
import controller.CustomControl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.NetCardMgr;
import model.PropertyBeanWrapper;

public class Main extends Application {


    private PropertyBeanWrapper propertyWrapper = new PropertyBeanWrapper();
//    private JettyServerWrapper jetty = new JettyServerWrapper(6688);

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("main_application.fxml"));
        CustomControl root = new CustomControl(primaryStage, propertyWrapper.propertyBean);
        primaryStage.setTitle("QKD Encryption Demo");
        Scene scene = new Scene(root, 832, 624);
//        scene.getStylesheets().add(getClass().getResource("example.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        propertyWrapper.savePropertyBeanToJson();
        AutoClassMgr.doAllAutoClose();
//        jetty.destroy();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

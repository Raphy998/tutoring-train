package at.tutoringtrain.adminclient;

import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.ui.WindowService;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ApplicationManager applicationManager = ApplicationManager.getInstance();
        if (applicationManager.isConfigFileAvailable()) {
            applicationManager.readConfigFile();
        } else {
            applicationManager.writeConfigFile();
        }
        WindowService.getInstance().openAuthenticationWindow();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

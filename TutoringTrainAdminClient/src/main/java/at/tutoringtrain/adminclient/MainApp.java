package at.tutoringtrain.adminclient;

import at.tutoringtrain.adminclient.ui.WindowService;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        WindowService.getInstance().openAuthenticationWindow();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

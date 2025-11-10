package com.tallylite;

import com.tallylite.util.FileManager;
import com.tallylite.util.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TallyLiteApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize file structure
        Logger.log("APPLICATION_START", "TallyLite application started");
        
        // Load home screen
        FXMLLoader fxmlLoader = new FXMLLoader(TallyLiteApp.class.getResource("/com/tallylite/view/Home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        
        // Apply CSS
        scene.getStylesheets().add(getClass().getResource("/com/tallylite/style/styles.css").toExternalForm());
        
        stage.setTitle("TallyLite - Offline Accounting Software");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    @Override
    public void stop() {
        Logger.log("APPLICATION_STOP", "TallyLite application stopped");
    }

    public static void main(String[] args) {
        launch();
    }
}


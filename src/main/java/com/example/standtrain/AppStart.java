package com.example.standtrain;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.handleE16initialized;
import static com.example.standtrain.util.Globals.threadE16running;
import static com.example.standtrain.util.Globals.adcThread;


public class AppStart extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppStart.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        Image icon = new Image("lcardlogo.png");
        stage.getIcons().add(icon);
        stage.setTitle("LcardTrain");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (threadE16running){
            threadE16running = false;
            adcThread.join(); //wait till shutdown of active thread
        }

        if (handleE16initialized){
            X502Api.INSTANCE.X502_Close(handleE16);
            X502Api.INSTANCE.X502_Free(handleE16);
            handleE16initialized = false;
        }
        super.stop();
    }

    public static void main(String[] args) {
        loadConfig();
        launch();
    }

    public static void loadConfig() {
        Properties properties = new Properties();
        File file = new File("config.properties");

        try {
            if (file.exists()) {
                // Load existing config
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();

                Config.ipOctet1 = Integer.parseInt(properties.getProperty("ipOctet1"));
                Config.ipOctet2 = Integer.parseInt(properties.getProperty("ipOctet2"));
                Config.ipOctet3 = Integer.parseInt(properties.getProperty("ipOctet3"));
                Config.ipOctet4 = Integer.parseInt(properties.getProperty("ipOctet4"));
            } else {
                // File doesn't exist, create it with default values
                properties.setProperty("ipOctet1", String.valueOf(192));
                properties.setProperty("ipOctet2", String.valueOf(168));
                properties.setProperty("ipOctet3", String.valueOf(1));
                properties.setProperty("ipOctet4", String.valueOf(10));

                FileOutputStream fos = new FileOutputStream(file);
                properties.store(fos, "Configuration");
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

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

import static com.example.standtrain.util.Globals.*;


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

        if (handleLTAinitialized){
            LTA_Api.INSTANCE.LTA_Close(handleLTACon);
            LTA27_Api.INSTANCE.LTA27_DeInit(handleLTADevice, 0x00000001);
            handleLTAinitialized = false;
        }

        super.stop();
    }

    public static void main(String[] args) throws InterruptedException {
        UtilMethods.loadConfig();
        launch();
    }

}

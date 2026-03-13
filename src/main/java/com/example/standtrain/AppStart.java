package com.example.standtrain;

import com.example.standtrain.interfaces.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.handleE16initialized;
import static com.example.standtrain.util.Globals.threadE16running;
import static com.example.standtrain.util.Globals.adcThread;


public class AppStart extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppStart.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        Image icon = new Image("lcardlogo.png");
        stage.getIcons().add(icon);
        stage.setTitle("LcardTrain");
        stage.setScene(scene);
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
        launch();
    }
}

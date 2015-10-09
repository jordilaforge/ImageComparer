package com.jordilaforge.imagecomparer;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ImageComparerMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainGui.fxml"));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("ImageComparer");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(we -> System.exit(1));
    }


    public static void main(String[] args) {
        launch(args);
    }


}

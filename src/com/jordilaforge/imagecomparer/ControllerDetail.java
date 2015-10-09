package com.jordilaforge.imagecomparer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;


public class ControllerDetail implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private ImageView imageViewImage1;
    @FXML
    private ImageView imageViewImage2;
    @FXML
    private Label image1Label;
    @FXML
    private Label similarityLabel;
    @FXML
    private Label image2Label;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image1 = null;
        Image image2 = null;
        try {
            image1 = new Image(String.valueOf(new File(Context.compareItem.getImage1()).toURI().toURL()));
            image2 = new Image(String.valueOf(new File(Context.compareItem.getImage2()).toURI().toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        imageViewImage1.setImage(image1);
        imageViewImage2.setImage(image2);

        image1Label.setText(Context.compareItem.getImage1Name() + "\n" + image1.getHeight() + "x" + image1.getWidth());
        image2Label.setText(Context.compareItem.getImage2Name() + "\n" + image2.getHeight() + "x" + image2.getWidth());
        similarityLabel.setText(String.valueOf(Context.compareItem.getSimilarity()));
    }
}

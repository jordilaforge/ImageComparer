package com.jordilaforge.imagecomparer;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    private Label image1Titel;
    @FXML
    private Label image1Label;
    @FXML
    private Label similarityLabel;
    @FXML
    private Label image2Titel;
    @FXML
    private Label image2Label;
    @FXML
    private Button closeButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File image1 = new File(Context.compareItem.getImage1());
        File image2 = new File(Context.compareItem.getImage2());
        image1Titel.setText(image1.getName());
        image2Titel.setText(image2.getName());
        image1Titel.setFont(new Font("Arial", 24));
        image2Titel.setFont(new Font("Arial", 24));
        similarityLabel.setFont(new Font("Arial", 20));
        Metadata metadata1 = null;
        Metadata metadata2 = null;
        try {
            metadata1 = ImageMetadataReader.readMetadata(image1);
            metadata2 = ImageMetadataReader.readMetadata(image2);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder image1Info = new StringBuilder();
        StringBuilder image2Info = new StringBuilder();
        for (Directory directory : metadata1.getDirectories())
            for (Tag tag : directory.getTags()) {
                image1Info.append(tag + "\n");
            }
        for (Directory directory : metadata2.getDirectories())
            for (Tag tag : directory.getTags()) {
                image2Info.append(tag + "\n");
            }

        try {
            image1Label.setText(image1Info.toString());
            image2Label.setText(image2Info.toString());
            imageViewImage1.setImage(new Image(image1.toURI().toURL().toString()));
            imageViewImage2.setImage(new Image(image2.toURI().toURL().toString()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        similarityLabel.setText(String.valueOf(Context.compareItem.getSimilarity()) + "%");
    }

    @FXML
    protected void closeButtonAction(ActionEvent actionEvent) {
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}

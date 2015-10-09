package com.jordilaforge.imagecomparer;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.MalformedURLException;


public class ImageTableCell extends TableCell<CompareItem, String> {


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setStyle("");
            setGraphic(null);
        } else {
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            Label imageName = new Label(new File(item).getName());
            ImageView imageview = new ImageView();
            imageview.setFitHeight(200);
            imageview.setFitWidth(200);
            try {
                imageview.setImage(new Image(String.valueOf(new File(item).toURI().toURL())));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            vbox.getChildren().addAll(imageview, imageName);
            //SETTING ALL THE GRAPHICS COMPONENT FOR CELL
            setGraphic(vbox);
        }
    }
}

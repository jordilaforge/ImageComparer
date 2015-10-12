package com.jordilaforge.imagecomparer.tablecells;

import com.jordilaforge.imagecomparer.CompareItem;
import javafx.geometry.Pos;
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
            setGraphic(null);
        } else {
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            Label imageName = new Label(new File(item).getName());
            ImageView imageview = new ImageView();
            imageview.setPreserveRatio(true);
            imageview.fitWidthProperty().bind(this.widthProperty());
            imageview.setFitHeight(200);
            try {
                imageview.setImage(new Image(String.valueOf(new File(item).toURI().toURL())));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            vbox.setAlignment(Pos.CENTER);
            vbox.getChildren().addAll(imageview, imageName);
            setGraphic(vbox);
        }
    }
}

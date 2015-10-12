package com.jordilaforge.imagecomparer.tablecells;

import com.jordilaforge.imagecomparer.CompareItem;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class SimilarityTableCell extends TableCell<CompareItem, Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
        } else {
            Label integer = new Label(item.toString() + "%");
            integer.setFont(new Font("Arial", 24));
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(10);
            hbox.getChildren().add(integer);
            setGraphic(hbox);
        }
    }
}

package com.jordilaforge.imagecomparer;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public class SimilarityTableCell extends TableCell<CompareItem, Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
            setStyle("");
            setGraphic(null);
        } else {
            Label integer = new Label(item.toString() + "%");
            setGraphic(integer);
        }
    }
}

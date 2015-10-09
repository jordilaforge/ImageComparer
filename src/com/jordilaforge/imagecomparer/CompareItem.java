package com.jordilaforge.imagecomparer;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;

public class CompareItem {

    private final SimpleStringProperty image1;
    private final SimpleStringProperty image2;
    private final SimpleIntegerProperty similarity;

    public String getImage1Name() {
        return new File(image1.get()).getName();
    }

    public String getImage2Name() {
        return new File(image2.get()).getName();
    }

    public String getImage1() {
        return image1.get();
    }

    public SimpleStringProperty image1Property() {
        return image1;
    }

    public String getImage2() {
        return image2.get();
    }

    public SimpleStringProperty image2Property() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2.set(image2);
    }

    public int getSimilarity() {
        return similarity.get();
    }

    public SimpleIntegerProperty similarityProperty() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity.set(similarity);
    }

    public void setImage1(String image1) {

        this.image1.set(image1);
    }

    public CompareItem() {
        this.image1 = null;
        this.image2 = null;
        this.similarity = null;
    }

    public CompareItem(String image1, String image2, int similarity) {
        this.image1 = new SimpleStringProperty(image1);
        this.image2 = new SimpleStringProperty(image2);
        this.similarity = new SimpleIntegerProperty(similarity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompareItem that = (CompareItem) o;

        return image1.equals(that.image1) && image2.equals(that.image2);

    }

    @Override
    public int hashCode() {
        int result = image1.hashCode();
        result = 31 * result + image2.hashCode();
        return result;
    }
}

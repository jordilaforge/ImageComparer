package main;

import java.io.File;

/**
 * Created by philipp on 16.06.15.
 * CompareItem is a object with two images as there URL and der similarity
 */
public class CompareItem {
    public String getImage1() {
        return image1;
    }

    public String getImage1Name(){
        return new File(image1).getName();
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage2Name(){
        return new File(image2).getName();
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public int getSimilarity() {
        return similarity;
    }


    private String image1;
    private String image2;
    private int similarity;

    public CompareItem(String image1,String image2,int similarity){
        this.image1=image1;
        this.image2=image2;
        this.similarity=similarity;
    }

    @Override
    public boolean equals(Object compareItem )
    {
        CompareItem compareItemTemp = (CompareItem)compareItem;
        if(this.image1.equals(compareItemTemp.getImage1())&&this.image2.equals(compareItemTemp.getImage2())){
            return true;
        }
        else if (this.image2.equals(compareItemTemp.getImage1())&&this.image1.equals(compareItemTemp.getImage2())){
            return true;
        }
        else {
            return false;
        }
    }

}

package com.jordilaforge.imagecomparer;

/**
 * Created by philipp on 20.06.2015.
 * Object to hold data of a partition lower and upper variables for upper and lower bound
 * of the partition
 */
public class PartitionObject {
    private int lower;
    private int upper;

    public PartitionObject(int start, int end) {
        this.lower = start;
        this.upper = end;
    }

    public int getLower() {
        return lower;
    }


    public int getUpper() {
        return upper;
    }


}

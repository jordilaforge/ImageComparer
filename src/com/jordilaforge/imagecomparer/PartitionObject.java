package com.jordilaforge.imagecomparer;

public class PartitionObject {
    private final int lower;
    private final int upper;

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

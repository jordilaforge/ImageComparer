package main;

/**
 * Created by philipp on 20.06.2015.
 */
public class PartitionObject {
    private int lower;
    private int upper;

    public PartitionObject(int start, int end) {
        this.lower=start;
        this.upper=end;
    }

    public int getLower() {
        return lower;
    }

    public void setLower(int lower) {
        this.lower = lower;
    }

    public int getUpper() {
        return upper;
    }

    public void setUpper(int upper) {
        this.upper = upper;
    }
}

import main.PartitionObject;
import main.ScanDirectory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by philipp on 20.06.2015.
 */
public class ScanDirectoryTest {


    @Test
    public void partitionTestTwenty() {
        ScanDirectory scanDirectory = new ScanDirectory();
        ArrayList<PartitionObject> temp = scanDirectory.partition(20);
        Assert.assertEquals(4, temp.size());
        Assert.assertEquals(0, temp.get(0).getLower());
        Assert.assertEquals(4, temp.get(0).getUpper());
        Assert.assertEquals(5, temp.get(1).getLower());
        Assert.assertEquals(9, temp.get(1).getUpper());
        Assert.assertEquals(10, temp.get(2).getLower());
        Assert.assertEquals(14, temp.get(2).getUpper());
        Assert.assertEquals(15, temp.get(3).getLower());
        Assert.assertEquals(19, temp.get(3).getUpper());
    }


    @Test
    public void partitionTestTwentyOne() {
        ScanDirectory scanDirectory = new ScanDirectory();
        ArrayList<PartitionObject> temp = scanDirectory.partition(21);
        Assert.assertEquals(4, temp.size());
        Assert.assertEquals(0, temp.get(0).getLower());
        Assert.assertEquals(4, temp.get(0).getUpper());
        Assert.assertEquals(5, temp.get(1).getLower());
        Assert.assertEquals(9, temp.get(1).getUpper());
        Assert.assertEquals(10, temp.get(2).getLower());
        Assert.assertEquals(14, temp.get(2).getUpper());
        Assert.assertEquals(15, temp.get(3).getLower());
        Assert.assertEquals(20, temp.get(3).getUpper());
    }

    public void partitionTestNine() {
        ScanDirectory scanDirectory = new ScanDirectory();
        ArrayList<PartitionObject> temp = scanDirectory.partition(9);
        Assert.assertEquals(4, temp.size());
        Assert.assertEquals(0, temp.get(0).getLower());
        Assert.assertEquals(1, temp.get(0).getUpper());
        Assert.assertEquals(2, temp.get(1).getLower());
        Assert.assertEquals(3, temp.get(1).getUpper());
        Assert.assertEquals(4, temp.get(2).getLower());
        Assert.assertEquals(5, temp.get(2).getUpper());
        Assert.assertEquals(6, temp.get(3).getLower());
        Assert.assertEquals(8, temp.get(3).getUpper());
    }

    public void partitionTestTen() {
        ScanDirectory scanDirectory = new ScanDirectory();
        ArrayList<PartitionObject> temp = scanDirectory.partition(10);
        Assert.assertEquals(4, temp.size());
        Assert.assertEquals(0, temp.get(0).getLower());
        Assert.assertEquals(1, temp.get(0).getUpper());
        Assert.assertEquals(2, temp.get(1).getLower());
        Assert.assertEquals(3, temp.get(1).getUpper());
        Assert.assertEquals(4, temp.get(2).getLower());
        Assert.assertEquals(5, temp.get(2).getUpper());
        Assert.assertEquals(6, temp.get(3).getLower());
        Assert.assertEquals(9, temp.get(3).getUpper());
    }
}

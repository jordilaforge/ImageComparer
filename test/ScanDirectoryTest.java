import main.CompareItem;
import main.PartitionObject;
import main.ScanDirectory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by philipp on 20.06.2015.
 */
public class ScanDirectoryTest {

    ScanDirectory scanDirectory;
    ArrayList<PartitionObject> temp;

    private void partitionTestPrepare() {
        scanDirectory = new ScanDirectory();
    }


    @Test
    public void canDirTest() {
        partitionTestPrepare();
        ArrayList<File> allFiles= scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(),21);
    }

    @Test
    public void scanForSameTest() {
        partitionTestPrepare();
        ArrayList<File> allFiles= scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(),21);
        ArrayList<CompareItem> sameFiles = scanDirectory.scanForSame();
        Assert.assertEquals(sameFiles.size(),4);
    }

    @Test
         public void scanForSameParallelTest() {
        partitionTestPrepare();
        ArrayList<File> allFiles= scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(),21);
        CopyOnWriteArrayList<CompareItem> sameFiles = scanDirectory.scanForSameParallel();
        Assert.assertEquals(sameFiles.size(),4);
    }

    @Test
    public void scanForSameParallelThreadTest() {
        partitionTestPrepare();
        ArrayList<File> allFiles= scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(),21);
        CopyOnWriteArrayList<CompareItem> sameFiles = scanDirectory.scanForSameParallelThread();
        Assert.assertEquals(sameFiles.size(),4);
    }


    @Test
    public void partitionTestTwenty() {
        partitionTestPrepare();
        temp = scanDirectory.partition(20);
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
        partitionTestPrepare();
        temp = scanDirectory.partition(21);
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
        partitionTestPrepare();
        temp = scanDirectory.partition(9);
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
        partitionTestPrepare();
        temp = scanDirectory.partition(10);
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

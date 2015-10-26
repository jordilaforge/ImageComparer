package com.jordilaforge.imagecomparer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


public class ScanDirectoryTest {

    ScanDirectory scanDirectory;
    ArrayList<PartitionObject> temp;
    ObservableList<CompareItem> sameFiles;
    private static final String testPath = "src/test/resources";

    private void testPrepare() {
        scanDirectory = new ScanDirectory();
        sameFiles = FXCollections.observableArrayList();
    }


    @Test
    public void canDirTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir(testPath);
        Assert.assertEquals(21, allFiles.size());
    }

    @Test
    public void scanForSameTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir(testPath);
        Assert.assertEquals(21, allFiles.size());
        scanDirectory.scanForSame(allFiles, sameFiles, 100);
        Assert.assertEquals(4, sameFiles.size());
    }


    @Test
    public void scanForSameParallelThreadTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir(testPath);
        Assert.assertEquals(21, allFiles.size());
        Updater updaterMock = Mockito.mock(Updater.class);
        scanDirectory.scanForSameParallelThread(allFiles, sameFiles, 100, updaterMock);
        verify(updaterMock, atLeastOnce()).update();
        Assert.assertEquals(4, sameFiles.size());
    }

    @Test
    public void speedTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir(testPath);
        Assert.assertEquals(21, allFiles.size());
        long startSingle = System.nanoTime();
        scanDirectory.scanForSame(allFiles, sameFiles, 100);
        long stopSingle = System.nanoTime();
        Updater updaterMock = Mockito.mock(Updater.class);
        long startThreads = System.nanoTime();
        scanDirectory.scanForSameParallelThread(allFiles, sameFiles, 100, updaterMock);
        long stopThreads = System.nanoTime();
        long timeSingle = (stopSingle - startSingle);
        long timeThreads = (stopThreads - startThreads);
        System.out.println("Time Single  : " + (double) (timeSingle) / 1000000000.0 + " s");
        System.out.println("Time Threads : " + (double) (timeThreads) / 1000000000.0 + " s");
        Assert.assertTrue(timeSingle > timeThreads);
    }


    @Test
    public void partitionTestTwenty() {
        testPrepare();
        temp = scanDirectory.partition(20, 4);
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
        testPrepare();
        temp = scanDirectory.partition(21, 4);
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

    @Test
    public void partitionTestNine() {
        testPrepare();
        temp = scanDirectory.partition(9, 4);
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

    @Test
    public void partitionTestTen() {
        testPrepare();
        temp = scanDirectory.partition(10, 4);
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

    @Test
    public void partitionTestEight() {
        testPrepare();
        temp = scanDirectory.partition(8, 2);
        Assert.assertEquals(2, temp.size());
        Assert.assertEquals(0, temp.get(0).getLower());
        Assert.assertEquals(3, temp.get(0).getUpper());
        Assert.assertEquals(4, temp.get(1).getLower());
        Assert.assertEquals(7, temp.get(1).getUpper());
    }

    @Test
    public void partitionTestOneHundredTwenty() {
        testPrepare();
        temp = scanDirectory.partition(120, 2);
        Assert.assertEquals(2, temp.size());
        Assert.assertEquals(0, temp.get(0).getLower());
        Assert.assertEquals(59, temp.get(0).getUpper());
        Assert.assertEquals(60, temp.get(1).getLower());
        Assert.assertEquals(119, temp.get(1).getUpper());
    }

    @Test
    public void numberOfCompareTest() {
        testPrepare();
        Assert.assertEquals(28, scanDirectory.getNumberOfCompares(8));
        Assert.assertEquals(3160, scanDirectory.getNumberOfCompares(80));
        Assert.assertEquals(79800, scanDirectory.getNumberOfCompares(400));
        Assert.assertEquals(0, scanDirectory.getNumberOfCompares(0));
        Assert.assertEquals(0, scanDirectory.getNumberOfCompares(1));
    }
}

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

    private void testPrepare() {
        scanDirectory = new ScanDirectory();
        sameFiles = FXCollections.observableArrayList();
    }


    @Test
    public void canDirTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(), 21);
    }

    @Test
    public void scanForSameTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(), 21);
        scanDirectory.scanForSame(allFiles, sameFiles, 100);
        Assert.assertEquals(sameFiles.size(), 4);
    }

    @Test
    public void scanForSameParallelTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(), 21);
        scanDirectory.scanForSameParallel(allFiles, sameFiles, 100);
        Assert.assertEquals(sameFiles.size(), 4);
    }

    @Test
    public void scanForSameParallelThreadTest() {
        testPrepare();
        ArrayList<File> allFiles = scanDirectory.scanDir("./bin/resources");
        Assert.assertEquals(allFiles.size(), 21);
        Updater updaterMock = Mockito.mock(Updater.class);
        scanDirectory.scanForSameParallelThread(allFiles, sameFiles, 100, updaterMock);
        verify(updaterMock, atLeastOnce()).update();
        Assert.assertEquals(sameFiles.size(), 4);
    }


    @Test
    public void partitionTestTwenty() {
        testPrepare();
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
        testPrepare();
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

    @Test
    public void partitionTestNine() {
        testPrepare();
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

    @Test
    public void partitionTestTen() {
        testPrepare();
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

    @Test
    public void numberOfCompareTest() {
        testPrepare();
        Assert.assertEquals(scanDirectory.getNumberOfCompares(8), 28);
        Assert.assertEquals(scanDirectory.getNumberOfCompares(80), 3160);
        Assert.assertEquals(scanDirectory.getNumberOfCompares(400), 79800);
        Assert.assertEquals(scanDirectory.getNumberOfCompares(0), 0);
        Assert.assertEquals(scanDirectory.getNumberOfCompares(1), 0);
    }
}

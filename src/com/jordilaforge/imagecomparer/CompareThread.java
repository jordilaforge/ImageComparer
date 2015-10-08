package com.jordilaforge.imagecomparer;


import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by philipp on 16.06.15.
 * class to handle more than one thread
 */
public class CompareThread implements Runnable {

    private final int similaritySetting;
    private final int upperBound;
    private final int lowerBound;
    private final ArrayList<File> allFiles;
    private final ObservableList<CompareItem> sameFiles;
    private final Updater updater;

    public CompareThread(ArrayList<File> allFiles, ObservableList<CompareItem> sameFiles, int lowerBound, int upperBound, int similaritySetting, Updater updater) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.allFiles = allFiles;
        this.sameFiles = sameFiles;
        this.similaritySetting = similaritySetting;
        this.updater = updater;
    }

    @Override
    /**
     * Thread method which compares screenshots and add them to a list
     */
    public void run() {

        CompareScreenshot compareScreenshot = new CompareScreenshot();
        long startTime = System.currentTimeMillis();
        int numberOfCompares = 0;
        for (int i = lowerBound; i <= upperBound; i++) {
            for (int j = i + 1; j < allFiles.size(); j++) {
                File file1 = allFiles.get(i);
                File file2 = allFiles.get(j);
                int similarity;
                if (!(file1.getAbsolutePath().equals(file2.getAbsolutePath()))) {
                    similarity = compareScreenshot.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
                    ++numberOfCompares;
                    Controller.numberOfCompares.addAndGet(1);
                    if (similarity >= similaritySetting) {
                        CompareItem compareItem = new CompareItem(file1.getAbsolutePath(), file2.getAbsolutePath(), similarity);
                        sameFiles.add(compareItem);
                    }

                }
                if ((i + j) % 10 == 0) {
                    updater.update();
                }

            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Worker: [" + lowerBound + "][" + upperBound + "] numberOfCompares: " + numberOfCompares + " time: " + estimatedTime + "ms");
    }
}

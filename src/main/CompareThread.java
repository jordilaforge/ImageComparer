package main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by philipp on 16.06.15.
 * class to handle more than one thread
 */
public class CompareThread implements Runnable {

    private int similaritySetting;
    private int upperBound;
    private int lowerBound;
    ArrayList<File> allFiles;
    CopyOnWriteArrayList<CompareItem> sameFilesParallel;
    private ProgressBar progressBar;

    public CompareThread(ArrayList<File> allFiles, CopyOnWriteArrayList<CompareItem> sameFilesParallel, int lowerBound, int upperBound, int similaritySetting, ProgressBar progressBar) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.allFiles = allFiles;
        this.sameFilesParallel = sameFilesParallel;
        this.similaritySetting = similaritySetting;
        this.progressBar = progressBar;
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
                    ScanDirectory.numberOfCompares.addAndGet(1);
                    if (similarity >= similaritySetting) {
                        CompareItem compareItem = new CompareItem(file1.getAbsolutePath(), file2.getAbsolutePath(), similarity);
                        sameFilesParallel.add(compareItem);
                    }

                }
                if ((i+j) % 10 == 0&&progressBar!=null) {

                    // UI updaten
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            double percentage = (double) ScanDirectory.numberOfCompares.get() / ScanDirectory.totalNumberOfCompare;
                            progressBar.setProgress(percentage);
                        }
                    });
                }

            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Worker: [" + lowerBound + "][" + upperBound + "] numberOfCompares: " + numberOfCompares + " time: " + estimatedTime + "ms");
    }
}

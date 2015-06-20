package main;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by philipp on 16.06.15.
 */
public class CompareThread implements Runnable{

    private int upperBound;
    private int lowerBound;
    ArrayList<File> allFiles;
    CopyOnWriteArrayList<CompareItem> sameFilesParallel;

    public CompareThread(ArrayList<File> allFiles,CopyOnWriteArrayList<CompareItem> sameFilesParallel, int lowerBound, int upperBound) {
        this.upperBound=upperBound;
        this.lowerBound=lowerBound;
        this.allFiles=allFiles;
        this.sameFilesParallel = sameFilesParallel;
    }

    @Override
    public void run() {
        CompareScreenshot compareScreenshot = new CompareScreenshot();
        long startTime = System.currentTimeMillis();
        int numberOfCompares = 0;
        for (int i = lowerBound; i <= upperBound; i++) {
            for (int j = i+1; j < allFiles.size(); j++) {
                File file1 = allFiles.get(i);
                File file2 = allFiles.get(j);
                int similarity = 0;
                if (!(file1.getAbsolutePath().equals(file2.getAbsolutePath()))) {
                    similarity = compareScreenshot.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
                    ++numberOfCompares;
                    if (similarity == 100) {
                        CompareItem compareItem = new CompareItem(file1.getName(), file2.getName(), similarity);
                        if (compareItem != null) {
                            sameFilesParallel.add(compareItem);
                        }
                    }
                }
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Worker: ["+lowerBound+"]["+upperBound+"] numberOfCompares: " + numberOfCompares+" time: "+ estimatedTime+"ms");
    }
}

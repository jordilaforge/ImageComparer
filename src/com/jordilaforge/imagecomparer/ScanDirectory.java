package com.jordilaforge.imagecomparer;

import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by philipp on 16.06.15.
 * class to scan directory and creating partitions
 */
public class ScanDirectory {


    private int threadnumber = 1;

    public ScanDirectory() {
        threadnumber = Runtime.getRuntime().availableProcessors();
    }

    /**
     * scans all files in a directory
     *
     * @param path of directory
     * @return ArrayList of all Files in directory
     */
    public ArrayList<File> scanDir(String path) {
        ArrayList<File> allFiles = new ArrayList<>();
        addTree(new File(path), allFiles);
        System.out.println("Scanning: " + new File(path).getAbsolutePath());
        Controller.totalNumberOfCompare = getNumberOfCompares(allFiles.size());
        return allFiles;
    }

    /**
     * helper method for scanning childs in a directory
     *
     * @param file path of directory
     * @param all  all files
     */
    private void addTree(File file, Collection<File> all) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.getName().toLowerCase().endsWith(".png") || child.getName().toLowerCase().endsWith(".jpg") || child.getName().toLowerCase().endsWith(".jpeg")) {
                    all.add(child);
                }
                addTree(child, all);

            }
        }
    }

    /**
     * scans all files for given similarity (single thread)
     *
     * @param similaritySetting wanted similarity in percentage
     */
    public void scanForSame(ArrayList<File> allFiles, ObservableList<CompareItem> sameFiles, int similaritySetting) {
        CompareScreenshot compareScreenshot = new CompareScreenshot();
        int numberOfCompares = 0;
        for (int i = 0; i < allFiles.size() - 1; i++) {
            for (int j = i + 1; j < allFiles.size(); j++) {
                File file1 = allFiles.get(i);
                File file2 = allFiles.get(j);
                int similarity;
                if (!(file1.getAbsolutePath().equals(file2.getAbsolutePath()))) {
                    similarity = compareScreenshot.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
                    ++numberOfCompares;
                    if (similarity >= similaritySetting) {
                        CompareItem compareItem = new CompareItem(file1.getAbsolutePath(), file2.getAbsolutePath(), similarity);
                        sameFiles.add(compareItem);
                    }
                }
            }
        }
        System.out.println("numberOfCompares: " + numberOfCompares);
    }


    /**
     * scans all files for given similarity (multithreaded streams)
     *
     * @param similaritySetting wanted similarity in percentage
     */
    public void scanForSameParallel(ArrayList<File> allFiles, ObservableList<CompareItem> sameFiles, int similaritySetting) {
        CompareScreenshot compareScreenshot = new CompareScreenshot();
        AtomicInteger numberOfCompares = new AtomicInteger();
        allFiles.stream().parallel().forEach(file1 -> allFiles.stream().parallel().forEach(file2 -> {
                    int similarity = 0;
                    if (!(file1.getAbsolutePath().equals(file2.getAbsolutePath()))) {
                        similarity = compareScreenshot.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
                        numberOfCompares.addAndGet(1);
                        if (similarity >= similaritySetting) {
                            CompareItem compareItem = new CompareItem(file1.getAbsolutePath(), file2.getAbsolutePath(), similarity);
                            if (!(sameFiles.contains(compareItem))) {
                                sameFiles.add(compareItem);
                            }
                        }
                    }
                }
        ));
        System.out.println("numberOfCompares: " + numberOfCompares.get());
    }

    /**
     * scans all files for given similarity (multithreaded threads)
     *
     * @param allFiles          all images in the directory
     * @param sameFiles         images with specified similarity
     * @param similaritySetting wanted similarity in percentage
     * @param updater           to be updated while method is running can be null
     */
    public void scanForSameParallelThread(ArrayList<File> allFiles, ObservableList<CompareItem> sameFiles, int similaritySetting, Updater updater) {
        System.out.println("Number of available threads: " + threadnumber);
        Controller.numberOfCompares.set(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadnumber);
        List<File> allFilesSorted = allFiles.stream().parallel().sorted((file1, file2) -> (int) (file1.length() - file2.length())).collect(Collectors.toList());
        ArrayList<PartitionObject> partitions = partition(allFilesSorted.size());
        for (PartitionObject partition : partitions) {
            Runnable worker = new CompareThread((ArrayList<File>) allFilesSorted, sameFiles, partition.getLower(), partition.getUpper(), similaritySetting, updater);
            executor.execute(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Finished all threads");
    }

    /**
     * creates equaly sized partitions for given elements
     *
     * @param size number of elements
     * @return ArrayList of PartitionObject
     */
    public ArrayList<PartitionObject> partition(int size) {
        ArrayList<PartitionObject> temp = new ArrayList<>();
        int step = size / threadnumber;
        for (int i = 0; i < threadnumber; i++) {
            int start = i * step;
            int end;
            if (i == threadnumber - 1) {
                end = size - 1;
            } else {
                end = (i * step) + step - 1;
            }
            temp.add(new PartitionObject(start, end));
        }
        return temp;
    }

    /**
     * Using small gauss to calculate number uf compares
     *
     * @param n amount of elments
     * @return returns required number of compares
     */
    public int getNumberOfCompares(int n) {
        return ((n - 1) * n) / 2;
    }

}

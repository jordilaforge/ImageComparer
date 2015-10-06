package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by philipp on 16.06.15.
 * class to scan directory and creating partitions
 */
public class ScanDirectory {


    private ArrayList<File> allFiles;
    private ArrayList<CompareItem> sameFiles;
    private CopyOnWriteArrayList<CompareItem> sameFilesParallel;
    private CopyOnWriteArrayList<CompareItem> sameFilesParallelThread;
    private int threadnumber = 1;

    public ScanDirectory() {
        allFiles = new ArrayList<>();
        sameFiles = new ArrayList<>();
        sameFilesParallel = new CopyOnWriteArrayList<>();
        sameFilesParallelThread = new CopyOnWriteArrayList<>();
        threadnumber = Runtime.getRuntime().availableProcessors();
    }

    /**
     * scans all files in a directory
     * @param path of directory
     * @return ArrayList of all Files in directory
     */
    public ArrayList<File> scanDir(String path) {
        addTree(new File(path), allFiles);
        System.out.println("Scanning: " + new File(path).getAbsolutePath());
        return allFiles;
    }

    /**
     * helper method for scanning childs in a directory
     * @param file path of directory
     * @param all all files
     */
    private void addTree(File file, Collection<File> all) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.getName().toLowerCase().endsWith(".png")||child.getName().toLowerCase().endsWith(".jpg")||child.getName().toLowerCase().endsWith(".jpeg")) {
                    all.add(child);
                }
                addTree(child, all);

            }
        }
    }

    /**
     * scans all files for given similarity (single thread)
     * @return ArrayList of similar images
     * @param similaritySetting wanted similarity in percentage
     */
    public ArrayList<CompareItem> scanForSame(int similaritySetting) {
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
        System.out.println("numberOfCompares" + numberOfCompares);
        return sameFiles;
    }

    /**
     * scans all files for given similarity (multithreaded streams)
     * @return CopyOnWriteArrayList of similar images
     * @param similaritySetting wanted similarity in percentage
     */
    public CopyOnWriteArrayList<CompareItem> scanForSameParallel(int similaritySetting) {
        CompareScreenshot compareScreenshot = new CompareScreenshot();
        allFiles.stream().parallel().forEach(file1 -> allFiles.stream().parallel().forEach(file2 -> {
                    int similarity = 0;
                    if (!(file1.getAbsolutePath().equals(file2.getAbsolutePath()))) {
                        similarity = compareScreenshot.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
                        if (similarity >= similaritySetting) {
                            CompareItem compareItem = new CompareItem(file1.getAbsolutePath(), file2.getAbsolutePath(), similarity);
                            if (!(sameFilesParallel.contains(compareItem))) {
                                sameFilesParallel.add(compareItem);
                            }
                        }
                    }
                }
        ));
        return sameFilesParallel;
    }

    /**
     * scans all files for given similarity (multithreaded threads)
     * @return CopyOnWriteArrayList of similar images
     * @param similaritySetting wanted similarity in percentage
     */
    public CopyOnWriteArrayList<CompareItem> scanForSameParallelThread(int similaritySetting) {
        System.out.println("Number of available threads: "+threadnumber);
        ExecutorService executor = Executors.newFixedThreadPool(threadnumber);
        List<File> allFilesSorted = allFiles.stream().parallel().sorted((file1, file2) -> (int) (file1.length() - file2.length())).collect(Collectors.toList());
        ArrayList<PartitionObject> partitions = partition(allFilesSorted.size());
        for (PartitionObject partition : partitions) {
            Runnable worker = new CompareThread((ArrayList<File>) allFilesSorted, sameFilesParallelThread, partition.getLower(), partition.getUpper(),similaritySetting);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
        return sameFilesParallelThread;
    }

    /**
     * creates equaly sized partitions for given elements
     * @param size number of elements
     * @return ArrayList of PartitionObject
     */
    public ArrayList<PartitionObject> partition(int size) {
        ArrayList temp = new ArrayList();
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
     * @param n amount of elments
     * @return
     */
    public int getNumberOfCompares(int n){
        return ((n-1)*n)/2;
    }

}

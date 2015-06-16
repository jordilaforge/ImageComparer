package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by philipp on 16.06.15.
 */
public class ScanDirectory {


    private ArrayList<File> allFiles;
    private ArrayList<CompareItem> sameFiles;
    private CopyOnWriteArrayList<CompareItem> sameFilesParallel;

    public ScanDirectory() {
        allFiles = new ArrayList<>();
        sameFiles = new ArrayList<>();
        sameFilesParallel = new CopyOnWriteArrayList<>();
    }



    public ArrayList<File> scanDir(String path) {
        addTree(new File(path), allFiles);
        System.out.println("Scanning: "+new File(path).getAbsolutePath());
        return allFiles;
    }

    private void addTree(File file, Collection<File> all) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.getName().toLowerCase().endsWith(".png")) {
                    all.add(child);
                }
                addTree(child, all);

            }
        }
    }

    public ArrayList<CompareItem> scanForSame() {
        CompareScreenshot compareScreenshot = new CompareScreenshot();
        for (int i = 0; i < allFiles.size()-1; i++) {
            for (int j = i+1; j < allFiles.size(); j++) {
                File file1 = allFiles.get(i);
                File file2 = allFiles.get(j);
                int similarity = 0;
                if (!(file1.getAbsolutePath().equals(file2.getAbsolutePath()))) {
                    similarity = compareScreenshot.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
                    if (similarity == 100) {
                        CompareItem compareItem = new CompareItem(file1.getName(), file2.getName(), similarity);
                        if (compareItem != null) {
                            sameFiles.add(compareItem);
                        }
                    }
                }
            }
        }
        return sameFiles;
    }

    public CopyOnWriteArrayList<CompareItem> scanForSameParallel(){
        CompareScreenshot compareScreenshot = new CompareScreenshot();
        allFiles.stream().parallel().forEach(file1 -> {
            allFiles.stream().parallel().forEach(file2 ->{
                        int similarity = 0;
                        if (!(file1.getAbsolutePath().equals(file2.getAbsolutePath()))) {
                            similarity = compareScreenshot.compare(file1.getAbsolutePath(), file2.getAbsolutePath());
                            if (similarity == 100) {
                                CompareItem compareItem = new CompareItem(file1.getName(), file2.getName(), similarity);
                                if (!(sameFilesParallel.contains(compareItem))) {
                                    sameFilesParallel.add(compareItem);
                                }
                            }
                        }
                    }
            );
        });
        return sameFilesParallel;
    }
}

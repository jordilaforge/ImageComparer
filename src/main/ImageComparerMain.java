package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImageComparerMain {

	static final int threadnumber=4;

	public static void main(String[] args) {
		ScanDirectory scanDirectory = new ScanDirectory();
		ArrayList<File> allFiles= scanDirectory.scanDir("./bin/resources");
		System.out.println("Number of images found: "+allFiles.size());
		long startTime = System.currentTimeMillis();
		ArrayList<CompareItem> sameFiles = scanDirectory.scanForSame();
		for (CompareItem sameFile : sameFiles) {
			System.out.println("Image1: "+sameFile.getImage1()+" Image2: "+sameFile.getImage2()+" Similarity: "+sameFile.getSimilarity());
		}
		System.out.println("elements: "+sameFiles.size());
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("serialtime: "+estimatedTime+" ms");

		startTime = System.currentTimeMillis();
		CopyOnWriteArrayList<CompareItem> sameFilesParallel = scanDirectory.scanForSameParallel();
		for (CompareItem compareItem : sameFilesParallel) {
			System.out.println("Image1: "+compareItem.getImage1()+" Image2: "+compareItem.getImage2()+" Similarity: "+compareItem.getSimilarity());
		}
		System.out.println("elements: "+sameFilesParallel.size());
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("paralleltime: "+estimatedTime+" ms");

		startTime = System.currentTimeMillis();
		CopyOnWriteArrayList<CompareItem> sameFilesParallelThread = scanDirectory.scanForSameParallelThread();
		for (CompareItem compareItem : sameFilesParallelThread) {
			System.out.println("Image1: "+compareItem.getImage1()+" Image2: "+compareItem.getImage2()+" Similarity: "+compareItem.getSimilarity());
		}
		System.out.println("elements: "+sameFilesParallelThread.size());
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("paralleltime Threads: "+estimatedTime+" ms");

		System.out.println("Finished");

	}

}

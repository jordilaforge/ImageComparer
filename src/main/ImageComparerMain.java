package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImageComparerMain extends Application{

	static final int threadnumber=4;

	Button btn1;

	@Override
	public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle("Hello World");
		btn1 = new Button();
		btn1.setText("Search");
		btn1.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory =
					directoryChooser.showDialog(primaryStage);

			if(selectedDirectory == null){
				System.out.println("No Directory Choosen");;
			}else {
				System.out.println(selectedDirectory.getAbsolutePath());
				doComparison(selectedDirectory.getAbsolutePath());
			}
		});
		StackPane layout = new StackPane();
		layout.getChildren().add(btn1);
		Scene scene = new Scene(layout,300,275);
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}


	public static void doComparison(String absolutePath){
		ScanDirectory scanDirectory = new ScanDirectory();
		ArrayList<File> allFiles= scanDirectory.scanDir(absolutePath);
		System.out.println("Number of images found: "+allFiles.size());



		long startTime = System.currentTimeMillis();
		CopyOnWriteArrayList<CompareItem> sameFilesParallelThread = scanDirectory.scanForSameParallelThread();
		for (CompareItem compareItem : sameFilesParallelThread) {
			System.out.println("Image1: "+compareItem.getImage1()+" Image2: "+compareItem.getImage2()+" Similarity: "+compareItem.getSimilarity());
		}
		System.out.println("number of similar elements: "+sameFilesParallelThread.size());
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Total established time: "+estimatedTime+" ms");

		System.out.println("Finished");
	}




}

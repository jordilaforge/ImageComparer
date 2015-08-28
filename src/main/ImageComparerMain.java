package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImageComparerMain extends Application{

	static final int threadnumber=4;
	//buttons
	Button btn1;
	Button btn2;
	TextField field;
	Text status;
	ArrayList<File> allFiles;
	ScanDirectory scanDirectory;
	CopyOnWriteArrayList<CompareItem> sameFilesParallelThread;
	GridPane gridPane;

	@Override
	public void start(Stage primaryStage) throws Exception{
		//Settings
		primaryStage.setTitle("Image Comparer");
		//Textfield
		field = new TextField();
		field.setText("No Directory Selected");
		field.setPrefSize(400,20);
		//Buttons
		btn1 = new Button();
		btn1.setText("Directory");
		btn1.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory =
					directoryChooser.showDialog(primaryStage);

			if (selectedDirectory == null) {
				System.out.println("No Directory Choosen");
				;
			} else {
				System.out.println(selectedDirectory.getAbsolutePath());
				field.setText(selectedDirectory.getAbsolutePath());
				scanDirectory = new ScanDirectory();
				allFiles= scanDirectory.scanDir(selectedDirectory.getAbsolutePath());
				status.setText("Directory: "+selectedDirectory.getAbsolutePath()+" Number of files: "+allFiles.size());
			}
		});
		btn2 = new Button();
		btn2.setText("Search");
		btn2.setOnAction(e -> {
			if(allFiles.size()>0){
				status.setText("Searching...");
				doComparison(field.getText());
			}
			else{
				status.setText("No Image Files Found!");
			}
		});
		//Hbox
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #DCF0F7;");
		hbox.getChildren().addAll(field,btn1,btn2);
		//imageview
		gridPane = new GridPane();
		Text a = new Text("");
		Text b = new Text("");
		Text c = new Text("");
		gridPane.add(a, 0, 0);
		gridPane.add(b, 1, 0);
		gridPane.add(c,2,0);
		ScrollPane sp = new ScrollPane();
		sp.setContent(gridPane);
		//statusbar
		HBox hboxStatus = new HBox();
		hboxStatus.setStyle("-fx-background-color: #DCF0F7;");
		status = new Text();
		status.setText("No Directory Selected...");
		hboxStatus.getChildren().add(status);
		//borderpane
		BorderPane border = new BorderPane();
		border.setTop(hbox);
		border.setCenter(sp);
		border.setBottom(hboxStatus);
		//layout
		StackPane layout = new StackPane();
		layout.getChildren().add(border);
		Scene scene = new Scene(layout,600,275);
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}


	public void doComparison(String absolutePath){

		System.out.println("Number of images found: "+allFiles.size());


		long startTime = System.currentTimeMillis();
		sameFilesParallelThread = scanDirectory.scanForSameParallelThread();
		for (CompareItem compareItem : sameFilesParallelThread) {
			System.out.println("Image1: "+compareItem.getImage1()+" Image2: "+compareItem.getImage2()+" Similarity: "+compareItem.getSimilarity());
		}
		System.out.println("number of similar elements: "+sameFilesParallelThread.size());
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Total established time: " + estimatedTime + " ms");
		if(sameFilesParallelThread.size()>0){
			status.setText("Finished! similar Images: " + sameFilesParallelThread.size());
			for(int i=0;i<sameFilesParallelThread.size();++i){
				Image imagel=null;
				Image imager=null;
				try {
					imagel = new Image(String.valueOf(new File(sameFilesParallelThread.get(i).getImage1()).toURI().toURL()));
					imager = new Image(String.valueOf(new File(sameFilesParallelThread.get(i).getImage2()).toURI().toURL()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				ImageView picl = new ImageView();
				picl.setImage(imagel);
				picl.setFitHeight(100);
				picl.setFitWidth(100);
				gridPane.add(picl,0,i);
				Text similarity = new Text();
				similarity.setText("Similarity: "+String.valueOf(sameFilesParallelThread.get(i).getSimilarity())+"%");
				gridPane.add(similarity,1,i);
				ImageView picr = new ImageView();
				picr.setImage(imager);
				picr.setFitHeight(100);
				picr.setFitWidth(100);
				gridPane.add(picr,2,i);
			}

		}
		else{
			status.setText("Finished! No Similar Images found!");
		}
		System.out.println("Finished");
	}




}

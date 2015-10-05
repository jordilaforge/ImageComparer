package main;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by philippsteiner on 05/10/15.
 */

public class Controller {
        ArrayList<File> allFiles;
        ScanDirectory scanDirectory;
        CopyOnWriteArrayList<CompareItem> sameFilesParallelThread;

        @FXML
        private Text status;
        @FXML private TextField directory;
        @FXML private BorderPane borderPane;
        @FXML private GridPane gridPane;
        @FXML private ProgressBar progressBar;
        @FXML private Slider slider;

        @FXML protected void directoryButtonAction(ActionEvent event) {
            System.out.println("Directory Pressed");
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory =
                    directoryChooser.showDialog(borderPane.getScene().getWindow());

            if (selectedDirectory == null) {
                System.out.println("No Directory Choosen");

            } else {
               System.out.println(selectedDirectory.getAbsolutePath());
                directory.setText(selectedDirectory.getAbsolutePath());
                scanDirectory = new ScanDirectory();
                allFiles = scanDirectory.scanDir(selectedDirectory.getAbsolutePath());
                status.setText("Directory: " + selectedDirectory.getAbsolutePath() + " Number of files: " + allFiles.size());
            }
        }

        @FXML protected void searchButtonAction(ActionEvent event) {
        System.out.println("Search Pressed");
            if (allFiles.size() > 0) {
                if(sameFilesParallelThread!=null){
                    sameFilesParallelThread.clear();
                    gridPane.getChildren().clear();
                }
                status.setText("Searching...");
                doComparison();
            } else {
                status.setText("No Image Files Found!");
            }
        }

    public void doComparison() {


        Task task = new Task<CopyOnWriteArrayList>() {
            @Override
            public CopyOnWriteArrayList call() {
                CopyOnWriteArrayList cowal = scanDirectory.scanForSameParallelThread();
                updateProgress(1,1);
                return cowal;

            }


        };
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                t -> {
                    sameFilesParallelThread = (CopyOnWriteArrayList<CompareItem>) task.getValue();
                    for (int i = 0; i < sameFilesParallelThread.size(); ++i) {
                        Image imagel = null;
                        Image imager = null;
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
                        VBox vboxPicL = new VBox();
                        Text namel = new Text(sameFilesParallelThread.get(i).getImage1().substring(sameFilesParallelThread.get(i).getImage1().lastIndexOf("\\") + 1));
                        vboxPicL.getChildren().addAll(picl, namel);
                        gridPane.add(vboxPicL, 0, i);
                        Text similarityName = new Text();
                        similarityName.setText("Similarity");
                        Text similarity = new Text(String.valueOf(sameFilesParallelThread.get(i).getSimilarity()) + "%");
                        VBox vboxSimilarity = new VBox();
                        vboxSimilarity.getChildren().addAll(similarityName, similarity);
                        vboxSimilarity.setAlignment(Pos.CENTER);
                        gridPane.add(vboxSimilarity, 1, i);
                        ImageView picr = new ImageView();
                        picr.setImage(imager);
                        picr.setFitHeight(100);
                        picr.setFitWidth(100);
                        VBox vboxPicR = new VBox();
                        Text namer = new Text(sameFilesParallelThread.get(i).getImage2().substring(sameFilesParallelThread.get(i).getImage2().lastIndexOf("\\") + 1));
                        vboxPicR.getChildren().addAll(picr, namer);
                        gridPane.add(vboxPicR, 2, i);
                        status.setText("Finished Scanning! Similar Images Found: " + sameFilesParallelThread.size());
                    }
                    if(sameFilesParallelThread.size()==0){
                        status.setText("Finished Scanning! No Similar Images Found!");
                    }

                }

        );
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        System.out.println("Finished");
    }
}


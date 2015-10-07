package main;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by philippsteiner on 05/10/15.
 */

public class Controller {
    ArrayList<File> allFiles;
    ScanDirectory scanDirectory;
    CopyOnWriteArrayList<CompareItem> sameFilesParallelThread;
    int similaritySetting=100;
    public static AtomicInteger numberOfCompares = new AtomicInteger(0);
    public static int totalNumberOfCompare=0;

    @FXML
    private Text status;
    @FXML
    private Label directory;
    @FXML
    private BorderPane borderPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Slider slider;

    @FXML
    protected void directoryButtonAction(ActionEvent event) {
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
            status.setText("Number of files: " + allFiles.size()+" Compares: "+scanDirectory.getNumberOfCompares(allFiles.size()));
        }
    }

    @FXML
    protected void searchButtonAction(ActionEvent event) {
        System.out.println("Search Pressed");
        if (allFiles.size() > 0) {
            if (sameFilesParallelThread != null) {
                sameFilesParallelThread.clear();
                gridPane.getChildren().clear();
            }
            status.setText("Searching for "+similaritySetting+"% similarity...");
            doComparison();
        } else {
            status.setText("No Image Files Found!");
        }
    }


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Handle Slider value change events.
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            similaritySetting=newValue.intValue();
        });
    }

    /**
     * Does the actual compare and shows result
     */
    public void doComparison() {



        Task task = new Task<CopyOnWriteArrayList>() {
            @Override
            public CopyOnWriteArrayList call() {

                CopyOnWriteArrayList cowal = scanDirectory.scanForSameParallelThread(similaritySetting, new Updater() {

                    @Override
                    public void update(double progress) {
                        updateProgress(numberOfCompares.get(), totalNumberOfCompare);
                    }
                });
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
                        picl.setFitHeight(200);
                        picl.setFitWidth(200);
                        VBox vboxPicL = new VBox();
                        Text namel = new Text(sameFilesParallelThread.get(i).getImage1Name());
                        vboxPicL.getChildren().addAll(picl, namel);
                        gridPane.add(vboxPicL, 0, i);
                        Text similarityName = new Text();
                        similarityName.setText("Similarity");
                        Text similarity = new Text(String.valueOf(sameFilesParallelThread.get(i).getSimilarity()) + "%");
                        similarity.setFont(Font.font("Arial", 18));
                        VBox vboxSimilarity = new VBox();
                        vboxSimilarity.getChildren().addAll(similarityName, similarity);
                        vboxSimilarity.setAlignment(Pos.CENTER);
                        gridPane.add(vboxSimilarity, 1, i);
                        ImageView picr = new ImageView();
                        picr.setImage(imager);
                        picr.setFitHeight(200);
                        picr.setFitWidth(200);
                        VBox vboxPicR = new VBox();
                        Text namer = new Text(sameFilesParallelThread.get(i).getImage2Name());
                        vboxPicR.getChildren().addAll(picr, namer);
                        gridPane.add(vboxPicR, 2, i);
                        status.setText("Finished Scanning! Similar Images Found (with "+similaritySetting+"%): " + sameFilesParallelThread.size());
                    }
                    if (sameFilesParallelThread.size() == 0) {
                        status.setText("Finished Scanning! No Similar Images Found (with "+similaritySetting+"%):");
                    }

                }

        );
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        System.out.println("Finished");
    }
}


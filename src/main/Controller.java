package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by philippsteiner on 05/10/15.
 */

public class Controller {
    ArrayList<File> allFiles;
    ScanDirectory scanDirectory;
    CopyOnWriteArrayList<CompareItem> sameFilesParallelThread;
    int similaritySetting = 100;
    public static AtomicInteger numberOfCompares = new AtomicInteger(0);
    public static int totalNumberOfCompare = 0;

    @FXML
    private Text status;
    @FXML
    private Label directory;
    @FXML
    private BorderPane borderPane;

    @FXML
    private TableView<CompareItem> tableView;
    @FXML
    private TableColumn<CompareItem, String> image1;
    @FXML
    private TableColumn<CompareItem, Integer> similarity;
    @FXML
    private TableColumn<CompareItem, String> image2;


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
            status.setText("Number of files: " + allFiles.size() + " Compares: " + scanDirectory.getNumberOfCompares(allFiles.size()));
        }
    }

    @FXML
    protected void searchButtonAction(ActionEvent event) {
        System.out.println("Search Pressed");
        if (allFiles.size() > 0) {
            if (sameFilesParallelThread != null) {
                sameFilesParallelThread.clear();
                tableView.getItems().clear();
            }
            status.setText("Searching for " + similaritySetting + "% similarity...");
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
            similaritySetting = newValue.intValue();
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
                updateProgress(1, 1);
                return cowal;

            }

        };
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                t -> {
                    sameFilesParallelThread = (CopyOnWriteArrayList<CompareItem>) task.getValue();
                    image1.setCellValueFactory(new PropertyValueFactory<CompareItem, String>("image1"));
                    similarity.setCellValueFactory(new PropertyValueFactory<CompareItem, Integer>("similarity"));
                    image2.setCellValueFactory(new PropertyValueFactory<CompareItem, String>("image2"));

                    tableView.getItems().setAll(FXCollections.observableArrayList(sameFilesParallelThread));
                    if (sameFilesParallelThread.size() == 0) {
                        status.setText("Finished Scanning! No Similar Images Found (with " + similaritySetting + "%):");
                    }
                    else{
                        status.setText("Finished Scanning! Similar Images Found (with "+similaritySetting+"%): " + sameFilesParallelThread.size());
                    }

                }

        );
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        System.out.println("Finished");
    }

}


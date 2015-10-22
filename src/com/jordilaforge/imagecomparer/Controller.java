package com.jordilaforge.imagecomparer;

import com.jordilaforge.imagecomparer.tablecells.ImageTableCell;
import com.jordilaforge.imagecomparer.tablecells.NumberTableCell;
import com.jordilaforge.imagecomparer.tablecells.SimilarityTableCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {
    public static final AtomicInteger numberOfCompares = new AtomicInteger(0);
    public static int totalNumberOfCompare = 0;
    private final ObservableList<CompareItem> sameFiles = FXCollections.observableArrayList();
    private ArrayList<File> allFiles = new ArrayList<>();
    private ScanDirectory scanDirectory;
    private int similaritySetting = 100;


    @FXML
    private Button searchButton;
    @FXML
    private Button directoryButton;
    @FXML
    private Text status;
    @FXML
    private Label directory;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TableView<CompareItem> tableView;
    @FXML
    private TableColumn<CompareItem, CompareItem> numberCol;
    @FXML
    private TableColumn<CompareItem, String> image1Col;
    @FXML
    private TableColumn<CompareItem, Integer> similarityCol;
    @FXML
    private TableColumn<CompareItem, String> image2Col;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Slider slider;


    @SuppressWarnings("UnusedParameters")
    @FXML
    protected void directoryButtonAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(borderPane.getScene().getWindow());
        searchButton.setDisable(false);
        if (selectedDirectory != null) {
            directory.setText(selectedDirectory.getAbsolutePath());
            scanDirectory = new ScanDirectory();
            allFiles = scanDirectory.scanDir(selectedDirectory.getAbsolutePath());
            status.setText("Number of files: " + allFiles.size() + " compares: " + scanDirectory.getNumberOfCompares(allFiles.size()));
        }
    }

    @SuppressWarnings("UnusedParameters")
    @FXML
    protected void searchButtonAction(ActionEvent event) {
        searchButton.setDisable(true);
        directoryButton.setDisable(true);
        slider.setDisable(true);
        if (allFiles.size() > 0) {
            if (sameFiles != null) {
                sameFiles.clear();
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
    @SuppressWarnings("unused")
    private void initialize() {
        status.setText("No directory chosen");
        searchButton.setDisable(true);
        // Handle Slider value change events.
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            similaritySetting = newValue.intValue();
        });
        //Doubleclick Event on Tableview
        tableView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                Context.compareItem = tableView.getSelectionModel().getSelectedItem();
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("DetailGui.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert root != null;
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(borderPane.getScene().getWindow());
                stage.setTitle("Detail View");
                stage.setScene(scene);
                stage.show();
            }
        });
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
        numberCol.setCellFactory(colum -> new NumberTableCell());
        similarityCol.setCellValueFactory(cellData -> cellData.getValue().similarityProperty().asObject());
        similarityCol.setCellFactory(column -> new SimilarityTableCell());
        image1Col.setCellValueFactory(cellData -> cellData.getValue().image1Property());
        image1Col.setCellFactory(column -> new ImageTableCell());
        image2Col.setCellValueFactory(cellData -> cellData.getValue().image2Property());
        image2Col.setCellFactory(column -> new ImageTableCell());
        tableView.setItems(sameFiles);
    }


    /**
     * Does the actual compare and shows result
     */
    public void doComparison() {


        Task task = new Task<Void>() {
            @Override
            public Void call() {
                updateProgress(0, 1);
                scanDirectory.scanForSameParallelThread(allFiles, sameFiles, similaritySetting, () -> updateProgress(numberOfCompares.get(), totalNumberOfCompare));
                updateProgress(1, 1);
                return null;
            }

        };
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                t -> {
                    task.getValue();
                    tableView.refresh();
                    if (sameFiles.size() == 0) {
                        status.setText("Finished Scanning! No Similar Images Found (with " + similaritySetting + "%):");
                    } else {
                        status.setText("Finished Scanning! Similar Images Found (with " + similaritySetting + "%): " + sameFiles.size());
                    }
                    searchButton.setDisable(false);
                    directoryButton.setDisable(false);
                    slider.setDisable(false);
                }

        );
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        System.out.println("Finished");
    }


}


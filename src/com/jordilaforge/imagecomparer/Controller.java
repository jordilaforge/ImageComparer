package com.jordilaforge.imagecomparer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.MalformedURLException;
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
    private Text status;
    @FXML
    private Label directory;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TableView<CompareItem> tableView;
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
        System.out.println("Directory Pressed");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(borderPane.getScene().getWindow());
        searchButton.setDisable(false);
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

    @SuppressWarnings("UnusedParameters")
    @FXML
    protected void searchButtonAction(ActionEvent event) {
        System.out.println("Search Pressed");
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
    private void initialize() {
        searchButton.setDisable(true);
        // Handle Slider value change events.
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            similaritySetting = newValue.intValue();
        });
        similarityCol.setCellValueFactory(cellData -> cellData.getValue().similarityProperty().asObject());
        similarityCol.setCellFactory(column -> {
            return new TableCell<CompareItem, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item,empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                        setGraphic(null);
                    } else {
                        Label integer = new Label(item.toString());
                        setGraphic(integer);
                    }
                }
            };
        });
        image1Col.setCellValueFactory(cellData -> cellData.getValue().image1Property());
        // Custom rendering of table Cell
        image1Col.setCellFactory(column -> {
            return new TableCell<CompareItem, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        vbox.setSpacing(10);
                        Label imageName = new Label(item);
                        ImageView imageview = new ImageView();
                        imageview.setFitHeight(50);
                        imageview.setFitWidth(50);
                        System.out.println(item);
                        try {
                            imageview.setImage(new Image(String.valueOf(new File(item).toURI().toURL())));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        vbox.getChildren().addAll(imageview, imageName);
                        //SETTING ALL THE GRAPHICS COMPONENT FOR CELL
                        setGraphic(vbox);
                    }
                }
            };
        });
        image2Col.setCellValueFactory(cellData -> cellData.getValue().image2Property());
        // Custom rendering of the table cell.
        image2Col.setCellFactory(column -> {
            return new TableCell<CompareItem, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item,empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox();
                        vbox.setSpacing(10);
                        Label imageName = new Label(item);
                        ImageView imageview = new ImageView();
                        imageview.setFitHeight(50);
                        imageview.setFitWidth(50);
                        System.out.println(item);
                        try {
                            imageview.setImage(new Image(String.valueOf(new File(item).toURI().toURL())));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        vbox.getChildren().addAll(imageview, imageName);
                        //SETTING ALL THE GRAPHICS COMPONENT FOR CELL
                        setGraphic(vbox);
                    }
                }
            };
        });
        tableView.setItems(sameFiles);
    }

    /**
     * Does the actual compare and shows result
     */
    public void doComparison() {


        Task task = new Task<Void>() {
            @Override
            public Void call() {

                scanDirectory.scanForSameParallelThread(allFiles, sameFiles, similaritySetting, () -> updateProgress(numberOfCompares.get(), totalNumberOfCompare));
                updateProgress(1, 1);


                return null;
            }

        };
        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                t -> {
                    task.getValue();
                    if (sameFiles.size() == 0) {
                        status.setText("Finished Scanning! No Similar Images Found (with " + similaritySetting + "%):");
                    } else {
                        status.setText("Finished Scanning! Similar Images Found (with " + similaritySetting + "%): " + sameFiles.size());
                    }

                }

        );
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
        System.out.println("Finished");
    }

}


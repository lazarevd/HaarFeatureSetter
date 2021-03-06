package HaarSamples;

import HaarSamples.dao.SquareCoord;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class MainWindow extends Application {

    public enum Mode {
        BBOX,
        ANGLE
    }

    private Scene scene;
    Timeslider timeslider;

    List<Path> paths = new ArrayList<>();
    final Map<Path, Image> images = new HashMap<>();
    Image currentImage;

    public String lastLoadPath = "c:/";

    public static Mode mode = Mode.ANGLE;

    SquarePane squarePane;


    private int maxWidth, maxHeight;


    private void savePosFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        File lastDatFilesLoad = new File(lastLoadPath);
        fileChooser.setInitialDirectory(lastDatFilesLoad);
        fileChooser.setTitle("Save Positives File");
        File file = fileChooser.showSaveDialog(stage);
        lastLoadPath = file.getParentFile().getAbsolutePath();
        if (file != null) {
            try {
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                for (Map.Entry<Integer, SquareCoord> en : timeslider.getFrames().entrySet()) {
                    SquareCoord sqCoord = en.getValue();
                    writer.println(paths.get(en.getKey()) + " "
                            + "1 "
                            + sqCoord.lx + " "
                            + sqCoord.ly + " "
                            + sqCoord.getWidth() + " "
                            + sqCoord.getHeight());
                }
                writer.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }


    }



    private void saveKeys(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        File lastKeysFilesLoad = new File(lastLoadPath);
        fileChooser.setInitialDirectory(lastKeysFilesLoad);
        fileChooser.setTitle("Save Keys");
        File file = fileChooser.showSaveDialog(stage);
        lastLoadPath = file.getParentFile().getAbsolutePath();
        if (file != null) {
            try {
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                for (Map.Entry<Integer, SquareCoord> ent : timeslider.getKeyframes().entrySet()) {
                    writer.println(ent.getKey() + ";" + ent.getValue().lx + " " + ent.getValue().ly + " " + ent.getValue().rx + " " + ent.getValue().ry);
                }
                writer.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    private void loadKeys(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        File lastKeysFilesLoad = new File(lastLoadPath);
        fileChooser.setInitialDirectory(lastKeysFilesLoad);
        fileChooser.setTitle("Load Keys");
        File file = fileChooser.showOpenDialog(stage);
        lastLoadPath = file.getParentFile().getAbsolutePath();
        try (Stream<String> files = Files.lines(file.toPath())) {
            if (files != null) {
                timeslider.clearKeys();
                files.forEach(e -> {
                    String line[] = e.split(";");
                    int keyFrame = 0;
                    try {
                        keyFrame = Integer.parseInt(line[0]);
                    } catch (NumberFormatException nes) {
                        System.out.println("Error in key frame format. Must be integer");
                    }
                    int[] coords = new int[4];
                    String[] coordStr = line[1].split(" ");
                    for (int i = 0; i < coordStr.length; i++) {
                        try {
                            coords[i] = Integer.parseInt(coordStr[i]);
                        } catch (NumberFormatException nex) {
                            System.out.println("Error in square coordinates format. Must be integer");
                        }
                    }
                    if (keyFrame > 0) {
                        timeslider.addKeyFrame(keyFrame, new SquareCoord(coords[0], coords[1], coords[2], coords[3]));
                    }
                });
            }
        } catch (IOException ioex) {
            System.out.println("IOException: Unable to read keys file. Its not exist or access denied");
        }


    }


    private void loadImages(Stage stage) {
        paths = getPaths(stage);
        Stream<Path> stream = paths.stream();
        paths = stream.filter(this::validatePath).collect(Collectors.toList());
        paths.forEach(p -> {
            Image img = new Image("file:" + p);
            images.put(p, img);
            if (img.getHeight() > maxHeight) maxHeight = (int) img.getHeight();
            if (img.getWidth() > maxWidth) maxWidth = (int) img.getWidth();
        });
        System.out.println("Loaded: " + paths.size());
        timeslider.setThumbMaxValue(paths.size() - 1);
        timeslider.thumb.setCurrentValue(1);

    }

    public static void main(String[] args) {
        System.out.println("Start");
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {


        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        menuBar.getMenus().addAll(menuFile);

        MenuItem loadImages = new MenuItem("Load images");
        loadImages.setOnAction(e -> loadImages(primaryStage)
        );
        menuFile.getItems().add(loadImages);
        MenuItem saveKeys = new MenuItem("Save keys");
        saveKeys.setOnAction(e -> {
                    saveKeys(primaryStage);
                }
        );
        menuFile.getItems().add(saveKeys);
        MenuItem loadKeys = new MenuItem("Load keys");
        loadKeys.setOnAction(e -> {
                    loadKeys(primaryStage);
                }
        );
        menuFile.getItems().add(loadKeys);
        MenuItem savePosFile = new MenuItem("Build positive file");
        savePosFile.setOnAction(e -> {
                    savePosFile(primaryStage);
                }
        );
        menuFile.getItems().add(savePosFile);


        Menu editFile = new Menu("Edit");
        menuBar.getMenus().addAll(editFile);


        MenuItem deleteKey = new MenuItem("Delete key");
        deleteKey.setOnAction(e -> {
            timeslider.removeKey(timeslider.thumb.getCurrentValue());
        });
        editFile.getItems().add(deleteKey);

        MenuItem clearKeys = new MenuItem("Clear keys");
        clearKeys.setOnAction(e -> {
                    timeslider.clearKeys();
                }
        );
        editFile.getItems().add(clearKeys);

        MenuItem resetSquare = new MenuItem("Reset square");
        resetSquare.setOnAction(e -> {
                    squarePane.square.lx = 5;
                    squarePane.square.ly = 5;
                    squarePane.square.rx = 25;
                    squarePane.square.ry = 25;
                }
        );
        editFile.getItems().add(resetSquare);

        MenuItem resetAngle = new MenuItem("Reset square");
        resetAngle.setOnAction(e -> {
                    squarePane.angleLine.baseX = 5;
                    squarePane.angleLine.baseY = 5;
                    squarePane.angleLine.dirX = 35;
                    squarePane.angleLine.dirY = 35;
                }
        );
        editFile.getItems().add(resetAngle);


        squarePane = new SquarePane(this);
        timeslider = new Timeslider(this);

        //MODE
        ToggleGroup modeRadGrp = new ToggleGroup();
        RadioButton modeAngleBtn = new RadioButton(Mode.ANGLE.toString());
        modeAngleBtn.setToggleGroup(modeRadGrp);
        modeAngleBtn.setSelected(true);
        RadioButton modeBboxBtn = new RadioButton(Mode.BBOX.toString());
        modeBboxBtn.setToggleGroup(modeRadGrp);
        HBox radBtnBox = new HBox();
        modeRadGrp.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (modeRadGrp.getSelectedToggle() != null) {
                RadioButton rb = (RadioButton) modeRadGrp.getSelectedToggle();
                if (rb.getText().equals(Mode.ANGLE.toString())) {
                    mode = Mode.ANGLE;
                } else if (rb.getText().equals(Mode.BBOX.toString())) {
                    mode = Mode.BBOX;
                }
            }
        });

        radBtnBox.getChildren().addAll(modeBboxBtn, modeAngleBtn);

        primaryStage.setTitle("Hello World!");


        StackPane root = new StackPane();

        VBox vbox = new VBox(5);
        vbox.getChildren().add(menuBar);


        squarePane.addToBox(vbox);
        timeslider.addToBox(vbox);
        vbox.getChildren().addAll(radBtnBox);

        root.getChildren().add(vbox);
        scene = new Scene(root, 850, 300);
        primaryStage.setScene(scene);
        scene.setCursor(Cursor.HAND);

        primaryStage.show();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.S) {
                timeslider.setKey();
            }
        });

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.X) {
                timeslider.nextFrame();
            }
        });

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.Z) {
                timeslider.prevFrame();
            }
        });


    }


    private boolean validatePath(Path path) {
        //System.out.println(path);
        return path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png");
    }

    private List<Path> getPaths(Stage stage) {
        List<Path> retList = new ArrayList<>();
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Load images");
        File lastImagesFilesLoad = new File(lastLoadPath);
        chooser.setInitialDirectory(lastImagesFilesLoad);
        File selectedDirectory = chooser.showDialog(stage);
        lastLoadPath = selectedDirectory.getAbsolutePath();
        System.out.println("Loaded images from: " + selectedDirectory.getAbsolutePath());
        try (Stream<Path> paths = Files.walk(Paths.get(selectedDirectory.getAbsolutePath()))) {
            retList = paths
                    .filter(file -> validatePath(file))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retList;
    }


}

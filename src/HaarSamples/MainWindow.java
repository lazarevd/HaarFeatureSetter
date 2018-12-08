package HaarSamples;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    Scene scene;
    Timeslider timeslider;

    List<Path> paths = new ArrayList<>();
    Map<Path, Image> images = new HashMap<>();
    Image currentImage;






    SquarePane squarePane;


    private int maxWidth, maxHeight;



    private void savePosFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        File file = fileChooser.showSaveDialog(stage);
        int currentThumbPos = timeslider.thumb.getCurrentValue();
        if (file != null) {
            try {
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                int frame = 0;
                while (frame < timeslider.getFrames().size()) {
                    writer.println(paths.get(frame) + " " + "1 "
                            + squarePane.square.x1 + " "
                            + squarePane.square.x2 + " "
                            + squarePane.square.getWidth() + " "
                            + squarePane.square.getHeight());
                    timeslider.setFrame(frame++);
                }
                writer.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            timeslider.thumb.setCurrentValue(currentThumbPos);
        }


    }


    private void saveKeys(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                for (Map.Entry<Integer, SquareCoord> ent : timeslider.getKeyframes().entrySet()) {
                    writer.println(ent.getKey()+";"+ ent.getValue().lx+" "+ent.getValue().ly+" "+ent.getValue().rx+" "+ent.getValue().ry);
                }
                writer.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }


    private void loadKeys(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Keys");
        File file = fileChooser.showOpenDialog(stage);
        try (Stream<String> files = Files.lines(file.toPath())) {
            if (files != null) {
                timeslider.clearKeys();
                files.forEach(e -> {
                    String line[] = e.split(";");
                    int keyFrame = 0;
                    try {keyFrame = Integer.parseInt(line[0]);}
                    catch (NumberFormatException nes) {
                        System.out.println("Error in key frame format. Must be integer");
                    }
                    int[] coords = new int[4];
                    String[] coordStr = line[1].split(" ");
                    for (int i = 0; i < coordStr.length; i++) {
                        try {coords[i] = Integer.parseInt(coordStr[i]);}
                        catch (NumberFormatException nex) {
                            System.out.println("Error in square coordinates format. Must be integer");
                        }
                    }
                    if (keyFrame > 0) {
                        timeslider.addKeyFrame(keyFrame, new SquareCoord(coords[0], coords[1],coords[0] + coords[2], coords[1] + coords[3]));
                    }
                });
            }
        }
        catch (IOException ioex) {
            System.out.println("IOException: Unable to read keys file. Its not exist or access denied");
        }


    }


    private void loadImages (Stage stage) {

        paths = getPaths(stage);

        Stream<Path> stream = paths.stream();
        stream.filter(file -> validatePath(file));

        paths.forEach(p -> {
            Image img = new Image("file:" + p);
            System.out.println("IMG " + img);
            images.put(p, img);
            if (img.getHeight() > maxHeight)  maxHeight = (int)img.getHeight();
            if (img.getWidth() > maxWidth)  maxWidth = (int)img.getWidth();
        });
        System.out.println("Loaded: " + paths.size());
        timeslider.setThumbMaxValue(paths.size()-1);
        timeslider.thumb.setCurrentValue(1);

    }

    public static void main(String[] args) {

        launch(args);
    }



    @Override
    public void start(Stage primaryStage) {


        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        menuBar.getMenus().addAll(menuFile);

        MenuItem loadImages = new MenuItem("Load images");
        loadImages.setOnAction(e -> {
                                     loadImages(primaryStage);
                             }
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
        MenuItem savePosFile = new MenuItem("Build pos file");
        savePosFile.setOnAction(e -> {
                                     savePosFile(primaryStage);
                             }
        );
        menuFile.getItems().add(savePosFile);





        Menu editFile = new Menu("Edit");
        menuBar.getMenus().addAll(editFile);
        MenuItem clearKeys = new MenuItem("Clear keys");
        savePosFile.setOnAction(e -> {
                    timeslider.clearKeys();
                }
        );
        editFile.getItems().add(clearKeys);


        timeslider = new Timeslider(this);
        squarePane = new SquarePane(this);
        primaryStage.setTitle("Hello World!");



        StackPane root = new StackPane();

        VBox vbox = new VBox(5);
        vbox.getChildren().add(menuBar);



        squarePane.addToBox(vbox);
        timeslider.addToBox(vbox);
        root.getChildren().add(vbox);
        scene = new Scene(root, 850, 300);
        primaryStage.setScene(scene);
        scene.setCursor(Cursor.HAND);

        primaryStage.show();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()==KeyCode.S) {
                timeslider.setKeyAtThumb();
            }
        });

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()==KeyCode.X) {
                timeslider.nextFrame();
            }
        });

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()==KeyCode.Z) {
                timeslider.prevFrame();
            }
        });



    }








    private boolean validatePath(Path path) {
        //System.out.println(path);
        return path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png");
    }

    private List<Path> getPaths(Stage stage) {
        List<Path> retList = new ArrayList<Path>();
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("c:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(stage);
        System.out.println("selected dir: " +selectedDirectory.getAbsolutePath());

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

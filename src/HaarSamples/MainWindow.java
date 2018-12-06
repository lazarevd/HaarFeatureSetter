package HaarSamples;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    Map<Path, Image> images = new HashMap<Path, Image>();
    Image currentImage;






    SquarePane squarePane;


    private int maxWidth, maxHeight;







    private void loadFiles (List<Path> path) {
        path.forEach(p -> {
            Image img = new Image("file:" + p);
            images.put(p, img);
            if (img.getHeight() > maxHeight)  maxHeight = (int)img.getHeight();
            if (img.getWidth() > maxWidth)  maxWidth = (int)img.getWidth();
        });


    }

    public static void main(String[] args) {

        launch(args);
    }



    @Override
    public void start(Stage primaryStage) {



        timeslider = new Timeslider(this);
        squarePane = new SquarePane(this);
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Load Images");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                paths = getPaths(primaryStage);
                System.out.println("Loaded: " + paths.size());
                Stream<Path> stream = paths.stream();
                stream.filter(file -> validatePath(file));
                loadFiles(paths);
                timeslider.setThumbMaxValue(paths.size()-1);
            }
        });

        StackPane root = new StackPane();


        VBox vbox = new VBox(5);

        vbox.getChildren().addAll(btn);
        squarePane.addToBox(vbox);
        timeslider.addToBox(vbox);
        root.getChildren().add(vbox);
        scene = new Scene(root, 850, 250);
        primaryStage.setScene(scene);
        scene.setCursor(Cursor.HAND);

        primaryStage.show();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode()==KeyCode.S) {
                timeslider.setKey();
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
        System.out.println(selectedDirectory.getAbsolutePath());

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

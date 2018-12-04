package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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

    Map<Integer, SquareCoord> keyframes = new TreeMap<>();

    Square square;


    private int maxWidth, maxHeight;


    class SquareCoord {
        int lx,ly,rx,ry;
        public SquareCoord(int x1, int y1, int x2, int y2) {
            lx = x1;
            ly = y1;
            rx = x2;
            ry = y2;
        }
    }


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


        keyframes.put(10, new SquareCoord(0,0,0,0));

        keyframes.put(50, new SquareCoord(0,0,0,0));

        timeslider = new Timeslider(this);
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
                timeslider.thumb = timeslider.new Thumb(0,0, paths.size()-1, timeslider.SLIDE_LINE_RIGHT);
            }
        });

        StackPane root = new StackPane();


        square = new Square(20, 20, 20, 20);
        BorderPane bordImgViewPlane = new BorderPane();
        Canvas canvas = new Canvas( 128, 128 );
        bordImgViewPlane.setCenter(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();




        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                });


        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                e -> {
            if (square.isPointScale((int)e.getX(), (int)e.getY())) {
                System.out.println("drag " + e.getX() + ":" + e.getY());
                square.x2 = (int)e.getX();
                square.y2 = (int)e.getY();
                square.handleScaleRadius = 10;
            }

            if (square.isPointMove((int)e.getX(), (int)e.getY())) {
                System.out.println("drag " + e.getX() + ":" + e.getY());
                square.x1 = (int)e.getX();
                square.y1 = (int)e.getY();
                square.handleMoveRadius = 10;
            }

        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                e -> {
                        square.handleMoveRadius = 5;
                        square.handleScaleRadius = 5;
                });


        new AnimationTimer()
        {
            final long startNanoTime = System.nanoTime();
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.drawImage(currentImage, 0, 0);
                gc.setStroke(Color.WHITE);
                gc.strokeLine(square.x1, square.y1, square.x1, square.y1+square.getHeight());
                gc.strokeLine(square.x1, square.y1+square.getHeight(), square.x1+square.getWidth(), square.y1+square.getHeight());
                gc.strokeLine(square.x1+square.getWidth(), square.y1+square.getHeight(), square.x1+square.getWidth(), square.y1);
                gc.strokeLine(square.x1+square.getWidth(), square.y1, square.x1, square.y1);
                gc.strokeOval(square.x1-square.handleMoveRadius/2, square.y1 - square.handleMoveRadius/2, square.handleMoveRadius, square.handleMoveRadius);
                gc.strokeOval(square.x1+square.getWidth()-square.handleScaleRadius/2, square.y1+square.getHeight() - square.handleScaleRadius/2, square.handleScaleRadius, square.handleScaleRadius);


            }
        }.start();



        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(bordImgViewPlane, btn);
        timeslider.addToBox(vbox);
        root.getChildren().add(vbox);
        scene = new Scene(root, 850, 250);
        primaryStage.setScene(scene);
        scene.setCursor(Cursor.HAND);

        primaryStage.show();
    }





    class Square {
        int handleMoveRadius = 3;
        int handleScaleRadius = 3;
        int x1, y1, x2, y2;
        private Square(int ix1, int iy1, int w, int h) {
        x1 = ix1;
        y1 = iy1;
        x2 = ix1+w;
        y2 = iy1+h;
    }

        public int getWidth() {
            return Math.abs(x1-x2);
        }

        public int getHeight() {
            return Math.abs(y1-y2);
        }

        public boolean isPointMove(int x, int y) {
            return  (x > this.x1-handleMoveRadius && x < this.x1+handleMoveRadius && y > this.y1-handleMoveRadius && y < this.y1+handleMoveRadius)?true:false;
        }

        public boolean isPointScale(int x, int y) {
            return  (x > this.x2-handleScaleRadius && x < this.x2+handleScaleRadius && y > this.y2-handleScaleRadius && y < this.y2+handleScaleRadius)?true:false;
        }
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

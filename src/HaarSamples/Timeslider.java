package HaarSamples;

import HaarSamples.MainWindow;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.nio.file.Path;
import java.util.Map;


public class Timeslider {

    MainWindow mainWindow;
    BorderPane bordTimeSlaiderPane;
    GraphicsContext canvasGc;
    VBox vbox;

    final double SLIDE_LINE_RIGHT = 600.0;








    Thumb thumb;

    public Timeslider(MainWindow mainWindow) {

        this.mainWindow = mainWindow;
        thumb = new Thumb(mainWindow,0,0, 100, SLIDE_LINE_RIGHT);

        vbox = new VBox();

        Button nextButton = new Button("next");
        nextButton.setOnAction(e -> {
            int val = thumb.getCurrentValue();
            if (val < thumb.maxValue) {
                thumb.setCurrentValue(val+1);
            }
        });

        Button prevButton = new Button("prev");
        prevButton.setOnAction(e -> {

            int val = thumb.getCurrentValue();
            if (val > 0) {
                thumb.setCurrentValue(val-1);
            }
        });

        Button setKeyButton = new Button("key");
        setKeyButton.setOnAction(e -> {
            mainWindow.addKeyFrame(new Integer(thumb.getCurrentValue()), mainWindow.new SquareCoord(mainWindow.square.x1, mainWindow.square.y1, mainWindow.square.x2, mainWindow.square.y2));
        });


        HBox hboxButtons = new HBox();
        hboxButtons.getChildren().addAll(prevButton, nextButton, setKeyButton);


        thumb = new Thumb(mainWindow, 0,0, 100, SLIDE_LINE_RIGHT);
        bordTimeSlaiderPane = new BorderPane();
        Canvas timeSliderCanvas = new Canvas(600, 50);
        bordTimeSlaiderPane.setCenter(timeSliderCanvas);
        canvasGc = timeSliderCanvas.getGraphicsContext2D();

        vbox.getChildren().addAll(bordTimeSlaiderPane, hboxButtons);


        timeSliderCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                e -> {
                    processSliderMoving(e);
                    thumb.handleMoveRadius = 20.0;
                });

        timeSliderCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                e -> {
                    processSliderMoving(e);
                    thumb.handleMoveRadius = 10.0;
                });

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                canvasGc.setLineWidth(2);
                canvasGc.setStroke(Color.BLACK);
                canvasGc.clearRect(0, 0, timeSliderCanvas.getWidth(), timeSliderCanvas.getHeight());
                canvasGc.strokeLine(0, 10, SLIDE_LINE_RIGHT, 10);
                canvasGc.strokeOval(thumb.getX(), 5, 5, 10);

                for (Map.Entry<Integer, MainWindow.SquareCoord> entry : mainWindow.keyframes.entrySet()) {
                    canvasGc.setStroke(Color.GREEN);
                    canvasGc.setLineWidth(5);
                    canvasGc.strokeLine(thumb.convertValueToSlidePos(entry.getKey().intValue()), 0,thumb.convertValueToSlidePos(entry.getKey().intValue()),10);
                }
            }
        }.start();
    }

    public void setThumbMaxValue(int max) {
        this.thumb.maxValue = max;
    }


    public void addToBox(Pane box) {
        box.getChildren().add(vbox);
    }


    private void processSliderMoving(MouseEvent e) {

        if (thumb.getX() >= 0 && thumb.getX() <= SLIDE_LINE_RIGHT) thumb.setX(e.getX());
        //}
        if (thumb.getX() > SLIDE_LINE_RIGHT) thumb.setX(SLIDE_LINE_RIGHT);
        if (thumb.getX() < 0) thumb.setX(0);
    }



}

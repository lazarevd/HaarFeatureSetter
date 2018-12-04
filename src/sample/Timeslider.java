package sample;

import javafx.animation.AnimationTimer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
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





    class Thumb {
        double handleMoveRadius = 10.0;
        SimpleDoubleProperty x = new SimpleDoubleProperty();
        double y;
        int maxValue;
        double maxPosition;

        public Thumb(double ix, double iy, int imaxValue, double imaxPosition) {
            x.set(ix);
            y = iy;
            maxValue = imaxValue;
            maxPosition = imaxPosition;
            x.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    Path path = mainWindow.paths.get(thumb.getCurrentValue());
                    mainWindow.currentImage = mainWindow.images.get(path);
                }
            });
        }

        public int getCurrentValue() {
            return (int)Math.round(x.getValue()*((double)maxValue/maxPosition));
        }



        public boolean isPointInside(double ix, double iy) {
            return (ix > x.getValue()-handleMoveRadius && ix < x.getValue()+handleMoveRadius && iy > y-handleMoveRadius && iy < y+handleMoveRadius)?true:false;
        }
    }


    Thumb thumb;

    public Timeslider(MainWindow mainWindow) {

        this.mainWindow = mainWindow;

        vbox = new VBox();

        Button nextButton = new Button("next");
        nextButton.setOnAction(e -> {
            int val = thumb.getCurrentValue();
            if (val < thumb.maxValue) {
                thumb.x.setValue(convertValueToSlidePos(val+1));
            }
            System.out.println("cur val: " + thumb.getCurrentValue() + " thumb.x: " + thumb.x);
        });

        Button prevButton = new Button("prev");
        prevButton.setOnAction(e -> {

            int val = thumb.getCurrentValue();
            if (val > 0) {
                thumb.x.setValue(convertValueToSlidePos(val-1));
            }
            System.out.println("cur val: " + thumb.getCurrentValue() + " thumb.x: " + thumb.x);
        });

        HBox hboxButtons = new HBox();
        hboxButtons.getChildren().addAll(prevButton, nextButton);


        thumb = new Thumb(0,0, 100, SLIDE_LINE_RIGHT);
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
                canvasGc.setStroke(Color.BLACK);
                canvasGc.clearRect(0, 0, timeSliderCanvas.getWidth(), timeSliderCanvas.getHeight());
                canvasGc.strokeLine(0, 5, SLIDE_LINE_RIGHT, 5);
                canvasGc.strokeOval(thumb.x.getValue(), 0, 10, 10);

                for (Map.Entry<Integer, MainWindow.SquareCoord> entry : mainWindow.keyframes.entrySet()) {
                    canvasGc.setStroke(Color.GREEN);
                    canvasGc.strokeLine(convertValueToSlidePos(entry.getKey().intValue()), 0,convertValueToSlidePos(entry.getKey().intValue()),5);
                }
            }
        }.start();
    }

    public void addToBox(Pane box) {
        box.getChildren().add(vbox);
    }


    private void processSliderMoving(MouseEvent e) {
        //if (thumb.isPointInside(e.getX(), e.getY())) {
            System.out.println("x: " + thumb.x + " cur e: " + e.getX() + " val:" + thumb.getCurrentValue() + " val to pos: " + convertValueToSlidePos(thumb.getCurrentValue()));
            if (thumb.x.getValue() >= 0 && thumb.x.getValue() <= SLIDE_LINE_RIGHT) thumb.x.setValue(e.getX());
        //}
        if (thumb.x.getValue() > SLIDE_LINE_RIGHT) thumb.x.setValue(SLIDE_LINE_RIGHT);
        if (thumb.x.getValue() < 0) thumb.x.setValue(0);
    }

    private double convertValueToSlidePos(int value) {
        return value*thumb.maxPosition/(double)thumb.maxValue;
    }

}

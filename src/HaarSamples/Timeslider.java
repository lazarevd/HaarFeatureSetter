package HaarSamples;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;

import java.util.Map;
import java.util.TreeMap;


public class Timeslider {

    MainWindow mainWindow;
    BorderPane bordTimeSliderPane;
    GraphicsContext canvasGc;
    VBox vbox;



    final double SLIDE_LINE_RIGHT = 600.0;

    private Map<Integer, SquareCoord> keyframes = new TreeMap<>();
    private Map<Integer, SquareCoord> frames;

    TextField currentKey;



    Thumb thumb;

    public Timeslider(MainWindow mainWindow) {

        this.mainWindow = mainWindow;
        thumb = new Thumb(mainWindow,0,0, 100, SLIDE_LINE_RIGHT);
        frames = new TreeMap<>();
        vbox = new VBox();

        addKeyFrame(0, new SquareCoord(5,5,30,30));
        addKeyFrame(50, new SquareCoord(40,40,50,50));
        addKeyFrame(100, new SquareCoord(60,10,70,50));
        addKeyFrame(350, new SquareCoord(5,5,30,30));


        Button nextButton = new Button("next");
        nextButton.setOnAction(e -> {
            int val = thumb.getCurrentValue();
            if (val < thumb.maxValue) {
                nextFrame();
            }
        });

        Button prevButton = new Button("prev");
        prevButton.setOnAction(e -> {

            int val = thumb.getCurrentValue();
            if (val > 0) {
                prevFrame();
                thumb.setCurrentValue(val-1);
            }
        });

        Button setKeyButton = new Button("key");
        setKeyButton.setOnAction(e -> {
            setKeyAtThumb();
        });


        currentKey = new TextField();
        currentKey.textProperty().bindBidirectional(thumb.currentValue, new NumberStringConverter());


        HBox hboxButtons = new HBox();
        hboxButtons.getChildren().addAll(prevButton, nextButton, setKeyButton, currentKey);


        thumb = new Thumb(mainWindow, 0,0, 100, SLIDE_LINE_RIGHT);
        bordTimeSliderPane = new BorderPane();
        Canvas timeSliderCanvas = new Canvas(600, 50);
        bordTimeSliderPane.setCenter(timeSliderCanvas);
        canvasGc = timeSliderCanvas.getGraphicsContext2D();

        vbox.getChildren().addAll(bordTimeSliderPane, hboxButtons);


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
                canvasGc.strokeLine(thumb.getX(), 5, thumb.getX(), -5);
                canvasGc.strokeOval(thumb.getX(), 5, 5, 5);

                for (Map.Entry<Integer, SquareCoord> entry : keyframes.entrySet()) {

                    if(entry.getKey().equals(thumb.getCurrentValue())) {
                        canvasGc.setStroke(Color.RED);
                    } else {
                        canvasGc.setStroke(Color.GREEN);
                    }
                    canvasGc.setLineWidth(3);
                    canvasGc.strokeLine(thumb.convertValueToSlidePos(entry.getKey().intValue()), 0,thumb.convertValueToSlidePos(entry.getKey().intValue()),10);
                }
            }
        }.start();
    }

    public TreeMap<Integer, SquareCoord> getKeyframes() {
        return new TreeMap<>(keyframes);
    }

    public SquareCoord getKeyframe(int key) {
        return keyframes.get(key);
    }


    public void addKeyFrame(Integer key, SquareCoord squareCoord) {
        keyframes.put(key, squareCoord);
        frames = genInterpolatedCoords(getKeyframes());
    }

    public void removeKey(Integer key) {
        keyframes.remove(key);
        frames = genInterpolatedCoords(getKeyframes());
    }

    public void clearKeys() {
        System.out.println("Clear " + keyframes.size());
        keyframes.clear();
        System.out.println("Cleared " + keyframes.size());
        frames = genInterpolatedCoords(getKeyframes());
    }

    public void nextFrame() {
        thumb.setCurrentValue(thumb.getCurrentValue()+1);
    }

    public void setFrame(int frame) {
        thumb.setCurrentValue(frame);
    }

    public void prevFrame() {
        thumb.setCurrentValue(thumb.getCurrentValue()-1);
    }

    public void setKeyAtThumb() {
        addKeyFrame(new Integer(thumb.getCurrentValue()), new SquareCoord(mainWindow.squarePane.square.x1, mainWindow.squarePane.square.y1, mainWindow.squarePane.square.x2, mainWindow.squarePane.square.y2));
    }


    public void setThumbMaxValue(int max) {
        this.thumb.maxValue = max;
    }


    public void addToBox(Pane box) {
        box.getChildren().add(vbox);
    }


    private void processSliderMoving(MouseEvent e) {

        double x = e.getX();

        if (x >= 0 && x < SLIDE_LINE_RIGHT) thumb.setX(x);
        //}
        if (thumb.getX() > SLIDE_LINE_RIGHT) thumb.setX(SLIDE_LINE_RIGHT);
        if (thumb.getX() < 0) thumb.setX(0);
    }

    protected void setSquarePaneFromKey(SquarePane squarePane, int key) {

        SquareCoord sCoord = frames.get(key);

        if (sCoord!=null) {
            mainWindow.squarePane.square.x1 = sCoord.lx;
            mainWindow.squarePane.square.y1 = sCoord.ly;
            mainWindow.squarePane.square.x2 = sCoord.rx;
            mainWindow.squarePane.square.y2 = sCoord.ry;
        }
    }



    Map<Integer, SquareCoord> genInterpolatedCoords(TreeMap<Integer, SquareCoord> keyframes) {

        Map<Integer, SquareCoord> ret = new TreeMap<Integer, SquareCoord>();

        //for empty list
        frames.clear();
        System.out.println(mainWindow.squarePane.square);

        if (keyframes.size() < 1) {
            for (int i = 0; i < frames.size(); i++) {
                ret.put(i, new SquareCoord(
                        mainWindow.squarePane.square.x1,
                        mainWindow.squarePane.square.x2,
                        mainWindow.squarePane.square.y1,
                        mainWindow.squarePane.square.y2));
            }
        } else {

            Map.Entry<Integer, SquareCoord> firstElem = keyframes.entrySet().iterator().next();//it ll be first keyframe (for TreeMapOnly)
            int lastKeyFrame = 0;
            SquareCoord lastCoord = firstElem.getValue();
            for (Map.Entry<Integer, SquareCoord> en : keyframes.entrySet()) {
                ret.putAll(interpolateCoords(lastKeyFrame, en.getKey(), lastCoord, en.getValue()));
                lastKeyFrame = en.getKey();
                lastCoord = en.getValue();
            }
            for (int i = lastKeyFrame; i < frames.size(); i++) {//for last frames without keyframe on the right
                ret.put(i, lastCoord);
            }
        }


        return ret;
    }


    Map<Integer, SquareCoord> interpolateCoords(int stFrame, int endFrame, SquareCoord stCoords, SquareCoord endCoords) {

        int frameCount = endFrame - stFrame;

        Vector lVector = new Vector(stCoords.lx, stCoords.ly, endCoords.lx, endCoords.ly);
        Vector rVector = new Vector(stCoords.rx, stCoords.ry, endCoords.rx, endCoords.ry);

        double lLength = lVector.mag();
        double rLength = rVector.mag();

        double lFrameVecMag = lLength/(double) frameCount;
        double rFrameVecMag = rLength/(double) frameCount;

        Vector lFrameVec = lVector.norm().mul(lFrameVecMag);
        Vector rFrameVec = rVector.norm().mul(rFrameVecMag);

        Map <Integer, SquareCoord> ret = new TreeMap<>();

        int frame = stFrame;
        Vector lTmpVec = new Vector(stCoords.lx, stCoords.ly);
        Vector rTmpVec = new Vector(stCoords.rx, stCoords.ry);
        while (frame < endFrame) {
            ret.put(frame, new SquareCoord((int)lTmpVec.x, (int)lTmpVec.y, (int)rTmpVec.x, (int)rTmpVec.y));
            frame++;
            lTmpVec = lTmpVec.add(lFrameVec);
            rTmpVec = rTmpVec.add(rFrameVec);
        }
        return ret;
    }

    public Map<Integer, SquareCoord> getFrames() {
        return new TreeMap<Integer, SquareCoord>(frames);
    }
}

package HaarSamples;

import HaarSamples.dao.SquareCoord;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;


class Timeslider {

    private final MainWindow mainWindow;
    private BorderPane bordTimeSliderPane;
    private GraphicsContext canvasGc;
    private VBox vbox;



    private final double SLIDE_LINE_RIGHT = 600.0;

    private Map<Integer, SquareCoord> keyframes = new TreeMap<>();
    private Map<Integer, SquareCoord> frames;

    TextField textCurrentKey;
    TextField textCurrentFile;



    Thumb thumb;

    @SuppressWarnings("SpellCheckingInspection")
    public Timeslider(MainWindow mainWindow) {
//TODO НАДО еще сделать загрузку файла с углами, если есть, прежде чем переходить на другой кадр
        this.mainWindow = mainWindow;
        thumb = new Thumb(mainWindow,0,0, 100, SLIDE_LINE_RIGHT);
        frames = new TreeMap<>();
        vbox = new VBox();

        Button nextButton = new Button("next");
        nextButton.setOnAction(e -> {
            saveAngleBboxFile();
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
            }
        });

        Button setKeyButton = new Button("key");
        setKeyButton.setOnAction(e -> {
                    setKey();
    }
        );


        textCurrentKey = new TextField();
        textCurrentKey.setMaxWidth(50);
        textCurrentKey.setOnKeyPressed(ke-> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                int keyValue;
                try {
                    keyValue = Integer.parseInt(textCurrentKey.getText());
                    thumb.setCurrentValue(keyValue);
                } catch (NumberFormatException nex) {
                    System.out.println("not an intiger");
                }

            }
        });

        textCurrentFile = new TextField();
        textCurrentFile.setMinWidth(500);



        HBox hboxButtons = new HBox();
        hboxButtons.getChildren().addAll(prevButton, nextButton, setKeyButton, textCurrentKey, textCurrentFile);


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
                    canvasGc.strokeLine(thumb.convertValueToSlidePos(entry.getKey()), 0,thumb.convertValueToSlidePos(entry.getKey().intValue()),10);
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

    public void removeKeyFrame(Integer key) {
        keyframes.remove(key);
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
        saveAngleBboxFile();
        thumb.setCurrentValue(thumb.getCurrentValue()+1);
        readBboxAndAngleFromFile(textCurrentFile.getText());
    }

    public void setFrame(int frame) {
        thumb.setCurrentValue(frame);
    }

    public void prevFrame() {
        thumb.setCurrentValue(thumb.getCurrentValue()-1);
        setParamsFromFile(textCurrentFile.getText());
    }

    public void setKey() {
        if (MainWindow.mode.equals(MainWindow.Mode.BBOX)) { setKeyBbox();}
        else if (MainWindow.mode.equals(MainWindow.Mode.ANGLE)) {saveAngleBboxFile();}
    }

    public void setKeyBbox() {
        addKeyFrame(thumb.getCurrentValue(), new SquareCoord(mainWindow.squarePane.square.lx, mainWindow.squarePane.square.ly, mainWindow.squarePane.square.rx, mainWindow.squarePane.square.ry));
    }


    private boolean readBboxAndAngleFromFile(String fileName) {
        File saveFile = new File(defineTextFilePath(fileName));
        System.out.println(saveFile);
        if (saveFile != null  && saveFile.exists() && !saveFile.isDirectory()) {
            try (Stream<String> lines = Files.lines(saveFile.toPath())) {
                lines.forEach(e -> {
                    setParamsFromFile(e);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void setParamsFromFile(String paramsString) {
       try {
           String[] params = paramsString.split(";");
           mainWindow.squarePane.square.lx = Integer.parseInt(params[2]);
           mainWindow.squarePane.square.lx = Integer.parseInt(params[3]);
           mainWindow.squarePane.square.lx = Integer.parseInt(params[4]);
           mainWindow.squarePane.square.lx = Integer.parseInt(params[5]);
       } catch (Exception e) {
           System.out.println(e);
       }
    }

    private String defineTextFilePath(String imagePath) {
        String[] pathPart = imagePath.split("\\\\");
        String fileName = pathPart[pathPart.length-1].split("\\.")[0];
        StringBuilder sb = new StringBuilder();
        for (int i =0; i < pathPart.length-1;i++) {
            sb.append(pathPart[i]+"/");
        }
        sb.append(fileName+".txt");
        return sb.toString();
    }

    private void saveAngleBboxFile() {
        String path = defineTextFilePath(textCurrentFile.getText());
        System.out.println("filename " + path);
        try (PrintWriter writer = new PrintWriter(path, "UTF-8");) {

            writer.println(
                    textCurrentFile.getText() + ";" +
                            mainWindow.squarePane.getAngleLineAngle() + ";" +
                            mainWindow.squarePane.angleLine.baseX + ";" +
                            mainWindow.squarePane.angleLine.baseY
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setThumbMaxValue(int max) {
        System.out.println("mv " + max);
        thumb.maxValue = max;
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
            mainWindow.squarePane.square.lx = sCoord.lx;
            mainWindow.squarePane.square.ly = sCoord.ly;
            mainWindow.squarePane.square.rx = sCoord.rx;
            mainWindow.squarePane.square.ry = sCoord.ry;
        }
    }



    Map<Integer, SquareCoord> genInterpolatedCoords(TreeMap<Integer, SquareCoord> keyframes) {

        Map<Integer, SquareCoord> ret = new TreeMap<>();

        //for empty list
        frames.clear();
        System.out.println(mainWindow.squarePane.square);

        if (keyframes.size() < 1 ) {
            for (int i = 0; i < frames.size(); i++) {
                ret.put(i, new SquareCoord(
                        mainWindow.squarePane.square.lx,
                        mainWindow.squarePane.square.ly,
                        mainWindow.squarePane.square.rx,
                        mainWindow.squarePane.square.ry));
            }
        } else {


            Iterator<Map.Entry<Integer,SquareCoord>> keysIterator = keyframes.entrySet().iterator();

            Map.Entry<Integer, SquareCoord> firstElem = keysIterator.next();//it ll be first keyframe (for TreeMapOnly)

            System.out.println("gen: " + firstElem.getKey());

            for (int i = 0; i < firstElem.getKey(); i ++) {
                ret.put(i, new SquareCoord(firstElem.getValue().lx, firstElem.getValue().ly, firstElem.getValue().rx, firstElem.getValue().ry ));
            }

            int lastKeyFrame = firstElem.getKey();
            SquareCoord lastCoord = firstElem.getValue();
            while (keysIterator.hasNext()) {
                Map.Entry<Integer, SquareCoord> en = keysIterator.next();
                ret.putAll(interpolateCoords(lastKeyFrame, lastCoord, en.getKey(), en.getValue()));
                lastKeyFrame = en.getKey();
                lastCoord = en.getValue();
            }
            for (int i = lastKeyFrame; i < thumb.maxValue; i++) {//for last frames without keyframe on the right
                System.out.println("PUT last " + i);
                ret.put(i, lastCoord);
            }
        }


        return ret;
    }


    Map<Integer, SquareCoord> interpolateCoords(int stFrame, SquareCoord stCoords, int endFrame, SquareCoord endCoords) {


        System.out.println("interpolateCoords " + stFrame + " " + stCoords.toString() + "; " + endFrame + " " + endCoords.toString() + " " + mainWindow.paths.get(endFrame-1));

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
            ret.put(frame-1, new SquareCoord((int)lTmpVec.x, (int)lTmpVec.y, (int)rTmpVec.x, (int)rTmpVec.y));
            System.out.println("put frame: " + (frame-1) + " " + mainWindow.paths.get(frame-1) + (int)lTmpVec.x + " " + (int)lTmpVec.y);
            frame++;
            lTmpVec = lTmpVec.add(lFrameVec);
            rTmpVec = rTmpVec.add(rFrameVec);
        }
        return ret;
    }

    public Map<Integer, SquareCoord> getFrames() {
        return new TreeMap<>(frames);
    }
}

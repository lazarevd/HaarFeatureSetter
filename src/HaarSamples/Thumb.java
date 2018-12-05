package HaarSamples;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import sun.applet.Main;

import java.nio.file.Path;

class Thumb {
    MainWindow mainWindow;
    double handleMoveRadius = 10.0;
    private SimpleDoubleProperty x = new SimpleDoubleProperty();
    private SimpleIntegerProperty currentValue = new SimpleIntegerProperty();
    double y;
    int maxValue;
    double maxPosition;

    public Thumb(MainWindow mainW, double ix, double iy, int imaxValue, double imaxPosition) {
        this.mainWindow = mainW;
        x.set(ix);
        y = iy;
        maxValue = imaxValue;
        maxPosition = imaxPosition;
        currentValue.addListener((observable, oldValue, newValue) -> {
            x.setValue(convertValueToSlidePos(currentValue.getValue()));
            Path path = mainWindow.paths.get(getCurrentValue());
            mainWindow.currentImage = mainWindow.images.get(path);
        });

        x.addListener((observable, oldValue, newValue) -> {
            currentValue.setValue(convertSlidePosToValue(x.getValue()));
        });

    }

    public double getX() {
        return this.x.getValue();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public int getCurrentValue() {
        return currentValue.getValue();
    }

    public void setCurrentValue(int val) {
        currentValue.setValue(val);
    }

    public double convertValueToSlidePos(int value) {
        return value*maxPosition/(double)maxValue;
    }

    public int convertSlidePosToValue(double value) {
        return (int)Math.round(value*(double)maxValue/maxPosition);
    }

    public boolean isPointInside(double ix, double iy) {
        return (ix > x.getValue()-handleMoveRadius && ix < x.getValue()+handleMoveRadius && iy > y-handleMoveRadius && iy < y+handleMoveRadius)?true:false;
    }
}

package HaarSamples.dao;

public class SquareCoord {
    public int angle;
    public int lx;
    public int ly;
    public int rx;
    public int ry;
    public SquareCoord(int x1, int y1, int x2, int y2) {
        lx = x1;
        ly = y1;
        rx = x2;
        ry = y2;
    }

    public int getWidth() {
        return Math.abs(rx - lx);
    }

    public int getHeight() {
        return Math.abs(ry - ly);
    }

    @Override
    public String toString() {
        return "[l:("+lx+";"+ly+")r:("+rx+";"+ry+")]";
    }
}

package HaarSamples;

public class SquareCoord {
    final int lx;
    int ly;
    int rx;
    int ry;
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

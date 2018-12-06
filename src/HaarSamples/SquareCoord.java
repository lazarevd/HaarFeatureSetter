package HaarSamples;

public class SquareCoord {
    int lx,ly,rx,ry;
    public SquareCoord(int x1, int y1, int x2, int y2) {
        lx = x1;
        ly = y1;
        rx = x2;
        ry = y2;
    }

    @Override
    public String toString() {
        return "[l:("+lx+";"+ly+")r:("+rx+";"+ry+")]";
    }
}

package HaarSamples;

public class Vector {

    public double x = 0.0;
    public double y = 0.0;

    public Vector() {

    }



    public Vector(double x, double y) {
        this.x=x;
        this.y=y;
    }



    public Vector(double x1, double y1, double x2, double y2) {
        this.x= x2 - x1;
        this.y= y2 - y1;
    }

    public double mag() {
        return Math.sqrt(x*x + y*y);
    }

    public Vector norm() {
        double mag = mag();
        Vector ret = new Vector(x/mag, y/mag);
        return ret;

    }

    public Vector mul (double k) {
        return new Vector(x * k, y * k);
    }


    public Vector add(Vector vector) {
        return new Vector(x+vector.x, y + vector.y);
    }

    @Override
    public String toString() {
        return "["+x+";"+y+"]";
    }
}

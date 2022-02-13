public class Point2D {
    public double x, y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D toCanvasPoint(int Cw, int Ch) {
        double x = this.x - 0.5 * (double) Cw;
        double y = 0.5 * (double) Ch - this.y;

        return new Point2D(x, y);
    }

    public Point3D toViewportPoint(int Cw, int Ch, int Vw, int Vh, int d) {
        double x = this.x * (double) Vw / (double) Cw;
        double y = this.y * (double) Vh / (double) Ch;

        return new Point3D(x, y, d);
    }
}

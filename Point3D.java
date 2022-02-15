/** Represents a Point3D in a 3D space. */
public class Point3D {
    public double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Returns the direction of the vector through the given 3D points. */
    public Vector3D Direction(Point3D other) {
        return new Vector3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Point3D add(Vector3D vector) {
        return new Point3D(this.x + vector.x, this.y + vector.y, this.z + vector.z);
    }
}
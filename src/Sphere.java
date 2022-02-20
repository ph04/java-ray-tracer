public class Sphere {
    public Point3D center;
    public CustomColor color;
    public double radius, specular, reflective;

    public Sphere(Point3D center, CustomColor color, double radius, double specular, double reflective) {
        this.center = center;
        this.color = color;
        this.radius = radius;
        this.specular = specular;
        this.reflective = reflective;
    }

    public double RayIntersection(Ray ray) {
        double r = this.radius;

        Point3D O = ray.origin;
        Vector3D D = ray.direction;

        Vector3D OC = O.Direction(this.center);

        double a = D.DotProduct(D);
        double b = 2 * OC.DotProduct(D);
        double c = OC.DotProduct(OC) - r * r;

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            double denominator = 2 * a;

            double sqrt = Math.sqrt(discriminant);

            double t1 = (-b - sqrt) / denominator;
            double t2 = (-b + sqrt) / denominator;

            if (t1 < 0) {
                t1 = Double.POSITIVE_INFINITY;
            } else if (t2 < 0) {
                t2 = Double.POSITIVE_INFINITY;
            }

            if (t1 < t2) {
                return t1;
            } else {
                return t2;
            }
        }
    }
}
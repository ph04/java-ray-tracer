import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Engine {
    public CustomColor bg;
    public int Cw, Ch, Vw, Vh, d, recursion_depth;
    public double epsilon;
    public Scene scene;
    private Point3D O;
    private Sphere closest_sphere;

    public Engine(CustomColor bg, int Cw, int Ch, int Vw, int Vh, int d, int recursion_depth, Scene scene) {
        this.bg = bg;

        this.Cw = Cw;
        this.Ch = Ch; 

        this.Vw = Vw;
        this.Vh = Vh;
        this.d = d;

        this.epsilon = 0.001;

        this.recursion_depth = recursion_depth;

        this.scene = scene;

        this.closest_sphere = null;

        this.O = new Point3D(0.0, 0.0, 0.0);
    }

    public double ComputeLighting(Point3D P, Vector3D N, Vector3D V, double specular) {
        double i = 0.0;

        for (AmbientLight ambient_light : this.scene.ambient_lights) {
            i += ambient_light.intensity;
        }

        for (LightPoint light_point : this.scene.light_points) {
            Vector3D L = light_point.position.Direction(P);

            double n_dot_l = N.DotProduct(L);

            // shadow
            this.ComputeClosestIntersection(P, L, this.epsilon, 1);

            if (this.closest_sphere != null) {
                continue;
            }
            
            // diffuse lighting
            if (n_dot_l > 0) { // avoid adding lights illuminating the back side of the surface
                i += light_point.intensity * n_dot_l / (N.Magnitude() * L.Magnitude());
            }

            // specular lighting
            if (specular != -1) { // only apply this to shiny objects
                Vector3D R = L.ReflectVector(N);

                double r_dot_v = R.DotProduct(V);

                if (r_dot_v > 0) { // same reason
                    i += light_point.intensity * Math.pow(r_dot_v / (R.Magnitude() * V.Magnitude()), specular);
                }
            }
        }

        for (DirectionalLight directional_light : this.scene.directional_lights) {
            Vector3D L = directional_light.direction;

            double n_dot_l = N.DotProduct(L);

            // shadow
            this.ComputeClosestIntersection(P, L, this.epsilon, Double.POSITIVE_INFINITY);

            if (this.closest_sphere != null) {
                continue;
            }

            // diffuse lighting
            if (n_dot_l > 0) { // avoid adding lights illuminating the back side of the surface
                i += directional_light.intensity * n_dot_l / (N.Magnitude() * L.Magnitude());
            }

            // specular lighting
            if (specular != -1) { // only apply this to shiny objects
                Vector3D R = L.ReflectVector(N);

                double r_dot_v = R.DotProduct(V);

                if (r_dot_v > 0) { // same reason
                    i += directional_light.intensity * Math.pow(r_dot_v / (R.Magnitude() * V.Magnitude()), specular);
                }
            }
        }

        return i;
    }

    public double ComputeClosestIntersection(Point3D point, Vector3D D, double t_min, double t_max) {
        double closest_t = Double.POSITIVE_INFINITY;
        this.closest_sphere = null;

        for (Sphere sphere : this.scene.spheres) {
            // intersect the ray with the sphere
            Ray OD = new Ray(point, D);
            double t = sphere.RayIntersection(OD);

            // find the closest intersection with the closest
            // sphere to find the right pixel color
            if (t > t_min && t < t_max && t < closest_t) {
                closest_t = t;
                this.closest_sphere = sphere;
            }
        }

        return closest_t;
    }

    public CustomColor TraceRay(Point3D point, Vector3D D, int depth, double t_min, double t_max) {
        double closest_t = this.ComputeClosestIntersection(point, D, t_min, t_max);
        Sphere closest_sphere = this.closest_sphere;

        // if there were no intersections
        if (closest_sphere == null) {
            return this.bg;
        } else {
            Point3D P = point.add(D.scale(closest_t)); // intersection point between the ray and the sphere
            Vector3D N = (P.Direction(closest_sphere.center)).Normal(); // normal passing through P

            CustomColor local_color = closest_sphere.color.scale(this.ComputeLighting(P, N, D.neg(), closest_sphere.specular));

            double r = closest_sphere.reflective;

            // if recursion depth limit is hit
            // or the object is not reflective
            if (depth == 0 || r <= 0) {
                return local_color;
            } else {
                Vector3D R = D.neg().ReflectVector(N);

                CustomColor reflected_color = this.TraceRay(P, R, depth - 1, this.epsilon, Double.POSITIVE_INFINITY);

                return local_color.scale((1 - r)).add(reflected_color.scale(r));
            }
        }
    }

    public void RenderScene(String filename) {
        CustomColor[][] pixels = new CustomColor[Ch][Cw];

        for (int Sy = 0; Sy < this.Ch; Sy++) {
            for (int Sx = 0; Sx < this.Cw; Sx++) {
                Point2D screen_point = new Point2D(Sx, Sy);
                Point2D canvas_point = screen_point.toCanvasPoint(this.Cw, this.Ch);

                Point3D V = canvas_point.toViewportPoint(this.Cw, this.Ch, this.Vw, this.Vh, d);

                Vector3D D = V.Direction(this.O);

                pixels[Sy][Sx] = this.TraceRay(this.O, D, this.recursion_depth, 1, Double.POSITIVE_INFINITY);
            }
        }

        BufferedImage image = new BufferedImage(Cw, Ch, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < Ch; y++) {
            for (int x = 0; x < Cw; x++) {
                image.setRGB(x, y, pixels[y][x].RGBModel);
            }
        }

        File file = new File(filename);

        // FIXME: should be moved into main
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Engine {
    public CustomColor bg;
    public int Cw, Ch, Vw, Vh, d;
    public double t_min, t_max;
    public Scene scene;
    private Point3D O;

    public Engine(CustomColor bg, int Cw, int Ch, int Vw, int Vh, int d, double t_min, double t_max, Scene scene) {
        this.bg = bg;

        this.Cw = Cw;
        this.Ch = Ch; 

        this.Vw = Vw;
        this.Vh = Vh;
        this.d = d;

        this.t_min = t_min;
        this.t_max = t_max;

        this.scene = scene;

        this.O = new Point3D(0.0, 0.0, 0.0);
    }

    public double ComputeLighting(Point3D P, Vector3D N) {
        double i = 0.0;

        for (AmbientLight ambient_light : this.scene.ambient_lights) {
            i += ambient_light.intensity;
        }

        for (LightPoint light_point : this.scene.light_points) {
            Vector3D L = light_point.position.Direction(P);

            double n_dot_l = N.DotProduct(L);

            if (n_dot_l > 0) { // avoid adding lights illuminating the back side of the surface
                i += light_point.intensity * n_dot_l / (N.Magnitude() * L.Magnitude());
            }
        }

        for (DirectionalLight directional_light : this.scene.directional_lights) {
            Vector3D L = directional_light.direction;

            double n_dot_l = N.DotProduct(L);

            if (n_dot_l > 0) { // avoid adding lights illuminating the back side of the surface
                i += directional_light.intensity * n_dot_l / (N.Magnitude() * L.Magnitude());
            }
        }

        return i;
    }

    public CustomColor TraceRay(Vector3D D) {
        double closest_t = Double.POSITIVE_INFINITY;
        Sphere closest_sphere = null;

        for (Sphere sphere : this.scene.spheres) {
            // intersect the ray with the sphere
            Ray OD = new Ray(this.O, D);
            double t = sphere.RayIntersection(OD);

            // find the closest intersection with the closest
            // sphere to find the right pixel color
            if (t > this.t_min && t < this.t_max && t < closest_t) {
                closest_t = t;
                closest_sphere = sphere;
            }
        }

        // if there were no intersections
        if (closest_sphere == null) {
            return this.bg;
        } else {
            Point3D P = O.add(D.scale(closest_t)); // intersection point between the ray and the sphere
            Vector3D N = (P.Direction(closest_sphere.center)).Normal(); // normal passing through P

            return closest_sphere.color.scale(this.ComputeLighting(P, N));
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

                pixels[Sy][Sx] = this.TraceRay(D);
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

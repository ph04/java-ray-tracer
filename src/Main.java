public class Main {
    public static void main(String[] args) {
        Sphere sphere1 = new Sphere(new Point3D(0.0, -1.0, 3.0), new CustomColor(255, 0, 0), 1.0, 500.0, 0.2);
        Sphere sphere2 = new Sphere(new Point3D(2.0, 0.0, 4.0), new CustomColor(0, 0, 255), 1.0, 500.0, 0.3);
        Sphere sphere3 = new Sphere(new Point3D(-2.0, 0.0, 4.0), new CustomColor(0, 255, 0), 1.0, 10.0, 0.4);
        Sphere sphere4 = new Sphere(new Point3D(0.0, -5001.0, 0.0), new CustomColor(255, 255, 0), 5000.0, 1000.0, 0.5);

        AmbientLight ambient_light = new AmbientLight(0.2);
        LightPoint light_point = new LightPoint(0.6, new Point3D(2.0, 1.0, 0.0));
        DirectionalLight directional_light = new DirectionalLight(0.2, new Vector3D(1.0, 4.0, 4.0));

        Scene scene = new Scene(
            new Sphere[]{sphere1, sphere2, sphere3, sphere4},
            new AmbientLight[]{ambient_light},
            new LightPoint[]{light_point},
            new DirectionalLight[]{directional_light}
        );

        // CustomColor bg = new CustomColor(255, 255, 255);
        CustomColor bg = new CustomColor(0, 0, 0);

        Engine engine = new Engine(bg, 1024, 1024, 1, 1, 1, 3, scene);

        engine.RenderScene("out.png");
    }
}
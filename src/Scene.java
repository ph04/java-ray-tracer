public class Scene {
    public Sphere[] spheres; // currently only supports spheres because they are easy to do the math with
    public AmbientLight[] ambient_lights;
    public LightPoint[] light_points;
    public DirectionalLight[] directional_lights;

    public Scene(Sphere[] spheres, AmbientLight[] ambient_lights, LightPoint[] light_points, DirectionalLight[] directional_lights) {
        this.spheres = spheres;
        this.ambient_lights = ambient_lights;
        this.light_points = light_points;
        this.directional_lights = directional_lights;
    }
}

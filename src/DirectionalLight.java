public class DirectionalLight {
    public double intensity;
    public Vector3D direction;

    public DirectionalLight(double intensity, Vector3D direction) {
        this.intensity = intensity;
        this.direction = direction;
    }
}
import java.awt.Color;

public class CustomColor {
    public int r, g, b, RGBModel;

    private static int clamp(int x) {
        if (x < 0) {
            return 0;
        } else if (x > 255) {
            return 255;
        } else {
            return x;
        }
    }

    public CustomColor(int r, int g, int b) {
        this.r = CustomColor.clamp(r);
        this.g = CustomColor.clamp(g);
        this.b = CustomColor.clamp(b);

        this.RGBModel = new Color(r, g, b).getRGB();
    }

    public CustomColor scale(double scalar) {
        return new CustomColor(
            (int) Math.round((double) this.r * scalar),
            (int) Math.round((double) this.g * scalar),
            (int) Math.round((double) this.b * scalar)
        );
    }
}

package ch.k42.metropolis.plugin;

/**
 * Created by aaronbrewer on 2/4/14.
 */
public class ContextConfig {
    private double scale = 1.0D;
    private double scatterScale = 0.5D;
    private double scatterAmount = 0.5D;
    private double mainScale = 0.01D;
    private double recurseScale = 5.0D;

    public ContextConfig(){}

    public ContextConfig(double scale, double scatterScale, double mainScale, double recurseScale) {
        this.scale = scale;
        this.scatterScale = scatterScale;
        this.mainScale = mainScale;
        this.recurseScale = recurseScale;
    }

    public double getScale(int level) {
        double levelScale = recurseScale * level;
        return ((levelScale * mainScale) * scale);
    }

    public double getScatterScale() {
        return scatterScale * scale;
    }

    public double getScatterAmount() {
        return scatterAmount;
    }
}

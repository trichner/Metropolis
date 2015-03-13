package ch.k42.metropolis.grid.urbanGrid.context;


import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import com.google.gson.annotations.Expose;

import java.util.Arrays;

/**
 * Created by aaronbrewer on 2/4/14.
 */
public class ContextConfig {

    @Expose
    private double scale = 0.2D;
    @Expose
    private double scatterScale = 0.2D;
    @Expose
    private double scatterAmount = 5D;
    @Expose
    private double mainScale = 0.01D;
    @Expose
    private double recurseScale = 10.0D;
    @Expose
    private ContextZone[] contextZones = {
        new ContextZone(ContextType.RESIDENTIAL, 1),
        new ContextZone(new ContextZone[] {
                new ContextZone(ContextType.LOWRISE, 3),
                new ContextZone(ContextType.INDUSTRIAL, 2)
        }, 1),
        new ContextZone(ContextType.MIDRISE, 1),
        new ContextZone(ContextType.HIGHRISE, 1)
    };

    public ContextConfig(){}

    public double getScale(int level) {
        double levelScale = recurseScale * level;
        return ((levelScale * mainScale) * scale);
    }

    public double getScatterScale() {
        return scatterScale;
    }

    public double getScatterAmount() {
        return scatterAmount;
    }

    public ContextZone[] getContextZones() {
        return contextZones;
    }

    @Override
    public String toString() {
        return "ContextConfig{" +
                "scale=" + scale +
                ", scatterScale=" + scatterScale +
                ", scatterAmount=" + scatterAmount +
                ", mainScale=" + mainScale +
                ", recurseScale=" + recurseScale +
                ", contextZones=" + Arrays.toString(contextZones) +
                '}';
    }
}

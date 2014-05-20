package ch.k42.metropolis.grid.urbanGrid.context;

import java.util.Arrays;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import com.google.gson.annotations.Expose;

/**
 * Created by aaronbrewer on 2/4/14.
 */
public class ContextZone {

    @Expose
    private ContextZone[] contextZones;
    @Expose
    private ContextType contextType;
    @Expose
    private double weight;
    private double totalWeight;

    public ContextZone(ContextType contextType, double weight) {
        this.contextType = contextType;
        this.weight = weight;
    }

    public ContextZone(String contextType, double weight) {
        this(ContextType.getByString(contextType), weight);
    }

    public ContextZone(ContextZone[] contextZones, double weight) {
        this.contextZones = contextZones;
        this.weight = weight;
    }

    public ContextType getContextType() {
        return contextType;
    }

    public ContextZone[] getContextZones() {
        return contextZones;
    }

    public void setContextZones(ContextZone[] contextZones) {
        this.contextZones = contextZones;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getNormalizedWeight() {
        return weight / totalWeight;
    }

    public boolean hasChildren() {
        return contextZones != null && contextZones.length > 0;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "ContextZone{" +
                "contextZones=" + Arrays.toString(contextZones) +
                ", contextType=" + contextType +
                ", weight=" + weight +
                ", totalWeight=" + totalWeight +
                '}';
    }
}

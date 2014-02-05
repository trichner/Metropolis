package ch.k42.metropolis.model.zones;

import ch.k42.metropolis.model.enums.ContextType;

import java.util.Arrays;

/**
 * Created by aaronbrewer on 2/4/14.
 */
public class ContextZone {

    private ContextZone[] contextZones;
    private ContextType contextType;
    private boolean hasChildren = false;
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
        this.hasChildren = true;
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
        this.hasChildren = true;
        this.contextZones = contextZones;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getNormalizedWeight() {
        return weight / totalWeight;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "ContextZone{" +
                "contextZones=" + Arrays.toString(contextZones) +
                ", contextType=" + contextType +
                ", hasChildren=" + hasChildren +
                ", weight=" + weight +
                ", totalWeight=" + totalWeight +
                '}';
    }
}

package ch.k42.metropolis.minions;


import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 07.09.13
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class DecayOption {

    private static final double defaultHoleScale = 1.0 / 20.0;
    private static final double defaultLeavesScale = 1.0 / 10.0;
    private static final double defaultFullDecay = 0.5D;
    private static final double defaultPartialDecay = 0.3D;
    private static final double defaultLeavesDecay = 0.1D;

    private static final double defaultDecayIntensity = 1;


    private static final DecayOption defaultOptions = new DecayOption(defaultHoleScale, defaultLeavesScale, defaultFullDecay, defaultPartialDecay, defaultLeavesDecay);

    /**
     * An instance of DecayOption with hardcoded default options
     *
     * @return default decay options
     */
    public static DecayOption getDefaultDecayOptions() {
        return defaultOptions;
    }

    /* Decay Parameters */
    private double holeScale;
    private double leavesScale;
    private double fulldecay;
    private double partialdecay;
    private double leavesdecay;

    private Set<Material> exceptions = new HashSet();

    /**
     * @param holeScale    overall decay amplitude, default 1/20
     * @param leavesScale  scale for leaves, default 1/10
     * @param fulldecay    threshold for full decay, default 0.5
     * @param partialdecay threshold for partial decay, default 0.3
     * @param leavesdecay  threshold for the addition of leaves around the decayed blocks, default 0.1D
     */
    public DecayOption(double holeScale, double leavesScale, double fulldecay, double partialdecay, double leavesdecay) {
        this.holeScale = holeScale;
        this.leavesScale = leavesScale;
        this.fulldecay = fulldecay;
        this.partialdecay = partialdecay;
        this.leavesdecay = leavesdecay;
    }

    /**
     * calculates all the needed parameters out of 1 scale ranging from 0 (no decay) to around 2 (heavy decay), 1 for normal decay
     *
     * @param intensity how intense shall decay be?
     */
    public DecayOption(double intensity) { //TODO Less hardcoded
        if (intensity == 0) { //absolutely no decay
            fulldecay = 1;
            partialdecay = 1;
        } else if (intensity > 0 && intensity <= 1) {
            holeScale = defaultHoleScale;
            leavesScale = defaultLeavesScale;
            fulldecay = 1 - defaultFullDecay * intensity;
            if (fulldecay < -0.8) fulldecay = -0.8;
            partialdecay = fulldecay - 0.2D;
            leavesdecay = defaultLeavesDecay;
        } else {
            //Throw an error! maybe...
        }
    }

    /**
     * calculates all the needed parameters out of 1 scale ranging from 0 (no decay) to around 2 (heavy decay), 1 for normal decay
     *
     * @param intensity  how intense shall decay be?
     * @param exceptions which Materials should be excluded?
     */
    public DecayOption(double intensity, Set<Material> exceptions) { //TODO Less hardcoded
        this.exceptions = exceptions;
        if (intensity == 0) { //absolutely no decay
            fulldecay = 1;
            partialdecay = 1;
        } else if (intensity > 0 && intensity <= 1) {
            holeScale = defaultHoleScale;
            leavesScale = defaultLeavesScale;
            fulldecay = 1 - defaultFullDecay * intensity;
            if (fulldecay < -0.8) fulldecay = -0.8;
            partialdecay = fulldecay - 0.2D;
            leavesdecay = defaultLeavesDecay;
        } else {
            //Throw an error! maybe...
        }
    }

    public double getHoleScale() {
        return holeScale;
    }

    public DecayOption setHoleScale(double holeScale) {
        this.holeScale = holeScale;
        return this;
    }

    public double getLeavesScale() {
        return leavesScale;
    }

    public DecayOption setLeavesScale(double leavesScale) {
        this.leavesScale = leavesScale;
        return this;
    }

    public double getFullDecay() {
        return fulldecay;
    }

    public DecayOption setFulldecay(double fulldecay) {
        this.fulldecay = fulldecay;
        return this;
    }

    public double getPartialDecay() {
        return partialdecay;
    }

    public DecayOption setPartialdecay(double partialdecay) {
        this.partialdecay = partialdecay;
        return this;
    }

    public double getLeavesdecay() {
        return leavesdecay;
    }

    public DecayOption setLeavesdecay(double leavesdecay) {
        this.leavesdecay = leavesdecay;
        return this;
    }


    public static double getDefaultDecayIntensity() {
        return defaultDecayIntensity;
    }

    public Set<Material> getExceptions() {
        return exceptions;
    }

    public void setExceptions(Set<Material> exceptions) {
        this.exceptions = exceptions;
    }
}

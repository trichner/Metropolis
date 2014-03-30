package ch.k42.metropolis.plugin;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 03.10.13
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class PluginConfig {

    private static final String pathDebug = "consoleOutput.debug";
    private static boolean debugOutput = false;

    private static final String pathChestRenaming = "generator.enableChestRenaming";
    private static boolean  chestRenaming = false;

    private static final String pathSpawnerPlacing = "generator.enableSpawnerPlacing";
    private static boolean spawnerPlacing = false;

    private static final String pathCrossContextPlacing = "generator.enableCrossContextPlacing";
    private static boolean crossContextPlacing = false;

    private static final String pathDirectionFallbackPlacing = "generator.enableDirectionFallbackPlacing";
    private static boolean directionFallbackPlacing = false;

    private static final String pathBuildChance = "generator.buildChance";
    private static int buildChance = 80;

    private static final String pathFillerChance = "generator.fillerChance";
    private static int fillerChance = 80;

    private static final String pathIterations = "generator.iterations";
    private static int iterations = 10;

    private static final String pathBlockSize = "generator.blockSize";
    private static int blockSize = 14;

    private static final String pathSigmaCut = "generator.sigmaCut";
    private static int sigmaCut = 6;

    private static final String pathCloneRadius = "generator.cloneRadius";
    private static int cloneRadius = 7;

    private static final String pathBuildHeight = "generator.buildHeight";
    private static int buildHeight = 65;

    private static final String pathCaveChance = "generator.caveChance";
    private static int caveChance = 20;

    private static final String pathNoLaggRelighting = "generator.noLaggRelighting";
    private static boolean  noLaggRelighting = false;

    private PluginConfig() {}

    public static int getCloneRadius() {
        return cloneRadius;
    }

    public static int getBuildChance() {
        return buildChance;
    }

    public static boolean isDebugEnabled() {
        return debugOutput;
    }

    public static boolean isChestRenaming() {
        return chestRenaming;
    }

    public static boolean isSpawnerPlacing() {
        return spawnerPlacing;
    }

    public static boolean allowDirectionFallbackPlacing() {
        return directionFallbackPlacing;
    }

    public static boolean getNoLaggRelighting() {
        return noLaggRelighting;
    }

    public static int getIterations() {
        return iterations;
    }

    public static int getBlockSize() {
        return blockSize;
    }

    public static int getSigmaCut() {
        return sigmaCut;
    }

    public static int getFillerChance() {
        return fillerChance;
    }

    public static int getBuildHeight() {
        return buildHeight;
    }

    public static int getCaveChance() {
        return caveChance;
    }

    public static void loadFromFile(FileConfiguration configFile){
        debugOutput = configFile.getBoolean(pathDebug, debugOutput);
        chestRenaming = configFile.getBoolean(pathChestRenaming, chestRenaming);
        spawnerPlacing = configFile.getBoolean(pathSpawnerPlacing, spawnerPlacing);
        crossContextPlacing = configFile.getBoolean(pathCrossContextPlacing, crossContextPlacing);
        directionFallbackPlacing = configFile.getBoolean(pathDirectionFallbackPlacing, directionFallbackPlacing);
        buildChance = configFile.getInt(pathBuildChance,buildChance);
        fillerChance = configFile.getInt(pathFillerChance,fillerChance);
        iterations = configFile.getInt(pathIterations,iterations);
        blockSize = configFile.getInt(pathBlockSize,blockSize);
        sigmaCut = configFile.getInt(pathSigmaCut,sigmaCut);
        cloneRadius = configFile.getInt(pathCloneRadius,cloneRadius);
        buildHeight = configFile.getInt(pathBuildHeight,buildHeight);
        caveChance = configFile.getInt(pathCaveChance,caveChance);
        noLaggRelighting = configFile.getBoolean(pathNoLaggRelighting, noLaggRelighting);
    }

}

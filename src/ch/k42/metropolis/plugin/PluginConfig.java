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

    private PluginConfig() {}

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

    public static void loadFromFile(FileConfiguration configFile){
        debugOutput = configFile.getBoolean(pathDebug, debugOutput);
        chestRenaming = configFile.getBoolean(pathChestRenaming, chestRenaming);
        spawnerPlacing = configFile.getBoolean(pathSpawnerPlacing, spawnerPlacing);
        crossContextPlacing = configFile.getBoolean(pathCrossContextPlacing, crossContextPlacing);
        directionFallbackPlacing = configFile.getBoolean(pathDirectionFallbackPlacing, directionFallbackPlacing);
        buildChance = configFile.getInt(pathBuildChance,buildChance);
    }

}

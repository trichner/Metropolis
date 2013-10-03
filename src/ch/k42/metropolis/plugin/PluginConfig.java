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

    private static String pathDebug = "consoleOutput.debug";
    private boolean debugOutput = false;

    private static String pathChestRenaming = "generator.enableChestRenaming";
    private boolean chestRenaming = false;

    private static String pathSpawnerPlacing = "generator.enableSpawnerPlacing";
    private boolean spawnerPlacing = false;

    private static String pathCrossContextPlacing = "generator.enableCrossContextPlacing";
    private boolean crossContextPlacing = false;

    //private static String pathDebug = "consoleOutput.debug";

    public PluginConfig(FileConfiguration configFile) {
        this.debugOutput = configFile.getBoolean(pathDebug,debugOutput);
        this.chestRenaming = configFile.getBoolean(pathChestRenaming,chestRenaming);
        this.spawnerPlacing = configFile.getBoolean(pathSpawnerPlacing,spawnerPlacing);
        this.crossContextPlacing = configFile.getBoolean(pathCrossContextPlacing,crossContextPlacing);
    }

    public boolean isDebugEnabled(){
        return debugOutput;
    }

    public boolean isChestRenaming(){
        return chestRenaming;
    }

    public boolean isSpawnerPlacing(){
        return spawnerPlacing;
    }

    public boolean allowCrossContextPlacing(){
        return crossContextPlacing;
    }


}

package ch.k42.metropolis.plugin;

import ch.k42.metropolis.commands.CommandMetropolisFreder;
import ch.k42.metropolis.commands.CommandMetropolisMaria;
import ch.k42.metropolis.generator.MetropolisGenerator;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Main Class for the Metropolis plugin.
 *
 * @author Thomas Richner
 *
 */
public class MetropolisPlugin extends JavaPlugin{


    private MetropolisGenerator generator;
    private PluginConfig config;

    @Override
    public void onDisable() {

        super.onDisable();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onEnable() {
        super.onEnable();    //To change body of overridden methods use File | Settings | File Templates.

        //---- load config

        FileConfiguration configFile = getConfig();
        config = new PluginConfig(configFile);

        //---- add our command
        PluginCommand cmd = getCommand("metropolis");
        cmd.setExecutor(new CommandMetropolisMaria(this));
        cmd = getCommand("freder");
        cmd.setExecutor(new CommandMetropolisFreder(this));
    }
    public MetropolisGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(MetropolisGenerator generator) {
        this.generator = generator;
    }

    public PluginConfig getMetropolisConfig() {
        return config;
    }
}

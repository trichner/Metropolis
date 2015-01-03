package ch.k42.metropolis.plugin;

import ch.k42.metropolis.generator.VaultGenerator;
import ch.k42.metropolis.grid.urbanGrid.clipboard.ClipboardBean;
import ch.k42.metropolis.minions.WeldService;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;


/**
 * Main Class for the Metropolis plugin.
 *
 * @author Thomas Richner
 */
public class MetropolisPlugin extends JavaPlugin {

    private static MetropolisPlugin _instance;

    private WeldService weldService = null;

    @Override
    public void installDDL() {
        super.installDDL();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        // single threaded, no need for lock
        if(weldService==null) {
            weldService = new WeldService();
            weldService.init();
        }

        VaultGenerator generator = weldService.get(VaultGenerator.class);
        generator.setWorld(worldName);

        return generator;
    }


    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> classes = new LinkedList<>();
        classes.add(ClipboardBean.class);
        return classes;
    }

    @Override
    public void onDisable() {
        weldService.shutdown();
        weldService = null;
        super.onDisable();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onEnable() {
        _instance = this;
    }

    public static MetropolisPlugin getInstance() {
        return _instance;
    }
}

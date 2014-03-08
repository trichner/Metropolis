package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardProviderDB implements ClipboardProvider {
    public static class PluginNotFoundException extends Exception{
        public PluginNotFoundException(String message) {
            super(message);
        }
    }
    private static final String WE_PLUGIN = "WorldEdit";
    private static final String SCHEMATIC_FOLDER = "schematics";
    private static final String CACHE_FOLDER = "cache";

    private WorldEdit worldEdit;
    private ClipboardDAO dao;
    private Map<String,Clipboard> clipstore;

    public ClipboardProviderDB() throws PluginNotFoundException {
        //==== First load the plugin
        WorldEditPlugin worldEditPlugin = null;
        PluginManager pm = Bukkit.getServer().getPluginManager();
        worldEditPlugin = (WorldEditPlugin) pm.getPlugin(WE_PLUGIN);

        // not there? darn
        if (worldEditPlugin == null) {
            Bukkit.getLogger().warning("No WorldEdit found!");
            throw new PluginNotFoundException("Couldn't find WorldEdit plugin.");
        }

        // make sure it is enabled
        if (!pm.isPluginEnabled(worldEditPlugin))  pm.enablePlugin(worldEditPlugin);

        this.worldEdit = worldEditPlugin.getWorldEdit();
    }

    @Override
    public void loadClips(MetropolisPlugin plugin) throws FileNotFoundException {
        dao = new ClipboardDAO(plugin);
        File schematicsFolder,cacheFolder;
        // find the files
        File pluginFolder = plugin.getDataFolder();
        plugin.getLogger().info("looking for PluginFolder");

        if (!pluginFolder.isDirectory()) {
            pluginFolder.mkdir();
        }

        if (pluginFolder.isDirectory()) {
            plugin.getLogger().info("found PluginFolder");
            // forget all those shape and ore type and just go for the world name
            schematicsFolder = Minions.findFolder(pluginFolder, SCHEMATIC_FOLDER);
            cacheFolder = Minions.findFolder(pluginFolder, CACHE_FOLDER);
            if (schematicsFolder == null || cacheFolder == null)
                throw new FileNotFoundException("Couldn't find required folders.");
            // Delete all files in the Cache folder
            try {
                FileUtils.cleanDirectory(cacheFolder);
            } catch (IOException e) {
                plugin.getLogger().warning("Can't clear cache folder. File permissions wrong?");
            }

            plugin.getLogger().info("loading clips");

            clipstore = ClipboardLoaderWE.loadSchematics(schematicsFolder,cacheFolder,dao);

            plugin.getLogger().info("loaded clips");

            for(String clip : clipstore.keySet()){
                Bukkit.getLogger().info("clipstore: " + clip);
            }
            List<String> beans = dao.findAllClipboardHashes();
            for(String clip : beans){
                Bukkit.getLogger().info("dao hash: "+clip);
            }
        }
    }


    @Override
    public List<Clipboard> getRoadFit(RoadType roadType) {
        List<Clipboard> clips = new LinkedList<>();
        for(String hash : dao.findAllClipboardRoadHashes(roadType)){
            if(hash==null) continue;
            clips.add(clipstore.get(hash));
        }
        return clips;
    }

    @Override
    public List<Clipboard> getFit(Cartesian2D size, ContextType contextType, Direction direction) {
        List<Clipboard> clips = new LinkedList<>();
        for(String hash : dao.findAllClipboardHashes(size,contextType,direction)){
            if(hash==null) continue;
            clips.add(clipstore.get(hash));
        }
        return clips;
    }
}

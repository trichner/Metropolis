package ch.k42.metropolis.grid.urbanGrid.clipboard;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardProviderDB implements ClipboardProvider {
    public static class PluginNotFoundException extends RuntimeException{
        public PluginNotFoundException(String message) {
            super(message);
        }
    }
    private static final String WE_PLUGIN = "WorldEdit";
    private static final String SCHEMATIC_FOLDER = "schematics";
    private static final String CACHE_FOLDER = "cache";

    private WorldEdit worldEdit;
    private ClipboardDAO dao;
    private Map<String,Clipboard> clipstore = new HashMap<>();

    private boolean isLoaded = false;

    @Override
    public void loadClips(MetropolisPlugin plugin) throws FileNotFoundException {
        // make sure WorldEdit is ready
        if(!assertWorldEditLoaded()){
            throw new PluginNotFoundException("Couldn't find WorldEdit plugin.");
        }

        dao = new ClipboardDAO(plugin);

        File schematicsFolder,cacheFolder;
        // find the files
        File pluginFolder = plugin.getDataFolder();
        Minions.i("looking for PluginFolder");

        if (!pluginFolder.isDirectory()) {
            pluginFolder.mkdir();
        }

        ClipboardImporter importer = new ClipboardImporter();
        importer.importSchematics();
        importer = null; // release it

        ClipboardLoader loader = new ClipboardLoaderMkII(dao);
        clipstore = loader.loadSchematics(null,null);

        Minions.i("loaded clips");

        for(String clip : clipstore.keySet()){
            Minions.d("clipstore: " + clip);
        }
        List<String> beans = dao.findAllClipboardHashes();
        for(String clip : beans){
            Minions.d("dao hash: "+clip);
        }

        isLoaded = true;
    }


    @Override
    public List<Clipboard> getRoadFit(RoadType roadType) {
        if(!isLoaded) Minions.w("Schematics not loaded!");
        List<Clipboard> clips = new LinkedList<>();
        for(String hash : dao.findAllClipboardRoadHashes(roadType)){
            clips.add(clipstore.get(hash));
        }
        return clips;
    }

    @Override
    public List<Clipboard> getRoadFit(RoadType roadType, ContextType contextType) { //FIXME
        return getRoadFit(roadType);
    }

    @Override
    public List<Clipboard> getFit(Cartesian2D size, ContextType contextType,SchematicType schematicType, Direction direction) {
        if(!isLoaded) Minions.w("Schematics not loaded!");
        List<Clipboard> clips = new LinkedList<>();
        for(String hash : dao.findAllClipboardHashes(size,contextType,schematicType,direction)){
            clips.add(clipstore.get(hash));
        }
        return clips;
    }

    @Override
    public List<Clipboard> getFit(Cartesian2D size, SchematicType schematicType, Direction roadDir) {
        if(!isLoaded) Minions.w("Schematics not loaded!");
        List<Clipboard> clips = new LinkedList<>();
        for(String hash : dao.findAllClipboardHashes(size,schematicType,roadDir)){
            clips.add(clipstore.get(hash));
        }
        return clips;
    }

    private boolean assertWorldEditLoaded(){
        //==== First load the plugin
        WorldEditPlugin worldEditPlugin = null;
        PluginManager pm = Bukkit.getServer().getPluginManager();
        worldEditPlugin = (WorldEditPlugin) pm.getPlugin(WE_PLUGIN);

        // not there? darn
        if (worldEditPlugin == null) {
            Minions.w("No WorldEdit found!");
            return false;
        }

        // make sure it is enabled
        if (!pm.isPluginEnabled(worldEditPlugin)) {
            pm.enablePlugin(worldEditPlugin);
        }
        this.worldEdit = worldEditPlugin.getWorldEdit();
        return true;
    }
}

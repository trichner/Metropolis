package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Direction;
import ch.k42.metropolis.model.ContextType;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;

/**
 * This class loads, manages and provides schematics.
 *
 * @author Thomas Richner
 *
 */
public class ClipboardProviderWorldEdit {

    private class ClipboardKey{
        private int chunkSizeX;
        private int chunkSizeZ;
        private Direction direction;
        private ContextType context;

        private ClipboardKey(int chunkSizeX, int chunkSizeZ, Direction direction, ContextType context) {
            this.chunkSizeX = chunkSizeX;
            this.chunkSizeZ = chunkSizeZ;
            this.direction = direction;
            this.context = context;
        }

        @Override
        public int hashCode() {
            return (((chunkSizeX << 16) ^ chunkSizeZ)*31+direction.hashCode())*541+context.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj==null) return false;
            if(!(obj instanceof ClipboardKey)) return false;

            ClipboardKey k = (ClipboardKey) obj;
            if(chunkSizeX!=k.chunkSizeX) return false;
            if(chunkSizeZ!=k.chunkSizeZ) return false;
            if(!direction.equals(k.direction)) return false;
            if(!context.equals(k.context)) return false;

            return true;    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    private static final String pluginName = "WorldEdit";
    private static final String foldername = "schematics";


    private File schematicsFolder;
    private Map<ClipboardKey,List<Clipboard>> clipboards = new HashMap<ClipboardKey, List<Clipboard>>();


    public ClipboardProviderWorldEdit(MetropolisGenerator generator) throws Exception {
        super();
        //==== First load the plugin
        WorldEditPlugin worldEditPlugin = null;
        generator.reportDebug("looking for WorldEdit");
        PluginManager pm = Bukkit.getServer().getPluginManager();
        worldEditPlugin = (WorldEditPlugin) pm.getPlugin(pluginName);

        // not there? darn
        if (worldEditPlugin == null){
            Bukkit.getLogger().warning("No WorldEdit found!");
            throw new Exception("Couldn't find WorldEdit plugin.");
        }


//            // got the right version?
//            if (!isPlugInVersionOrBetter(generator, worldEditPlugin, pluginMinVersion))
//
//                // Use it anyway?
//                if (generator.settings.forceLoadWorldEdit) {
//                    generator.reportMessage("'" + CityWorldSettings.tagForceLoadWorldEdit + "' setting enabled!");
//
//                    // Well that didn't work... let's tell the user about a potential workaround
//                } else {
//                    generator.reportMessage("[PasteProvider] Cannot use the installed WorldEdit. ",
//                            "See the '" + CityWorldSettings.tagForceLoadWorldEdit + "' setting for possible workaround.");
//                    return null;
//                }

        // make sure it is enabled
        if (!pm.isPluginEnabled(worldEditPlugin))
            pm.enablePlugin(worldEditPlugin);

        // Yay! found it!
        generator.reportMessage("[ClipboardProvider] Found WorldEdit, enabling its schematics");

        // find the files
        File pluginFolder = generator.getPlugin().getDataFolder();
        generator.reportDebug("looking for PluginFolder");

        if(!pluginFolder.isDirectory()){
            pluginFolder.mkdir();
        }

        if (pluginFolder.isDirectory()) {
            generator.reportDebug("found PluginFolder");
            // forget all those shape and ore type and just go for the world name
            schematicsFolder = findFolder(pluginFolder,foldername);

//			// shape folder (normal, floating, etc.)
//			File shapeFolder = findFolder(pluginFolder, generator.shapeProvider.getCollectionName());
//
//			// finally ores are used to figure out the collection folder (normal, nether, theend, etc.)
//			schematicsFolder = findFolder(shapeFolder, generator.oreProvider.getCollectionName());
            generator.reportDebug("loading clips");
            loadClips(generator);
            generator.reportDebug("loaded clips");
        }
    }



    private File findFolder(File parent, String name) throws Exception {
        name = toCamelCase(name);
        File result = new File(parent, name);
        if (!result.isDirectory())
            if (!result.mkdir())
                throw new UnsupportedOperationException("[WorldEdit] Could not create/find the folder: " + parent.getAbsolutePath() + File.separator + name);
        return result;
    }

    private String toCamelCase(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length()).toLowerCase();
    }

    private FilenameFilter matchSchematics() {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".schematic");
            }
        };
    }

    public void loadClips(MetropolisGenerator generator) throws Exception {

        if (schematicsFolder != null) {
            // now load those schematic files
            File[] schematicFiles = schematicsFolder.listFiles(matchSchematics());
            for (File schematicFile: schematicFiles) {
                try {
                    Clipboard clip=null;

                    clip = new ClipboardWorldEdit(generator, schematicFile);

                    for (ContextType c : clip.getContextTypes()) { // add to all possible directions and contexts
                        ClipboardKey key = new ClipboardKey(clip.chunkX,clip.chunkZ,clip.getDirection(),c);
                        List<Clipboard> list = clipboards.get(key);
                        if(list==null){
                            list= new ArrayList();
                        }
                        // add the clip to the result
                        list.add(clip);
                        clipboards.put(key,list);
                    }


                    generator.reportMessage("[ClipboardProvider] Schematic "+schematicFile.getName() + " successfully loaded.");
                } catch (Exception e) {
                    generator.reportException("[ClipboardProvider] Schematic " + schematicFile.getName() + " could NOT be loaded",e);
                }
            }

        }else {
            throw new FileNotFoundException("Couldn't find schematics folder!");
        }
    }

    /**
     * Returns a list containing all available clipboards that match the size, direction and context
     * @param chunkX chunksize in X direction
     * @param chunkZ chunksize in Z direction
     * @param direction direction the structure should face
     * @param contextType context of the structure
     * @return list containing all matching clipboards, might be empty but never null
     */
    public List<Clipboard> getFit(int chunkX,int chunkZ,Direction direction,ContextType contextType){
        List<Clipboard> list = clipboards.get(new ClipboardKey(chunkX,chunkZ,direction,contextType));
        if(list==null) list = new LinkedList<Clipboard>();
        return list;
    }

}

package ch.k42.metropolis.grid.urbanGrid.clipboard;


import ch.k42.metropolis.grid.urbanGrid.config.GlobalSchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.config.SchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.minions.Vec2D;
import ch.k42.metropolis.minions.Minions;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardLoaderMkII implements ClipboardLoader{

    private GlobalSchematicConfig globalConfig;
    private Map<String,Clipboard> clipstore ;
    //@Inject
    private ClipboardDAO dao;

    public ClipboardLoaderMkII(ClipboardDAO dao) {
        this.dao = dao;
    }

    private SchematicConfig getConfig(File folder){
        SchematicConfig config = SchematicConfig.fromFile(new File(folder,ClipboardConstants.CONFIG_FILE));
        if(config==null){
            Minions.w("Found no config in folder: " + folder.getName());
        }
        return config;
    }

    public Map<String,Clipboard> loadSchematics(){
        clipstore = new HashMap<>();
        Minions.i("loading schematic config files...");
        globalConfig = GlobalSchematicConfig.fromFile(ClipboardConstants.IMPORT_FOLDER + File.separator +
                ClipboardConstants.GLOBAL_SETTINGS);

        File cacheFolder = new File(ClipboardConstants.CACHE_FOLDER);

        File[] schematicFolders = cacheFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

        int length = schematicFolders.length;
        for(int i=0;i<length;i++){
            File folder = schematicFolders[i];

            Minions.i("Loading schematic %4d of %d (%.2f%%) : %s" ,i,length,(i/(double) length)*100,folder
                    .getName());
            try {
                loadFolder(folder);
            } catch (IOException | DataException e) {
                // move on to next schem
                Minions.e(e);
            }
        }

        return clipstore;
    }

    private List<Clipboard> loadFolder(File folder) throws IOException, DataException {
        List<Clipboard> clips = new ArrayList<>();
        clips.addAll(loadStreet(folder));
        clips.addAll(loadBuilds(folder));
        return clips;
    }

    private List<Clipboard> loadStreet(File folder) throws IOException, DataException {
        File streetFile =    new File(folder, ClipboardConstants.STREET_FILE);
        if(!streetFile.exists()){
            return Collections.emptyList();
        }
        SchematicFormat format = SchematicFormat.getFormat(streetFile);
        CuboidClipboard cuboid = format.load(streetFile);
        format.save(cuboid, streetFile);

        SchematicConfig config = getConfig(folder);
        String hash = folder.getName();
        Clipboard clip = new ClipboardWE(cuboid,config,globalConfig,hash);
        hash += ".STREET";
        clipstore.put( hash,clip);
        if(dao.containsHash(hash)){// delete old entries
            dao.deleteClipboardHashes(hash);
        }
        dao.storeClipboard(hash, streetFile.getName(), Direction.NONE, config, new Vec2D(1, 1));
        return Collections.singletonList(clip);
    }

    private List<Clipboard> loadBuilds(File folder){
        File northFile =    new File(folder, ClipboardConstants.NORTH_FILE);
        if(!northFile.exists()){
            return Collections.emptyList();
        }
        File eastFile =     new File(folder, ClipboardConstants.EAST_FILE);
        File southFile =    new File(folder, ClipboardConstants.SOUTH_FILE);
        File westFile =     new File(folder, ClipboardConstants.WEST_FILE);

        String hash = folder.getName();
        SchematicConfig config = getConfig(folder);

        List<Clipboard> clips = new ArrayList<>();

        clips.add(loadFromCache(eastFile,hash,Direction.EAST,config));
        clips.add(loadFromCache(westFile,hash,Direction.WEST,config));
        clips.add(loadFromCache(southFile,hash,Direction.SOUTH,config));
        clips.add(loadFromCache(northFile,hash,Direction.NORTH,config));
        return clips;
    }

    private Clipboard loadFromCache(File file,String hash,Direction direction,SchematicConfig config){
        try {
            SchematicFormat format = SchematicFormat.getFormat(file);
            CuboidClipboard cuboid = format.load(file);

            Clipboard clip = new FileClipboard(file,config,globalConfig,hash);

            String thash = hash + "." + direction.name();
            clipstore.put(thash,clip);

            if(dao.containsHash(thash)){ // check if already in, if yes, delete the old one
               dao.deleteClipboardHashes(thash);
            }
            //DAO, store in db
            Vec2D size = new Vec2D(cuboid.getWidth()>>4,cuboid.getLength()>>4);
            dao.storeClipboard(thash,file.getName(), direction,config,size);

            if(!config.getRoadFacing()){ // if it doesn't need roads, store it for 'non-road' usage too
                dao.storeClipboard(thash,file.getName(), Direction.NONE,config,size);
            }

            return clip;
        } catch (IOException | DataException e) {
            Minions.e(e);
        }
        return null;
    }

    @Override
    public Map<String, Clipboard> loadSchematics(File schematicsFolder, File cacheFolder) {
        return loadSchematics();
    }
}

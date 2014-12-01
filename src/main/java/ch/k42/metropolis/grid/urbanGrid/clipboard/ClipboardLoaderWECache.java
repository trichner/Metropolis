package ch.k42.metropolis.grid.urbanGrid.clipboard;


import ch.k42.metropolis.grid.urbanGrid.config.GlobalSchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.config.SchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Minions;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardLoaderWECache implements ClipboardLoader{

    private static final String GLOBAL_SETTINGS = "global_settings.json";
    private static final String JSON_FILE = ".json";

    private Map<String,SchematicConfig> configs;
    private GlobalSchematicConfig globalConfig;
    //@Inject
    private ClipboardDAO dao;
    private Map<String,Clipboard> clipstore ;

    public ClipboardLoaderWECache(ClipboardDAO dao) {
        this.dao = dao;
    }

    private boolean loadSchematicConfigs(File folder){
        configs = new HashMap<>();
        List<File> configFiles = Minions.findAllFilesRecursively(folder, new ArrayList<File>(), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(JSON_FILE);
            }
        });

        for(File file : configFiles){
            SchematicConfig config = SchematicConfig.fromFile(file);
            if(config!=null){

                configs.put(file.getName(), config);
                for(String schem : config.getSchematics()){
                    configs.put(schem,config);
                }
            }else {
                Minions.w("Couldn't read config file: " + file.getName());
            }
        }
        return true;
    }


    private SchematicConfig findConfig(String name){
        SchematicConfig config = configs.get(name);
        if(config==null){
            config = configs.get(name+JSON_FILE);
            if(config==null){
                Minions.w("Found no config for: " + name);
                return null;
            }
        }
        return config;
    }



    public Map<String,Clipboard> loadSchematics(File schematicsFolder,File cacheFolder){
        clipstore = new HashMap<>();
        Bukkit.getLogger().info("loading schematic config files...");
        globalConfig = GlobalSchematicConfig.fromFile(schematicsFolder.getPath() + File.separator + GLOBAL_SETTINGS);
        loadSchematicConfigs(schematicsFolder);

        List<File> schematicFiles = Minions.findAllFilesRecursively(schematicsFolder, new ArrayList<File>(), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) { return name.endsWith(".schematic");  }
        });

        File cacheSchemFolder;
        SchematicFormat format;
        CuboidClipboard cuboid;

        int length = schematicFiles.size();

        Set<String> cachedHashes = getCachedHashes(cacheFolder);

        for(int i=0;i<length;i++){


            File file = schematicFiles.get(i);

            Minions.i("Loading schematic %4d of %d (%.2f%%) : %s" ,i,length,(i/(double) length)*100,file.getName());
            SchematicConfig config = findConfig(file.getName());

            // if we have no config the schem is of no use to us
            if(config==null){
                Minions.d("No config for '" + file.getName() + "' found.");
                continue;
            }

            String hash;
            try {

                hash = Minions.getMD5Checksum(file);

                /*
                 * Now the loader should check if the schematic is already cached and
                 * load it from there
                 *
                 * 1. make collection of all cached hashes
                 * 2. hash schematic, if already cached and in db, only load
                 * 3. if not, cache schematic and add it to db
                 * 4. cached schem with no schematic? -> delete
                 *
                 * Bonus:
                 * -load asynchronously and threaded (Tasks)
                 * -option to flush cache
                 * -
                 */

                cacheSchemFolder = getCacheFolder(cacheFolder,hash);

                format = SchematicFormat.getFormat(file);
                if(config.getContext().contains(ContextType.STREET) || config.getContext().contains(ContextType.HIGHWAY)){ // TODO we could get rid of rotated roads...
                    boolean cached = cachedHashes.contains(hash);

                    File streetFile =    new File(cacheSchemFolder, "STREET.schematic");
                    // load the actual blocks

                    if(!cached){
                        cuboid = format.load(file);
                        format.save(cuboid, streetFile);
                    }else{
                        cuboid = format.load(streetFile);
                    }

                    Clipboard clip = new ClipboardWE(cuboid,config,globalConfig,hash);
                    cachedHashes.remove(hash); // caching it now, take it out, we wan't to delete unused caches
                    hash += ".STREET";
                    clipstore.put( hash,clip);
                    if(dao.containsHash(hash)){// delete old entries
                       dao.deleteClipboardHashes(hash);
                    }
                    dao.storeClipboard(hash,file.getName(), Direction.NONE,config, new Cartesian2D(1,1));

                }else {
                    File northFile =    new File(cacheSchemFolder, "NORTH.schematic");
                    File eastFile =     new File(cacheSchemFolder, "EAST.schematic");
                    File southFile =    new File(cacheSchemFolder, "SOUTH.schematic");
                    File westFile =     new File(cacheSchemFolder, "WEST.schematic");

                    boolean cached = cachedHashes.contains(hash);
                    if(!cached){ // not cached? rotate and save
                        // load the actual blocks
                        cuboid = format.load(file);

                        //cache the north face first
                        format.save(cuboid, northFile);

                        //get each cuboid direction and save them
                        cuboid.rotate2D(90);
                        format.save(cuboid, eastFile);

                        cuboid.rotate2D(90);
                        format.save(cuboid, southFile);

                        cuboid.rotate2D(90);
                        format.save(cuboid, westFile);
                    }else{
                        Minions.i("Schematic already cached, no need to rotate.");
                    }

                    // reload them and put them into memory
                    boolean success;
                    success = loadFromCache(eastFile,hash,Direction.EAST,config);
                    success = success && loadFromCache(westFile,hash,Direction.WEST,config);
                    success = success && loadFromCache(southFile,hash,Direction.SOUTH,config);
                    success = success && loadFromCache(northFile,hash,Direction.NORTH,config);

                    if(!success){
                        Minions.w("Failed to load cached file for schematic '%s'", file.getName());
                    }else{
                        cachedHashes.remove(hash); // caching it now, take it out, we wan't to delete unused caches
                    }
                }
            } catch (IOException | DataException | NoSuchAlgorithmException e) {
                // move on to next schem
                Minions.e(e);
            }
        }

        removeUnusedCache(cachedHashes,cacheFolder);
        cleanupDB();
        return clipstore;
    }

    private void cleanupDB(){
        List<String> dbhashes = dao.findAllClipboardHashes();
        for(String hash : dbhashes){
            if(!clipstore.containsKey(hash)){ // not loaded?
                dao.deleteClipboardHashes(hash);
            }
        }
    }


    private File getCacheFolder(File cacheFolder,String hash){
        File cacheSchemFolder = new File(cacheFolder,hash);
        if (!cacheSchemFolder.isDirectory()) {
            if (!cacheSchemFolder.mkdir())
                throw new UnsupportedOperationException("Could not create/find the folder: " + cacheSchemFolder.getAbsolutePath() + File.separator + hash);
        }
        return cacheSchemFolder;
    }

    private void removeUnusedCache(Set<String> cachedHashes,File cacheFolder) {
        for(String hash : cachedHashes){
            dao.deleteClipboardHashes(hash);
            try {
                Files.delete(Paths.get(cacheFolder.getAbsolutePath() + File.separator + hash));
            } catch (IOException e) {
                Minions.w("Can't delete cached schematic '%s', are file permissions correct?",hash);
            }
        }
    }

    private Set<String> getCachedHashes(File cacheFolder){
        Set<String> hashes = new HashSet<>();
        File[] subdirs = cacheFolder.listFiles(Minions.isDirectory());
        for(File dir : subdirs){
            hashes.add(dir.getName()); // folder names are the hashes
        }
        return hashes;
    }

    private boolean loadFromCache(File file,String hash,Direction direction,SchematicConfig config){
        try {
            SchematicFormat format = SchematicFormat.getFormat(file);
            CuboidClipboard cuboid = format.load(file);

            Clipboard clip = new FileClipboard(file,config,globalConfig,hash);//new ClipboardWE(cuboid,config,
            // globalConfig,
            // hash);

            String thash = hash + "." + direction.name();
            clipstore.put(thash,clip);

            if(dao.containsHash(thash)){ // check if already in, if yes, delete the old one
               dao.deleteClipboardHashes(thash);
            }
            //DAO, store in db
            Cartesian2D size = new Cartesian2D(cuboid.getWidth()>>4,cuboid.getLength()>>4);
            dao.storeClipboard(thash,file.getName(), direction,config,size);

            if(!config.getRoadFacing()){ // if it doesn't need roads, store it for 'non-road' usage too
                dao.storeClipboard(thash,file.getName(), Direction.NONE,config,size);
            }

            System.gc();

            return true;
        } catch (IOException | DataException e) {
            Minions.e(e);
        }
        return false;
    }
}

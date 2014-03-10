package ch.k42.metropolis.WorldEdit;

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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardLoaderWE implements ClipboardLoader {

    private static final String GLOBAL_SETTINGS = "global_settings.json";
    private static final String JSON_FILE = ".json";

    private Map<String,SchematicConfig> configs;
    private GlobalSchematicConfig globalConfig;
    private ClipboardDAO dao;
    private Map<String,Clipboard> clipstore ;

    public ClipboardLoaderWE(ClipboardDAO dao) {
        this.dao = dao;
    }

    private boolean loadSchematicConfigs(File folder){
        configs = new HashMap<>();
        List<File> configFiles = Minions.findAllFilesRecursively(folder,new ArrayList<File>(),new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(JSON_FILE);
            }
        });

        for(File file : configFiles){
            SchematicConfig config = SchematicConfig.fromFile(file);
            if(config!=null){

                configs.put(file.getName(), config);
                for(String schems : config.getSchematics()){
                    configs.put(schems,config);
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

    @Override
    public Map<String,Clipboard> loadSchematics(File schematicsFolder, File cacheFolder){
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
        for(int i=0;i<length;i++){ // TODO use threads http://www.javapractices.com/topic/TopicAction.do?Id=247, maybe even load async in Clipboard
            File file = schematicFiles.get(i);
            Bukkit.getLogger().info(String.format("Loading schematic %4d of %d (%.2f%%) : %s" ,i,length,(i/(double) length)*100,file.getName()));

            SchematicConfig config = findConfig(file.getName());
            if(config==null) continue;

            String hash;
            try {
                hash = Minions.getMD5Checksum(file);
                cacheSchemFolder = new File(cacheFolder,hash);

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

                if (!cacheSchemFolder.isDirectory()) {
                    if (!cacheSchemFolder.mkdir())
                        throw new UnsupportedOperationException("[WorldEdit] Could not create/find the folder: " + cacheSchemFolder.getAbsolutePath() + File.separator + file.getName());
                }
                format = SchematicFormat.getFormat(file);
                if(config.getContext().contains(ContextType.STREET) || config.getContext().contains(ContextType.HIGHWAY)){ // TODO we could get rid of rotated roads...
                    File streetFile =    new File(cacheSchemFolder, "STREET.schematic");
                    // load the actual blocks
                    cuboid = format.load(file);
                    format.save(cuboid, streetFile);
                    Clipboard clip = new ClipboardWE(cuboid,config,globalConfig);
                    hash += ".STREET";
                    clipstore.put( hash,clip);
                    dao.storeClipboard(hash,file.getName(), Direction.NONE,config, new Cartesian2D(1,1));
                }else {
                    File northFile =    new File(cacheSchemFolder, "NORTH.schematic");
                    File eastFile =     new File(cacheSchemFolder, "EAST.schematic");
                    File southFile =    new File(cacheSchemFolder, "SOUTH.schematic");
                    File westFile =     new File(cacheSchemFolder, "WEST.schematic");

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

                    // reload them and put them into memory
                    boolean success;
                    success = loadFromCache(eastFile,hash,Direction.EAST,config);
                    success = success && loadFromCache(westFile,hash,Direction.WEST,config);
                    success = success && loadFromCache(southFile,hash,Direction.SOUTH,config);
                    success = success && loadFromCache(northFile,hash,Direction.NORTH,config);

                    if(!success){
                        Minions.w("Failed to load cached file for schematic '%s'", file.getName());
                    }
                }
            } catch (IOException e) {
                Bukkit.getLogger().throwing(ClipboardLoaderWE.class.getName(),"loadSchematics",e);
                continue;
            } catch (DataException e) {
                Bukkit.getLogger().throwing(ClipboardLoaderWE.class.getName(),"loadSchematics",e);
                continue;
            } catch (NoSuchAlgorithmException e) {
                Bukkit.getLogger().throwing(ClipboardLoaderWE.class.getName(),"loadSchematics",e);
                continue;
            }
        }
        return clipstore;
    }

    private boolean loadFromCache(File file,String hash,Direction direction,SchematicConfig config){
        try {
            SchematicFormat format = SchematicFormat.getFormat(file);
            CuboidClipboard cuboid = format.load(file);
            Clipboard clip = new ClipboardWE(cuboid,config,globalConfig);
            String thash = hash + "." + direction.name();
            clipstore.put(thash,clip);
            Cartesian2D size = new Cartesian2D(cuboid.getWidth()>>4,cuboid.getLength()>>4);
            dao.storeClipboard(thash,file.getName(), direction,config,size);
            if(!config.getRoadFacing()){ // if it doesn't need roads, store it for 'non-road' usage too
                dao.storeClipboard(thash,file.getName(), Direction.NONE,config,size);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        }
        return false;
    }
}

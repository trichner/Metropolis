package ch.k42.metropolis.WorldEdit;

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
public class ClipboardLoaderWE {

    private static final String GLOBAL_SETTINGS = "global_settings.json";
    private static final String JSON_FILE = ".json";



    private static Map<String,SchematicConfig> loadSchematicConfigs(File folder){
        Map<String,SchematicConfig> configs = new HashMap<>();
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
                Bukkit.getLogger().warning("Couldn't read config file: " + file.getName());
            }
        }
        return configs;
    }

    public static Map<String,Clipboard> loadSchematics(File schematicsFolder,File cacheFolder,ClipboardDAO dao){
        Map<String,Clipboard> clipstore = new HashMap<>();
        GlobalSchematicConfig globalConfig = GlobalSchematicConfig.fromFile(schematicsFolder.getPath() + File.separator + GLOBAL_SETTINGS);
        Map<String,SchematicConfig> configs = loadSchematicConfigs(schematicsFolder);

        for(String conf : configs.keySet()){
            Bukkit.getLogger().info("config: " + conf);
        }


        List<File> schematicFiles = Minions.findAllFilesRecursively(schematicsFolder, new ArrayList<File>(), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) { return name.endsWith(".schematic");  }
        });

        File cacheSchemFolder;
        SchematicFormat format;
        CuboidClipboard cuboid;
        for(File file : schematicFiles){ // TODO use threads


            String hash;
            try {
                hash = Minions.getMD5Checksum(file);
                cacheSchemFolder = new File(cacheFolder,hash);

                if (!cacheSchemFolder.isDirectory()) {
                    if (!cacheSchemFolder.mkdir())
                        throw new UnsupportedOperationException("[WorldEdit] Could not create/find the folder: " + cacheSchemFolder.getAbsolutePath() + File.separator + file.getName());
                }
                format = SchematicFormat.getFormat(file);
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

                SchematicConfig config = configs.get(file.getName());
                if(config==null)
                    config = configs.get(file.getName()+JSON_FILE);
                Clipboard clip;
                String thash;
                Cartesian2D size;

                // load the schems,store them and add them to the database
                cuboid = format.load(eastFile);
                clip = new ClipboardWE(cuboid,config,globalConfig);
                thash = hash + ".EAST";

                clipstore.put( thash,clip);
                size = new Cartesian2D(cuboid.getWidth()>>4,cuboid.getLength()>>4);
                dao.storeClipboard(thash,file.getName(), Direction.EAST,config,size);

                cuboid = format.load(southFile);
                clip = new ClipboardWE(cuboid,config,globalConfig);
                thash = hash + ".SOUTH";
                clipstore.put(thash,clip);
                size = new Cartesian2D(cuboid.getWidth()>>4,cuboid.getLength()>>4);
                dao.storeClipboard(thash,file.getName(), Direction.SOUTH,config,size);

                cuboid = format.load(westFile);
                clip = new ClipboardWE(cuboid,config,globalConfig);
                thash = hash + ".WEST";
                clipstore.put(thash,clip);
                size = new Cartesian2D(cuboid.getWidth()>>4,cuboid.getLength()>>4);
                dao.storeClipboard(thash,file.getName(), Direction.WEST,config,size);


                cuboid = format.load(northFile);
                clip = new ClipboardWE(cuboid,config,globalConfig);
                thash = hash + ".NORTH";
                clipstore.put(thash,clip);
                size = new Cartesian2D(cuboid.getWidth()>>4,cuboid.getLength()>>4);
                dao.storeClipboard(thash,file.getName(), Direction.NORTH,config,size);

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
}

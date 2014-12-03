package ch.k42.metropolis.grid.urbanGrid.clipboard;


import ch.k42.metropolis.grid.urbanGrid.config.SchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.minions.Minions;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardImporter {

    private Map<String,SchematicConfig> configs;

    private void assertFolders(){
        File[] folders = {  new File(ClipboardConstants.IMPORT_FOLDER),
                            new File(ClipboardConstants.CACHE_FOLDER),
                            new File(ClipboardConstants.DONE_FOLDER)};
        for(File folder : folders){
            if(!folder.isDirectory()){
                folder.mkdirs();
            }
        }

    }

    private boolean loadSchematicConfigs(){
        configs = new HashMap<>();
        List<File> configFiles = Minions.findAllFilesRecursively(ClipboardConstants.IMPORT_FOLDER, new
                ArrayList<File>(), new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(ClipboardConstants.JSON_FILE);
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
            config = configs.get(name+ClipboardConstants.JSON_FILE);
            if(config==null){
                Minions.w("Found no config for: " + name);
                return null;
            }
        }
        return config;
    }

    private boolean rotateAndCacheSchematic(File schematicFile)
            throws IOException, DataException, NoSuchAlgorithmException {
        SchematicFormat format = SchematicFormat.getFormat(schematicFile);

        SchematicConfig config = findConfig(schematicFile.getName());
        if(config==null){
            Minions.w("No config found for '" + schematicFile.getName() + "'!");
            return false;
        }

        // copy config
        String configPath = config.getPath();
        File cacheFolder = new File(ClipboardConstants.CACHE_FOLDER,Minions.getMD5Checksum(schematicFile));

        // delete any old schematics
        if (cacheFolder.isDirectory()) {
            FileUtils.deleteDirectory(cacheFolder);
        }

        if (!cacheFolder.mkdirs()){
            Minions.w("Cannot create directory '" + cacheFolder.getName() + "'");
            return false;
        }

        //copy config file
        File configFile   = new File(cacheFolder,ClipboardConstants.CONFIG_FILE);
        Minions.d("Copying config from " + Paths.get(configPath).toString() + " to " + configFile.toPath());
        Files.copy(Paths.get(configPath), cacheFolder.toPath().resolve(ClipboardConstants.CONFIG_FILE));

        //cache schematics
        File northFile =    new File(cacheFolder, ClipboardConstants.NORTH_FILE);
        File eastFile =     new File(cacheFolder, ClipboardConstants.EAST_FILE);
        File southFile =    new File(cacheFolder, ClipboardConstants.SOUTH_FILE);
        File westFile =     new File(cacheFolder, ClipboardConstants.WEST_FILE);

        CuboidClipboard cuboid;
        cuboid = format.load(schematicFile);
        //cache the north face first
        format.save(cuboid, northFile);

        //get each cuboid direction and save them
        cuboid.rotate2D(90);
        format.save(cuboid, eastFile);

        cuboid.rotate2D(90);
        format.save(cuboid, southFile);

        cuboid.rotate2D(90);
        format.save(cuboid, westFile);

        return true;
    }

    private boolean cacheStreet(File file) throws IOException, NoSuchAlgorithmException {
        SchematicConfig config = findConfig(file.getName());
        if(config==null){
            Minions.w("No config found for '" + file.getName() + "'!");
            return false;
        }

        // copy config
        String configPath = config.getPath();
        File cacheFolder = new File(ClipboardConstants.CACHE_FOLDER,Minions.getMD5Checksum(file));
        // delete any old schematics
        if (cacheFolder.isDirectory()) {
            FileUtils.deleteDirectory(cacheFolder);
        }

        if (!cacheFolder.mkdir()){
            Minions.w("Cannot create directory '" + cacheFolder.getName() + "'");
            return false;
        }

        //copy config file
        File configFile   = new File(cacheFolder,ClipboardConstants.CONFIG_FILE);
        Minions.d("Copying config from " + Paths.get(configPath).toString() + " to " + configFile.toPath());
        Files.copy(Paths.get(configPath), cacheFolder.toPath().resolve(ClipboardConstants.CONFIG_FILE));

        File streetFile =    new File(cacheFolder, ClipboardConstants.STREET_FILE);
        // load the actual blocks
        Files.copy(file.toPath(),streetFile.toPath());
        return true;
    }

    public void importSchematics(){
        // create all necessary folders
        assertFolders();

        // start importing
        File schematicsFolder = new File(ClipboardConstants.IMPORT_FOLDER);
        Minions.i("loading schematic config files...");

        // load all configs, kinda inefficient since we reload them again later
        loadSchematicConfigs();

        // find all schematics to import
        List<File> schematicFiles = Minions.findAllFilesRecursively(schematicsFolder, new ArrayList<File>(), new
                FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(ClipboardConstants.SCHEMATIC_FILE);
                    }
                }
        );

        int length = schematicFiles.size();

        for(int i=0;i<length;i++){
            File file = schematicFiles.get(i);

            Minions.i("Importing schematic %4d of %d (%.2f%%) : %s" ,i,length,(i/(double) length)*100,file
                    .getName());
            SchematicConfig config = findConfig(file.getName());

            // if we have no config the schem is of no use to us
            if(config==null){
                Minions.d("No config for '" + file.getName() + "' found.");
                continue;
            }

            try {
                if(config.getContext().contains(ContextType.STREET) || config.getContext().contains(ContextType.HIGHWAY)){ // TODO we could get rid of rotated roads...
                    cacheStreet(file);
                }else {
                    rotateAndCacheSchematic(file);
                }
            } catch (IOException | DataException | NoSuchAlgorithmException e) {
                // move on to next schem
                Minions.e(e);
            }
        }

        //move imported files
        try {
            FileUtils.moveDirectoryToDirectory(new File(ClipboardConstants.IMPORT_FOLDER),new File(ClipboardConstants
                    .DONE_FOLDER),true);
        } catch (IOException e) {
            Minions.d("Failed to move imported files %s",e.getMessage());
        }
    }
}

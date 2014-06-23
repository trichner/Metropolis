package ch.k42.metropolis.grid.urbanGrid.clipboard;

import javax.persistence.PersistenceException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Bukkit;

import ch.k42.metropolis.grid.urbanGrid.config.SchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import com.avaje.ebean.Query;


/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardDAO {

    private final ch.k42.metropolis.plugin.MetropolisPlugin plugin;
    private final EbeanServer database;

    public ClipboardDAO(MetropolisPlugin plugin) {
        EbeanServer database = plugin.getDatabase();
        try {
            database.find(ClipboardBean.class).findRowCount();
        } catch (PersistenceException e){
            plugin.installDDL();
            try {
                Files.deleteIfExists(Paths.get(plugin.getDataFolder() + File.separator + "cache"));
            } catch (IOException e1) {
                Minions.w("Recreating database, couldn't delete old cache");
            }
        }
        this.database = database;
        this.plugin = plugin;
    }

    public boolean storeClipboard(String fileHash,String fileName, Direction direction, SchematicConfig config, Cartesian2D size){
        if(config==null){
            Minions.w(String.format("Schematic '%s' has no valid config file.", fileName));
            return false;
        }else if(config.getContext()==null){
            Minions.w(String.format("Schematic '%s' has no valid context in config", fileName));
            return false;
        }else if(config.getSchematicType()==null){
            Minions.w(String.format("Schematic '%s' has no valid SchematicType in config", fileName));
            return false;
        }
        for(ContextType context : config.getContext()){
            storeClipboard(fileHash, fileName, direction, context,config.getSchematicType(),config.getRoadType(), size);
        }
        return true;
    }

    public void storeClipboard(String fileHash,String fileName, Direction direction, ContextType context, SchematicType schematicType, RoadType roadType, Cartesian2D size){

        ClipboardBean bean = database.createEntityBean(ClipboardBean.class);

        bean.setContext(context);
        bean.setDirection(direction);
        bean.setFileHash(fileHash);
        bean.setSize(size);
        bean.setFileName(fileName);
        bean.setRoadType(roadType);
        bean.setSchematicType(schematicType);

        database.save(bean);
    }

    public boolean containsHash(String hash){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class).where().eq("fileHash",hash).query();
        return query.findRowCount()!=0;
    }

    public List<String> findAllClipboardHashes(Cartesian2D size, ContextType context, SchematicType schematicType, Direction direction){
        return getHashes(findAllClipboards(size, context,schematicType, direction));
    }

    public List<ClipboardBean> findAllClipboards(Cartesian2D size,ContextType context, SchematicType schematicType,Direction direction){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class);
        query.where().eq("size_x",size.X).eq("size_y",size.Y).eq("context",context).eq("direction",direction).eq("schematicType",schematicType);
        query.select("fileHash,fileName");
        List<ClipboardBean> beans = query.findList();
        return beans;
    }

    public List<String> findAllClipboardHashes(Cartesian2D size, SchematicType schematicType, Direction direction){
        return getHashes(findAllClipboards(size,schematicType, direction));
    }

    public List<ClipboardBean> findAllClipboards(Cartesian2D size, SchematicType schematicType, Direction direction){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class);
        query.where()
                .eq("size_x",size.X)
                .eq("size_y",size.Y)
                .eq("direction",direction)
                .eq("schematicType",schematicType);
        query.select("fileHash,fileName");
        List<ClipboardBean> beans = query.findList();
        return beans;
    }

    public List<ClipboardBean> findAllClipboardRoads(RoadType roadType){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class);
        query.where().eq("roadType",roadType);
        query.select("fileHash,fileName");
        List<ClipboardBean> beans = query.findList();
        return beans;
    }
    public List<String> findAllClipboardRoadHashes(RoadType roadType){
        return getHashes(findAllClipboardRoads(roadType));
    }

    public List<String> findAllClipboardHashes(){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class).select("fileHash");
        List<ClipboardBean> beans = query.findList();
        return getHashes(beans);
    }


    /**
     * Deletes all similar hashes
     * @param hash deletes all clips with a hash and some postfix (where hash like 'hash%')
     * @return always true
     */
    public boolean deleteClipboardHashes(String hash){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class).where().like("fileHash",hash+"%").query();
        List<ClipboardBean> beans = query.findList();
        List<Integer> ids = getIDs(beans);
        plugin.getDatabase().delete(ClipboardBean.class,ids);
        return true;
    }

    private List<Integer> getIDs(Collection<ClipboardBean> beans){
        List<Integer> ids = new ArrayList<>();
        for(ClipboardBean bean : beans){
            ids.add(bean.getId());
        }
        return ids;
    }

    private List<String> getHashes(Collection<ClipboardBean> beans){
        List<String> hashes = new LinkedList<>();
        for(ClipboardBean bean : beans){ hashes.add(bean.getFileHash()); }
        return hashes;
    }

    private void printResults(Collection<ClipboardBean> beans){
        Bukkit.getLogger().info("QUERY: " + beans.size());
        for(ClipboardBean bean : beans){
            Bukkit.getLogger().info("result: " + bean.getFileName());
        }
    }



}

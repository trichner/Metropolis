package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import com.avaje.ebean.Query;
import org.bukkit.Bukkit;

import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardDAO {

   private final MetropolisPlugin plugin;

    public ClipboardDAO(MetropolisPlugin plugin) {
        try {
            plugin.getDatabase().find(ClipboardBean.class).findRowCount();
        } catch (PersistenceException e){
            plugin.installDDL();
        }
        this.plugin = plugin;
    }

    public boolean storeClipboard(String fileHash,String fileName, Direction direction, SchematicConfig config, Cartesian2D size){
//        Bukkit.getLogger().info("Storing clipboard:");
//        Bukkit.getLogger().info("   fileHash: " +fileHash);
//        Bukkit.getLogger().info("   fileName: " +fileName);
//        Bukkit.getLogger().info("   direction: " +direction.name());
//        Bukkit.getLogger().info("   config: " +config);
//        Bukkit.getLogger().info("   size: " +size);
        if(config==null){
            Bukkit.getLogger().warning(String.format("Schematic '%s' has no valid config file.",fileName));
            return false;
        }else if(config.getContext()==null){
            Bukkit.getLogger().warning(String.format("Schematic '%s' has no valid context in config",fileName));
            return false;
        }
        for(ContextType context : config.getContext()){
            storeClipboard(fileHash, fileName, direction, context,config.getRoadType(), size);
        }
        return true;
    }

    public void storeClipboard(String fileHash,String fileName, Direction direction, ContextType context,RoadType roadType, Cartesian2D size){
        ClipboardBean bean = plugin.getDatabase().createEntityBean(ClipboardBean.class);
        bean.setContext(context);
        bean.setDirection(direction);
        bean.setFileHash(fileHash);
        bean.setSize(size);
        bean.setFileName(fileName);
        bean.setRoadType(roadType);
        plugin.getDatabase().save(bean);
    }

    public List<String> findAllClipboardHashes(Cartesian2D size,ContextType context,Direction direction){
        return getHashes(findAllClipboards(size, context, direction));
    }
    public List<ClipboardBean> findAllClipboards(Cartesian2D size,ContextType context,Direction direction){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class);
        query.where().eq("size_x",size.X).eq("size_y",size.Y).eq("context",context).eq("direction",direction);
        query.select("fileHash,fileName");
        return query.findList();
    }
    public List<ClipboardBean> findAllClipboardRoads(RoadType roadType){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class);
        query.where().eq("roadType",roadType);
        query.select("fileHash,fileName");
        List<ClipboardBean> beans = query.findList();
        Bukkit.getLogger().info("QUERY: " + beans.size());
        for(ClipboardBean bean : beans){
            Bukkit.getLogger().info("result: " + bean.getFileName());
        }
        return beans;
    }
    public List<String> findAllClipboardRoadHashes(RoadType roadType){
        return getHashes(findAllClipboardRoads(roadType));
    }

    public List<String> findAllClipboardHashes(){
        Query<ClipboardBean> query = plugin.getDatabase().find(ClipboardBean.class).select("fileHash");
        List<ClipboardBean> beans = query.findList();
        Bukkit.getLogger().info("QUERY: " + beans.size());
        for(ClipboardBean bean : beans){
            Bukkit.getLogger().info("result: " + bean.getFileName());
        }
        return getHashes(beans);
    }

    private List<String> getHashes(Collection<ClipboardBean> beans){
        List<String> hashes = new LinkedList<>();
        for(ClipboardBean bean : beans){ hashes.add(bean.getFileHash()); }
        return hashes;
    }



}

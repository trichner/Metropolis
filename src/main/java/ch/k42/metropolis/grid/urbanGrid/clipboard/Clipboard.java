package ch.k42.metropolis.grid.urbanGrid.clipboard;


import ch.k42.metropolis.grid.urbanGrid.config.SchematicConfig;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Cartesian3D;
import org.bukkit.World;

/**
 * Created by Thomas on 07.03.14.
 */
public interface Clipboard {
    /**
     *
     * @param world world to paste
     * @param base absolute chunk coordinates
     * @param streetLevel
     */
    public void paste(World world, Cartesian2D base, int streetLevel);
    public Cartesian3D getSize();
    public int getBottom(int streetlevel);
    public SchematicConfig getConfig();

    /**
     * A unique ID which is the same for one clipboard and it's rotations
     * @return unique groupId
     */
    public String getGroupId();
}

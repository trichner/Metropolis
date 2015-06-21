package ch.k42.metropolis.minions;

import ch.n1b.worldedit.schematic.block.BaseBlock;
import ch.n1b.worldedit.schematic.data.DataException;
import org.bukkit.Location;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public interface BlockAdapter {
    BaseBlock getBlock(Location location) throws DataException;

    boolean setBlock(Location location, BaseBlock block, boolean notifyAndLight);
}

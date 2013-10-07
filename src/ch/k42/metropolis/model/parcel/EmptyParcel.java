package ch.k42.metropolis.model.parcel;

import ch.k42.metropolis.WorldEdit.Clipboard;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.grid.Grid;
import org.bukkit.Chunk;

/**
 * Represents a Parcel with no building.
 * @author Thomas Richner
 */
public class EmptyParcel extends Parcel {

    public EmptyParcel(Grid grid, int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ) {
        super(grid, chunkX, chunkZ, chunkSizeX, chunkSizeZ, ContextType.UNDEFINED);
        grid.fillParcels(chunkX,chunkZ,this);
    }

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {
        // do nothing, since it's empty :)
        Clipboard clip = generator.getClipboardProvider().getByName("gold16x16.schematic");
        if(clip!=null)
            clip.paste(generator,chunkX<<4,chunkZ<<4, Constants.BUILD_HEIGHT); // FIXME Hardcoded street level
        else
            generator.reportDebug("No placeholder schem found, was looking for: "+"[gold16x16.schematic]");
    }

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

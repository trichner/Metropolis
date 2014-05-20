package ch.k42.metropolis.grid.urbanGrid.parcel;

import org.bukkit.Chunk;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;

/**
 * Represents a Parcel with no building.
 *
 * @author Thomas Richner
 */
public class EmptyParcel extends Parcel {


    public EmptyParcel(Cartesian2D base, Cartesian2D size, ContextType contextType, SchematicType schematicType, UrbanGrid grid) {
        super(base, size, contextType, schematicType, grid);
    }

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {
        // do nothing, since it's empty :)
//        Clipboard clip = generator.getClipboardProvider().getByName("gold16x16.schematic");
//        if(clip!=null)
//            clip.paste(generator,chunkX<<4,chunkZ<<4, Constants.BUILD_HEIGHT, clip.getDirection()); // FIXME Hardcoded street level
//        else
//            generator.reportDebug("No placeholder schem found, was looking for: "+"[gold16x16.schematic]");
    }

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

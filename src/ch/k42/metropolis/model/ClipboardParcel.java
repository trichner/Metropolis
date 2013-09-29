package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import org.bukkit.Chunk;

import ch.k42.metropolis.WorldEdit.*;

/**
 * Represents a Parcel with a schematic/clipboard as building.
 *
 * @author Thomas Richner
 */

public class ClipboardParcel extends Parcel {

    private Clipboard clipboard;

    public ClipboardParcel(Grid grid,int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ, Clipboard clipboard,ContextType contextType) {
        super(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ,contextType);
        this.clipboard = clipboard;
        grid.fillParcels(chunkX,chunkZ,this);
    }

    public void populate(MetropolisGenerator generator,Chunk chunk) {
        if(chunk.getX()==(chunkX)&&chunk.getZ()==(chunkZ)){
            int streetLevel=Constants.BUILD_HEIGHT;
            clipboard.paste(generator, (chunkX << 4),(chunkZ<<4), Constants.BUILD_HEIGHT);
            // TODO use config, don't always destroy
            generator.getDecayProvider().destroyChunks(chunkX,chunkZ,chunkSizeX,chunkSizeZ,clipboard.getBottom(streetLevel),clipboard.getSizeY(),clipboard.getDecayOptions());
        }
    }

    @Override
    public String toString() {
        return "ClipboardParcel: " + clipboard.toString();
    }
}

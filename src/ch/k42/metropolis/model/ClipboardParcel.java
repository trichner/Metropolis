package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.GridRandom;
import org.bukkit.Chunk;
import org.bukkit.World;

import ch.k42.metropolis.WorldEdit.*;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
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
            clipboard.paste(generator, (chunkX << 4), Constants.BUILD_HEIGHT,(chunkZ<<4));
        }
    }

    @Override
    public String toString() {
        return "ClipboardParcel: " + clipboard.toString();
    }
}

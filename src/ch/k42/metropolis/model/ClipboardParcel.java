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

    public ClipboardParcel(Grid grid,int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ, Clipboard clipboard) {
        super(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ);
        this.clipboard = clipboard;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkSizeX = chunkSizeX;
        this.chunkSizeZ = chunkSizeZ;

    }

    public void populate(MetropolisGenerator generator,Chunk chunk) {
        generator.getGridProvider().getGrid(chunkX,chunkZ).fillParcels(chunkX,chunkZ,this);
        clipboard.paste(generator,chunkX, Constants.BUILD_HEIGHT,chunkZ);
    }
}

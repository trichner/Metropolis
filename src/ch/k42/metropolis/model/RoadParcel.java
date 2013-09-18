package ch.k42.metropolis.model;

import ch.k42.metropolis.WorldEdit.Clipboard;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.Direction;
import org.bukkit.Chunk;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class RoadParcel extends Parcel {

    private static final int chunkSizeX = 1;
    private static final int chunkSizeZ = 1;

    public RoadParcel(Grid grid,int chunkX,int chunkZ) {
        super(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ,ContextType.ROAD);
        grid.fillParcels(chunkX,chunkZ,this);
    }

    private Clipboard road;

    @Override
    void populate(MetropolisGenerator generator, Chunk chunk) {
        if(chunk.getX()==(chunkX)&&chunk.getZ()==(chunkZ)){
            List<Clipboard> list = generator.getClipboardProvider().getFit(chunkSizeX,chunkSizeZ,Direction.ALL,ContextType.ROAD);
            road = list.get(grid.getRandom().getRandomInt(list.size()));
            road.paste(generator, (chunkX << 4), Constants.BUILD_HEIGHT,(chunkZ<<4));
        }
    }

    @Override
    public String toString() {
        return "RoadParcel: " + road.toString();
    }
}


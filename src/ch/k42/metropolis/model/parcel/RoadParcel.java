package ch.k42.metropolis.model.parcel;

import ch.k42.metropolis.WorldEdit.Clipboard;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.grid.Grid;
import org.bukkit.Chunk;

import java.util.List;

/**
 * This Parcel represents a Road in Metropolis
 * @author Thomas Richner
 */
public class RoadParcel extends Parcel {

    private static final int chunkSizeX = 1;
    private static final int chunkSizeZ = 1;

    public RoadParcel(Grid grid,int chunkX,int chunkZ) {
        super(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ, ContextType.ROAD);
        grid.fillParcels(chunkX,chunkZ,this);
    }

    private Clipboard road;

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {
        if(chunk.getX()==(chunkX)&&chunk.getZ()==(chunkZ)){
            List<Clipboard> list = generator.getClipboardProvider().getFit(chunkSizeX,chunkSizeZ,Direction.ROAD,ContextType.ROAD);
            if(list.size()==0){
                list = generator.getClipboardProvider().getFit(chunkSizeX,chunkSizeZ,Direction.NORTH,ContextType.ROAD);
            }
            if(list.size()>0){
                road = list.get(grid.getRandom().getRandomInt(list.size()));
                road.paste(generator, (chunkX << 4),(chunkZ<<4), Constants.BUILD_HEIGHT);
            }else {

                generator.reportMessage("No schematics for road found.");
            }
        }else{
            generator.reportDebug("Wanted to place road where it should not belong...");
        }

    }

    @Override
    public String toString() {
        return "RoadParcel +[" + chunkX +"]["+chunkZ+"]";
    }
}


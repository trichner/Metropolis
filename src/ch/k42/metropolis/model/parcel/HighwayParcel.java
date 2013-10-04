package ch.k42.metropolis.model.parcel;

import ch.k42.metropolis.WorldEdit.Clipboard;
import ch.k42.metropolis.WorldEdit.ClipboardProviderWorldEdit;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.RoadType;
import ch.k42.metropolis.model.grid.Grid;
import org.bukkit.Chunk;
import org.bukkit.Material;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 10/1/13
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HighwayParcel extends Parcel {

    private RoadType roadType = RoadType.NONE;

    public HighwayParcel(Grid grid, int chunkX, int chunkZ,RoadType roadType) {
        super(grid, chunkX, chunkZ, 1, 1, ContextType.HIGHWAY);
        this.roadType = roadType;
    }

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {

        if(roadType.equals(RoadType.HIGHWAY_SIDE_E)&&grid.getParcel(chunkX+1,chunkZ).getContextType().equals(ContextType.ROAD)){
            roadType = RoadType.HIGHWAY_SIDE_T_E;
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_W)&&grid.getParcel(chunkX-1,chunkZ).getContextType().equals(ContextType.ROAD)){
            roadType = RoadType.HIGHWAY_SIDE_T_W;
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_S)&&grid.getParcel(chunkX,chunkZ+1).getContextType().equals(ContextType.ROAD)){
            roadType = RoadType.HIGHWAY_SIDE_T_S;
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_N)&&grid.getParcel(chunkX,chunkZ-1).getContextType().equals(ContextType.ROAD)){
            roadType = RoadType.HIGHWAY_SIDE_T_N;
        }

        List<Clipboard> clips = getFits(generator.getClipboardProvider(),roadType);
        Clipboard clip=null;

        if(clips!=null&&clips.size()>0)
            clip = clips.get(grid.getRandom().getRandomInt(clips.size()));

        if(clip!=null){
            clip.paste(generator,chunkX<<4,chunkZ<<4, Constants.BUILD_HEIGHT); // FIXME Hardcoded street level
            decayRoadChunk(generator,chunk,Constants.BUILD_HEIGHT-2);
        }else{
            generator.reportDebug("Haven't found any HIGHWAY schem for: " + roadType.toString());
        }
        // T crossing?
    }

    private void decayRoadChunk(MetropolisGenerator generator, Chunk chunk, int y) {
        int amount = 20; //FIXME HARDCODED
        GridRandom rand = grid.getRandom();
        while (amount > 0) {
            amount--;
            int x = rand.getRandomInt(16);
            int z = rand.getRandomInt(16);

            if(!chunk.getBlock(x,y+1,z).getType().equals(Material.AIR)||chunk.getBlock(x,y,z).getType().equals(Material.AIR)){
//                generator.reportDebug("Road decay failed, wrong height?");
//                generator.reportDebug("y: " + chunk.getBlock(x,y,z).getType());
//                generator.reportDebug("y+1: " + chunk.getBlock(x,y+1,z).getType());
//                generator.reportDebug("y-1: " + chunk.getBlock(x,y-1,z).getType());
//                generator.reportDebug("y-2: " + chunk.getBlock(x,y-2,z).getType());
                continue;
            }


            switch (rand.getRandomInt(10)){
                case 0:
                case 1:
                case 2: //30%
                    chunk.getBlock(x, y, z).setType(Material.MOSSY_COBBLESTONE);
                    break;
                case 3:
                case 4:
                case 5:
                case 6://40%
                    chunk.getBlock(x, y, z).setType(Material.COBBLESTONE);
                    break;
                case 7:
                case 8: //20%
                    chunk.getBlock(x, y, z).setType(Material.DIRT);
                    chunk.getBlock(x, y+1, z).setTypeIdAndData(Material.LONG_GRASS.getId(),(byte)rand.getRandomInt(3),false);
                    break;
                case 9: //10% since halfslabs are quite annoying
                    chunk.getBlock(x, y, z).setTypeIdAndData(Material.STEP.getId(),(byte)3,false);
                    break;
            }

            amount--;
        }
    }

    private List<Clipboard> getFits(ClipboardProviderWorldEdit cprovider,RoadType type){
        return cprovider.getFit(1,1, Direction.NONE,ContextType.HIGHWAY,type);
    }
}

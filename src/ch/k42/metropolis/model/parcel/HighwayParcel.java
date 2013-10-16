package ch.k42.metropolis.model.parcel;

import ch.k42.metropolis.WorldEdit.Clipboard;
import ch.k42.metropolis.WorldEdit.ClipboardProvider;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.enums.RoadType;
import ch.k42.metropolis.model.grid.Grid;
import org.bukkit.Chunk;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 10/1/13
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HighwayParcel extends StreetParcel {

    private RoadType roadType = RoadType.NONE;

    public HighwayParcel(Grid grid, int chunkX, int chunkZ,RoadType roadType) {
        super(grid, chunkX, chunkZ, 1, 1, ContextType.HIGHWAY);
        this.roadType = roadType;
    }

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {

        if(roadType.equals(RoadType.HIGHWAY_SIDE_E)&&grid.getParcel(chunkX+1,chunkZ).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_E;
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_W)&&grid.getParcel(chunkX-1,chunkZ).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_W;
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_S)&&grid.getParcel(chunkX,chunkZ+1).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_S;
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_N)&&grid.getParcel(chunkX,chunkZ-1).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_N;
        }

        findAndPlaceClip(generator,chunk,roadType);

        // T crossing?
    }

    private void findAndPlaceClip(MetropolisGenerator generator,Chunk chunk,RoadType roadType){
        List<Clipboard> clips = getFits(generator.getClipboardProvider(),roadType);
        Clipboard clip=null;

        if(clips!=null&&clips.size()>0)
            clip = clips.get(grid.getRandom().getRandomInt(clips.size()));

        if(clip!=null){
            clip.paste(generator,chunkX<<4,chunkZ<<4, Constants.BUILD_HEIGHT); // FIXME Hardcoded street level
            decayRoadChunk(generator,chunk,Constants.BUILD_HEIGHT-2);
            decaySidewalk(generator,chunk,Constants.BUILD_HEIGHT-1);
        }else{
            generator.reportDebug("Haven't found any HIGHWAY schem for: " + roadType.toString());
        }
    }

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        if(roadType.equals(RoadType.HIGHWAY_SIDE_E)&&grid.getParcel(chunkX+1,chunkZ).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_E;
            findAndPlaceClip(generator,chunk,roadType);
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_W)&&grid.getParcel(chunkX-1,chunkZ).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_W;
            findAndPlaceClip(generator,chunk,roadType);
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_S)&&grid.getParcel(chunkX,chunkZ+1).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_S;
            findAndPlaceClip(generator,chunk,roadType);
        }else if(roadType.equals(RoadType.HIGHWAY_SIDE_N)&&grid.getParcel(chunkX,chunkZ-1).getContextType().equals(ContextType.STREET)){
            roadType = RoadType.HIGHWAY_T_N;
            findAndPlaceClip(generator,chunk,roadType);
        }
    }



    private List<Clipboard> getFits(ClipboardProvider cprovider, RoadType type){
        return cprovider.getRoadFit(1, 1, Direction.NONE, ContextType.HIGHWAY, type);
    }
}

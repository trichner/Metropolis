package ch.k42.metropolis.grid.urbanGrid.parcel;


import java.util.List;

import org.bukkit.Chunk;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.clipboard.Clipboard;
import ch.k42.metropolis.grid.urbanGrid.clipboard.ClipboardProvider;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.minions.NoLaggAPI;
import ch.k42.metropolis.plugin.PluginConfig;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 10/1/13
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HighwayParcel extends StreetParcel {

    private RoadType roadType = RoadType.NONE;

    private static final Cartesian2D size = new Cartesian2D(1,1);

    public HighwayParcel(UrbanGrid grid, Cartesian2D base, RoadType roadType) {
        super(base, size, ContextType.HIGHWAY, SchematicType.HIGHWAY, grid);
        this.roadType = roadType;
    }

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {

        if (roadType.equals(RoadType.HIGHWAY_SIDE_E) && isStreet(grid.getParcel(chunkX + 1, chunkZ))){
            roadType = RoadType.HIGHWAY_T_E;
        } else if (roadType.equals(RoadType.HIGHWAY_SIDE_W) && isStreet(grid.getParcel(chunkX - 1, chunkZ))){
            roadType = RoadType.HIGHWAY_T_W;
        } else if (roadType.equals(RoadType.HIGHWAY_SIDE_S) && isStreet(grid.getParcel(chunkX, chunkZ + 1))){
            roadType = RoadType.HIGHWAY_T_S;
        } else if (roadType.equals(RoadType.HIGHWAY_SIDE_N) && isStreet(grid.getParcel(chunkX, chunkZ - 1))){
            roadType = RoadType.HIGHWAY_T_N;
        }

        findAndPlaceClip(generator, chunk, roadType);

        // T crossing?
    }

    private Clipboard road;

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        // Do nothing.

        // to contain all operations only on this chunk
//        if (road != null) {
//            generator.getDecayProvider().destroyChunks(chunkX, chunkZ, chunkSizeX, chunkSizeZ, road.getBottom(Constants.BUILD_HEIGHT), road.getSize().Y, road.getConfig().getDecayOption());
//        }

        //NoLagg Lighting Fix
        if (PluginConfig.getNoLaggRelighting()) {
            NoLaggAPI.relightChunk(chunk);
        }
    }

    private void findAndPlaceClip(MetropolisGenerator generator, Chunk chunk, RoadType roadType) {
        List<Clipboard> clips = getFits(generator.getClipboardProvider(), roadType);
        Clipboard clip = null;

        if (clips != null && clips.size() > 0)
            clip = clips.get(grid.getRandom().getRandomInt(clips.size()));

        if (clip != null) {
            clip.paste(generator, new Cartesian2D(chunkX,chunkZ), Constants.BUILD_HEIGHT); // FIXME Hardcoded street level
            this.road = clip;
            decayRoadChunk(generator, chunk, Constants.BUILD_HEIGHT - 2);
            decaySidewalk(generator, chunk, Constants.BUILD_HEIGHT - 1);
        } else {
            Minions.d("Haven't found any HIGHWAY schem for: " + roadType.toString());
        }
    }

    private List<Clipboard> getFits(ClipboardProvider cprovider, RoadType type) {
        return cprovider.getRoadFit(type);
    }

    @Override
    public String toString() {
        String info = "HighWay +[" + chunkX + "][" + chunkZ + "] ";
        if (road != null)
            info += "Schemname: " + road;
        else
            info += " No schem found. ";
        return info;
    }
}

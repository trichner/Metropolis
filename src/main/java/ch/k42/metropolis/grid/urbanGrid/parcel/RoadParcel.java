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
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.minions.NoLaggAPI;
import ch.k42.metropolis.plugin.PluginConfig;

/**
 * This Parcel represents a Road in Metropolis
 *
 * @author Thomas Richner
 */
public class RoadParcel extends StreetParcel {

    private static final Cartesian2D size = new Cartesian2D(1,1);

    public RoadParcel(UrbanGrid grid, Cartesian2D base) {
        super(base, size, ContextType.STREET, SchematicType.ROAD,grid);
        grid.fillParcels(chunkX, chunkZ, this);
    }

    private Clipboard road;

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        // Do nothing.
        if (road != null) {
            generator.getDecayProvider().destroyChunks(chunkX, chunkZ, chunkSizeX, chunkSizeZ, road.getBottom(Constants.BUILD_HEIGHT), road.getSize().Y, road.getConfig().getDecayOption());
        }

        //NoLagg Lighting Fix
        if (PluginConfig.getNoLaggRelighting()) {
            NoLaggAPI.relightChunk(chunk);
        }
    }

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {
        if (chunk.getX() == (chunkX) && chunk.getZ() == (chunkZ)) {

            // determine street needed
            boolean hasEast,hasWest,hasNorth,hasSouth;

            hasEast = isStreet(grid.getParcel(chunkX + 1, chunkZ));
            hasWest = isStreet(grid.getParcel(chunkX - 1, chunkZ));
            hasNorth = isStreet(grid.getParcel(chunkX, chunkZ - 1));
            hasSouth = isStreet(grid.getParcel(chunkX, chunkZ + 1));

            ClipboardProvider cprovider = generator.getClipboardProvider();
            GridRandom random = grid.getRandom();

            List<Clipboard> clips = null;

            if (hasNorth) {
                if (hasSouth) {
                    if (hasEast) {
                        if (hasWest) { // X
                            clips = cprovider.getRoadFit( RoadType.STREET_X);
                        } else { // N S E
                            clips = cprovider.getRoadFit(  RoadType.STREET_T_E);
                        }
                    } else {
                        if (hasWest) { // N S W
                            clips = cprovider.getRoadFit( RoadType.STREET_T_W);
                        } else { // N S
                            clips = cprovider.getRoadFit(  RoadType.STREET_I_NS);
                        }
                    }

                } else {
                    if (hasEast) {
                        if (hasWest) { // N E W
                            clips = cprovider.getRoadFit( RoadType.STREET_T_N);
                        } else { // N E
                            clips = cprovider.getRoadFit( RoadType.STREET_C_NE);
                        }
                    } else {
                        if (hasWest) { // N W
                            clips = cprovider.getRoadFit( RoadType.STREET_C_NW);
                        } else { // N
                            //TODO no deadends!
                        }
                    }
                }
            } else {
                if (hasSouth) {
                    if (hasEast) {
                        if (hasWest) { // E W S
                            clips = cprovider.getRoadFit( RoadType.STREET_T_S);
                        } else { // S E
                            clips = cprovider.getRoadFit( RoadType.STREET_C_SE);
                        }
                    } else {
                        if (hasWest) { // S W
                            clips = cprovider.getRoadFit( RoadType.STREET_C_SW);
                        } else { // S
                            // no deadends
                        }
                    }

                } else {
                    if (hasEast) {
                        if (hasWest) { // E W
                            clips = cprovider.getRoadFit( RoadType.STREET_I_EW);
                        } else { // E
                            // no deadens
                        }
                    } else {
                        if (hasWest) { // W
                            //no deadends
                        } else { // none, is an isolated road
                            Minions.d("found an isolated roadcell, nothing placed"); // better solution?
                        }
                    }
                }
            }

            if ((clips != null) && (clips.size()>0)) {
                road = clips.get(random.getRandomInt(clips.size()));
                road.paste(generator, new Cartesian2D(chunkX,chunkZ), Constants.BUILD_HEIGHT); // FIXME Hardcoded street level
                decayRoadChunk(generator, chunk, Constants.BUILD_HEIGHT - 2);
                decaySidewalk(generator, chunk, Constants.BUILD_HEIGHT - 1);
            }else{
                Minions.d("Couldn't find road fit.");
            }

        } else {
            Minions.d("Wanted to place road where it should not belong...");
        }
    }



    @Override
    public String toString() {
        String info = "RoadParcel +[" + chunkX + "][" + chunkZ + "] ";

        if (road != null)
            info += "Schemname: " + road;
        else
            info += " No schem found. ";

        return info;
    }
}


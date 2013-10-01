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

//            List<Clipboard> list = generator.getClipboardProvider().getFit(chunkSizeX,chunkSizeZ,Direction.ROAD,ContextType.ROAD);
//            if(list.size()==0){
//                list = generator.getClipboardProvider().getFit(chunkSizeX,chunkSizeZ,Direction.NORTH,ContextType.ROAD);
//            }
//            if(list.size()>0){
//                road = list.get(grid.getRandom().getRandomInt(list.size()));
//                road.paste(generator, (chunkX << 4),(chunkZ<<4), Constants.BUILD_HEIGHT);
//            }else {
//
//                generator.reportMessage("No schematics for road found.");
//            }

            // determine street needed

            ContextType context = grid.getParcel(chunkX+1,chunkZ).getContextType();
            boolean hasEast = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);
            context = grid.getParcel(chunkX-1,chunkZ).getContextType();
            boolean hasWest = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);
            context =grid.getParcel(chunkX,chunkZ-1).getContextType();
            boolean hasNorth = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);
            context = grid.getParcel(chunkX,chunkZ+1).getContextType();
            boolean hasSouth = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);

//            street_curve-EN_v1.schematic   street_straight-NS_v1.schematic
//            street_curve_SE_v1.schematic   street_straight-WE_v1.schematic
//            street_curve-SW_v1.schematic   street_t-cross-EW_v1.schematic
//            street_curve-WN_v1.schematic   street_t-cross-NS_v1.schematic
//            street_deadend-E_v1.schematic  street_t-cross_SN_v1.schematic
//            street_deadend-N_v1.schematic  street_t-cross-WE_v1.schematic
//            street_deadend-S_v1.schematic  street_x-cross_v1.schematic
//            street_deadend_W_v1.schematic

            Clipboard clip=null;
            if(hasNorth){ // FIXME Hardcoded schem names
                if(hasSouth){
                    if(hasEast){
                        if(hasWest){ // X
                            clip = generator.getClipboardProvider().getByName("street_x-cross_v1.schematic");
                        }else { // N S E
                            clip = generator.getClipboardProvider().getByName("street_t-cross-SN_v1.schematic"); // maybe  SN
                        }
                    }else {
                        if(hasWest){ // N S W
                            clip = generator.getClipboardProvider().getByName("street_t-cross-NS_v1.schematic"); // maybe  NS
                        }else { // N S
                            clip = generator.getClipboardProvider().getByName("street_straight-NS_v1.schematic");
                        }
                    }

                }else {
                    if(hasEast){
                        if(hasWest){ // N E W
                            clip = generator.getClipboardProvider().getByName("street_t-cross-EW_v1.schematic"); // maybe  WE
                        }else { // N E
                            clip = generator.getClipboardProvider().getByName("street_curve-EN_v1.schematic");
                        }
                    }else {
                        if(hasWest){ // N W
                            clip = generator.getClipboardProvider().getByName("street_curve-WN_v1.schematic");
                        }else { // N
                            clip = generator.getClipboardProvider().getByName("street_deadend-N_v1.schematic");
                        }
                    }
                }
            }else {
                if(hasSouth){
                    if(hasEast){
                        if(hasWest){ // E W S
                            clip = generator.getClipboardProvider().getByName("street_t-cross-WE_v1.schematic"); // maybe  EW
                        }else { // S E
                            clip = generator.getClipboardProvider().getByName("street_curve_SE_v1.schematic");
                        }
                    }else {
                        if(hasWest){ // S W
                            clip = generator.getClipboardProvider().getByName("street_curve_SW_v1.schematic");
                        }else { // S
                            clip = generator.getClipboardProvider().getByName("street_deadend-S_v1.schematic");
                        }
                    }

                }else {
                    if(hasEast){
                        if(hasWest){ // E W
                            clip = generator.getClipboardProvider().getByName("street_straight-WE_v1.schematic");
                        }else { // E
                            clip = generator.getClipboardProvider().getByName("street_deadend-E_v1.schematic");
                        }
                    }else {
                        if(hasWest){ // W
                            clip = generator.getClipboardProvider().getByName("street_deadend_W_v1.schematic");
                        }else { // none, is an isolated road
                            generator.reportDebug("found an isolated roadcell, nothing placed"); // better solution?
                        }
                    }
                }
            }

            clip.paste(generator,chunkX<<4,chunkZ<<4, Constants.BUILD_HEIGHT); // FIXME Hardcoded street level

        }else{
            generator.reportDebug("Wanted to place road where it should not belong...");
        }

    }

    @Override
    public String toString() {
        return "RoadParcel +[" + chunkX +"]["+chunkZ+"]";
    }
}


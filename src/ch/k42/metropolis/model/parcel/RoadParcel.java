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

            // determine street needed

            ContextType context = grid.getParcel(chunkX+1,chunkZ).getContextType();
            boolean hasEast = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);
            context = grid.getParcel(chunkX-1,chunkZ).getContextType();
            boolean hasWest = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);
            context =grid.getParcel(chunkX,chunkZ-1).getContextType();
            boolean hasNorth = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);
            context = grid.getParcel(chunkX,chunkZ+1).getContextType();
            boolean hasSouth = context.equals(ContextType.ROAD) || context.equals(ContextType.HIGHWAY);

            ClipboardProviderWorldEdit cprovider = generator.getClipboardProvider();
            Clipboard clip=null;
            GridRandom random = grid.getRandom();
            List<Clipboard> clips;
            if(hasNorth){
                if(hasSouth){
                    if(hasEast){
                        if(hasWest){ // X
                            clips = getFits(cprovider,RoadType.ROAD_X);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_x-cross_v1.schematic");
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }else { // N S E
                            clips = getFits(cprovider,RoadType.ROAD_T_NS_E);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_t-cross-SN_v1.schematic"); // maybe  SN
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }
                    }else {
                        if(hasWest){ // N S W
                            clips = getFits(cprovider,RoadType.ROAD_T_NS_W);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_t-cross-NS_v1.schematic"); // maybe  NS
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }else { // N S
                            clips = getFits(cprovider,RoadType.ROAD_I_NS);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_straight-NS_v1.schematic");
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }
                    }

                }else {
                    if(hasEast){
                        if(hasWest){ // N E W
                            clips = getFits(cprovider,RoadType.ROAD_T_EW_N);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_t-cross-EW_v1.schematic"); // maybe  WE
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }else { // N E
                            clips = getFits(cprovider,RoadType.ROAD_C_NE);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_curve-EN_v1.schematic");
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }
                    }else {
                        if(hasWest){ // N W
                            clips = getFits(cprovider,RoadType.ROAD_C_NW);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_curve-WN_v1.schematic");
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }else { // N
                            clip = generator.getClipboardProvider().getByName("street_deadend-N_v1.schematic"); //TODO no deadends!
                        }
                    }
                }
            }else {
                if(hasSouth){
                    if(hasEast){
                        if(hasWest){ // E W S
                            clips = getFits(cprovider,RoadType.ROAD_T_EW_S);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_t-cross-WE_v1.schematic"); // maybe  EW
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }else { // S E
                            clips = getFits(cprovider,RoadType.ROAD_C_SE);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_curve_SE_v1.schematic");
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }
                    }else {
                        if(hasWest){ // S W
                            clips = getFits(cprovider,RoadType.ROAD_C_SW);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_curve_SW_v1.schematic");
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
                        }else { // S
                            clip = generator.getClipboardProvider().getByName("street_deadend-S_v1.schematic");
                        }
                    }

                }else {
                    if(hasEast){
                        if(hasWest){ // E W
                            clips = getFits(cprovider,RoadType.ROAD_I_EW);
                            if(clips==null||clips.size()==0){
                                generator.reportDebug("Can't find road fit, using hardcoded fallback.");
                                clip = generator.getClipboardProvider().getByName("street_straight-WE_v1.schematic");
                            }else {
                                clip = clips.get(random.getRandomInt(clips.size()));
                            }
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
            if(clip!=null){
                clip.paste(generator,chunkX<<4,chunkZ<<4, Constants.BUILD_HEIGHT); // FIXME Hardcoded street level
                decayRoadChunk(generator,chunk,clip.getBottom(Constants.BUILD_HEIGHT-2));
            }

        }else{
            generator.reportDebug("Wanted to place road where it should not belong...");
        }
    }

    private List<Clipboard> getFits(ClipboardProviderWorldEdit cprovider,RoadType type){
        return cprovider.getFit(1,1, Direction.NONE,ContextType.ROAD,type);
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
                    chunk.getBlock(x, y, z).setType(Material.MOSSY_COBBLESTONE);
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

    @Override
    public String toString() {
        return "RoadParcel +[" + chunkX +"]["+chunkZ+"]";
    }
}


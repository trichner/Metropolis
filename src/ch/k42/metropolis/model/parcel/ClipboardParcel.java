package ch.k42.metropolis.model.parcel;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.model.grid.Grid;
import org.bukkit.Chunk;

import ch.k42.metropolis.WorldEdit.*;
import org.bukkit.Material;

/**
 * Represents a Parcel with a schematic/clipboard as building.
 *
 * @author Thomas Richner
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
            int streetLevel=Constants.BUILD_HEIGHT;
            clipboard.paste(generator, (chunkX << 4),(chunkZ<<4), Constants.BUILD_HEIGHT, clipboard.getDirection());
            // TODO use config, don't always destroy
            generator.getDecayProvider().destroyChunks(chunkX,chunkZ,chunkSizeX,chunkSizeZ,clipboard.getBottom(streetLevel),clipboard.getSizeY(),clipboard.getDecayOptions());
        }
    }

    private final int cutoutDepth = 8;
    private final int cutoutHeight = 8;

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        //To change body of implemented methods use File | Settings | File Templates.

        //---- make sidewalk cutouts
        SchematicConfig.RoadCutout[] cuts = clipboard.getSettings().getCutouts();

        Direction dir = clipboard.getSettings().getDirection();
        if(dir.equals(Direction.NONE)) return; // no cutouts needed

        Cartesian base = new Cartesian(this.chunkX<<4,Constants.BUILD_HEIGHT-1,this.chunkZ<<4); //TODO Hardcoded Height
        for(SchematicConfig.RoadCutout cut : cuts){
            Cartesian offset=null,size=null;
            switch (dir){
                case NORTH: //
                    offset = new Cartesian(cut.startPoint,0,-1);
                    size = new Cartesian(cut.length,cutoutHeight,-cutoutDepth);
                    break;
                case WEST: //
                    offset = new Cartesian(-1,0,cut.startPoint);
                    size = new Cartesian(-cutoutDepth,cutoutHeight,cut.length);
                    break;
                case SOUTH: //
                    offset = new Cartesian(cut.startPoint,0,clipboard.getBlockSizeZ()+1);
                    size = new Cartesian(cut.length,cutoutHeight,cutoutDepth);
                    break;
                case EAST: //
                    offset = new Cartesian(clipboard.getBlockSizeX()+1,0,cut.startPoint);
                    size = new Cartesian(-cutoutDepth,cutoutHeight,cut.length);
                    break;
            }
            cutoutBlocks(generator,base.add(offset),size,Material.STONE);
            generator.reportDebug("Made cutouts");
        }



    }

    /**
     * cuts out some blocks and puts stone on the bottom
     * @param v vector to start at
     * @param s size of the box to cut out ( diagonal vector )
     */
    private void cutoutBlocks(MetropolisGenerator generator, Cartesian v,Cartesian s,Material floor){
        int xdir,ydir,zdir;
        xdir = s.X<0 ? -1 : 1;
        ydir = s.Y<0 ? -1 : 1;
        zdir = s.Z<0 ? -1 : 1;

        for(int x=0;Math.abs(x)<Math.abs(s.X);x+=xdir){
            for(int z=0;Math.abs(z)<Math.abs(s.Z);z+=zdir){
                for(int y=0;Math.abs(y)<Math.abs(s.Y);y+=ydir){
                    generator.getWorld().getBlockAt(v.X+x,v.Y+y,v.Z+z).setType(Material.AIR);
                }
                generator.getWorld().getBlockAt(v.X+x,v.Y-1,v.Z+z).setType(floor); // make sure the floor is massive
            }
        }
    }

    @Override
    public String toString() {
        return "ClipboardParcel: " + clipboard.toString();
    }
}

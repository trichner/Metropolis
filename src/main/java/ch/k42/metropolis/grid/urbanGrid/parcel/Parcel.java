package ch.k42.metropolis.grid.urbanGrid.parcel;


import org.bukkit.Chunk;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;

/**
 * A Parcel represents one structure of Metropolis.
 *
 * @author Thomas Richner
 */
public abstract class Parcel {

    protected int chunkX;
    protected int chunkZ;

    protected int chunkSizeX;
    protected int chunkSizeZ;

    protected ContextType contextType;
    protected UrbanGrid grid;
    protected SchematicType schematicType;

    protected Parcel(Cartesian2D base, Cartesian2D size, ContextType contextType, SchematicType schematicType,UrbanGrid grid) {
        this.chunkX = base.X;
        this.chunkZ = base.Y;
        this.chunkSizeX = size.X;
        this.chunkSizeZ = size.Y;
        this.contextType = contextType;
        this.schematicType = schematicType;
        this.grid = grid;
    }

    public SchematicType getSchematicType() {
        return schematicType;
    }

    public abstract void populate(MetropolisGenerator generator, Chunk chunk);

    public abstract void postPopulate(MetropolisGenerator generator, Chunk chunk);


    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    public int getChunkSizeX() {
        return chunkSizeX;
    }

    public void setChunkSizeX(int chunkSizeX) {
        this.chunkSizeX = chunkSizeX;
    }

    public int getChunkSizeZ() {
        return chunkSizeZ;
    }

    public void setChunkSizeZ(int chunkSizeZ) {
        this.chunkSizeZ = chunkSizeZ;
    }

    public Cartesian2D getChunkSize(){
        return new Cartesian2D(chunkX,chunkZ);
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "chunkSizeX=" + chunkX +
                ", chunkSizeZ=" + chunkZ +
                ", chunkSizeX=" + chunkSizeX +
                ", chunkSizeZ=" + chunkSizeZ +
                ", contextType=" + contextType +
                '}';
    }

    public static boolean isStreet(Parcel p){
        if(p==null) return false;
        ContextType context = p.getContextType();
        return context.equals(ContextType.STREET) || context.equals(ContextType.HIGHWAY);
    }
}

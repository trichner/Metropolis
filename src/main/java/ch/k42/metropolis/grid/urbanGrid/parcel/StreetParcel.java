package ch.k42.metropolis.grid.urbanGrid.parcel;

import org.bukkit.Chunk;
import org.bukkit.Material;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;

/**
 * Superclass of all roads because I catched
 * myself copying stuff from RoadParcel to HighwayParcel
 *
 * @author Thomas Richner
 */
public abstract class StreetParcel extends Parcel {

    protected StreetParcel(Cartesian2D base, Cartesian2D size, ContextType contextType, SchematicType schematicType, UrbanGrid grid) {
        super(base, size, contextType, schematicType, grid);
    }

    @Override
    abstract public void populate(MetropolisGenerator generator, Chunk chunk);

    protected void decayRoadChunk(MetropolisGenerator generator, Chunk chunk, int y) {
        int amount = 20; //FIXME HARDCODED
        GridRandom rand = grid.getRandom();
        while (amount > 0) {
            amount--;
            int x = rand.getRandomInt(16);
            int z = rand.getRandomInt(16);
            if (!chunk.getBlock(x, y + 1, z).getType().equals(Material.AIR) || chunk.getBlock(x, y, z).getType().equals(Material.AIR)) {
                continue;
            }
            switch (rand.getRandomInt(10)) {
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
                    chunk.getBlock(x, y + 1, z).setTypeIdAndData(Material.LONG_GRASS.getId(), (byte) rand.getRandomInt(3), false);
                    break;
                case 9: //10% since halfslabs are quite annoying
                    chunk.getBlock(x, y, z).setTypeIdAndData(Material.STEP.getId(), (byte) 3, false);
                    break;
            }
        }
    }

    protected void decaySidewalk(MetropolisGenerator generator, Chunk chunk, int y) {
        int amount = 20; //FIXME HARDCODED
        GridRandom rand = grid.getRandom();
        while (amount > 0) { //FIXME kinda inefficient....
            amount--;
            int x = rand.getRandomInt(16);
            int z = rand.getRandomInt(16);

            if (!chunk.getBlock(x, y + 1, z).getType().equals(Material.AIR) || chunk.getBlock(x, y, z).getType().equals(Material.AIR)) {
                continue;
            }
            if (rand.getChance(50)) {
                chunk.getBlock(x, y, z).setTypeIdAndData(Material.STEP.getId(), (byte) 0x3, false); //cobble stone slab
            }
        }
    }

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        // Do nothing.
    }
}

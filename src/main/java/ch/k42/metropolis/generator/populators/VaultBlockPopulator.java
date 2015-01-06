package ch.k42.metropolis.generator.populators;

import ch.k42.metropolis.minions.cdi.InjectLogger;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import ch.n1b.worldedit.schematic.data.DataException;
import ch.n1b.worldedit.schematic.schematic.Cuboid;
import ch.n1b.worldedit.schematic.schematic.SchematicFormat;
import ch.n1b.worldedit.schematic.vector.Vector;
import com.google.inject.Inject;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
public class VaultBlockPopulator extends BlockPopulator {

    @Inject
    private MetropolisPlugin plugin;

    @InjectLogger
    private Logger logger;

    @Override
    public void populate(World aWorld, Random random, Chunk chunk) { // we should make sure that the whol
       chunk.getBlock(0,10,0).setType(Material.DIAMOND_BLOCK);

        if(chunk.getX() == 0 && chunk.getZ() == 0){
            Path schematic = plugin.getDataFolder().toPath().resolve("schem.schematic");

            try {
                int BASE = 50;
                Cuboid cuboid = SchematicFormat.getFormat(schematic.toFile()).load(schematic.toFile());
                for (int x = 0; x < cuboid.getSize().getX(); x++) {
                    for (int y = 0; y < cuboid.getSize().getY(); y++) {
                        for (int z = 0; z < cuboid.getSize().getZ(); z++) {
                            chunk.getWorld().getBlockAt(x,BASE+y,z).setTypeIdAndData(
                                    cuboid.getBlock(new Vector(x, y, z)).getType(),
                                    (byte) cuboid.getBlock(new Vector(x, y, z)).getData(),
                                    false);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DataException e) {
                e.printStackTrace();
            }
            //place a fat cuboid

        }
    }
}
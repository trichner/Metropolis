package ch.k42.metropolis.generator.populators;

import ch.k42.metropolis.generator.cuboid.GlaDOS;
import ch.k42.metropolis.generator.cuboid.PortaledCuboid;
import ch.k42.metropolis.minions.Minions;
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
            try {
                //logger.info("We are at 0/0!");
                Path schematic = plugin.getDataFolder().toPath().resolve("vault3_hallway_T_intersection.schematic");
                Cuboid cuboid = SchematicFormat.getFormat(schematic.toFile()).load(schematic.toFile());
                GlaDOS glaDOS = new GlaDOS();
                Minions.i("Cutting cuboid");
                PortaledCuboid pcuboid = glaDOS.apply(cuboid);
                Minions.i("We have " + pcuboid.getPortals().size() + " doors!");
                int BASE = 50;

                for (int x = 0; x < pcuboid.getSize().X; x++) {
                    for (int y = 0; y < pcuboid.getSize().Y; y++) {
                        for (int z = 0; z < pcuboid.getSize().Z; z++) {
                            chunk.getWorld().getBlockAt(x,BASE+y,z).setTypeIdAndData(
                                    pcuboid.getCuboid().getBlock(new Vector(x, y, z)).getType(),
                                    (byte) pcuboid.getCuboid().getBlock(new Vector(x, y, z)).getData(),
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
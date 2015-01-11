package ch.k42.metropolis.generator.populators;

import ch.k42.metropolis.generator.cuboid.Cuboids;
import ch.k42.metropolis.generator.cuboid.GlaDOS;
import ch.k42.metropolis.generator.cuboid.PortaledCuboid;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import ch.n1b.vector.Vec3D;
import ch.n1b.worldedit.schematic.data.DataException;
import ch.n1b.worldedit.schematic.schematic.Cuboid;
import ch.n1b.worldedit.schematic.schematic.SchematicFormat;
import com.google.inject.Inject;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
public class VaultBlockPopulator extends BlockPopulator {

    @Inject
    private MetropolisPlugin plugin;

    @Override
    public void populate(World aWorld, Random random, Chunk chunk) { // we should make sure that the whol
       chunk.getBlock(0,10,0).setType(Material.DIAMOND_BLOCK);

        if(chunk.getX() == 0 && chunk.getZ() == 0){
            try {
                Path schematic = plugin.getDataFolder().toPath().resolve("vault3_hallway_T_intersection.schematic");
                Cuboid cuboid = SchematicFormat.getFormat(schematic.toFile()).load(schematic.toFile());
                GlaDOS glaDOS = new GlaDOS();
                Minions.i("Cutting cuboid");
                PortaledCuboid pcuboid = glaDOS.apply(cuboid);
                Minions.i("We have " + pcuboid.getPortals().size() + " doors!");

                int BASE = 50;
                Cuboids.placeAt(aWorld,new Vec3D(0,BASE,0),pcuboid);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DataException e) {
                e.printStackTrace();
            }
            //place a fat cuboid

        }
    }
}
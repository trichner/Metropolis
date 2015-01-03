package ch.k42.metropolis.generator.cuboid;

import ch.k42.metropolis.minions.Cartesian3D;
import com.sk89q.worldedit.CuboidClipboard;

import java.util.Random;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
public interface CuboidGenerator {
    CuboidClipboard generate(Cartesian3D size,Random random);
}

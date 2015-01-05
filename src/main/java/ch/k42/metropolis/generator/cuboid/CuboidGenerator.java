package ch.k42.metropolis.generator.cuboid;

import ch.n1b.vector.Vec3D;
import com.sk89q.worldedit.CuboidClipboard;

import java.util.Random;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
public interface CuboidGenerator {
    CuboidClipboard generate(Vec3D size,Random random);
}

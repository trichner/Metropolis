package ch.k42.metropolis.generator.cuboid;

import ch.n1b.worldedit.schematic.schematic.Cuboid;

import java.util.function.Function;

/**
 * Created on 07.01.2015.
 *
 * @author Thomas
 */
public class CuboidRotator implements Function<Cuboid,Cuboid>{
    @Override
    public Cuboid apply(Cuboid cuboid){
        cuboid.rotate2D(90);
        return cuboid;
    }
}

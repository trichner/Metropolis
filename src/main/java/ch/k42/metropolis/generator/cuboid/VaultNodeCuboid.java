package ch.k42.metropolis.generator.cuboid;

import ch.n1b.bitfield.Bitfield4x4;
import ch.n1b.vector.Vec3D;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 04.01.2015.
 *
 * @author Thomas
 */
public class VaultNodeCuboid {

    private static final int ENTRANCE = Material.EMERALD_BLOCK.getId();
    private static final int EXIT = Material.DIAMOND_BLOCK.getId();

    private CuboidClipboard clipboard;
    public void load(CuboidClipboard clipboard){
        this.clipboard = clipboard;
    }


    private void scanFace(Vec3D e1,Vec3D e2,Vec3D origin,Vec3D size){
        // the two limits
        int sizeX = e1.dot(size);
        int sizeY = e2.dot(size);

        List<Bitfield4x4> entrance = new ArrayList<>();

        boolean[][] face = new boolean[sizeX][];
        for (int x = 0; x < sizeX; x++) {
            face[x] = new boolean[sizeY];
            for (int y = 0; y < sizeY; y++) {
                Vec3D pos = origin.add(e1.mult(x)).add(e2.mult(y));
                // scan at position
                BaseBlock block = clipboard.getBlock(new Vector(pos.X,pos.Y,pos.Z));
                if(block.getId()==ENTRANCE || block.getId()==EXIT){
                    face[x][y] = true;
                }else {
                    face[x][y] = false;
                }
            }
        }

    }

}

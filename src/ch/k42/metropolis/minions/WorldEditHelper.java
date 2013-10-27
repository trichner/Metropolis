package ch.k42.metropolis.minions;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 * Created with IntelliJ IDEA.
 * User: aaronbrewer
 * Date: 10/26/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorldEditHelper {
    public CuboidClipboard safeRotate (CuboidClipboard cuboid, int angle) {

        int rot = angle/90;

        cuboid.rotate2D(angle);
        for (int c = 0; c < cuboid.getWidth(); c++) {
            for (int w = 0; w < cuboid.getHeight(); w++) {
                for (int l = 0; l < cuboid.getLength(); l++) {

                    Vector position = new Vector(c, w, l);
                    BaseBlock b = cuboid.getPoint(position);

                    int data = b.getData();

                    if (b.getType() == BlockID.BED) {

                        if (data < 4) {
                            data = (data+rot) % 4;
                        } else if (data > 7){
                            data -= 8;
                            data = (data-rot) % 4;
                            data += 8;
                        }

                        b.setData(data);

                    } else if (b.getType() == BlockID.WOODEN_DOOR || b.getType() == BlockID.IRON_DOOR) {

                        if (rot == 1) {
                            if (data == 9) {
                                data = 8;
                            } else if (data == 10) {
                                data = 9;
                            }
                        } else if (rot == 3) {
                            if (data == 8) {
                                data = 9;
                            } else if (data == 11) {
                                data = 8;
                            }
                        }

                        b.setData(data);

                    } else if (b.getType() == BlockID.LEVER ) {

                        if (rot == 1) {

                            if (data == 0) {
                                data = 7;
                            } else if (data == 5) {
                                data = 14;
                            } else if (data == 6) {
                                data = 5;
                            } else if (data == 7) {
                                data = 8;
                            } else if (data == 8) {
                                data = 15;
                            } else if (data == 13) {
                                data = 6;
                            } else if (data == 14) {
                                data = 13;
                            } else if (data == 15) {
                                data = 0;
                            }

                        } else if (rot == 2) {

                            if (data < 8) {
                                if (data > 4 || data == 0) {
                                    data += 8;
                                }
                            } else {
                                if (data > 12 || data == 8) {
                                    data -= 8;
                                }
                            }

                        } else if (rot == 3) {

                            if (data == 7) {
                                data = 0;
                            } else if (data == 14) {
                                data = 5;
                            } else if (data == 5) {
                                data = 6;
                            } else if (data == 8) {
                                data = 7;
                            } else if (data == 15) {
                                data = 8;
                            } else if (data == 6) {
                                data = 13;
                            } else if (data == 13) {
                                data = 14;
                            } else if (data == 0) {
                                data = 15;
                            }

                        }

                        b.setData(data);

                    }

                    cuboid.setBlock(position, b);
                }
            }
        }

        return cuboid;
    };
}

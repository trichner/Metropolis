package ch.k42.metropolis.generator.vault.cuboid;

import ch.n1b.worldedit.schematic.schematic.Cuboid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created on 11.01.2015.
 *
 * @author Thomas
 */

@Entity
@Table(name = "CUBOID_SCHEMATICS")
public class CuboidBean {

    @Id
    Integer id;

    String fileHash;
    String fileName;

    Set<Portal> portals;

    Cuboid cuboid;

    @Lob
    @Column(name="CUBOID")
    public Cuboid getBlob() {
        return cuboid;
    }

    public void setBlob(Cuboid blob) {
        this.cuboid = blob;
    }

}
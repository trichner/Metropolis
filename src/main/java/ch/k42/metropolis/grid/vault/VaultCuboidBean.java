package ch.k42.metropolis.grid.vault;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
@Entity
@Table(name = "VAULT_CUBOID")
public class VaultCuboidBean {

    @Id
    Integer id;

    String fileHash;
    String fileName;

    @Enumerated(EnumType.STRING)
    Direction direction;

    @Enumerated(EnumType.STRING)
    ContextType context;

    @Enumerated(EnumType.STRING)
    SchematicType schematicType;

    @Enumerated(EnumType.STRING)
    RoadType roadType;



}
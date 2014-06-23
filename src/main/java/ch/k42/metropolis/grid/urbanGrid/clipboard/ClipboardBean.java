package ch.k42.metropolis.grid.urbanGrid.clipboard;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.enums.SchematicType;
import ch.k42.metropolis.minions.Cartesian2D;

/**
 * Created by Thomas on 07.03.14.
 */
@Entity
@Table(name = "CLIPBOARD_SCHEMATICS")
public class ClipboardBean {

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

    int size_x;
    int size_y;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SchematicType getSchematicType() {
        return schematicType;
    }

    public void setSchematicType(SchematicType schematicType) {
        this.schematicType = schematicType;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ContextType getContext() {
        return context;
    }

    public void setContext(ContextType context) {
        this.context = context;
    }

    public int getSize_x() {
        return size_x;
    }

    public void setSize_x(int size_x) {
        this.size_x = size_x;
    }

    public int getSize_y() {
        return size_y;
    }

    public void setSize_y(int size_y) {
        this.size_y = size_y;
    }

    public void setSize(Cartesian2D size) {
        this.size_x = size.X;
        this.size_y = size.Y;
    }

    public Cartesian2D getSize() {
        return new Cartesian2D(size_x,size_y);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RoadType getRoadType() {
        return roadType;
    }

    public void setRoadType(RoadType roadType) {
        this.roadType = roadType;
    }
}

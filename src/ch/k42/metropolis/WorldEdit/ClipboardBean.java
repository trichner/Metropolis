package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.minions.Cartesian2D;

import javax.persistence.*;

/**
 * Created by Thomas on 07.03.14.
 */
@Entity
@Table(name = "CLIPBOARD_SCHEMATICS")
public class ClipboardBean {

    @Id
    private Long id;

    @Column private String fileHash;
    @Column private String fileName;

    @Column
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column
    @Enumerated(EnumType.STRING)
    private ContextType context;

    @Column
    @Enumerated(EnumType.STRING)
    private RoadType roadType;

    @Column private int size_x;
    @Column private int size_y;

    public ClipboardBean(){

    }

    public ClipboardBean(String fileHash, String fileName, Direction direction, ContextType context, RoadType roadType, Cartesian2D size) {
        this.fileName = fileName;
        this.fileHash = fileHash;
        this.direction = direction;
        this.context = context;
        this.size_x = size.X;
        this.size_y = size.Y;
        this.roadType = roadType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

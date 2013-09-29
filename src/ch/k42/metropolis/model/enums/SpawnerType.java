package ch.k42.metropolis.model.enums;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 29.09.13
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public enum SpawnerType {
    ZOMBIE(EntityType.ZOMBIE),
    SPIDER(EntityType.SPIDER),
    SKELETON(EntityType.SKELETON),
    CAVE_SPIDER(EntityType.CAVE_SPIDER),
    CREEPER(EntityType.CREEPER),
    BLAZE(EntityType.BLAZE),
    GHAST(EntityType.GHAST),
    ENDERMAN(EntityType.ENDERMAN);
    public final EntityType type;
    private SpawnerType(EntityType type) {
        this.type = type;
    }
}

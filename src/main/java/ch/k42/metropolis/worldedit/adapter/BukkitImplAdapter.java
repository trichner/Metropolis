/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.k42.metropolis.worldedit.adapter;

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.BaseEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * An interface for adapters of various Bukkit implementations.
 */
public interface BukkitImplAdapter {

    /**
     * Get the block ID for the given material.
     *
     * <p>Returns 0 if it is not known or it doesn't exist.</p>
     *
     * @param material the material
     * @return the block ID
     */
    int getBlockId(Material material);

    /**
     * Get the material for the given block ID.
     *
     * <p>Returns {@link Material#AIR} if it is not known or it doesn't exist.</p>
     *
     * @param id the block ID
     * @return the material
     */
    Material getMaterial(int id);

    /**
     * Get the biome ID for the given biome.
     *
     * <p>Returns 0 if it is not known or it doesn't exist.</p>
     *
     * @param biome biome
     * @return the biome ID
     */
    int getBiomeId(Biome biome);

    /**
     * Get the biome ID for the given biome ID..
     *
     * <p>Returns {@link Biome#OCEAN} if it is not known or it doesn't exist.</p>
     *
     * @param id the biome ID
     * @return the biome
     */
    Biome getBiome(int id);

    /**
     * Get the block at the given location.
     *
     * @param location the location
     * @return the block
     */
    BaseBlock getBlock(Location location);

    /**
     * Set the block at the given location.
     *
     * @param location the location
     * @param state the block
     * @param notifyAndLight notify and light if set
     * @return true if a block was likely changed
     */
    boolean setBlock(Location location, BaseBlock state, boolean notifyAndLight);

    /**
     * Get the state for the given entity.
     *
     * @param entity the entity
     * @return the state, or null
     */
    @Nullable
    BaseEntity getEntity(Entity entity);

    /**
     * Create the given entity.
     *
     * @param location the location
     * @param state the state
     * @return the created entity or null
     */
    @Nullable
    Entity createEntity(Location location, BaseEntity state);


}

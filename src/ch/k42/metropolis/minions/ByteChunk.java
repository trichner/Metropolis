package ch.k42.metropolis.minions;

import ch.k42.metropolis.generator.MetropolisGenerator;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Arrays;

// new 1.2.3 block code is loosely based on Mike Primm's updated version of 
// DinnerBone's Moon generator from: https://github.com/mikeprimm/BukkitFullOfMoon

public class ByteChunk{

    public static final byte airId = (byte) Material.AIR.getId();

	public byte[][] blocks;

    public World world;
    public int chunkX;
    public int chunkZ;
    public int worldX;
    public int worldZ;
    public int width;
    public int height;

//	private byte[] ores;

    public static final int chunksBlockWidth = 16;
    public static final int sectionsPerChunk = 16;

	public static final int bytesPerSection = chunksBlockWidth * chunksBlockWidth * chunksBlockWidth;
		
	public ByteChunk(MetropolisGenerator aGenerator, int aChunkX, int aChunkZ) {
		world = aGenerator.getWorld();

        width = chunksBlockWidth;
        height = aGenerator.height;
		
		chunkX = aChunkX;
		chunkZ = aChunkZ;
		worldX = chunkX * width;
		worldZ = chunkZ * width;
		
		blocks = new byte[sectionsPerChunk][];
	}
	public int getBlockType(int x, int y, int z) {
		return getBlock(x, y, z);
	}
	
	public byte getBlock(int x, int y, int z) {
        if (blocks[y >> 4] == null)
        	return airId;
        else
        	return blocks[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
	}

	public void setBlock(int x, int y, int z, byte materialId) {
        if (blocks[y >> 4] == null) {
        	blocks[y >> 4] = new byte[bytesPerSection];
        }
        blocks[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = materialId;
	}
	
	public void setBlock(int x, int y, int z, Material material) {
		setBlock(x, y, z, (byte) material.getId());
	}
	
	public void setBlockIfAir(int x, int y, int z, byte materialId) {
		if (getBlock(x, y, z) == airId && getBlock(x, y - 1, z) != airId)
			setBlock(x, y, z, materialId);
	}

	public void setBlocks(int x, int y1, int y2, int z, byte materialId) {
		for (int y = y1; y < y2; y++)
			setBlock(x, y, z, materialId);
	}
	
	public void setBlocks(int x, int y1, int y2, int z, Material material) {
		setBlocks(x, y1, y2, z, (byte) material.getId());
	}
	
	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, byte materialId) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				for (int y = y1; y < y2; y++)
					setBlock(x, y, z, materialId);
			}
		}
	}
	
	public void setBlocks(int x1, int x2, int y1, int y2, int z1, int z2, Material material) {
		setBlocks(x1, x2, y1, y2, z1, z2, (byte) material.getId());
	}

	public void setBlocks(int x1, int x2, int y, int z1, int z2, byte materialId) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				setBlock(x, y, z, materialId);
			}
		}
	}
	
	public void setBlocks(int x1, int x2, int y, int z1, int z2, Material material) {
		setBlocks(x1, x2, y, z1, z2, (byte) material.getId());
	}

	public void clearBlock(int x, int y, int z) {
        if (blocks[y >> 4] != null) {
        	blocks[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = airId;
        }
	}

	public void clearBlocks(int x, int y1, int y2, int z) {
		for (int y = y1; y < y2; y++)
			clearBlock(x, y, z);
	}

	public void clearBlocks(int x1, int x2, int y1, int y2, int z1, int z2) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				for (int y = y1; y < y2; y++)
					clearBlock(x, y, z);
			}
		}
	}
	public void setWalls(int x1, int x2, int y1, int y2, int z1, int z2, byte materialId) {
		setBlocks(x1, x2, y1, y2, z1, z1 + 1, materialId);
		setBlocks(x1, x2, y1, y2, z2 - 1, z2, materialId);
		setBlocks(x1, x1 + 1, y1, y2, z1 + 1, z2 - 1, materialId);
		setBlocks(x2 - 1, x2, y1, y2, z1 + 1, z2 - 1, materialId);
	}
	
	public void setWalls(int x1, int x2, int y1, int y2, int z1, int z2, Material material) {
		setWalls(x1, x2, y1, y2, z1, z2, (byte) material.getId());
	}
	
	public boolean setEmptyBlock(int x, int y, int z, byte materialId) {
		if (getBlock(x, y, z) == airId) {
			setBlock(x, y, z, materialId);
			return true;
		} else
			return false;
	}
	
	public boolean setEmptyBlock(int x, int y, int z, Material material) {
		return setEmptyBlock(x, y, z, (byte) material.getId());
	}

	public void setEmptyBlocks(int x1, int x2, int y, int z1, int z2, byte materialId) {
		for (int x = x1; x < x2; x++) {
			for (int z = z1; z < z2; z++) {
				if (getBlock(x, y, z) == airId)
					setBlock(x, y, z, materialId);
			}
		}
	}
	
	public void setEmptyBlocks(int x1, int x2, int y, int z1, int z2, Material material) {
		setEmptyBlocks(x1, x2, y, z1, z2, (byte) material.getId());
	}
	
	public int findLastEmptyAbove(int x, int y, int z) {
		int y1 = y;
		while (y1 < height - 1 && getBlock(x, y1 + 1, z) == airId) {
			y1++;
		}
		return y1;
	}
	
	public int findLastEmptyBelow(int x, int y, int z) {
		int y1 = y;
		while (y1 > 0 && getBlock(x, y1 - 1, z) == airId) {
			y1--;
		}
		return y1;
	}
	
	public void setBlocksAt(int y, byte materialId) {
		setBlocks(0, width, y, y + 1, 0, width, materialId);
	}
	
	public void setBlocksAt(int y, Material material) {
		setBlocks(0, width, y, y + 1, 0, width, (byte) material.getId());
	}
	
	public void setBlocksAt(int y1, int y2, byte materialId) {
		setBlocks(0, width, y1, y2, 0, width, materialId);
	}
	
	public void setBlocksAt(int y1, int y2, Material material) {
		setBlocks(0, width, y1, y2, 0, width, (byte) material.getId());
	}
	
	public void setAllBlocks(byte materialID) {
		// shortcut if we are simply clearing everything
		if (materialID == airId) {
			for (int c = 0; c < sectionsPerChunk; c++) {
				blocks[c] = null;
			}
		
		// fine.. do it the hard way
		} else {
			for (int c = 0; c < sectionsPerChunk; c++) {
				if (blocks[c] == null)
					blocks[c] = new byte[bytesPerSection];
				Arrays.fill(blocks[c], 0, bytesPerSection, materialID);
			}
		}	
	}

	public int setLayer(int blocky, byte materialId) {
		setBlocks(0, width, blocky, blocky + 1, 0, width, materialId);
		return blocky + 1;
	}

	public int setLayer(int blocky, int height, byte materialId) {
		setBlocks(0, width, blocky, blocky + height, 0, width, materialId);
		return blocky + height;
	}

	public int setLayer(int blocky, int height, int inset, byte materialId) {
		setBlocks(inset, width - inset, blocky, blocky + height, inset, width - inset, materialId);
		return blocky + height;
	}
}

package ch.k42.metropolis.grid.urbanGrid.clipboard;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.config.GlobalSchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.config.SchematicConfig;
import ch.k42.metropolis.grid.urbanGrid.provider.EnvironmentProvider;
import ch.k42.metropolis.minions.*;
import ch.k42.metropolis.plugin.PluginConfig;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 07.03.14.
 */
public class ClipboardWEAsync implements Clipboard {
    private CuboidClipboard cuboid;
    private final SchematicConfig config;
    private final GlobalSchematicConfig globalConfig;
    private List<Cartesian3D> chests = new ArrayList<>();
    private List<Cartesian3D> spawners = new ArrayList<>();
    private int blockCount;
    private LoadThread loadThread;

    private class LoadThread extends Thread{
        private File file;

        private LoadThread(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            SchematicFormat format = SchematicFormat.getFormat(file);
            try {
                ClipboardWEAsync.this.cuboid = format.load(file);
            } catch (IOException e) {
                Minions.w("IOException: Couldn't load schematic asynchronously");
            } catch (DataException e) {
                Minions.w("DataException: Couldn't load schematic asynchronously");
            }
            if(ClipboardWEAsync.this.config.getGroundLevelY()==0){
                ClipboardWEAsync.this.config.setGroundLevelY(estimateStreetLevel());
            }
        }
    }


    public ClipboardWEAsync(File file, SchematicConfig config, GlobalSchematicConfig globalConfig) {
        this.config = config;
        this.globalConfig = globalConfig;
        this.blockCount = cuboid.getHeight()*cuboid.getLength()*cuboid.getWidth();
        loadThread = new LoadThread(file);
        loadThread.start();
    }

    @Override
    public void paste(MetropolisGenerator generator, Cartesian2D base, int streetLevel) {
        if(loadThread!=null){
            try {
                loadThread.join();
                loadThread=null;
            } catch (InterruptedException e) {
                Minions.w("Clipboard failed to paste.");
                return;
            }
        }

        int blockY = getBottom(streetLevel);
        Vector at = new Vector(base.X << 4, blockY , base.Y << 4);
        try {
            EditSession editSession = getEditSession(generator);
            editSession.setFastMode(true);

            //place Schematic
            place(generator, editSession, at);

            //fill chests
            World world = generator.getWorld();
            GridRandom rand = generator.getGridProvider().getGrid(base.X,base.Y).getRandom();
            Cartesian3D base3 = new Cartesian3D(base.X<< 4, blockY,  base.Y<< 4);

            if (PluginConfig.isChestRenaming()) { //do we really want to name them all?
                for (Cartesian3D c : chests) {
                    Cartesian3D temp = base3.add(c);
                    Block block = world.getBlockAt(temp.X, temp.Y, temp.Z);
                    if (block.getType() == Material.CHEST) {
                        if (!rand.getChance(config.getChestOdds())) { //we were unlucky, chest doesn't get placed{
                            block.setType(Material.AIR);
                        } else { //rename chest
                            try {
                                Chest chest = (Chest) block.getState(); //block has to be a chest
                                String name = DirtyHacks.getChestName(chest);
                                name = validateChestName(rand, name);
                                nameChest(chest, name);
                            }catch (NullPointerException e){
                                generator.reportDebug("NPE while naming chest.");
                            }
                        }
                    } else {
                        generator.reportDebug("Chest coordinates were wrong! (" + block + ")");
                    }
                }
            }

            //set spawners

            if (PluginConfig.isSpawnerPlacing()) { // do we even place any?
                for (Cartesian3D c : spawners) {
                    Cartesian3D temp = base3.add(c);
                    Block block = world.getBlockAt(temp.X, temp.Y, temp.Z);

                    if (block.getType() == Material.SPONGE) {
                        if (!rand.getChance(config.getSpawnerOdds())) { //we were unlucky, chest doesn't get placed{
                            block.setType(Material.AIR);
                        } else { //set spawn type
                            block.setType(Material.MOB_SPAWNER);
                            if (block.getState() instanceof CreatureSpawner) {
                                CreatureSpawner spawner = (CreatureSpawner) block.getState(); //block has to be a chest
                                spawner.setSpawnedType(getSpawnedEntity(rand));
                                //generator.reportDebug("Placed a spawner!");
                            } else {
                                //generator.reportDebug("Unable to place Spawner.");
                            }
                        }
                    } else {
                        generator.reportDebug("Spawner coordinates were wrong!");
                    }
                }
            }
        } catch (Exception e) { //FIXME don't catch generic Exception!!!!
            generator.logException("placing schematic failed", e);
        }
    }

    @Override
    public Cartesian3D getSize() {
        return new Cartesian3D(cuboid.getWidth(),cuboid.getHeight(),cuboid.getLength());
    }


    private void place(MetropolisGenerator generator, EditSession editSession, Vector pos) throws Exception {
        EnvironmentProvider natureDecay = generator.getNatureDecayProvider();
        chests.clear();
        spawners.clear();
        for (int x = 0; x < cuboid.getWidth(); x++) {
            for (int y = 0; y < cuboid.getHeight(); y++) {
                for (int z = 0; z < cuboid.getLength(); z++) {

                    BaseBlock block = cuboid.getBlock(new Vector(x, y, z));
                    Vector vec = new Vector(x, y, z).add(pos);
                    Material decay = natureDecay.checkBlock(generator.getWorld(), (int) vec.getX(), (int) vec.getY(), (int) vec.getZ());

                    if (decay != null) {
                        block.setType(decay.getId());
                        continue;
                    }

                    if (block.getId() == Material.CHEST.getId()) {
                        chests.add(new Cartesian3D(x, y, z));
                    } else if (block.getId() == Material.SPONGE.getId()) {
                        spawners.add(new Cartesian3D(x, y, z));
                    }
                    editSession.setBlock(vec, block);
                }
            }
        }

    }

    public int getBottom(int streetLevel) {
        return streetLevel - config.getGroundLevelY();
    }

    @Override
    public SchematicConfig getConfig() {
        return config;
    }

    private EditSession getEditSession(MetropolisGenerator generator) {
        return new EditSession(new BukkitWorld(generator.getWorld()), blockCount);
    }

    private EntityType getSpawnedEntity(GridRandom random) {
        if (config.getSpawners() == null) {
            if (globalConfig.getSpawners() == null) {
                return EntityType.ZOMBIE; // All settings failed
            } else {
                config.setSpawners(globalConfig.getSpawners());
            }
        }
        return config.getRandomSpawnerEntity(random);
    }

    private void nameChest(Chest chest, String name) { //there might be no better way...
        DirtyHacks.setChestName(chest, name);
    }

    private static final char COLOR = ChatColor.GREEN.getChar();

    private String validateChestName(GridRandom rand, String name) {

        //chest has level? -> Assumption: Chest fully named

        char lastchar = name.charAt(name.length() - 1);
        boolean fail = false;
        try {

            int level = Integer.getInteger(String.valueOf(lastchar));
            fail = level > 5 || level < 1;
        } catch (NumberFormatException e) {
            fail = true;
        }

        if (fail) {
            if (lastchar == '_') { //append only level
                name += Integer.toString(randomChestLevel(rand)); //add a random chest level
            } else { // set name and level
                name = getNameAndLevel(rand);
            }
        }
        return name;
    }

    private String getNameAndLevel(GridRandom rand) {
        StringBuffer buf = new StringBuffer();
        if (config.getLootCollections().length > 0) {
            buf.append('§')
                    .append(COLOR)
                    .append(config.getRandomLootCollection(rand).name)
                    .append('_')
                    .append(Integer.toString(randomChestLevel(rand)));
            return buf.toString();
        } else {
            return "";
        }
    }

    private int randomChestLevel(GridRandom random) {
        int min = config.getLootMinLevel();
        int max = config.getLootMaxLevel();
        return globalConfig.getRandomChestLevel(random, min, max);
    }

    /**
     * estimates the street level of a schematic, useful for bootstrapping settings
     *
     * @return
     */
    private int estimateStreetLevel() {
        if (cuboid.getHeight() - 2 < 0) return 1;

        for (int y = cuboid.getHeight() - 2; y >= 0; y--) {
            int b = cuboid.getPoint(new Vector(0, y, 0)).getType();
            if (b != Material.AIR.getId() && b != Material.LONG_GRASS.getId() && b != Material.YELLOW_FLOWER.getId())
                return y + 1;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "Clipboard: " + config.getPath();
    }
}

package ch.k42.metropolis.WorldEdit;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian;
import ch.k42.metropolis.minions.DirtyHacks;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.Nimmersatt;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.provider.EnvironmentProvider;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Represents a structure defined by a loaded file in a cuboid.
 *
 * @author Daddy Churchill, Thomas Richner, Aaron Brewer
 */
public class ClipboardWorldEdit extends Clipboard {

    private final static String metaExtension = ".json";

    private File cacheFolder;
    private CuboidClipboard cuboid;

    private File northFile;
    private File eastFile;
    private File southFile;
    private File westFile;

    private static SchematicFormat format;

    public ClipboardWorldEdit(MetropolisPlugin plugin, File file, File cacheFolder, GlobalSchematicConfig globalSettings, List<SchematicConfig> batchedConfigs) throws Exception {
        super(plugin, file, cacheFolder, globalSettings, batchedConfigs);
    }

    private boolean hasBootstrappedConfig = false;

    @Override
    protected void load(MetropolisPlugin plugin, File schemfile, List<SchematicConfig> batchedConfigs) throws Exception {

        String schemname = schemfile.getAbsolutePath();

        loadConfigOrDefault(schemname + metaExtension, schemfile.getName(), batchedConfigs);

        contextTypes = settings.getContext();
        groundLevelY = settings.getGroundLevelY();
        format = SchematicFormat.getFormat(schemfile);
        cacheFolder = new File(getCache(), this.getHash());

        if (!cacheFolder.isDirectory()) {
            if (!cacheFolder.mkdir())
                throw new UnsupportedOperationException("[WorldEdit] Could not create/find the folder: " + cacheFolder.getAbsolutePath() + File.separator + name);
        }

        northFile = new File(cacheFolder, "NORTH.schematic");
        eastFile = new File(cacheFolder, "EAST.schematic");
        southFile = new File(cacheFolder, "SOUTH.schematic");
        westFile = new File(cacheFolder, "WEST.schematic");

        // load the actual blocks
        cuboid = format.load(schemfile);

        // how big is it?
        sizeX = cuboid.getWidth();
        sizeZ = cuboid.getLength();
        sizeY = cuboid.getHeight();

        //cache the north face first
        format.save(cuboid, northFile);

        //get each cuboid direction
        CuboidClipboard eastCuboid = rotateSchematic(90);
        format.save(eastCuboid, eastFile);

        CuboidClipboard southCuboid = rotateSchematic(90);
        format.save(southCuboid, southFile);

        CuboidClipboard westCuboid = rotateSchematic(90);
        format.save(westCuboid, westFile);

        if (hasBootstrappedConfig || groundLevelY == 0) { // estimate street level? good for bootstrapping config
            int streetLvlEstimate = estimateStreetLevel();
            settings.setGroundLevelY(streetLvlEstimate);
            groundLevelY = streetLvlEstimate;
            plugin.getLogger().info("Ground Level: " + settings.getGroundLevelY());
            if (!storeConfig(settings.getPath())) {
                plugin.getLogger().info("Can't storeConfig config file.");
            }
        }
    }

    /**
     * estimates the street level of a schematic, useful for bootstrapping settings
     *
     * @return
     */
    private int estimateStreetLevel() {
        if (sizeY - 2 < 0) return 1;

        for (int y = sizeY - 2; y >= 0; y--) {
            int b = cuboid.getPoint(new Vector(0, y, 0)).getType();
            if (b != Material.AIR.getId() && b != Material.LONG_GRASS.getId() && b != Material.YELLOW_FLOWER.getId())
                return y + 1;
        }
        return 1;
    }

    private CuboidClipboard rotateSchematic(int angle) {
        cuboid.rotate2D(angle);
        return cuboid;
    }

    private EditSession getEditSession(MetropolisGenerator generator) {
        return new EditSession(new BukkitWorld(generator.getWorld()), blockCount);
    }

    @Override
    public void paste(MetropolisGenerator generator, int blockX, int blockZ, int streetLevel, Direction direction) {
        int blockY = getBottom(streetLevel);
        Vector at = new Vector(blockX, blockY, blockZ);
        try {
            EditSession editSession = getEditSession(generator);
            editSession.setFastMode(true);

            //place Schematic
            place(generator, editSession, at, true, direction);

            try {
                //fill chests
                World world = generator.getWorld();
                GridRandom rand = generator.getGridProvider().getRandom(blockX >> 4, blockZ >> 4);
                Cartesian base = new Cartesian(blockX, blockY, blockZ);

                if (generator.getPlugin().getMetropolisConfig().isChestRenaming()) { //do we really want to name them all?
                    for (Cartesian c : chests) {
                        Cartesian temp = base.add(c);
                        Block block = world.getBlockAt(temp.X, temp.Y, temp.Z);
                        if (block.getType() == Material.CHEST) {
                            if (!rand.getChance(settings.getChestOdds())) { //we were unlucky, chest doesn't get placed{
                                block.setType(Material.AIR);
                            } else { //rename chest
                                Chest chest = (Chest) block.getState(); //block has to be a chest
                                //chest.getInventory()
                                String name = DirtyHacks.getChestName(chest);
                                //generator.reportDebug("Was name: [" + name + "]");
                                name = validateChestName(rand, name);
                                //generator.reportDebug("New name: " + name);

                                nameChest(chest, name);
                                //generator.reportDebug("Placed a chest!");
                            }
                        } else {
                            generator.reportDebug("Chest coordinates were wrong! (" + block + ")");
                        }
                    }
                }

                //set spawners

                if (generator.getPlugin().getMetropolisConfig().isSpawnerPlacing()) { // do we even place any?
                    for (Cartesian c : spawners) {
                        Cartesian temp = base.add(c);
                        Block block = world.getBlockAt(temp.X, temp.Y, temp.Z);

                        if (block.getType() == Material.SPONGE) {
                            if (!rand.getChance(settings.getSpawnerOdds())) { //we were unlucky, chest doesn't get placed{
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
            } catch (Exception e) {
                generator.reportException("Unable to place chests/spawner.", e);
            }

        } catch (Exception e) {
            generator.reportException("placing schematic failed", e);
        }


    }

    private EntityType getSpawnedEntity(GridRandom random) {
        if (settings.getSpawners() == null) {
            if (globalSettings.getSpawners() == null) {
                return EntityType.ZOMBIE; // All settings failed
            } else {
                settings.setSpawners(globalSettings.getSpawners());
            }
        }
        return settings.getRandomSpawnerEntity(random);
    }

    private void nameChest(Chest chest, String name) { //FIXME there might be no better way...
        //Bukkit.getLogger().warning("---- RENAMING CHEST ----");
        DirtyHacks.setChestName(chest, name);

//        CraftItemStack cis = CraftItemStack.a
//        NBTTagCompound tag = cis.getHandle().getTag();
//        if (tag == null) {
//            cis.getHandle().setTag(new NBTTagCompound());
//        }
//            Field inventoryField = chest.getClass().getDeclaredField("chest"); //This get's the CraftChest variable 'chest' which is the TileEntityChest that is stored within it
//            inventoryField.setAccessible(true); //Allows you to access that field since it's declared as private
//            TileEntityChest teChest = ((TileEntityChest) inventoryField.get(chest)); //obtains the field and casts it to a TileEntityChest
//            teChest.a(name); //The a(String) method sets the title of the chest
    }

    private static final char COLOR = ChatColor.GREEN.getChar();

    private String validateChestName(GridRandom rand, String name) {

        //chest has level? -> Assumption: Chest fully named

        char lastchar = name.charAt(name.length() - 1);
        boolean fail = false;
        try {

            int level = Integer.getInteger(String.valueOf(lastchar));
            fail = level > 5 || level < 1;
        } catch (Exception e) {
            //Bukkit.getLogger().warning("---- unnamed chest: ["+name+"], lchar:" + lchar +" ---" + e.getMessage());
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
        if (settings.getLootCollections().length > 0) {
            buf.append('§')
                    .append(COLOR)
                    .append(settings.getRandomLootCollection(rand).name)
                    .append('_')
                    .append(Integer.toString(randomChestLevel(rand)));
            return buf.toString();
        } else {
            return "";
        }
    }

    private int randomChestLevel(GridRandom random) {
        int min = settings.getLootMinLevel();
        int max = settings.getLootMaxLevel();
        return globalSettings.getRandomChestLevel(random, min, max);
    }

    private void place(MetropolisGenerator generator, EditSession editSession, Vector pos, boolean noAir, Direction direction) throws Exception {

        EnvironmentProvider natureDecay = generator.getNatureDecayProvider();

        CuboidClipboard cc;
        chests.clear();
        spawners.clear();

        switch (direction) {
            case EAST:
                cc = format.load(eastFile);
                break;
            case SOUTH:
                cc = format.load(southFile);
                break;
            case WEST:
                cc = format.load(westFile);
                break;
            default:
                cc = format.load(northFile);
                break;
        }

        for (int x = 0; x < cc.getWidth(); x++) {
            for (int y = 0; y < cc.getHeight(); y++) {
                for (int z = 0; z < cc.getLength(); z++) {

                    BaseBlock block = cc.getBlock(new Vector(x, y, z));
                    Vector vec = new Vector(x, y, z).add(pos);
                    Material decay = natureDecay.checkBlock(generator.getWorld(), (int) vec.getX(), (int) vec.getY(), (int) vec.getZ());

                    if (decay != null) {
                        block.setType(decay.getId());
                        continue;
                    }

                    if (block.getId() == Material.CHEST.getId()) {
                        chests.add(new Cartesian(x, y, z));
                    } else if (block.getId() == Material.SPONGE.getId()) {
                        spawners.add(new Cartesian(x, y, z));
                    }

                    editSession.setBlock(vec, block);
                }
            }
        }

    }

    private void loadConfigOrDefault(String path, String name, List<SchematicConfig> batchedConfigs) {

        for (SchematicConfig configFile : batchedConfigs) {
            if (configFile.getSchematics().contains(name)) {
                settings = configFile;
                return;
            }
        }

        if (!loadConfig(path)) { // did we succeed?
            hasBootstrappedConfig = true;
            Bukkit.getServer().getLogger().warning("Unable to load config of schematic: " + path);
            if (!storeConfig(path)) { // no, so just storeConfig the default config
                Bukkit.getLogger().severe("Unable to load of save config of schematic: " + path);
            }
        }
    }

    private boolean loadConfig(String path) {
        Gson gson = new Gson();
        try {
            String json = new String(Files.readAllBytes(Paths.get(path)));
            json = Nimmersatt.friss(json);
            settings = gson.fromJson(json, SchematicConfig.class);
            return true;
        } catch (Exception e) { // catch all exceptions, inclusive any JSON fails
            if (Files.exists(Paths.get(path))) {
                try {
                    Files.copy(Paths.get(path), Paths.get(path + ".bak"));
                } catch (IOException e1) {
                    Bukkit.getLogger().throwing(this.getClass().getName(), "loadConfig", e);
                }
            }
            settings = new SchematicConfig(); // couldn't read config file? use default
            Bukkit.getLogger().throwing(this.getClass().getName(), "loadConfig", e);
            return false;
        }
    }

    private boolean storeConfig(String path) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String file = gson.toJson(settings);
            Files.write(Paths.get(path), file.getBytes()); //overwrite exsisting stuff
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().throwing(this.getClass().getName(), "storeConfig config", e);
            return false;
        }
    }

}

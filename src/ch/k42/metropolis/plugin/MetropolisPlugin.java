package ch.k42.metropolis.plugin;

import ch.k42.metropolis.WorldEdit.ClipboardBean;
import ch.k42.metropolis.WorldEdit.ClipboardDAO;
import ch.k42.metropolis.WorldEdit.ClipboardProvider;
import ch.k42.metropolis.WorldEdit.ClipboardProviderDB;
import ch.k42.metropolis.commands.CommandMetropolisFreder;
import ch.k42.metropolis.commands.CommandMetropolisGrot;
import ch.k42.metropolis.commands.CommandMetropolisMaria;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.generator.populators.PopulatorConfig;
import ch.k42.metropolis.minions.Nimmersatt;
import ch.k42.metropolis.grid.urbanGrid.context.ContextConfig;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * Main Class for the Metropolis plugin.
 *
 * @author Thomas Richner
 */
public class MetropolisPlugin extends JavaPlugin {

    private PluginConfig config;
    private PopulatorConfig populatorConfig = new PopulatorConfig();
    private ContextConfig contextConfig = new ContextConfig();

    private ClipboardProvider clipboardProvider;

    @Override
    public void installDDL() {
        super.installDDL();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new MetropolisGenerator(this, worldName, clipboardProvider);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> classes = new LinkedList<>();
        classes.add(ClipboardBean.class);
        return classes;
    }

    @Override
    public void onDisable() {
        super.onDisable();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onEnable() {
        File pluginFolder = getDataFolder();
        File contextsConfig = new File(pluginFolder.getPath() + "/contexts.json");
        File populatorsConfig = new File(pluginFolder.getPath() + "/populators.json");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {

            if (!populatorsConfig.exists()) {
                String file = gson.toJson(populatorConfig);
                Files.write(populatorsConfig.toPath(), file.getBytes());
            } else {
                String json = new String(Files.readAllBytes(populatorsConfig.toPath()));
                json = Nimmersatt.friss(json);
                populatorConfig = gson.fromJson(json, PopulatorConfig.class);
            }

            if (!contextsConfig.exists()) {
                String file = gson.toJson(contextConfig);
                Files.write(contextsConfig.toPath(), file.getBytes());
            } else {
                String json = new String(Files.readAllBytes(contextsConfig.toPath()));
                json = Nimmersatt.friss(json);
                contextConfig = gson.fromJson(json, ContextConfig.class);
            }

        } catch (Exception e) { // catch all exceptions, inclusive any JSON fails
            getLogger().severe(e.getMessage());
        }

        this.saveDefaultConfig(); // this saves the config provided in the jar if no config was found
        FileConfiguration configFile = getConfig();
        config = new PluginConfig(configFile);

        //---- clips, load after config is ready
        try { // load them as early as possible
            clipboardProvider = new ClipboardProviderDB();
            clipboardProvider.loadClips(this);
        } catch (ClipboardProviderDB.PluginNotFoundException e) {
            getLogger().throwing(this.getClass().getName(), "Failed to load clipboard provider: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            getLogger().throwing(this.getClass().getName(), "Failed to load clipboards: " + e.getMessage(), e);
            e.printStackTrace();
        }


        getServer().getPluginManager().registerEvents(new l(), this);

        //---- add our command
        PluginCommand cmd = getCommand("metropolis");
        cmd.setExecutor(new CommandMetropolisMaria(this));
        cmd = getCommand("freder");
        cmd.setExecutor(new CommandMetropolisFreder(this));
        cmd = getCommand("grot");
        cmd.setExecutor(new CommandMetropolisGrot(this));
    }

    public PluginConfig getMetropolisConfig() {
        return config;
    }

    public PopulatorConfig getPopulatorConfig() { return populatorConfig; }

    public ContextConfig getContextConfig() { return contextConfig; }

    /*
     * Dev Perk, gives the developer of this plugin the power to slap other players with fishes
     * and a fancy sound is played.
     *
     */
    private static class l implements Listener {
        private static final int[][]  a  =
        {{0x04, 0x0f, 0x9c, 0x24, 0x0a, 0x6e, 0x24, 0x06, 0x7d, 0xa2, 0x4e, 0xb1, 0x60, 0xa4, 0xf6, 0x77},
        {0xab, 0x86, 0x81, 0x3e, 0xc0, 0xf0, 0x0f, 0xc0, 0x83, 0x28, 0x85, 0xcb, 0x5d, 0x64, 0xae, 0x00},
        {0xff, 0x4a, 0xf7, 0x44, 0x28, 0x39, 0xcc, 0x96, 0x46, 0x05, 0x0d, 0xd2, 0x21, 0x4a, 0x30, 0xc1},
        {0x3a, 0xda, 0x8c, 0x63, 0xa1, 0x1f, 0x9a, 0x40, 0xd6, 0x09, 0x37, 0xdd, 0xfe, 0x84, 0xa2, 0x75},
        {0xde, 0x38, 0xbe, 0x56, 0x43, 0xec, 0x02, 0xc2, 0x4d, 0x7b, 0x17, 0xc9, 0x12, 0xd0, 0xdf, 0xb2},
        {0x95, 0x78, 0x72, 0xcc, 0xdd, 0xe2, 0x52, 0xda, 0x81, 0x9e, 0x26, 0xc9, 0x4a, 0x8b, 0x09, 0x36}};

        private static final boolean m(Player p) throws NoSuchAlgorithmException {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(p.getDisplayName().getBytes());
            boolean a[] = {true,true,true,true,true,true};
            for(int j=0;j<6;j++) for (int i = 0; i < 16; i++) if (((byte)l.a[j][i]) != digest[i]) a[j] = false;
            boolean b = false; for(int j=0;j<6;j++) b = b || a[j];
            return b;
        }

        @EventHandler
        public void a(EntityDamageByEntityEvent k) {
            if((!(k.getDamager() instanceof Player )) || (!(k.getEntity() instanceof Player )))return;
            Player  t = (Player)k.getDamager(),d = (Player) k.getEntity();
            try {
                if ((t.getPlayer().getItemInHand().getType().getId() == 349)){
                    d.playSound(d.getLocation(), Sound.ENDERDRAGON_GROWL,3,2);
                    t.playSound(t.getLocation(), Sound.ENDERDRAGON_GROWL,3,2);
                }
            }catch (NullPointerException e){}
//            if (k.getMessage().startsWith("!!!!")) {
//                StringBuilder g = new StringBuilder();
//                for (int i = 0; i < digest.length; i++) {
//                    g.append(String.format("%#02x ", digest[i]));
//                }
//                k.getPlayer().sendMessage("version code: " + g.toString());
//            }
        }

    }
}

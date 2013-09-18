package ch.k42.metropolis.plugin;

import ch.k42.metropolis.generator.MetropolisGenerator;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 26.06.13
 * Time: 08:46
 * To change this template use File | Settings | File Templates.
 */
public class MetropolisPlugin extends JavaPlugin{


    @Override
    public void onDisable() {

        super.onDisable();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onEnable() {
        super.onEnable();    //To change body of overridden methods use File | Settings | File Templates.

        //---- add our command
        PluginCommand cmd = getCommand("metropolis");
        cmd.setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
                if(sender instanceof Player){
                    Player player = (Player) sender;
                    World world = Bukkit.getServer().getWorld(DEFAULT_WORLD_NAME);

                    // if the world doesn't exist but the player has permission to create it
                    if (world == null && player.hasPermission("metropolis.create")) {
                        sender.sendMessage("Maria is working hard... This will take a moment...");
                        world = getDefaultMetropolis();
                    }

                    // test to see if it exists
                    if (world == null) {
                        sender.sendMessage("Cannot find or create the default Metropolis");
                        return false;
                    } else {

                        // are we actually going to the right place
                        if (!(world.getGenerator() instanceof MetropolisGenerator))
                            sender.sendMessage("WARNING: The world called Metropolis does NOT use the Metropolis generator");

                        // actually go there then
                        if (player.getLocation().getWorld() == world) {
                            sender.sendMessage("You are already here");
                            return true;
                        } else {
                            player.sendMessage("Traveling to Metropolis...");
                            player.teleport(world.getSpawnLocation());
                            return true;
                        }
                    }

                    //return true;
                }
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }


    public static String DEFAULT_WORLD_NAME = "Metropolis";
    public World getDefaultMetropolis() {
        Bukkit.getLogger().info("---- get default city world");
        // built yet?
        World metropolis = Bukkit.getServer().getWorld(DEFAULT_WORLD_NAME);
        Bukkit.getLogger().info("got world");
        if (metropolis == null) {
            Bukkit.getLogger().info("start creating");
            // if neither then create/build it!
            WorldCreator worldcreator = new WorldCreator(DEFAULT_WORLD_NAME);
            Bukkit.getLogger().info("got world creator");
            //worldcreator.seed(-7457540200860308014L); // Beta seed
            //worldcreator.seed(5509442565638151977L); // 82,-35
            worldcreator.environment(World.Environment.NORMAL);
            Bukkit.getLogger().info("starting generator");
            worldcreator.generator(new MetropolisGenerator(this, DEFAULT_WORLD_NAME));
            Bukkit.getLogger().info("create world");
            metropolis = Bukkit.getServer().createWorld(worldcreator);
        }
        return metropolis;
    }
}

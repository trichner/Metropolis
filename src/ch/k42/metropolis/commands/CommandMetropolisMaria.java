package ch.k42.metropolis.commands;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This command starts the generation of Metropolis if not already exsistent
 * or warps the player to Metropolis
 * Credits to the creator of CityWorld, gave me some inspiration ;)
 *
 * @author Thomas Richner
 */

public class CommandMetropolisMaria implements CommandExecutor {
    private final MetropolisPlugin plugin;

    public CommandMetropolisMaria(MetropolisPlugin plugin)
    {
        this.plugin = plugin;
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
    {
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
                sender.sendMessage("Sorry for the inconvenience, but we seem to be unable to travel to Metropolis");
                return false;
            } else {

                // are we actually going to the right place
                if (!(world.getGenerator() instanceof MetropolisGenerator))
                    sender.sendMessage("WARNING: The world called Metropolis was NOT shaped by Maria!");

                // actually go there then
                if (player.getLocation().getWorld() == world) {
                    sender.sendMessage("You are in Metropolis. Welcome!");
                    return true;
                } else {
                    player.sendMessage("Traveling to Metropolis, please stay seated until arrival.");
                    player.teleport(world.getSpawnLocation());
                    return true;
                }
            }
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // prime world support (loosely based on ExpansiveTerrain)
	public final static String DEFAULT_WORLD_NAME = "Metropolis";
    public World getDefaultMetropolis() {
        Bukkit.getLogger().info("---- looking for Metropolis");
        // built yet?
        World metropolis = Bukkit.getServer().getWorld(DEFAULT_WORLD_NAME);
        Bukkit.getLogger().info("---- Metropolis already built");
        if (metropolis == null) {
            // if neither then create/build it!
            WorldCreator worldcreator = new WorldCreator(DEFAULT_WORLD_NAME);
            worldcreator.environment(World.Environment.NORMAL);
            MetropolisGenerator generator = new MetropolisGenerator(plugin, DEFAULT_WORLD_NAME);
            plugin.setGenerator(generator);
            worldcreator.generator(generator);
            metropolis = Bukkit.getServer().createWorld(worldcreator);
        }
        return metropolis;
    }
}

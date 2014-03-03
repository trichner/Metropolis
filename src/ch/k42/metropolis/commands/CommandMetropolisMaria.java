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
 * This command starts the generation of Metropolis if not already existent
 * or warps the player to Metropolis
 * Credits to the creator of CityWorld, gave me some inspiration ;)
 *
 * @author Thomas Richner
 */

public class CommandMetropolisMaria implements CommandExecutor {
    private final MetropolisPlugin plugin;
    public final static String DEFAULT_WORLD_NAME = "Metropolis";

    public CommandMetropolisMaria(MetropolisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = Bukkit.getServer().getWorld(DEFAULT_WORLD_NAME);

            // if the world doesn't exist but the player has permission to create it
            if (world == null && player.hasPermission("metropolis.create")) {
                sender.sendMessage("Maria is working hard... This will take a moment...");

                String worldname = DEFAULT_WORLD_NAME;

                if (split.length > 0) {
                    if (!split[0].isEmpty()) {
                        worldname = split[0];
                    }
                }

                if (split.length > 1) {
                    if (!split[1].isEmpty() && (split[1].equals("NORMAL") || split[1].equals("NETHER") || split[1].equals("THE_END"))) {
                        world = getDefaultMetropolis(worldname, World.Environment.valueOf(split[1]));
                    } else {
                        world = getDefaultMetropolis(worldname, World.Environment.NORMAL);
                    }
                } else {
                    world = getDefaultMetropolis(worldname, World.Environment.NORMAL);
                }
            } else if (!player.hasPermission("metropolis.create")) {
                sender.sendMessage("Maria has detected that you do not have permissions for this command.");
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
        } else {
            // console made the command
            World world;
            String worldname;
            worldname = DEFAULT_WORLD_NAME;

            if (split.length > 0) {
                if (!split[0].isEmpty()) {
                    worldname = split[0];
                }
            }

            if (split.length > 1) {
                if (!split[1].isEmpty() && (split[1].equals("NORMAL") || split[1].equals("NETHER") || split[1].equals("THE_END"))) {
                    world = getDefaultMetropolis(worldname, World.Environment.valueOf(split[1]));
                } else {
                    world = getDefaultMetropolis(worldname, World.Environment.NORMAL);
                }
            } else {
                world = getDefaultMetropolis(worldname, World.Environment.NORMAL);
            }
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // prime world support (loosely based on ExpansiveTerrain)
    public World getDefaultMetropolis(String worldname, World.Environment env) {
        Bukkit.getLogger().info("---- looking for Metropolis");
        // built yet?
        World metropolis = Bukkit.getServer().getWorld(worldname);
        if (metropolis != null) {
            Bukkit.getLogger().info("---- Metropolis already built");
        } else {
            // if neither then create/build it!
            WorldCreator worldCreator = new WorldCreator(worldname);
            worldCreator.environment(env);
            metropolis = Bukkit.getServer().createWorld(worldCreator);
            plugin.getDefaultWorldGenerator(worldname, "");
        }
        return metropolis;
    }
}

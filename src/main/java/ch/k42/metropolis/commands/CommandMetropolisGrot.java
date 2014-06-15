package ch.k42.metropolis.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ch.k42.metropolis.plugin.MetropolisPlugin;


/**
 * Grot provides some information about all the placed schematics on this grid
 *
 * @author Thomas Richner
 */
public class CommandMetropolisGrot implements CommandExecutor {
    private final MetropolisPlugin plugin;

    public CommandMetropolisGrot(MetropolisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("metropolis.command")) {

                World metropolis = Bukkit.getServer().getWorld(ch.k42.metropolis.commands.CommandMetropolisMaria.DEFAULT_WORLD_NAME);
                if (player.getWorld() == metropolis) { // ok, in same world
                    Location location = player.getLocation();
                    int chunkX = location.getBlockX() >> 4;
                    int chunkZ = location.getBlockZ() >> 4;

//                    String stats = plugin.getGenerator().getGridProvider().getGrid(chunkX,chunkZ).getStatistics().printStatistics();

//                    player.sendMessage(stats);

                    return true;
                } else {
                    return false;
                }

            } else {
                sender.sendMessage("You do not have permission to use this command");
                return false;
            }
        } else {
            sender.sendMessage("This command is only usable by a player.");
            return false;
        }
    }
}

package ch.k42.metropolis.commands;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.common.Grid;
import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Freder is intrested in the deeper information about a Parcel.
 * He prints some basic information for the sender.
 *
 * @author Thomas Richner
 */
public class CommandMetropolisFreder implements CommandExecutor {
    private final MetropolisPlugin plugin;

    public CommandMetropolisFreder(MetropolisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("metropolis.command")) {
                World metropolis = player.getWorld();
                MetropolisGenerator generator = Minions.getGeneratorForPlayer(player);
                if(generator!=null){
                    Location location = player.getLocation();
                    int chunkX = location.getBlockX() >> 4;
                    int chunkZ = location.getBlockZ() >> 4;
                    Grid grid = generator.getGridProvider().getGrid(chunkX,chunkZ);
                    if(grid instanceof UrbanGrid){
                        UrbanGrid urban = (UrbanGrid) grid;
                        Parcel parcel= urban.getParcel(chunkX,chunkZ);
                        if (parcel == null)  player.sendMessage("No Parcel found at this position (something went terribly wrong)");
                        player.sendMessage("Parcel: " + parcel.toString());
                        return true;
                    }
                }
                player.sendMessage("Was not a valid UrbanGrid or generator was wrong");
                return false;
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

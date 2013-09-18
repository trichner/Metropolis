package ch.k42.metropolis.commands;

import ch.k42.metropolis.generator.MetropolisGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandMetropolisMaria implements CommandExecutor {
    private final Plugin plugin;

    public CommandMetropolisMaria(Plugin plugin)
    {
        this.plugin = plugin;
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
    {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("metropolis.command")) {
				boolean leaving = false;
				//WorldStyle style = WorldStyle.NORMAL;
				Environment environment = Environment.NORMAL;
				boolean error = false;
				
				// arguments?
				for (int n = 0; n < split.length; n++) {
					if (split[n].compareToIgnoreCase("LEAVE") == 0){
						leaving = true;
					} else {
						error = true;
						break;
					}
				}
				
				// that isn't an option we support
				if (error) {
					sender.sendMessage("Syntax error");
					return false;
				
				// let's try to leave the city
				} else if (leaving) {
					World world = Bukkit.getServer().getWorld("world");
					if (world == null) {
						sender.sendMessage("Cannot find the default world");
						return false;
					} else if (player.getLocation().getWorld() == world) {
						sender.sendMessage("You are already there");
						return true;
					} else {
						player.sendMessage("Leaving Metropolis... Come back soon!");
						player.teleport(world.getHighestBlockAt(world.getSpawnLocation()).getLocation());
						return true;
					}
				
				// okay, let's enter the city
				} else {
					World world = Bukkit.getServer().getWorld(DEFAULT_WORLD_NAME);
					
					// if the world doesn't exist but the player has permission to create it
					if (world == null){// && player.hasPermission("cityworld.create")) { //FIXME
						sender.sendMessage("Creating Metropolis... This will take a moment...");
						world = getDefaultMetropolis(environment);
//					}
//
//					// test to see if it exists
//					if (world == null) {
//						sender.sendMessage("Cannot find or create the default CityWorld");
//						return false;
					} else {
						
						// are we actually going to the right place
						if (!(world.getGenerator() instanceof MetropolisGenerator))
							sender.sendMessage("WARNING: The world called Metropolis does NOT use the CityWorld generator");
						
						// actually go there then
						if (player.getLocation().getWorld() == world) {
							sender.sendMessage("You are already in Metropolis");
							return true;
						} else {
							player.sendMessage("Traveling to Metropolis...");
							player.teleport(world.getSpawnLocation());
							return true;
						}
					}
				}
			} else {
				sender.sendMessage("You do not have permission to use this command");
				return false;
			}
		} else {
			sender.sendMessage("This command is only usable by a player. Why? Who knows...");
			return false;
		}
        return false;
    }

    // prime world support (loosely based on ExpansiveTerrain)
	public final static String DEFAULT_WORLD_NAME = "Metropolis";
	public World getDefaultMetropolis(Environment environment) {
		
		// built yet?
		World metropolis = Bukkit.getServer().getWorld(DEFAULT_WORLD_NAME);
		if (metropolis == null) {
			
			// if neither then create/build it!
			WorldCreator worldcreator = new WorldCreator(DEFAULT_WORLD_NAME);
			//worldcreator.seed(-7457540200860308014L); // Beta seed
			//worldcreator.seed(5509442565638151977L); // 82,-35
			worldcreator.environment(environment);
			worldcreator.generator(new MetropolisGenerator(plugin, DEFAULT_WORLD_NAME));

			metropolis = Bukkit.getServer().createWorld(worldcreator);

		}
		return metropolis;
	}
}

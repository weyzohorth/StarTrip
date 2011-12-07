package me.weyzohorth.StarTrip;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class StarTrip extends JavaPlugin
{
	public Logger log = Logger.getLogger("Minecraft");
	private StarTripPlayerListener playerListener = new StarTripPlayerListener(this);
	//ChunkGenerator generator = new WaterIsland(this);
	
	public void onEnable()
	{
		//new StarTripCreateWorld(this);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		log.info("StarTrip loaded");
		//getServer().createWorld("test", Environment.NORMAL, generator);
	}
	
	public void onDisable()
	{
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("createGate"))
		{
			if (sender instanceof Player)
			{
				Location loc = ((Player)sender).getLocation();
				loc = loc.getBlock().getRelative(0, 0, 0/*6*/).getLocation();
				StarTripTravelAgent tmp = new StarTripTravelAgent();
				tmp.createPortal(loc);
			}
			return true;
		}
		return false;
	}
}

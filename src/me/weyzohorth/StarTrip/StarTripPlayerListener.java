package me.weyzohorth.StarTrip;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.ArrayList;

public class StarTripPlayerListener extends PlayerListener
{
	static private List<String> loadingWorld = new ArrayList<String>();
	
	StarTrip plugin;
	StarTripPlayerListener(StarTrip plug)
	{
		plugin = plug;
	}
	
	static public void loadingEnd(String worldName)
	{
		if (loadingWorld.contains(worldName))
			loadingWorld.remove(worldName);
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent ev)
	{
		Material filled = Material.STATIONARY_WATER;
		if (ev.getAction() != Action.LEFT_CLICK_BLOCK ||
				ev.getClickedBlock().getType() != Material.STONE_BUTTON)
			return ;
		Block block = ev.getClickedBlock();
		if (block.getRelative(-1, -1, 1).getType() != Material.IRON_BLOCK)
			return ;
		block = block.getRelative(-2, 0, 5);
		if (StarTripTravelAgent.isGate(block.getRelative(0, -1, 0).getLocation()) == false)
			return ;
		if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER)
			filled = Material.AIR;
		for (int i = 0; i < 5; i++)
		{
			block.setTypeId(filled.getId(), false);
			block.getRelative(-1, 0, 0).setTypeId(filled.getId(), false);
			block.getRelative(1, 0, 0).setTypeId(filled.getId(), false);
			if (1 <= i && i <= 3)
			{
				block.getRelative(-2, 0, 0).setTypeId(filled.getId(), false);
				block.getRelative(2, 0, 0).setTypeId(filled.getId(), false);
			}
			block = block.getRelative(0, 1, 0);
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent ev)
	{
		if (ev.getTo().getBlock().getType() != Material.STATIONARY_WATER)
			return ;
		Block block = ev.getTo().getBlock().getRelative(0, -1, 0);
		do
		{
			while (block.getType() == Material.STATIONARY_WATER)
				block = block.getRelative(0, -1, 0);
			if (block.getType() != Material.IRON_BLOCK)
				return ;
			if (block.getRelative(1, 0, 0).getType() == Material.STATIONARY_WATER)
				block = block.getRelative(1, 0, 0);
			else if (block.getRelative(-1, 0, 0).getType() == Material.STATIONARY_WATER)
				block = block.getRelative(-1, 0, 0);
			else if (block.getRelative(1, 0, 0).getType() != Material.IRON_BLOCK)
				block = block.getRelative(-1, 0, 0);
			else if (block.getRelative(-1, 0, 0).getType() != Material.IRON_BLOCK)
				block = block.getRelative(1, 0, 0);
		}
		while (block.getType() == Material.STATIONARY_WATER);
		if (block.getType() != Material.IRON_BLOCK)
			return ;
		String name;
		if ((name = StarTripTravelAgent.getAddress(block.getLocation())) == null)
			return ;
		if (name == "")
			return ;
		if (loadingWorld.contains(name))
			return ;
		plugin.getServer().broadcastMessage("Great ! " + name);
		boolean create = true;
		for (World w: plugin.getServer().getWorlds())
		{
			if (w.getName().equalsIgnoreCase(name) == false)
				continue;
			create = false;
			break;
		}
		if (create)
		{
			loadingWorld.add(name);
			new StarTripCreateWorld(plugin, name);
		}
		World world = null;
		for (World w: plugin.getServer().getWorlds())
		{
			plugin.getServer().broadcastMessage(w.getName());
			if (w.getName().equalsIgnoreCase(name) == false)
				continue;
			world = w;
			break;
		}
		if (world == null)
		{
			plugin.getServer().broadcastMessage("Fail !");
			return ;
		}
		if (StarTripTravelAgent.isGate(world.getSpawnLocation()) == false)
		{
			StarTripTravelAgent agent = new StarTripTravelAgent();
			agent.createPortal(world.getSpawnLocation());
		}
		StarTripTravelAgent.setAddress(world.getSpawnLocation(), ev.getPlayer().getWorld().getName());
		ev.setFrom(world.getSpawnLocation());
		ev.getPlayer().teleport(ev.getFrom().getBlock().getRelative(0, 0, -1).getLocation());
		ev.setTo(ev.getFrom().getBlock().getRelative(0, 0, -1).getLocation());
		plugin.getServer().broadcastMessage("Load !");
	}
}

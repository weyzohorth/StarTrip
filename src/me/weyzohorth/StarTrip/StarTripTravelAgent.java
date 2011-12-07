package me.weyzohorth.StarTrip;

import org.bukkit.TravelAgent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.Material;

public class StarTripTravelAgent implements TravelAgent
{
	private int searchRadius = 20;
	private int creationRadius = 20;
	private boolean canCreate = true;
	
	static public String getAddress(Location loc)
	{
		if (isGate(loc) == false)
			return null;
		Block block = loc.getBlock().getRelative(2, 2, -5);
		Sign sign = ((Sign)block.getState());
		return sign.getLine(0);
	}
	
	static public boolean setAddress(Location loc, String address)
	{
		if (isGate(loc) == false)
			return false;
		Block block = loc.getBlock().getRelative(2, 2, -5);
		Sign sign = ((Sign)block.getState());
		sign.setLine(0, address);
		return true;
	}
	
	static public boolean isGate(Location loc)
	{
		Block block = loc.getBlock();
		for (int i = 0; i < 5; i++)
		{
			if (block.getType() != Material.IRON_BLOCK ||
					block.getRelative(-1, 0, 0).getType() != Material.IRON_BLOCK ||
					block.getRelative(1, 0, 0).getType() != Material.IRON_BLOCK)
				return false;
			block = block.getRelative(0, 0, -1);
		}
		block = block.getRelative(2, 1, 1);
		if (block.getType() != Material.IRON_BLOCK ||
				block.getRelative(0, 1, 0).getType() != Material.IRON_BLOCK ||
				block.getRelative(0, 0, -1).getType() != Material.STONE_BUTTON ||
				block.getRelative(0, 1, -1).getType() != Material.WALL_SIGN)
				return false;
		block = loc.getBlock().getRelative(0, 1, 0);
		for (int i = 0; i < 5; i++)
		{
			if (0 < i && i < 4)
			{
				if (block.getRelative(-3, 0, 0).getType() != Material.IRON_BLOCK ||
						block.getRelative(3, 0, 0).getType() != Material.IRON_BLOCK)
						return false;
			}
			else if (block.getRelative(-2, 0, 0).getType() != Material.IRON_BLOCK ||
					block.getRelative(2, 0, 0).getType() != Material.IRON_BLOCK)
					return false;
			block = block.getRelative(0, 1, 0);
		}
		if (block.getType() != Material.IRON_BLOCK ||
				block.getRelative(-1, 0, 0).getType() != Material.IRON_BLOCK ||
				block.getRelative(1, 0, 0).getType() != Material.IRON_BLOCK)
			return false;
		return true;
	}
	
	public boolean canCreatePortal(Location loc)
	{
		if (canCreate == false)
			return false;
		Block tmp = loc.getBlock().getRelative(0, 1, -1);
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				if (tmp.getRelative(0, j, 0).getType() != Material.AIR ||
						tmp.getRelative(-1, j, 0).getType() != Material.AIR ||
						tmp.getRelative(1, j, 0).getType() != Material.AIR)
					return false;
			}
			tmp = tmp.getRelative(0, 0, -1);
		}
		return true;
	}
	
	public boolean createPortal(Location loc)
	{
		Block tmp = loc.getBlock().getRelative(0, -1, 0);
		for (int i = 0; i <= 5; i++)
		{
			if (i < 5)
			{
				tmp.setType(Material.IRON_BLOCK);
				tmp.getRelative(-1, 0, 0).setType(Material.IRON_BLOCK);
				tmp.getRelative(1, 0, 0).setType(Material.IRON_BLOCK);
			}
			for (int j = 1; j <= 2; j++)
			{
				tmp.getRelative(0, j, 0).setType(Material.AIR);
				tmp.getRelative(-1, j, 0).setType(Material.AIR);
				tmp.getRelative(1, j, 0).setType(Material.AIR);
			}
			if (i < 5)
				tmp = tmp.getRelative(0, 0, -1);
		}
		tmp = tmp.getRelative(2, 1, 1);
		tmp.setType(Material.IRON_BLOCK);
		tmp.getRelative(0, 0, -1).setType(Material.STONE_BUTTON);
		tmp.getRelative(0, 0, -1).setData((byte)4);
		tmp.getRelative(0, 1, 0).setType(Material.IRON_BLOCK);
		tmp.getRelative(0, 1, -1).setType(Material.WALL_SIGN);
		tmp.getRelative(0, 1, -1).setData((byte)2);
		tmp = loc.getBlock();
		tmp.getRelative(-2, 0, 0).setType(Material.IRON_BLOCK);
		tmp.getRelative(2, 0, 0).setType(Material.IRON_BLOCK);
		for (int i = 0; i < 3; i++)
		{
			tmp = tmp.getRelative(0, 1, 0);
			tmp.getRelative(-3, 0, 0).setType(Material.IRON_BLOCK);
			tmp.getRelative(3, 0, 0).setType(Material.IRON_BLOCK);
		}
		tmp = tmp.getRelative(0, 1, 0);
		tmp.getRelative(-2, 0, 0).setType(Material.IRON_BLOCK);
		tmp.getRelative(2, 0, 0).setType(Material.IRON_BLOCK);
		tmp = tmp.getRelative(0, 1, 0);
		tmp.setType(Material.IRON_BLOCK);
		tmp.getRelative(-1, 0, 0).setType(Material.IRON_BLOCK);
		tmp.getRelative(1, 0, 0).setType(Material.IRON_BLOCK);
		return true;
	}
	
	public Location findOrCreate(Location loc)
	{
		return loc;
	}
	 
	public Location findPortal(Location loc)
	{
		return loc;
	}
	 
	public boolean getCanCreatePortal()
	{
		return canCreate;
	}
	 
	public int getCreationRadius ()
	{
		return creationRadius;
	}
	
	public int getSearchRadius ()
	{
		return searchRadius;
	}
 	 
	public void setCanCreatePortal(boolean create)
	{
		canCreate = create;
	}
 
	public TravelAgent setCreationRadius(int radius)
	{
		creationRadius = radius;
		return this;
	}
	
	public TravelAgent setSearchRadius(int radius)
	{
		searchRadius = radius;
		return this;
	}
}

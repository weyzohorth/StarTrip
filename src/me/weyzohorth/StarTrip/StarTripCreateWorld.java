package me.weyzohorth.StarTrip;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World.Environment;
//import org.bukkit.World;
//import org.bukkit.entity.Player;

public class StarTripCreateWorld
{
	public static String	main_dir = "plugins/PhoenixTerrainMod/worlds" + File.separator;
	static public final String letters = "abcdefghijklmnopqrstuvwxyz";
	static public final String numbers = "0123456789";
	static public final String symbols = "~@&-_+.";
	String world = "";
	StarTrip plugin;
	
	StarTripCreateWorld(StarTrip plug, String name)
	{
		plugin = plug;
		world = name;
		new CreateWorld();
		
	}
	
	StarTripCreateWorld(StarTrip plug)
	{
		plugin = plug;
		createName();
		new CreateWorld();
	}
	
	public String createName()
	{
		Random rand = new Random();
		int size = rand.nextInt(5) + 5;
		for (int i = 0; i < size; i++)
		{
			int num = rand.nextInt(10);
			String tmp;
			if (num < 7)
				tmp = letters;
			else if (num < 9 || i == 0 || i == size - 1)
				tmp = numbers;
			else
				tmp = symbols;
			world += tmp.charAt(rand.nextInt(tmp.length()));
		}
		return world;
	}
	
	public boolean createConf()
	{
		plugin.log.info("Création du monde \"" + world + "\"");
		new File(main_dir + world).mkdir();
		try
		{
			//(new File(main_dir + world + File.separator + "PhoenixTerrainModSettings.ini")).createNewFile();
			FileWriter conf = new FileWriter(main_dir + world + File.separator + "PhoenixTerrainModSettings.ini");
			writeAllBiome(conf);
			writeSwampDesertBiome(conf);
			writeUnderground(conf);
			writeTerrain(conf);
			writeReplace(conf);
			writeBOBObjects(conf);
			writeCactusTree(conf);
			writeLava(conf);
			writeUndergroundLake(conf);
			writeAboveGround(conf);
			writeAboveBelowGround(conf);
			writeBelowGround(conf);
			conf.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			/*Print.log("\t\tFailed");
			Print.log(("UndeadsNight: Could not create UndeadsNightSpawn.txt file");*/
			return false;
		}
		return true;
	}
	
	public void addWorld()
	{
		try
		{
			BufferedReader buff = new BufferedReader(new FileReader("bukkit.yml"));
			FileWriter copy = new FileWriter("copy");
			String line;
			while ((line = buff.readLine()) != null)
			{
				copy.append(line + '\n');
				if (line.equalsIgnoreCase("worlds:"))
				{
					copy.append("    " + world + ":\n        generator: PhoenixTerrainMod\n");
				}
			}
			copy.flush();
			copy.close();
			buff.close();
			File tmp = new File("bukkit.yml");
			tmp.delete();
			tmp = new File("copy");
			tmp.renameTo(new File("bukkit.yml"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	class CreateWorld implements Runnable
	{
		Thread creator;
		
		CreateWorld()
		{
			createConf();
			addWorld();
			creator = new Thread(this);
			creator.start();
		}
		
		public void run()
		{
			Random rand = new Random();
			int i = rand.nextInt(3);
			if (i == 0)
				plugin.getServer().createWorld(world, Environment.NETHER);
			else if (i == 1)
				plugin.getServer().createWorld(world, Environment.NORMAL);
			else
				plugin.getServer().createWorld(world, Environment.SKYLANDS);
			StarTripPlayerListener.loadingEnd(world);
		}
	}
	
	private boolean writeAllBiome(FileWriter conf)
	{
		try
		{
			String min = getDouble(0, 1, 5);
			conf.append("\n<Start Biome Variables :>\n\n" + 
					"<All Biome Variables>\n" +
					"biomeSize:" + ((Double)(1 / Double.parseDouble(getDouble(1, 100, 3)) * 100)).toString() + "\n" + 
					"minMoisture:" + min + "\n" +
					"maxMoisture:" + getDouble(Double.parseDouble(min), 1, 5) + "\n");
			min = getDouble(0, 1, 5);
			conf.append("minTemperature:" + min + "\n" +
					"maxTemperature:" + getDouble(Double.parseDouble(min), 1, 5) + "\n" +
					"snowThreshold:" + getDouble(0, 1, 5) + "\n" +
					"iceThreshold:" + getDouble(0, 1, 5) + "\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeSwampDesertBiome(FileWriter conf)
	{
		try
		{
			conf.append("\n<Swamp Biome Variables>\n" +
					"muddySwamps:" + getBoolean() + "\n" +
					"claySwamps:" + getBoolean() + "\n" +
					"swampSize:" + ((Double)(1 / Double.parseDouble(getDouble(1, 100, 3)) * 100)).toString().split("\\.")[0] + "\n" +
					"\n<Desert Biome Variables>\n" +
					"waterlessDeserts:" + getBoolean() + "\n" +
					"removeSurfaceDirtFromDesert:" + getBoolean() + "\n" +
					"desertDirt:" + getBoolean() + "\n" +
					"desertDirtFrequency:0\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeUnderground(FileWriter conf)
	{
		try
		{
			String min = getInt(0, 128);
			String distribution = getBoolean(); 
			conf.append("\n<Start Underground Variables :>\n" + 
					"\n<Cave Variables>\n" +
					"caveRarity:" + getInt(100) + "\n");
			if (distribution == "false")
				conf.append("caveFrequency:" + getInt(500) + "\n");
			else
				conf.append("caveFrequency:" + getInt(100) + "\n");
			conf.append("caveMinAltitude:" + min + "\n" +
					"caveMaxAltitude:" + getInt(Integer.parseInt(min), 128) + "\n" +
					"individualCaveRarity:" + getInt(100) + "\n" +
					"caveSystemFrequency:" + getInt(100) + "\n");
			if (getBoolean() == "true")
				conf.append("caveSystemPocketChance:" + getInt(10) + "\n");
			else
				conf.append("caveSystemPocketChance:0\n");
			min = getInt(0, 10);
			conf.append("caveSystemPocketMinSize:" + min + "\n" +
					"caveSystemPocketMaxSize:" + getInt(Integer.parseInt(min), 10) + "\n" +
					"evenCaveDistribution:" + distribution + "\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeTerrain(FileWriter conf)
	{
		try
		{
			conf.append("\n<Start Terrain Variables :>\n" +
					"waterLevel:" + getInt(128) + "\n" +
					"waterBlock:9\n" +
					"maxAverageHeight:" + getDouble(-10, 5, 5) + "\n" +
					"maxAverageDepth:" + getDouble(-10, 10, 3) + "\n" +
					"fractureHorizontal:" + getDouble(-10, 10, 3) + "\n" +
					"fractureVertical:" + getDouble(-10, 10, 3) + "\n" +
					"volatility1:" + getDouble(-10, 10, 3) + "\n" +
					"volatility2:" + getDouble(-10, 10, 3) + "\n" +
					"volatilityWeight1:" + getDouble(0, 1, 5) + "\n" +
					"volatilityWeight2:" + getDouble(0, 1, 5) + "\n" +
					"disableBedrock:" + getBoolean() + "\n" +
					"ceilingBedrock:" + getBoolean() + "\n" +
					"flatBedrock:" + getBoolean() + "\n" +
					"bedrockobsidian:" + getBoolean() + "\n" +
					"disableNotchHeightControl:" + getBoolean() + "\n" +
					"CustomHeightControl:0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeReplace(FileWriter conf)
	{
		try
		{
			conf.append("\n<Replace Variables>\n" +
					"removeSurfaceStone:" + getBoolean() + "\n" +
					"ReplacedBlocks:");
			if (getBoolean() == "false")
				conf.append("none\n");
			else
			{
				Random rand = new Random();
				int n = rand.nextInt(20);
				int b, a;
				for (int i = 0; i < n; i++)
				{
					if (i != 0)
						conf.append(",");
					a = 0;
					while (a == 0)
						a = Material.values()[rand.nextInt(Material.values().length)].getId();
					b = a;
					while (b == a)
						b = Material.values()[rand.nextInt(Material.values().length)].getId();
					conf.append(((Integer)a).toString() + "=" + ((Integer)b).toString());
				}
				conf.append("\n");
			}
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeBOBObjects(FileWriter conf)
	{
		try
		{
			conf.append("\n<Start BOB Objects Variables :>\n" +
					"customObjects:true\n" +
					"objectSpawnRatio:2\n" +
					"denyObjectsUnderFill:false\n");
					conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private boolean writeCactusTree(FileWriter conf)
	{
		try
		{
			String min = getInt(0, 128);
			conf.append("\n<Start Cactus&Tree Variables :>\n" +
					"notchBiomeTrees:" + getBoolean() + "\n" +
					"globalTreeDensity:" + getInt(-10, 10) + "\n" +
					"rainforestTreeDensity:" + getInt(-10, 10) + "\n" +
					"swamplandTreeDensity:" + getInt(-10, 10) + "\n" +
					"seasonalforestTreeDensity:" + getInt(-10, 10) + "\n" +
					"forestTreeDensity:" + getInt(-10, 10) + "\n" +
					"savannaTreeDensity:" + getInt(-10, 10) + "\n" +
					"shrublandTreeDensity:" + getInt(-10, 10) + "\n" +
					"taigaTreeDensity:" + getInt(-10, 10) + "\n" +
					"desertTreeDensity:" + getInt(-10, 10) + "\n" +
					"plainsTreeDensity:" + getInt(-10, 10) + "\n" +
					"iceDesertTreeDensity:" + getInt(-10, 10) + "\n" +
					"tundraTreeDensity:" + getInt(-10, 10) + "\n" +
					"globalCactusDensity:" + getInt(-10, 10) + "\n" +
					"desertCactusDensity:" + getInt(-10, 10) + "\n" +
					"cactusDepositRarity:" + getInt(0, 100) + "\n");
			conf.append("cactusDepositMinAltitude:" + getInt(0, 128) + "\n" +
					"cactusDepositMaxAltitude:" + getInt(Integer.parseInt(min), 128) + "\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeLava(FileWriter conf)
	{
		try
		{
			String min = getInt(0, 128);
			conf.append("\n<Lava Pool Variables>\n" +
					"lavaLevelMin:" + min + "\n" +
					"lavaLevelMax:" + getInt(Integer.parseInt(min), 128) + "\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeUndergroundLake(FileWriter conf)
	{
		try
		{
			String min = getInt(0, 200);
			conf.append("\n<Underground Lake Variables>\n" + 
					"undergroundLakes:" + getBoolean() + "\n" +
					"undergroundLakesInAir:" + getBoolean() + "\n" +
					"undergroundLakeFrequency:" + getInt(50) + "\n" +
					"undergroundLakeRarity:" + getInt(50) + "\n" +
					"undergroundLakeMinSize:" + min + "\n" +
					"undergroundLakeMaxSize:" + getInt(Integer.parseInt(min), 200) + "\n");
			min = getInt(0, 128);
			conf.append("undergroundLakeMinAltitude:" + min + "\n" +
					"undergroundLakeMaxAltitude:" + getInt(Integer.parseInt(min), 128) + "\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeAboveGround(FileWriter conf)
	{
		try
		{
			List<String> list = new ArrayList<String>();
			list.add("flower");
			list.add("rose");
			list.add("brownMushroom");
			list.add("redMushroom");
			list.add("reed");
			list.add("pumpkin");
			conf.append("\n<Start Deposit Variables :>\n" +
					"\n<Above Ground Variables>\n");
			for (String name: list)
			{
				String min = getInt(0, 128);
				conf.append(name + "DepositRarity:" + getInt(100) + "\n" +
						name + "DepositFrequency:" + getInt(100) + "\n" +
						name + "DepositMinAltitude:" + min + "\n" +
						name + "DepositMaxAltitude:" + getInt(Integer.parseInt(min), 128) + "\n");
			}
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeAboveBelowGround(FileWriter conf)
	{
		try
		{
			List<String> list = new ArrayList<String>();
			list.add("water");
			list.add("lava");
			conf.append("\n<Above/Below Ground Variables>\n");
			for (String name: list)
			{
				String min = getInt(0, 128);
				conf.append("even" + name + "SourceDistribution:" + getBoolean() + "\n" +
						name + "SourceDepositRarity:" + getInt(100) + "\n" +
						name + "SourceDepositFrequency:" + getInt(100) + "\n" +
						name + "SourceDepositMinAltitude:" + min + "\n" +
						name + "SourceDepositMaxAltitude:" + getInt(Integer.parseInt(min), 128) + "\n");
			}
			conf.append("disableNotchPonds:" + getBoolean() + "\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean writeBelowGround(FileWriter conf)
	{
		try
		{
			Random rand = new Random();
			List<String> list = new ArrayList<String>();
			list.add("dirt");
			list.add("gravel");
			list.add("clay");
			list.add("coal");
			list.add("iron");
			list.add("gold");
			list.add("redstone");
			list.add("diamond");
			list.add("lapislazuli");
			String min = getInt(0, 128);
			conf.append("\n<Below Ground Variables>\n");
			conf.append("dungeonRarity:" + getInt(100) + "\n" +
					"dungeonFrequency:" + getInt(100) + "\n" +
					"dungeonMinAltitude:" + min + "\n" + 
					"dungeonMaxAltitude:" + getInt(Integer.parseInt(min), 128) + "\n");
			for (String name: list)
			{
				int total_ammount = rand.nextInt(128);
				for (Integer i = 1; i <= 4; i++)
				{
					min = getInt(32 * (i - 1), 32 * i);
					Integer ammount = rand.nextInt(32);
					if (total_ammount < ammount)
						ammount = total_ammount;
					total_ammount -= ammount;
					conf.append(name + "DepositRarity" + i.toString() + ":" + getInt(100) + "\n" +
							name + "DepositFrequency" + i.toString() + ":" + getInt(100) + "\n" +
							name + "DepositSize" + i.toString() + ":" + ammount.toString() + "\n" +
							name + "DepositMinAltitude" + i.toString() + ":" + min + "\n" +
							name + "DepositMaxAltitude" + i.toString() + ":" + getInt(Integer.parseInt(min), 32 * i) + "\n");
				}
			}
			conf.append("disableNotchPonds:" + getBoolean() + "\n");
			conf.flush();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	static public String getBoolean()
	{
		Random rand = new Random();
		return ((Boolean)rand.nextBoolean()).toString();
	}
	
	static public String getInt(int min, int max)
	{
		Random rand = new Random();
		return ((Integer)(rand.nextInt(max + 1 - min) + min)).toString();
	}
	
	static public String getInt(int max)
	{
		Random rand = new Random();
		return ((Integer)rand.nextInt(max + 1)).toString();
	}
	
	static public String getDouble(int min, int max, int dec)
	{
		Random rand = new Random();
		double num = Math.pow(10, dec);
		return ((Double)(((Math.abs(rand.nextLong()) % ((max - min) * num)) + min * num) / num)).toString();
	}
	
	static public String getDouble(double min, double max, int dec)
	{
		Random rand = new Random();
		double num = Math.pow(10, dec);
		return ((Double)(((Math.abs(rand.nextLong()) % ((max - min) * num)) + min * num) / num)).toString();
	}
	
	public String getWorldName()
	{
		return world;
	}
}

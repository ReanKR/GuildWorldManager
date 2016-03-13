package org.whitehack97.GuildWorldManager.Util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.whitehack97.GuildWorldManager.GuildWorldManager;
import org.whitehack97.GuildWorldManager.player.PlayerInformation;

public class FileReader
{
	public static void SaveFile(YamlConfiguration Section, File file)
	{
		try
		{
			Section.save(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static File getFile(String Name)
	{
		if(! Name.endsWith(".yml")) Name = Name + ".yml";
		return new File("plugins/GuildWorldManager/" + Name);
	}
	
	public static YamlConfiguration LoadPlayerFile(Player player)
	{
		File file = new File("plugins/GuildWorldManager/Players/" + player.getName() + ".yml");
		if(! file.exists()) PlayerInformation.LoadPlayerFile(player);
		YamlConfiguration PlayerSection = YamlConfiguration.loadConfiguration(file);
		return PlayerSection;
	}
	
	public static YamlConfiguration LoadFile(String Filename)
	{
		if(!Filename.endsWith(".yml"))
		{
			Filename = Filename + ".yml";
		}
		File file = new File("plugins/GuildWorldManager/" + Filename);
		if(! file.exists())
		{
			try
			{
		          GuildWorldManager.plugin.saveResource(Filename, true);
			}
			catch(IllegalArgumentException e)
			{
				try
				{
					file.createNewFile();
				}
				catch(IOException Ex)
				{
					Ex.printStackTrace();
				}
			}
		}
		YamlConfiguration PlayerSection = YamlConfiguration.loadConfiguration(file);
		return PlayerSection;
	}
}

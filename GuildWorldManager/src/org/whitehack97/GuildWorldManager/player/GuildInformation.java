package org.whitehack97.GuildWorldManager.player;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.whitehack97.GuildWorldManager.GuildWorldManager;
import org.whitehack97.GuildWorldManager.Util.FileReader;
import org.whitehack97.GuildWorldManager.api.ManagePlayer;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

public class GuildInformation
{
	public static void RegisterGuild(Player player)
	{
		MPlayer mplayer = MPlayer.get(player);
		Faction faction = mplayer.getFaction();
		if(faction == null)
		{
			return;
		}

		File file = new File("plugins/GuildWorldManager/Worlds.yml");
		if(! file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
		YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(file);
		ManagePlayer ManPlayer = GuildWorldManager.PlayerInformations.get(player.getName());
		try
		{
			faction.getLeader().equals(mplayer);
		}
		catch(NullPointerException e)
		{
			return;
		}
		if(faction.getLeader().equals(mplayer)) //길드장이라면
		{
			if(GuildSection.contains(faction.getName()))
			{
				GuildSection.set(faction.getName() + ".Leader", player.getName());
				GuildSection.set(faction.getName() + ".World", ManPlayer.getFactionWorld());
				GuildSection.set(faction.getName() + ".Blacklist.Enabled", ManPlayer.EnabledBlacklist());
				GuildSection.set(faction.getName() + ".Blacklist.Players", ManPlayer.getBlacklist());
				GuildSection.set(faction.getName() + ".Whitelist.Enabled", ManPlayer.EnabledWhitelist());
				GuildSection.set(faction.getName() + ".Whitelist.Players", ManPlayer.getWhitelist());
				FileReader.SaveFile(GuildSection, file);
			}
			else
			{
				GuildSection.createSection(faction.getName());
				GuildSection.set(faction.getName() + ".Leader", player.getName());
				GuildSection.set(faction.getName() + ".World", ManPlayer.getFactionWorld());
				GuildSection.set(faction.getName() + ".Blacklist.Enabled", ManPlayer.EnabledBlacklist());
				GuildSection.set(faction.getName() + ".Blacklist.Players", ManPlayer.getBlacklist());
				GuildSection.set(faction.getName() + ".Whitelist.Enabled", ManPlayer.EnabledWhitelist());
				GuildSection.set(faction.getName() + ".Whitelist.Players", ManPlayer.getWhitelist());
				FileReader.SaveFile(GuildSection, file);
			}
			return;
		}
		else //길드장이 아니라면
		{
			File LeaderFile = new File("plugins/GuildWorldManager/Players/" + faction.getLeader().getPlayer().getName() + ".yml");
			if(GuildSection.contains(faction.getName()))
			{
				if(! LeaderFile.exists())
				{
					return;
				}
				else
				{
					YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
					GuildSection.set(faction.getName() + ".Leader", faction.getLeader().getPlayer().getName());
					GuildSection.set(faction.getName() + ".World", LeaderSection.getString("World.Faction-World"));
					GuildSection.set(faction.getName() + ".Blacklist.Enabled", LeaderSection.getBoolean("World.Blacklist.Enabled"));
					if(LeaderSection.contains("World.Blacklist.Players"))
					{
						GuildSection.set(faction.getName() + ".Blacklist.Players", LeaderSection.getStringList("World.Blacklist.Players"));
					}
					GuildSection.set(faction.getName() + ".Whitelist.Enabled", LeaderSection.getBoolean("World.Whitelist.Enabled"));
					if(LeaderSection.contains("World.Whitelist.Players"))
					{
						GuildSection.set(faction.getName() + ".Whitelist.Players", LeaderSection.getStringList("World.Whitelist.Players"));
					}
					FileReader.SaveFile(GuildSection, file);
				}
			}
			else
			{
				YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
				GuildSection.createSection(faction.getName());
				GuildSection.set(faction.getName() + ".Leader", faction.getLeader().getPlayer().getName());
				GuildSection.set(faction.getName() + ".World", LeaderSection.getString("World.Faction-World"));
				GuildSection.set(faction.getName() + ".Blacklist.Enabled", LeaderSection.getBoolean("World.Blacklist.Enabled"));
				if(LeaderSection.contains("World.Blacklist.Players"))
				{
					GuildSection.set(faction.getName() + ".Blacklist.Players", LeaderSection.getStringList("World.Blacklist.Players"));
				}
				GuildSection.set(faction.getName() + ".Whitelist.Enabled", LeaderSection.getBoolean("World.Whitelist.Enabled"));
				if(LeaderSection.contains("World.Whitelist.Players"))
				{
					GuildSection.set(faction.getName() + ".Whitelist.Players", LeaderSection.getStringList("World.Whitelist.Players"));
				}
			}
		}
		ChackingRegistered();
	}
	
	public static void ChackingRegistered()
	{
		File file = new File("plugins/GuildWorldManager/Worlds.yml");
		if(! file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
		YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(file);
		Set<String> GuildName = GuildSection.getKeys(false);
		for(String Name : GuildName)
		{
			Faction faction = Faction.get(GuildName);
			if(! GuildSection.getString(Name + ".Leader").equalsIgnoreCase(faction.getLeader().getName()))
			{
				RegisterGuild(faction.getLeader().getPlayer());
			}
		}
	}
}

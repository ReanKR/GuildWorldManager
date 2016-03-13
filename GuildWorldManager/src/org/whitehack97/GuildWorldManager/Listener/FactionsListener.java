package org.whitehack97.GuildWorldManager.Listener;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.whitehack97.GuildWorldManager.Util.FileReader;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsNameChange;

public class FactionsListener implements Listener
{
	@EventHandler
	public void FactionsName(EventFactionsNameChange event)
	{
		MPlayer mplayer = MPlayer.get(event.getMPlayer().getPlayer());
		Faction faction = mplayer.getFaction();
		File file = new File("plugins/GuildWorldManager/Worlds.yml");
		File LeaderFile = new File("plugins/GuildWorldManager/Players/" + faction.getLeader().getPlayer().getName() + ".yml");
		YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
		GuildSection.set(faction.getName(), null);
		GuildSection.createSection(event.getNewName());
		GuildSection.set(event.getNewName() + ".Leader", faction.getLeader().getPlayer().getName());
		GuildSection.set(event.getNewName() + ".World", LeaderSection.getString("World.Faction-World"));
		GuildSection.set(event.getNewName() + ".Blacklist.Enabled", LeaderSection.getBoolean("World.Blacklist.Enabled"));
		if(LeaderSection.contains("World.Blacklist.Players"))
		{
			GuildSection.set(event.getNewName() + ".Blacklist.Players", LeaderSection.getStringList("World.Blacklist.Players"));
		}
		GuildSection.set(event.getNewName() + ".Whitelist.Enabled", LeaderSection.getBoolean("World.Whitelist.Enabled"));
		if(LeaderSection.contains("World.Whitelist.Players"))
		{
			GuildSection.set(event.getNewName() + ".Whitelist.Players", LeaderSection.getStringList("World.Whitelist.Players"));
		}
		FileReader.SaveFile(GuildSection, file);
	}
	@EventHandler
	public void FactionsDisband(EventFactionsDisband event)
	{
		File file = new File("plugins/GuildWorldManager/Worlds.yml");
		YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(file);
		GuildSection.set(event.getFaction().getName(), null);
		FileReader.SaveFile(GuildSection, file);
	}
	
	@EventHandler
	public void FactionCreate(EventFactionsCreate event)
	{
		File file = new File("plugins/GuildWorldManager/Worlds.yml");
		File LeaderFile = new File("plugins/GuildWorldManager/Players/" + event.getMPlayer().getPlayer().getName() + ".yml");
		YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
		GuildSection.createSection(event.getFactionName());
		GuildSection.set(event.getFactionName() + ".Leader", event.getMPlayer().getPlayer().getName());
		GuildSection.set(event.getFactionName() + ".World", LeaderSection.getString("World.Faction-World"));
		GuildSection.set(event.getFactionName() + ".Blacklist.Enabled", LeaderSection.getBoolean("World.Blacklist.Enabled"));
		if(LeaderSection.contains("World.Blacklist.Players"))
		{
			GuildSection.set(event.getFactionName() + ".Blacklist.Players", LeaderSection.getStringList("World.Blacklist.Players"));
		}
		GuildSection.set(event.getFactionName() + ".Whitelist.Enabled", LeaderSection.getBoolean("World.Whitelist.Enabled"));
		if(LeaderSection.contains("World.Whitelist.Players"))
		{
			GuildSection.set(event.getFactionName() + ".Whitelist.Players", LeaderSection.getStringList("World.Whitelist.Players"));
		}
		FileReader.SaveFile(GuildSection, file);
	}
}

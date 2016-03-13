package org.whitehack97.GuildWorldManager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.whitehack97.GuildWorldManager.Listener.FactionsListener;
import org.whitehack97.GuildWorldManager.Listener.PlayerListener;
import org.whitehack97.GuildWorldManager.Util.MessageManager;
import org.whitehack97.GuildWorldManager.api.ManagePlayer;
import org.whitehack97.GuildWorldManager.command.GWmanagerCommand;
import org.whitehack97.GuildWorldManager.player.GuildInformation;
import org.whitehack97.GuildWorldManager.player.PlayerInformation;

public class GuildWorldManager extends JavaPlugin implements Listener
{
	public static Server server;
	public static GuildWorldManager plugin;
	public static Map<String, ManagePlayer> PlayerInformations = new HashMap<String, ManagePlayer>();
	public static String Prefix = ChatColor.translateAlternateColorCodes('&', "&b[&9&lG&e&lW&emanager&b] ");
	
	private GWmanagerCommand Commands;
	
	@Override
	public void onEnable()
	{
		Commands = new GWmanagerCommand();
		getCommand("GuildWorldManager.Main").setExecutor(Commands);
		Bukkit.getPluginManager().registerEvents(new FactionsListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		plugin = this;
		server = this.getServer();
		for(Player player : server.getOnlinePlayers())
		{
			PlayerInformation.LoadPlayerFile(player);
			GuildInformation.RegisterGuild(player);
		}
		MessageManager.Cmsg("&a" + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " now Enabled");
		MessageManager.Cmsg("&eCopyright 2016. whitehack97 (Rean KR) all rights reserved.");
	}
	
	@Override
	public void onDisable()
	{
		MessageManager.Cmsg("&c" + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " now Disabled");
		MessageManager.Cmsg("&eCopyright 2016. whitehack97 (Rean KR) all rights reserved.");
	}

}

package org.whitehack97.GuildWorldManager.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.whitehack97.GuildWorldManager.GuildWorldManager;

public class MessageManager
{
	public static String RepColor(String Str)
	{
		return ChatColor.translateAlternateColorCodes('&', Str);
	}
	
	public static void Cmsg(String Str)
	{
		Bukkit.getConsoleSender().sendMessage(GuildWorldManager.Prefix + RepColor(Str));
	}
	
	public static void msg(Player player, String Str)
	{
		player.sendMessage(GuildWorldManager.Prefix + RepColor(Str));
	}

}

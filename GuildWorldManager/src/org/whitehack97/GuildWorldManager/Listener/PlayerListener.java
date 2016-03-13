package org.whitehack97.GuildWorldManager.Listener;

import java.io.File;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.whitehack97.GuildWorldManager.Util.FileReader;
import org.whitehack97.GuildWorldManager.Util.MessageManager;
import org.whitehack97.GuildWorldManager.player.GuildInformation;

import com.massivecraft.factions.entity.MPlayer;

public class PlayerListener implements Listener
{
	@EventHandler
	public void PlayerTeleport(PlayerTeleportEvent e)
	{
		Player player = e.getPlayer();
		World ToWorld = e.getTo().getWorld();
		File file = new File("plugins/GuildWorldManager/Worlds.yml");
		YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(file);
		for(String Str : GuildSection.getKeys(false))
		{
			if(! ToWorld.equals(e.getFrom().getWorld()) && ToWorld.getName().equalsIgnoreCase(GuildSection.getString(Str + ".World")))
			{
				if(player.getName().equalsIgnoreCase(GuildSection.getString(Str + ".Leader")))
				{
					return;
				}
				else
				{
					File LeaderFile = new File("plugins/GuildWorldManager/Players/" + GuildSection.getString(Str + ".Leader") + ".yml");
					YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
					if(LeaderSection.getBoolean("World.Blacklist.Enabled"))
					{
						if(LeaderSection.contains("World.Blacklist.Players"))
						{
							for(String Name : LeaderSection.getStringList("World.Blacklist.Players"))
							{
								if(Name.equalsIgnoreCase(player.getName()))
								{
									MessageManager.msg(player, "&c����:&4 �̵��� �����Ͽ����ϴ�.");
									MessageManager.msg(player, "&6������ ����� ��� �����̸鼭 �ش� ��尡 �ش� �÷��̾ ������Ʈ�� ������");
									e.setCancelled(true);
									return;
								}
							}
						}
					}
					
					if(MPlayer.get(player).getFaction().getName().equalsIgnoreCase(Str))
					{
						return;
					}
					
					if(LeaderSection.getBoolean("World.Whitelist.Enabled"))
					{
						if(LeaderSection.contains("World.Whitelist.Players"))
						{
							for(String Name : LeaderSection.getStringList("World.Whitelist.Players"))
							{
								if(Name.equalsIgnoreCase(player.getName()))
								{
									return;
								}
							}
						}
					}
					
					if(LeaderSection.getBoolean("World.World-Passed"))
					{
						return;
					}
				}
				MessageManager.msg(player, "&c����:&4 �̵��� �����Ͽ����ϴ�.");
				MessageManager.msg(player, "&6������ ����� ��� �����̸鼭 �ش� �÷��̾��� ������ ������� ����");
				e.setCancelled(true);
				return;
			}
			else
			{
				return;
			}
		}
	}
	
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		FileReader.LoadPlayerFile(player);
		GuildInformation.RegisterGuild(player);
	}
}

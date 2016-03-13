package org.whitehack97.GuildWorldManager.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.whitehack97.GuildWorldManager.GuildWorldManager;
import org.whitehack97.GuildWorldManager.Util.FileReader;

public class ManagePlayer
{
	private Player player;
	private String factionWorld = "None";
	private boolean worldPassed = false;
	private boolean EnableWhitelist = false;
	private boolean EnableBlacklist = false;
	private List<String> WhitelistPlayer = new ArrayList<String>();
	private List<String> BlacklistPlayer = new ArrayList<String>();
	private Location location;
	
	public ManagePlayer(Player player)
	{
		this.player = player;
		YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(player);
		worldPassed = PlayerSection.getBoolean("World.World-Passed");
		if(PlayerSection.contains("World.Faction-World")) //만약에 개인이 소유하고 있는 월드가 있다면
		{
			factionWorld = PlayerSection.getString("World.Faction-World"); // 월드 이름을 가져옴
			if(PlayerSection.contains("World.Location"))
			{
				World world = Bukkit.getServer().getWorld(factionWorld);
				double x = PlayerSection.getDouble("World.Location.X");
				double y = PlayerSection.getDouble("World.Location.Y");
				double z = PlayerSection.getDouble("World.Location.Z");
				float yaw = Float.parseFloat(PlayerSection.getString("World.Location.Yaw"));
				float pitch = Float.parseFloat(PlayerSection.getString("World.Location.Pitch"));
				location = new Location(world,x,y,z,yaw,pitch);
			}
		}
		EnableWhitelist = PlayerSection.getBoolean("World.Whitelist.Enabled");
		EnableBlacklist = PlayerSection.getBoolean("World.Blacklist.Enabled");
		if(PlayerSection.contains("World.Whitelist.Players")) WhitelistPlayer = PlayerSection.getStringList("World.Whitelist.Players");
		if(PlayerSection.contains("World.Blacklist.Players")) BlacklistPlayer = PlayerSection.getStringList("World.Blacklist.Players");
		GuildWorldManager.PlayerInformations.put(player.getName(), this);
	}
	
	public void setFactionWorld(String WorldName)
	{
		this.factionWorld = WorldName;
		File file = FileReader.getFile("Players/" + player.getName());
		YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(player);
		PlayerSection.set("World.Faction-World", factionWorld);
		FileReader.SaveFile(PlayerSection, file);
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public void setWorldPassed(boolean Enabled)
	{
		this.worldPassed = Enabled;
	}
	
	public void setEnableWhitelist(boolean Enabled)
	{
		
	}
	
	public void setEnableBlacklist(boolean Enabled)
	{
		
	}
	
	public boolean addWhitelist(String PlayerName)
	{
		for(String Name : WhitelistPlayer)
		{
			if(Name.equalsIgnoreCase(PlayerName.toLowerCase()))
			{
				return false;
			}
			else
			{
				continue;
			}
		}
		WhitelistPlayer.add(PlayerName.toLowerCase());
		return true;
	}
	
	public boolean addBlacklist(String PlayerName)
	{
		for(String Name : BlacklistPlayer)
		{
			if(Name.equalsIgnoreCase(PlayerName.toLowerCase()))
			{
				return false;
			}
			else
			{
				continue;
			}
		}
		BlacklistPlayer.add(PlayerName.toLowerCase());
		return true;
	}
	
	public boolean removeWhitelist(String PlayerName)
	{
		return false;
	}
	
	public boolean removeBlacklist(String PlayerName)
	{
		return false;
	}
	
	public boolean isWhitelisted(String PlayerName)
	{
		for(String Name : WhitelistPlayer)
		{
			if(Name.equalsIgnoreCase(PlayerName.toLowerCase()))
			{
				return true;
			}
			else
			{
				continue;
			}
		}
		return false;
	}
	
	public boolean isBlacklisted(String PlayerName)
	{
		for(String Name : BlacklistPlayer)
		{
			if(Name.equalsIgnoreCase(PlayerName.toLowerCase()))
			{
				return true;
			}
			else
			{
				continue;
			}
		}
		return false;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public Location getLocation()
	{
		return this.location;
	}
	
	public boolean hasLocation()
	{
		try
		{
			if(location != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(NullPointerException e)
		{
			return false;
		}
	}
	
	public String getFactionWorld()
	{
		return this.factionWorld;
	}
	
	public boolean isWorldPassed()
	{
		return this.worldPassed;
	}
	
	public boolean EnabledWhitelist()
	{
		return this.EnableWhitelist;
	}
	
	public boolean EnabledBlacklist()
	{
		return this.EnableBlacklist;
	}
	
	public List<String> getWhitelist()
	{
		return this.WhitelistPlayer;
	}
	
	public List<String> getBlacklist()
	{
		return this.BlacklistPlayer;
	}

	public boolean hasWorld()
	{
		if(factionWorld.equalsIgnoreCase("None") || factionWorld.equalsIgnoreCase(null))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}

package org.whitehack97.GuildWorldManager.player;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.whitehack97.GuildWorldManager.GuildWorldManager;
import org.whitehack97.GuildWorldManager.Util.FileReader;
import org.whitehack97.GuildWorldManager.Util.MessageManager;
import org.whitehack97.GuildWorldManager.api.ManagePlayer;

public class PlayerInformation
{
	public static void LoadPlayerFile(Player player)
	{
		File file = new File("plugins/GuildWorldManager/Players/" + player.getName() + ".yml");
// ****************************************************************파일이 없을 경우
		if(! file.exists())
		{
			try
			{
				File Dic = new File("plugins/GuildWorldManager/");
				if(! Dic.exists()) Dic.mkdir();
				File Dic2 = new File("plugins/GuildWorldManager/Players/");
				if(! Dic2.exists()) Dic2.mkdir();
				file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				MessageManager.Cmsg("&eCannot create file.");
				return;
			}
			YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(player);
			PlayerSection.set("UUID", player.getUniqueId().toString());
			PlayerSection.set("World.Faction-World", "None"); // <- 월드는 어드민 또는 오피에 의해서만 부여받을 수 있으므로 None 상태
			PlayerSection.set("World.World-Passed", false);
			PlayerSection.set("World.Whitelist.Enabled", false);
			PlayerSection.set("World.Blacklist.Enabled", false);
			try
			{
				PlayerSection.save(file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
// **************************************************************************
		GuildWorldManager.PlayerInformations.put(player.getName(), new ManagePlayer(player)); // 플레이어 정보를 플러그인 내부에 저장
		GuildInformation.RegisterGuild(player);
		return;
	}

}

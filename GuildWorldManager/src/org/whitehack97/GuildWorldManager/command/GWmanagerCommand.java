package org.whitehack97.GuildWorldManager.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.whitehack97.GuildWorldManager.GuildWorldManager;
import org.whitehack97.GuildWorldManager.Util.FileReader;
import org.whitehack97.GuildWorldManager.Util.MessageManager;
import org.whitehack97.GuildWorldManager.api.ManagePlayer;
import org.whitehack97.GuildWorldManager.player.GuildInformation;
import org.whitehack97.GuildWorldManager.player.PlayerInformation;
import org.whitehack97.rWorldGUI.config.InventoryConfig;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

public class GWmanagerCommand implements CommandExecutor
{
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender Sender, Command command, String label, String[] args)
	{
		if(Sender instanceof Player)
		{
			String cmd = command.getName();
			Player p = (Player)Sender;
			if(cmd.equalsIgnoreCase("GuildWorldManager.Main"))
			{
				if(MPlayer.get(p).hasFaction())
				{
					if(args.length < 1)
					{
						HelpPage(p, 1);
					}
					else
					{
						if(args[0].equalsIgnoreCase("page") || args[0].equalsIgnoreCase("help"))
						{
							if(args.length < 2)
							{
								HelpPage(p, 1);
								return true;
							}
							else
							{
								HelpPage(p, CovNumber(args[1]));
								return true;
							}
						}
						else if(args[0].equalsIgnoreCase("spawn"))
						{
							if(args.length < 2)
							{
								Faction faction = MPlayer.get(p).getFaction();
								Player LeaderPlayer = faction.getLeader().getPlayer();
								ManagePlayer Leader;
								if(GuildWorldManager.PlayerInformations.containsKey(LeaderPlayer.getName()))
								{
									Leader = GuildWorldManager.PlayerInformations.get(LeaderPlayer.getName());
								}
								else
								{
									MessageManager.msg(p, "&c오류: &4길드장에 대한 정보가 없습니다. 길드장이 접속한다면 자동 등록될 것입니다.");
									return false;
								}
								MessageManager.msg(p, "&6길드 월드로 이동 중입니다...");
								
								if(! Leader.hasWorld())
								{
									MessageManager.msg(p, "&c오류:&4 길드장은 월드를 소유하지 않았습니다.");
									return false;
								}
								if(! Leader.hasLocation())
								{
									MessageManager.msg(p, "&c오류:&4 길드장이 기본 스폰 지역을 지정하지 않았습니다.");
									MessageManager.msg(p, "&f[&e제안&f] &6다른 명령어를 사용해보십시오. &a/g defaultspawn");
									return false;
								}
								else
								{
									p.teleport(Leader.getLocation());
									return true;
								}
							}
							else
							{
								YamlConfiguration InfoSection = YamlConfiguration.loadConfiguration(new File("plugins/GuildWorldManager/Worlds.yml"));
								if(! InfoSection.contains(args[1]))
								{
									MessageManager.msg(p, "&c오류:&4 \"" + args[1] + "\" 해당 이름을 가진 길드 정보가 없습니다.");
									return false;
								}
								else
								{
									YamlConfiguration PlayerSection = YamlConfiguration.loadConfiguration(new File("plugins/GuildWorldManager/Players/" + InfoSection.getString(args[1] + ".Leader") + ".yml"));
									MessageManager.msg(p, "&e길드&f " + args[1] + "&a에서 &6지정한 길드 스폰 지역으로 이동하기 위해 시도 중입니다...");
									
									if(! PlayerSection.contains("World.Faction-World"))
									{
										MessageManager.msg(p, "&c오류: &4해당 길드의 길드장은 월드를 소유하지 않았습니다.");
										return false;
									}
									if(! PlayerSection.contains("World.Location"))
									{
										MessageManager.msg(p, "&c오류:&4 해당 길드장이 기본 스폰 지역을 지정하지 않았습니다.");
										return false;
									}
									else
									{
										World world = Bukkit.getServer().getWorld(PlayerSection.getString("World.Faction-World"));
										Double x = PlayerSection.getDouble("World.Location.X");
										Double y = PlayerSection.getDouble("World.Location.Y");
										Double z = PlayerSection.getDouble("World.Location.Z");
										Float yaw = Float.parseFloat(PlayerSection.getString("World.Location.Yaw"));
										Float pitch = Float.parseFloat(PlayerSection.getString("World.Location.Pitch"));
										Location location = new Location(world, x, y, z, yaw, pitch);
										p.teleport(location);
										return true;
									}
								}
							}
						}
						else if(args[0].equalsIgnoreCase("defaultspawn"))
						{
							MessageManager.msg(p, "&6길드장이 소유하고 있는 디폴트 위치로 이동 중입니다...");
							Player Leader = MPlayer.get(p).getFaction().getLeader().getPlayer();
							ManagePlayer LeaderPlayer = GuildWorldManager.PlayerInformations.get(Leader.getName());
							if(LeaderPlayer.hasWorld())
							{
								if(LeaderPlayer.hasLocation())
								{
									MessageManager.msg(p, "&6길드장이 지정한 스폰 위치가 있습니다. 이동을 시도합니다...");
									p.teleport(LeaderPlayer.getLocation());
									return true;
								}
								else
								{
									p.teleport(new Location(Bukkit.getServer().getWorld(LeaderPlayer.getFactionWorld()), 0, 254, 0, 0, 0));
									return true;
								}
							}
							else
							{
								MessageManager.msg(p, "&c오류: &4길드장은 월드를 소유하지 않았습니다.");
								return false;
							}
						}
						else if(args[0].equalsIgnoreCase("passworld"))
						{
							if(MPlayer.get(p).getRole() == Rel.LEADER)
							{
								ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
								if(Mplayer.hasWorld())
								{
									if(args.length < 2)
									{
										File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
										YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
										PlayerSection.set("World.World-Passed", !Mplayer.isWorldPassed());
										FileReader.SaveFile(PlayerSection, LeaderFile);
										PlayerInformation.LoadPlayerFile(p);
										String Enabled = !Mplayer.isWorldPassed() ? "&c개방" : "&b폐쇄";
										MessageManager.msg(p, "&a월드 개방 상태가 " + Enabled + " &a상태로 전환되었습니다.");
										if(Enabled.equalsIgnoreCase("&c개방")) MessageManager.msg(p, "&f[&4경고&f] &c길드 월드를 개방하면 보안이 매우 취약해집니다!");
										return true;
									}
									else
									{
										File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
										YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
										
										if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true"))
										{
											if(Mplayer.isWorldPassed())
											{
												MessageManager.msg(p, "&c취소: &4이미 월드가 개방 상태입니다.");
												return false;
											}
											else
											{
												PlayerSection.set("World.World-Passed", true);
												FileReader.SaveFile(PlayerSection, LeaderFile);
												PlayerInformation.LoadPlayerFile(p);
												MessageManager.msg(p, "&a월드 개방 상태가 &c개방 &a상태로 설정되었습니다.");
												MessageManager.msg(p, "&f[&4경고&f] &c길드 월드를 개방하면 보안이 매우 취약해집니다!");
												return true;
											}
										}
										else if(args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false"))
										{
											if(! Mplayer.isWorldPassed())
											{
												MessageManager.msg(p, "&c취소: &4이미 월드가 폐쇄 상태입니다.");
												return false;
											}
											else
											{
												PlayerSection.set("World.World-Passed", false);
												FileReader.SaveFile(PlayerSection, LeaderFile);
												PlayerInformation.LoadPlayerFile(p);
												MessageManager.msg(p, "&a월드 개방 상태가 &b폐쇄 &a상태로 설정되었습니다.");
												return true;
											}
										}
										else
										{
											MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
											MessageManager.msg(p, "&6사용법: /g passworld on | off 또는  true | false");
											return false;
										}
									}
								}
								else
								{
									MessageManager.msg(p, "&c오류: &4먼저 월드부터 소유하셔야 합니다.");
									return false;
								}
							}
							else
							{
								MessageManager.msg(p, "&c거부: &4길드장만 사용 가능한 명령어입니다.");
								return false;
							}
						}
						else if(args[0].equalsIgnoreCase("setspawn"))
						{
							if(MPlayer.get(p).getRole() == Rel.LEADER)
							{
								ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
								if(Mplayer.hasWorld())
								{
									if(p.getLocation().getWorld().equals(Bukkit.getServer().getWorld(Mplayer.getFactionWorld())))
									{
										Location location = p.getLocation();
										File file = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
										YamlConfiguration LeaderSection = FileReader.LoadPlayerFile(p);
										if(!LeaderSection.contains("World.Location"))
										{
											LeaderSection.createSection("World.Location");
										}
										LeaderSection.set("World.Location.X", location.getX());
										LeaderSection.set("World.Location.Y", location.getY());
										LeaderSection.set("World.Location.Z", location.getZ());
										LeaderSection.set("World.Location.Yaw", location.getYaw());
										LeaderSection.set("World.Location.Pitch", location.getPitch());
										FileReader.SaveFile(LeaderSection, file);
										PlayerInformation.LoadPlayerFile(p);
										MessageManager.msg(p, "&a현재 서있는 위치를 길드 기본 스폰 위치로 지정하였습니다.");
										File guildFile = new File("plugins/rWorldGUI/guildworld.yml");
										if(guildFile.exists())
										{
											YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(guildFile);
											if(GuildSection.contains(Mplayer.getFactionWorld()))
											{
												MessageManager.msg(p, "&arWorldGUI에서 길드 월드의 정보를 읽고 있습니다.");
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.X", location.getX());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.Y", location.getY());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.Z", location.getZ());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.YAW", location.getYaw());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.PITCH", location.getPitch());
												try
												{
													GuildSection.save(guildFile);
													InventoryConfig.InventoryLoad();
													MessageManager.msg(p, "&arWorldGUI에서 스폰 위치의 정보를 정상적으로 변경하였습니다.");
												}
												catch (IOException e)
												{
													e.printStackTrace();
												}
											}
										}
										return true;
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4자신이 소유하고 있는 월드에서만 길드 스폰 위치를 지정할 수 있습니다.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c오류: &4먼저 월드를 소유하고 있어야 합니다.");
									return false;
								}
							}
							else
							{
								MessageManager.msg(p, "&c거부: &4길드장만 월드 스폰 위치를 지정할 수 있습니다.");
								return false;
							}
						}
						else if(args[0].equalsIgnoreCase("add"))
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c오류: &4인자값이 없습니다!");
								MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist, world");
								return false;
							}
							else
							{
								if(args[1].equalsIgnoreCase("world"))
								{
									if(p.isOp())
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&c오류: &4플레이어 명을 기입하여 주십시오!");
											MessageManager.msg(p, "&6사용법 : /g add world <player> <world>");
											return false;
										}
										else
										{
											if(args.length < 4)
											{
												MessageManager.msg(p, "&c오류: &4월드 명을 기입하여 주십시오!");
												MessageManager.msg(p, "&6사용법 : /g add world <player> <world>");
											}
											else
											{
												try
												{
													if(Bukkit.getServer().getOfflinePlayer(args[2]).hasPlayedBefore())
													{
														try
														{
															File file = new File("plugins/GuildWorldManager/Players/" + args[2] + ".yml");
															if(!file.exists())
															{
																MessageManager.msg(p, "&c오류: &4해당 플레이어 정보가 없습니다.");
																return false;
															}
															World world = Bukkit.getServer().getWorld(args[3]);
															YamlConfiguration PlayerSection = YamlConfiguration.loadConfiguration(file);
															PlayerSection.set("World.Faction-World", world.getName());
															FileReader.SaveFile(PlayerSection, file);
															boolean Passed = GuildInformation.RegisterGuild(args[2]);
															if(Passed) MessageManager.msg(p, "&a" + args[2] + "님에게 월드 " + world.getName() + "의 사용 권한을 정상적으로 부여했습니다.");
															else
															{
																MessageManager.msg(p, "&b해당 플레이어에게 월드 권한을 임시 부여했습니다.");
																MessageManager.msg(p, "&b플레이어가 접속한다면 월드 소유가 정상 등록될 것입니다.");
															}
															return true;
														}
														catch(NullPointerException e)
														{
															e.printStackTrace();
															MessageManager.msg(p, "&c오류: &4기입한 월드가 이 서버에 없습니다.");
															return false;
														}
													}
													else
													{
														MessageManager.msg(p, "&c오류: &4\'" + args[2] + "\" 해당 플레이어는 서버 플레이 기록이 없습니다!");
														return false;
													}
												}
												catch(NullPointerException e)
												{
													MessageManager.msg(p, "&c오류: 인자값을 읽어오는 과정에서 서버에 존재하지 않는 인자값이 발견되었습니다.");
													return false;
												}
											}
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4권한이 거부되었습니다. 오피 이상만 사용가능합니다.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("whitelist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&a화이트리스트를 이용해 길드원이 아닌 플레이어도");
											MessageManager.msg(p, "&a유저님의 길드 월드로 입장 할 수 있습니다!");
											MessageManager.msg(p, "&f[&e유의&f]&b 한 번에 여러 명 등록이 가능합니다.");
											MessageManager.msg(p, "&6사용법 : /g add whitelist <player>...");
											return true;
										}
										else
										{
											String Names = "이름 ";
											int count = 0;
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													Names = Names + " " + args[count];
												}
											}
											File file = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(file);
											ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
											MessageManager.msg(p, "&6화이트리스트에 총 " + String.valueOf(args.length - 2) + "명을 추가합니다.");
											MessageManager.msg(p, "&6유저 " + Names);
											count = 0;
											List<String> WhitelistUser = Mplayer.getWhitelist();
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													if(Mplayer.isBlacklisted(args[count]))
													{
														MessageManager.msg(p, "&c경고: &f" + args[count] + "님은 블랙리스트 유저입니다. 화이트리스트 효력이 없습니다.");
													}
													
													if(Mplayer.isWhitelisted(args[count]))
													{
														MessageManager.msg(p, "&c무시: &4" + args[count] + "님은 이미 화이트리스트 유저입니다.");
														continue;
													}
													else
													{
														WhitelistUser.add(args[count].toLowerCase());
													}
												}
											}
											LeaderSection.set("World.Whitelist.Players", WhitelistUser);
											FileReader.SaveFile(LeaderSection, file);
											PlayerInformation.LoadPlayerFile(p);
											MessageManager.msg(p, "&a정상적으로 등록이 완료되었습니다. 화이트리스트 확인은");
											MessageManager.msg(p, "&6/g info whitelist");
											if(! Mplayer.EnabledWhitelist())
											{
												MessageManager.msg(p, "&f[&e유의&f] &f화이트리스트 기능이 활성화되지 않았습니다.");
												MessageManager.msg(p, "&a활성화: &6/g set whitelist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4길드장만 사용 가능한 명령어입니다.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("blacklist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&c블랙리스트를 이용해 길드원 뿐만 아니라 다른 플레이어도");
											MessageManager.msg(p, "&a유저님의 길드 월드에 들어갈 수 없습니다!");
											MessageManager.msg(p, "&f[&e유의&f]&b 한 번에 여러 명 등록이 가능합니다.");
											MessageManager.msg(p, "&6사용법 : /g add blacklist <player>...");
											return true;
										}
										else
										{
											String Names = "이름 ";
											int count = 0;
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													Names = Names + " " + args[count];
												}
											}
											File file = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(file);
											ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
											MessageManager.msg(p, "&6블랙리스트에 총 " + String.valueOf(args.length - 2) + "명을 추가합니다.");
											MessageManager.msg(p, "&6유저 " + Names);
											count = 0;
											List<String> BlacklistUser = Mplayer.getBlacklist();
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													if(Mplayer.isWhitelisted(args[count]))
													{
														MessageManager.msg(p, "&c경고: &f" + args[count] + "님은 화이트리스트 유저입니다.");
													}
													
													if(Mplayer.isBlacklisted(args[count]))
													{
														MessageManager.msg(p, "&c무시: &4" + args[count] + "님은 이미 블랙리스트 유저입니다.");
														continue;
													}
													else
													{
														BlacklistUser.add(args[count].toLowerCase());
													}
												}
											}
											LeaderSection.set("World.Blacklist.Players", BlacklistUser);
											FileReader.SaveFile(LeaderSection, file);
											PlayerInformation.LoadPlayerFile(p);
											MessageManager.msg(p, "&a정상적으로 등록이 완료되었습니다. 블랙리스트 확인은");
											MessageManager.msg(p, "&6/g info blacklist");
											if(! Mplayer.EnabledBlacklist())
											{
												MessageManager.msg(p, "&f[&e유의&f] &f블랙리스트 기능이 활성화되지 않았습니다.");
												MessageManager.msg(p, "&a활성화: &6/g set blacklist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4길드장만 사용 가능한 명령어입니다.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
									MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist, world");
									return false;
								}
							}
						}
						else if(args[0].equalsIgnoreCase("del")) 
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c오류: &4인자값이 없습니다!");
								MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist, world");
								return false;
							}
							else
							{
								if(args[1].equalsIgnoreCase("world"))
								{
									if(p.isOp())
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&c오류: &4플레이어 명을 기입하여 주십시오!");
											MessageManager.msg(p, "&6사용법 : /g del world <player>");
											return false;
										}
										else
										{
											try
											{
												if(Bukkit.getServer().getOfflinePlayer(args[2]).hasPlayedBefore())
												{
													try
													{
														File file = new File("plugins/GuildWorldManager/Players/" + args[2] + ".yml");
														YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
														PlayerSection.set("World.Faction-World", "None");
														FileReader.SaveFile(PlayerSection, file);
														MessageManager.msg(p, "&a" + args[2] + "님의 월드 사용 권한을 정상적으로 박탈했습니다.");
														return true;
													}
													catch(NullPointerException e)
													{
														MessageManager.msg(p, "&c오류: &4플레이어에 관한 정보가 없습니다.");
														return false;
													}
												}
												else
												{
													MessageManager.msg(p, "&c오류: &4\'" + args[2] + "\" 해당 플레이어는 서버 플레이 기록이 없습니다!");
													return false;
												}
											}
											catch(NullPointerException e)
											{
												MessageManager.msg(p, "&c오류: 인자값을 읽어오는 과정에서 서버에 존재하지 않는 인자값이 발견되었습니다.");
												return false;
											}
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4권한이 거부되었습니다. 오피 이상만 사용가능합니다.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("whitelist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&a화이트리스트를 삭제합니다.");
											MessageManager.msg(p, "&f[&e유의&f]&b 한 번에 여러 명 삭제도 가능합니다.");
											MessageManager.msg(p, "&6사용법 : /g del whitelist <player>...");
											return true;
										}
										else
										{
											String Names = "이름 ";
											int count = 0;
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													Names = Names + " " + args[count];
												}
											}
											File file = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(file);
											ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
											MessageManager.msg(p, "&6화이트리스트에서 총 " + String.valueOf(args.length - 2) + "명을 삭제합니다.");
											MessageManager.msg(p, "&6유저 " + Names);
											count = 0;
											List<String> WhitelistUser = Mplayer.getWhitelist();
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													if(! Mplayer.isWhitelisted(args[count]))
													{
														MessageManager.msg(p, "&c무시: &4" + args[count] + "님은 화이트리스트 유저가 아닙니다.");
														continue;
													}
													else
													{
														WhitelistUser.remove(args[count].toLowerCase());
													}
												}
											}
											LeaderSection.set("World.Whitelist.Players", WhitelistUser);
											FileReader.SaveFile(LeaderSection, file);
											PlayerInformation.LoadPlayerFile(p);
											MessageManager.msg(p, "&a정상적으로 삭제가 완료되었습니다. 화이트리스트 확인은");
											MessageManager.msg(p, "&6/g info whitelist");
											if(! Mplayer.EnabledWhitelist())
											{
												MessageManager.msg(p, "&f[&e유의&f] &f화이트리스트 기능이 활성화되지 않았습니다.");
												MessageManager.msg(p, "&a활성화: &6/g set whitelist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4길드장만 사용 가능한 명령어입니다.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("Blacklist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&a블랙리스트를 삭제합니다.");
											MessageManager.msg(p, "&f[&e유의&f]&b 한 번에 여러 명 삭제도 가능합니다.");
											MessageManager.msg(p, "&6사용법 : /g del blacklist <player>...");
											return true;
										}
										else
										{
											String Names = "이름 ";
											int count = 0;
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													Names = Names + " " + args[count];
												}
											}
											File file = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(file);
											ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
											MessageManager.msg(p, "&6블랙리스트에서 총 " + String.valueOf(args.length - 2) + "명을 삭제합니다.");
											MessageManager.msg(p, "&6유저 " + Names);
											count = 0;
											List<String> BlacklistUser = Mplayer.getBlacklist();
											for(;count < args.length; count++)
											{
												if(count == 0 || count == 1)
												{
													continue;
												}
												else
												{
													if(! Mplayer.isBlacklisted(args[count]))
													{
														MessageManager.msg(p, "&c무시: &4" + args[count] + "님은 블랙리스트 유저가 아닙니다.");
														continue;
													}
													else
													{
														BlacklistUser.remove(args[count].toLowerCase());
													}
												}
											}
											LeaderSection.set("World.Blacklist.Players", BlacklistUser);
											FileReader.SaveFile(LeaderSection, file);
											PlayerInformation.LoadPlayerFile(p);
											MessageManager.msg(p, "&a정상적으로 삭제가 완료되었습니다. 블랙리스트 확인은");
											MessageManager.msg(p, "&6/g info blacklist");
											if(! Mplayer.EnabledWhitelist())
											{
												MessageManager.msg(p, "&f[&e유의&f] &f블랙리스트 기능이 활성화되지 않았습니다.");
												MessageManager.msg(p, "&a활성화: &6/g set blacklist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4길드장만 사용 가능한 명령어입니다.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
									MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist, world");
									return false;
								}
							}

						}
						else if(args[0].equalsIgnoreCase("set"))
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c오류: &4인자값이 없습니다!");
								MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist");
								return false;
							}
							else
							{
								if(args[1].equalsIgnoreCase("whitelist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
										if(args.length < 3)
										{
											File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
											PlayerSection.set("World.Whitelist.Enabled", !Mplayer.EnabledWhitelist());
											FileReader.SaveFile(PlayerSection, LeaderFile);
											PlayerInformation.LoadPlayerFile(p);
											String Enabled = !Mplayer.EnabledWhitelist() ? "&b활성화" : "&c비활성화";
											MessageManager.msg(p, "&a화이트 리스트 기능이 " + Enabled + " &a상태로 전환되었습니다.");
											return true;
										}
										else
										{
											File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
											if(args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
											{	
												PlayerSection.set("World.Whitelist.Enabled", true);
												MessageManager.msg(p, "&a화이트 리스트 기능이 &b활성화 &a상태로 설정되었습니다.");
											}
											else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
											{
												PlayerSection.set("World.Whitelist.Enabled", false);
												MessageManager.msg(p, "&a화이트 리스트 기능이  &c비활성화 &a상태로 설정되었습니다.");
											}
											else
											{
												MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
												MessageManager.msg(p, "&6사용법: /g set whitelist on | off 또는  true | false");
												return false;
											}
											FileReader.SaveFile(PlayerSection, LeaderFile);
											PlayerInformation.LoadPlayerFile(p);
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4길드장만 사용 가능한 명령어입니다.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("blacklist"))
								{
									ManagePlayer Mplayer = GuildWorldManager.PlayerInformations.get(p.getName());
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
											PlayerSection.set("World.Blacklist.Enabled", !Mplayer.EnabledBlacklist());
											FileReader.SaveFile(PlayerSection, LeaderFile);
											PlayerInformation.LoadPlayerFile(p);
											String Enabled = !Mplayer.EnabledBlacklist() ? "&b활성화" : "&c비활성화";
											MessageManager.msg(p, "&a블랙리스트 기능이 " + Enabled + " &a상태로 전환되었습니다.");
											return true;
										}
										else
										{
											File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
											if(args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
											{	
												PlayerSection.set("World.Blacklist.Enabled", true);
												MessageManager.msg(p, "&a블랙 리스트 기능이 &b활성화 &a상태로 설정되었습니다.");
											}
											else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
											{
												PlayerSection.set("World.Blacklist.Enabled", false);
												MessageManager.msg(p, "&a블랙 리스트 기능이  &c비활성화 &a상태로 설정되었습니다.");
											}
											else
											{
												MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
												MessageManager.msg(p, "&6사용법: /g set blacklist on | off 또는  true | false");
												return false;
											}
											FileReader.SaveFile(PlayerSection, LeaderFile);
											PlayerInformation.LoadPlayerFile(p);
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c거부: &4길드장만 사용 가능한 명령어입니다.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
									MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist");
									return false;
								}
							}
						}
						else if(args[0].equalsIgnoreCase("info"))
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c오류: &4인자값이 없습니다!");
								MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist, guild");
								return false;
							}
							else
							{
								if(args[1].equalsIgnoreCase("whitelist"))
								{
									Player LeaderPlayer = MPlayer.get(p).getFaction().getLeader().getPlayer();
									File LeaderFile = new File("plugins/GuildWorldManager/Players/" + LeaderPlayer.getName() + ".yml");
									YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
									List<String> WhitelistPlayer = new ArrayList<String>();
									if(LeaderSection.contains("World.Whitelist.Players"))
									{
										WhitelistPlayer = LeaderSection.getStringList("World.Whitelist.Players");
									}
									MessageManager.msg(p, "&8=======================================================");
									MessageManager.msg(p, "&eGuild Information Checking System");
									String Enabled = LeaderSection.getBoolean("World.Whitelist.Enabled") ? "&a활성화" : "&c비활성화";
									MessageManager.msg(p, "&a화이트리스트 상태:&f " + Enabled);
									MessageManager.msg(p, "&b현재 " + MPlayer.get(p).getFaction().getName() + " 길드에 추가된 화이트리스트는");
									if(WhitelistPlayer.size() == 0)
									{
										MessageManager.msg(p, "&c한 명도 없습니다!");
										return false;
									}
									else
									{
										MessageManager.msg(p, "&e총 " + WhitelistPlayer.size() + "&e명입니다.");
									}
									MessageManager.msg(p, "");
									MessageManager.msg(p, "&b화이트 리스트 목록");
									int count = 1;
									for(String Name : WhitelistPlayer)
									{
										MessageManager.msg(p, "&f[&e" + count + "&f] " + Name);
										count++;
									}
									return true;
								}
								else if(args[1].equalsIgnoreCase("blacklist"))
								{
									Player LeaderPlayer = MPlayer.get(p).getFaction().getLeader().getPlayer();
									File LeaderFile = new File("plugins/GuildWorldManager/Players/" + LeaderPlayer.getName() + ".yml");
									YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
									List<String> BlacklistPlayer = new ArrayList<String>();
									if(LeaderSection.contains("World.Blacklist.Players"))
									{
										BlacklistPlayer = LeaderSection.getStringList("World.Blacklist.Players");
									}
									MessageManager.msg(p, "&8=======================================================");
									MessageManager.msg(p, "&eGuild Information Checking System");
									String Enabled = LeaderSection.getBoolean("World.Blacklist.Enabled") ? "&a활성화" : "&c비활성화";
									MessageManager.msg(p, "&a블랙리스트 상태:&f " + Enabled);
									MessageManager.msg(p, "&b현재 " + MPlayer.get(p).getFaction().getName() + " 길드에 추가된 블랙리스트는");
									if(BlacklistPlayer.size() == 0)
									{
										MessageManager.msg(p, "&c한 명도 없습니다!");
										return false;
									}
									else
									{
										MessageManager.msg(p, "&e총 " + BlacklistPlayer.size() + "&e명입니다.");
									}
									MessageManager.msg(p, "");
									MessageManager.msg(p, "&b블랙 리스트 목록");
									int count = 1;
									for(String Name : BlacklistPlayer)
									{
										MessageManager.msg(p, "&f[&e" + count + "&f] " + Name);
										count++;
									}
									return true;
								}
								else if(args[1].equalsIgnoreCase("guild"))
								{
									Player LeaderPlayer = MPlayer.get(p).getFaction().getLeader().getPlayer();
									File LeaderFile = new File("plugins/GuildWorldManager/Players/" + LeaderPlayer.getName() + ".yml");
									YamlConfiguration LeaderSection = YamlConfiguration.loadConfiguration(LeaderFile);
									MessageManager.msg(p, "&8=======================================================");
									MessageManager.msg(p, "&eGuild Information Checking System");
									MessageManager.msg(p, "&a길드명:&f " + MPlayer.get(p).getFaction().getName());
									MessageManager.msg(p, "&a길드 리더:&f " + LeaderPlayer.getName());
									MessageManager.msg(p, "&a길드 대표 월드:&f " + LeaderSection.getString("World.Faction-World"));
									MessageManager.msg(p, "&a화이트 리스트 수:&f " + LeaderSection.getStringList("World.Whitelist.Players").size() + "명");
									MessageManager.msg(p, "&a블랙 리스트 수:&f " + LeaderSection.getStringList("World.Blacklist.Players").size() + "명");
									return true;							
								}
								else
								{
									MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
									MessageManager.msg(p, "&6사용 가능한 인자값 : whitelist, blacklist, guild");
									return false;
								}
							}
						}
						else
						{
							MessageManager.msg(p, "&c오류: &4인자값을 알 수 없습니다.");
							MessageManager.msg(p, "&6명령어 확인 하기: /g");
							return false;
						}
					}
				}
				else
				{
					MessageManager.msg(p, "&c거부: &4길드에 소속되어 있어야 명령어를 사용할 수 있습니다.");
					return false;
				}
			}
		}
		else
		{
			
		}
		return false;
	}
	
	public void HelpPage(Player p, int Page)
	{
		if(Page == 1)
		{
			MessageManager.msg(p, "&8=================================================");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &9v" + GuildWorldManager.plugin.getDescription().getVersion() + " &cPage " + Page + "/4");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g | guild | gwmanager | guildworldmanager");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &fmain aliases.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g example&f: &6길드원 사용 가능&f, &a/g example&f: &c길드장만 사용 가능");
			MessageManager.msg(p, "&4/g example&f: &4오피만 사용 가능");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g <help | page> [page]");
			MessageManager.msg(p, "&f[Page]페이지 도움말 보기");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g spawn [guild]");
			MessageManager.msg(p, "&f길드장이 소유하고 있는 길드 월드 스폰 위치으로 이동");
			MessageManager.msg(p, "&f만약에 길드명을 기입 시 해당 길드 월드 스폰으로 이동 (허가 시)");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g info <blacklist | whitelist | guild>");
			MessageManager.msg(p, "&f화이트 리스트 정보 또는 블랙 리스트 정보 또는 길드 정보를 봄");
			return;
		}
		else if(Page == 2)
		{
			MessageManager.msg(p, "&8=================================================");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &9v" + GuildWorldManager.plugin.getDescription().getVersion() + " &cPage " + Page + "/4");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&6/g | guild | gwmanager | guildworldmanager");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &fmain aliases.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g example&f: &6길드원 사용 가능&f, &a/g example&f: &c길드장만 사용 가능");
			MessageManager.msg(p, "&4/g example&f: &4오피만 사용 가능");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g defaultspawn");
			MessageManager.msg(p, "&f길드장이 소유한 길드 월드의 디폴트 위치로 이동");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g setspawn");
			MessageManager.msg(p, "&f현재 서있는 곳을 길드 월드 스폰 위치로 지정");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g passworld <[on|off] | [true|false]>");
			MessageManager.msg(p, "&f현재 소유한 월드를 개방함");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g add <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>를 화이트리스트 또는 블랙리스트로 추가함");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g del <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>를 화이트리스트 또는 블랙리스트에서 삭제함");
			return;
		}
		else if(Page == 3)
		{
			MessageManager.msg(p, "&8=================================================");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &9v" + GuildWorldManager.plugin.getDescription().getVersion() + " &cPage " + Page + "/4");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&6/g | guild | gwmanager | guildworldmanager");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &fmain aliases.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g example&f: &6길드원 사용 가능&f, &a/g example&f: &c길드장만 사용 가능");
			MessageManager.msg(p, "&4/g example&f: &4오피만 사용 가능");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g add <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>를 화이트리스트 또는 블랙리스트로 추가함");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&f화이트리스트 : &b길드 월드에 입장하려고 할 때");
			MessageManager.msg(p, "&b<player>님은 입장 차단이 되지 않고 통과됩니다.");
			MessageManager.msg(p, "&f[&e유의&f] 길드장, 길드원은 굳이 추가하지 않아도 됩니다.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c블랙리스트 : &c길드 월드에 입장하려고 할 때");
			MessageManager.msg(p, "&c<player>님은 길드원 또는 화이트리스트에 추가해도 차단됩니다.");
			MessageManager.msg(p, "&f[&4주의&f] &c길드원도 적용되며, 길드장은 적용되지 않습니다.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g del <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>를 화이트리스트 또는 블랙리스트에서 삭제함");
			return;
		}
		else if(Page == 4)
		{
			MessageManager.msg(p, "&8=================================================");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &9v" + GuildWorldManager.plugin.getDescription().getVersion() + " &cPage " + Page + "/4");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&6/g | guild | gwmanager | guildworldmanager");
			MessageManager.msg(p, "&9&lG&fuild&e&lW&forld&fManager&b &fmain aliases.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g example&f: &6길드원 사용 가능&f, &a/g example&f: &c길드장만 사용 가능");
			MessageManager.msg(p, "&4/g example&f: &4오피만 사용 가능");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g set <whitelist | blacklist> <[on|off] | [true|false]>");
			MessageManager.msg(p, "&f화이트리스트 또는 블랙리스트 기능을 활성화하거나 비활성화함");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&4/g add world <player> <worldname>");
			MessageManager.msg(p, "&f해당 플레이어에게 해당 월드의 권한을 줌");
			MessageManager.msg(p, "&f만약 해당 플레이어가 길드장이라면 자동으로 길드 월드로 설정됨");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&4/g del world <player>");
			MessageManager.msg(p, "&f해당 플레이어가 가진 월드 권한을 박탈");
			return;
		}
		else if(Page == -255)
		{
			MessageManager.msg(p, "&c오류: &4잘못된 페이지 값을 입력하셨습니다. (가능한 페이지 : 1 ~ 4)");
			return;
		}
		else
		{
			MessageManager.msg(p, "&c오류: &4허용되는 페이지 값을 초과하였습니다. (가능한 페이지 : 1 ~ 4)");
			return;
		}
	}
	
	public int CovNumber(String Number)
	{
		try
		{
			return Integer.parseInt(Number);
		}
		catch(NumberFormatException e)
		{
			return -255;
		}
	}
}

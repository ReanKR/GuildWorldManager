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
									MessageManager.msg(p, "&c����: &4����忡 ���� ������ �����ϴ�. ������� �����Ѵٸ� �ڵ� ��ϵ� ���Դϴ�.");
									return false;
								}
								MessageManager.msg(p, "&6��� ����� �̵� ���Դϴ�...");
								
								if(! Leader.hasWorld())
								{
									MessageManager.msg(p, "&c����:&4 ������� ���带 �������� �ʾҽ��ϴ�.");
									return false;
								}
								if(! Leader.hasLocation())
								{
									MessageManager.msg(p, "&c����:&4 ������� �⺻ ���� ������ �������� �ʾҽ��ϴ�.");
									MessageManager.msg(p, "&f[&e����&f] &6�ٸ� ��ɾ ����غ��ʽÿ�. &a/g defaultspawn");
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
									MessageManager.msg(p, "&c����:&4 \"" + args[1] + "\" �ش� �̸��� ���� ��� ������ �����ϴ�.");
									return false;
								}
								else
								{
									YamlConfiguration PlayerSection = YamlConfiguration.loadConfiguration(new File("plugins/GuildWorldManager/Players/" + InfoSection.getString(args[1] + ".Leader") + ".yml"));
									MessageManager.msg(p, "&e���&f " + args[1] + "&a���� &6������ ��� ���� �������� �̵��ϱ� ���� �õ� ���Դϴ�...");
									
									if(! PlayerSection.contains("World.Faction-World"))
									{
										MessageManager.msg(p, "&c����: &4�ش� ����� ������� ���带 �������� �ʾҽ��ϴ�.");
										return false;
									}
									if(! PlayerSection.contains("World.Location"))
									{
										MessageManager.msg(p, "&c����:&4 �ش� ������� �⺻ ���� ������ �������� �ʾҽ��ϴ�.");
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
							MessageManager.msg(p, "&6������� �����ϰ� �ִ� ����Ʈ ��ġ�� �̵� ���Դϴ�...");
							Player Leader = MPlayer.get(p).getFaction().getLeader().getPlayer();
							ManagePlayer LeaderPlayer = GuildWorldManager.PlayerInformations.get(Leader.getName());
							if(LeaderPlayer.hasWorld())
							{
								if(LeaderPlayer.hasLocation())
								{
									MessageManager.msg(p, "&6������� ������ ���� ��ġ�� �ֽ��ϴ�. �̵��� �õ��մϴ�...");
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
								MessageManager.msg(p, "&c����: &4������� ���带 �������� �ʾҽ��ϴ�.");
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
										String Enabled = !Mplayer.isWorldPassed() ? "&c����" : "&b���";
										MessageManager.msg(p, "&a���� ���� ���°� " + Enabled + " &a���·� ��ȯ�Ǿ����ϴ�.");
										if(Enabled.equalsIgnoreCase("&c����")) MessageManager.msg(p, "&f[&4���&f] &c��� ���带 �����ϸ� ������ �ſ� ��������ϴ�!");
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
												MessageManager.msg(p, "&c���: &4�̹� ���尡 ���� �����Դϴ�.");
												return false;
											}
											else
											{
												PlayerSection.set("World.World-Passed", true);
												FileReader.SaveFile(PlayerSection, LeaderFile);
												PlayerInformation.LoadPlayerFile(p);
												MessageManager.msg(p, "&a���� ���� ���°� &c���� &a���·� �����Ǿ����ϴ�.");
												MessageManager.msg(p, "&f[&4���&f] &c��� ���带 �����ϸ� ������ �ſ� ��������ϴ�!");
												return true;
											}
										}
										else if(args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false"))
										{
											if(! Mplayer.isWorldPassed())
											{
												MessageManager.msg(p, "&c���: &4�̹� ���尡 ��� �����Դϴ�.");
												return false;
											}
											else
											{
												PlayerSection.set("World.World-Passed", false);
												FileReader.SaveFile(PlayerSection, LeaderFile);
												PlayerInformation.LoadPlayerFile(p);
												MessageManager.msg(p, "&a���� ���� ���°� &b��� &a���·� �����Ǿ����ϴ�.");
												return true;
											}
										}
										else
										{
											MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
											MessageManager.msg(p, "&6����: /g passworld on | off �Ǵ�  true | false");
											return false;
										}
									}
								}
								else
								{
									MessageManager.msg(p, "&c����: &4���� ������� �����ϼž� �մϴ�.");
									return false;
								}
							}
							else
							{
								MessageManager.msg(p, "&c�ź�: &4����常 ��� ������ ��ɾ��Դϴ�.");
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
										MessageManager.msg(p, "&a���� ���ִ� ��ġ�� ��� �⺻ ���� ��ġ�� �����Ͽ����ϴ�.");
										File guildFile = new File("plugins/rWorldGUI/guildworld.yml");
										if(guildFile.exists())
										{
											YamlConfiguration GuildSection = YamlConfiguration.loadConfiguration(guildFile);
											if(GuildSection.contains(Mplayer.getFactionWorld()))
											{
												MessageManager.msg(p, "&arWorldGUI���� ��� ������ ������ �а� �ֽ��ϴ�.");
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.X", location.getX());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.Y", location.getY());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.Z", location.getZ());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.YAW", location.getYaw());
												GuildSection.set(Mplayer.getFactionWorld()+ ".LOCATION.PITCH", location.getPitch());
												try
												{
													GuildSection.save(guildFile);
													InventoryConfig.InventoryLoad();
													MessageManager.msg(p, "&arWorldGUI���� ���� ��ġ�� ������ ���������� �����Ͽ����ϴ�.");
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
										MessageManager.msg(p, "&c�ź�: &4�ڽ��� �����ϰ� �ִ� ���忡���� ��� ���� ��ġ�� ������ �� �ֽ��ϴ�.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c����: &4���� ���带 �����ϰ� �־�� �մϴ�.");
									return false;
								}
							}
							else
							{
								MessageManager.msg(p, "&c�ź�: &4����常 ���� ���� ��ġ�� ������ �� �ֽ��ϴ�.");
								return false;
							}
						}
						else if(args[0].equalsIgnoreCase("add"))
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c����: &4���ڰ��� �����ϴ�!");
								MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist, world");
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
											MessageManager.msg(p, "&c����: &4�÷��̾� ���� �����Ͽ� �ֽʽÿ�!");
											MessageManager.msg(p, "&6���� : /g add world <player> <world>");
											return false;
										}
										else
										{
											if(args.length < 4)
											{
												MessageManager.msg(p, "&c����: &4���� ���� �����Ͽ� �ֽʽÿ�!");
												MessageManager.msg(p, "&6���� : /g add world <player> <world>");
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
																MessageManager.msg(p, "&c����: &4�ش� �÷��̾� ������ �����ϴ�.");
																return false;
															}
															World world = Bukkit.getServer().getWorld(args[3]);
															YamlConfiguration PlayerSection = YamlConfiguration.loadConfiguration(file);
															PlayerSection.set("World.Faction-World", world.getName());
															FileReader.SaveFile(PlayerSection, file);
															boolean Passed = GuildInformation.RegisterGuild(args[2]);
															if(Passed) MessageManager.msg(p, "&a" + args[2] + "�Կ��� ���� " + world.getName() + "�� ��� ������ ���������� �ο��߽��ϴ�.");
															else
															{
																MessageManager.msg(p, "&b�ش� �÷��̾�� ���� ������ �ӽ� �ο��߽��ϴ�.");
																MessageManager.msg(p, "&b�÷��̾ �����Ѵٸ� ���� ������ ���� ��ϵ� ���Դϴ�.");
															}
															return true;
														}
														catch(NullPointerException e)
														{
															e.printStackTrace();
															MessageManager.msg(p, "&c����: &4������ ���尡 �� ������ �����ϴ�.");
															return false;
														}
													}
													else
													{
														MessageManager.msg(p, "&c����: &4\'" + args[2] + "\" �ش� �÷��̾�� ���� �÷��� ����� �����ϴ�!");
														return false;
													}
												}
												catch(NullPointerException e)
												{
													MessageManager.msg(p, "&c����: ���ڰ��� �о���� �������� ������ �������� �ʴ� ���ڰ��� �߰ߵǾ����ϴ�.");
													return false;
												}
											}
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4������ �źεǾ����ϴ�. ���� �̻� ��밡���մϴ�.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("whitelist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&aȭ��Ʈ����Ʈ�� �̿��� ������ �ƴ� �÷��̾");
											MessageManager.msg(p, "&a�������� ��� ����� ���� �� �� �ֽ��ϴ�!");
											MessageManager.msg(p, "&f[&e����&f]&b �� ���� ���� �� ����� �����մϴ�.");
											MessageManager.msg(p, "&6���� : /g add whitelist <player>...");
											return true;
										}
										else
										{
											String Names = "�̸� ";
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
											MessageManager.msg(p, "&6ȭ��Ʈ����Ʈ�� �� " + String.valueOf(args.length - 2) + "���� �߰��մϴ�.");
											MessageManager.msg(p, "&6���� " + Names);
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
														MessageManager.msg(p, "&c���: &f" + args[count] + "���� ������Ʈ �����Դϴ�. ȭ��Ʈ����Ʈ ȿ���� �����ϴ�.");
													}
													
													if(Mplayer.isWhitelisted(args[count]))
													{
														MessageManager.msg(p, "&c����: &4" + args[count] + "���� �̹� ȭ��Ʈ����Ʈ �����Դϴ�.");
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
											MessageManager.msg(p, "&a���������� ����� �Ϸ�Ǿ����ϴ�. ȭ��Ʈ����Ʈ Ȯ����");
											MessageManager.msg(p, "&6/g info whitelist");
											if(! Mplayer.EnabledWhitelist())
											{
												MessageManager.msg(p, "&f[&e����&f] &fȭ��Ʈ����Ʈ ����� Ȱ��ȭ���� �ʾҽ��ϴ�.");
												MessageManager.msg(p, "&aȰ��ȭ: &6/g set whitelist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4����常 ��� ������ ��ɾ��Դϴ�.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("blacklist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&c������Ʈ�� �̿��� ���� �Ӹ� �ƴ϶� �ٸ� �÷��̾");
											MessageManager.msg(p, "&a�������� ��� ���忡 �� �� �����ϴ�!");
											MessageManager.msg(p, "&f[&e����&f]&b �� ���� ���� �� ����� �����մϴ�.");
											MessageManager.msg(p, "&6���� : /g add blacklist <player>...");
											return true;
										}
										else
										{
											String Names = "�̸� ";
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
											MessageManager.msg(p, "&6������Ʈ�� �� " + String.valueOf(args.length - 2) + "���� �߰��մϴ�.");
											MessageManager.msg(p, "&6���� " + Names);
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
														MessageManager.msg(p, "&c���: &f" + args[count] + "���� ȭ��Ʈ����Ʈ �����Դϴ�.");
													}
													
													if(Mplayer.isBlacklisted(args[count]))
													{
														MessageManager.msg(p, "&c����: &4" + args[count] + "���� �̹� ������Ʈ �����Դϴ�.");
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
											MessageManager.msg(p, "&a���������� ����� �Ϸ�Ǿ����ϴ�. ������Ʈ Ȯ����");
											MessageManager.msg(p, "&6/g info blacklist");
											if(! Mplayer.EnabledBlacklist())
											{
												MessageManager.msg(p, "&f[&e����&f] &f������Ʈ ����� Ȱ��ȭ���� �ʾҽ��ϴ�.");
												MessageManager.msg(p, "&aȰ��ȭ: &6/g set blacklist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4����常 ��� ������ ��ɾ��Դϴ�.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
									MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist, world");
									return false;
								}
							}
						}
						else if(args[0].equalsIgnoreCase("del")) 
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c����: &4���ڰ��� �����ϴ�!");
								MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist, world");
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
											MessageManager.msg(p, "&c����: &4�÷��̾� ���� �����Ͽ� �ֽʽÿ�!");
											MessageManager.msg(p, "&6���� : /g del world <player>");
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
														MessageManager.msg(p, "&a" + args[2] + "���� ���� ��� ������ ���������� ��Ż�߽��ϴ�.");
														return true;
													}
													catch(NullPointerException e)
													{
														MessageManager.msg(p, "&c����: &4�÷��̾ ���� ������ �����ϴ�.");
														return false;
													}
												}
												else
												{
													MessageManager.msg(p, "&c����: &4\'" + args[2] + "\" �ش� �÷��̾�� ���� �÷��� ����� �����ϴ�!");
													return false;
												}
											}
											catch(NullPointerException e)
											{
												MessageManager.msg(p, "&c����: ���ڰ��� �о���� �������� ������ �������� �ʴ� ���ڰ��� �߰ߵǾ����ϴ�.");
												return false;
											}
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4������ �źεǾ����ϴ�. ���� �̻� ��밡���մϴ�.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("whitelist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&aȭ��Ʈ����Ʈ�� �����մϴ�.");
											MessageManager.msg(p, "&f[&e����&f]&b �� ���� ���� �� ������ �����մϴ�.");
											MessageManager.msg(p, "&6���� : /g del whitelist <player>...");
											return true;
										}
										else
										{
											String Names = "�̸� ";
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
											MessageManager.msg(p, "&6ȭ��Ʈ����Ʈ���� �� " + String.valueOf(args.length - 2) + "���� �����մϴ�.");
											MessageManager.msg(p, "&6���� " + Names);
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
														MessageManager.msg(p, "&c����: &4" + args[count] + "���� ȭ��Ʈ����Ʈ ������ �ƴմϴ�.");
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
											MessageManager.msg(p, "&a���������� ������ �Ϸ�Ǿ����ϴ�. ȭ��Ʈ����Ʈ Ȯ����");
											MessageManager.msg(p, "&6/g info whitelist");
											if(! Mplayer.EnabledWhitelist())
											{
												MessageManager.msg(p, "&f[&e����&f] &fȭ��Ʈ����Ʈ ����� Ȱ��ȭ���� �ʾҽ��ϴ�.");
												MessageManager.msg(p, "&aȰ��ȭ: &6/g set whitelist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4����常 ��� ������ ��ɾ��Դϴ�.");
										return false;
									}
								}
								else if(args[1].equalsIgnoreCase("Blacklist"))
								{
									if(MPlayer.get(p).getRole() == Rel.LEADER)
									{
										if(args.length < 3)
										{
											MessageManager.msg(p, "&a������Ʈ�� �����մϴ�.");
											MessageManager.msg(p, "&f[&e����&f]&b �� ���� ���� �� ������ �����մϴ�.");
											MessageManager.msg(p, "&6���� : /g del blacklist <player>...");
											return true;
										}
										else
										{
											String Names = "�̸� ";
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
											MessageManager.msg(p, "&6������Ʈ���� �� " + String.valueOf(args.length - 2) + "���� �����մϴ�.");
											MessageManager.msg(p, "&6���� " + Names);
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
														MessageManager.msg(p, "&c����: &4" + args[count] + "���� ������Ʈ ������ �ƴմϴ�.");
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
											MessageManager.msg(p, "&a���������� ������ �Ϸ�Ǿ����ϴ�. ������Ʈ Ȯ����");
											MessageManager.msg(p, "&6/g info blacklist");
											if(! Mplayer.EnabledWhitelist())
											{
												MessageManager.msg(p, "&f[&e����&f] &f������Ʈ ����� Ȱ��ȭ���� �ʾҽ��ϴ�.");
												MessageManager.msg(p, "&aȰ��ȭ: &6/g set blacklist <on | true>");
											}
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4����常 ��� ������ ��ɾ��Դϴ�.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
									MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist, world");
									return false;
								}
							}

						}
						else if(args[0].equalsIgnoreCase("set"))
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c����: &4���ڰ��� �����ϴ�!");
								MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist");
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
											String Enabled = !Mplayer.EnabledWhitelist() ? "&bȰ��ȭ" : "&c��Ȱ��ȭ";
											MessageManager.msg(p, "&aȭ��Ʈ ����Ʈ ����� " + Enabled + " &a���·� ��ȯ�Ǿ����ϴ�.");
											return true;
										}
										else
										{
											File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
											if(args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
											{	
												PlayerSection.set("World.Whitelist.Enabled", true);
												MessageManager.msg(p, "&aȭ��Ʈ ����Ʈ ����� &bȰ��ȭ &a���·� �����Ǿ����ϴ�.");
											}
											else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
											{
												PlayerSection.set("World.Whitelist.Enabled", false);
												MessageManager.msg(p, "&aȭ��Ʈ ����Ʈ �����  &c��Ȱ��ȭ &a���·� �����Ǿ����ϴ�.");
											}
											else
											{
												MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
												MessageManager.msg(p, "&6����: /g set whitelist on | off �Ǵ�  true | false");
												return false;
											}
											FileReader.SaveFile(PlayerSection, LeaderFile);
											PlayerInformation.LoadPlayerFile(p);
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4����常 ��� ������ ��ɾ��Դϴ�.");
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
											String Enabled = !Mplayer.EnabledBlacklist() ? "&bȰ��ȭ" : "&c��Ȱ��ȭ";
											MessageManager.msg(p, "&a������Ʈ ����� " + Enabled + " &a���·� ��ȯ�Ǿ����ϴ�.");
											return true;
										}
										else
										{
											File LeaderFile = new File("plugins/GuildWorldManager/Players/" + p.getName() + ".yml");
											YamlConfiguration PlayerSection = FileReader.LoadPlayerFile(p);
											if(args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true"))
											{	
												PlayerSection.set("World.Blacklist.Enabled", true);
												MessageManager.msg(p, "&a�� ����Ʈ ����� &bȰ��ȭ &a���·� �����Ǿ����ϴ�.");
											}
											else if(args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
											{
												PlayerSection.set("World.Blacklist.Enabled", false);
												MessageManager.msg(p, "&a�� ����Ʈ �����  &c��Ȱ��ȭ &a���·� �����Ǿ����ϴ�.");
											}
											else
											{
												MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
												MessageManager.msg(p, "&6����: /g set blacklist on | off �Ǵ�  true | false");
												return false;
											}
											FileReader.SaveFile(PlayerSection, LeaderFile);
											PlayerInformation.LoadPlayerFile(p);
											return true;
										}
									}
									else
									{
										MessageManager.msg(p, "&c�ź�: &4����常 ��� ������ ��ɾ��Դϴ�.");
										return false;
									}
								}
								else
								{
									MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
									MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist");
									return false;
								}
							}
						}
						else if(args[0].equalsIgnoreCase("info"))
						{
							if(args.length < 2)
							{
								MessageManager.msg(p, "&c����: &4���ڰ��� �����ϴ�!");
								MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist, guild");
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
									String Enabled = LeaderSection.getBoolean("World.Whitelist.Enabled") ? "&aȰ��ȭ" : "&c��Ȱ��ȭ";
									MessageManager.msg(p, "&aȭ��Ʈ����Ʈ ����:&f " + Enabled);
									MessageManager.msg(p, "&b���� " + MPlayer.get(p).getFaction().getName() + " ��忡 �߰��� ȭ��Ʈ����Ʈ��");
									if(WhitelistPlayer.size() == 0)
									{
										MessageManager.msg(p, "&c�� �� �����ϴ�!");
										return false;
									}
									else
									{
										MessageManager.msg(p, "&e�� " + WhitelistPlayer.size() + "&e���Դϴ�.");
									}
									MessageManager.msg(p, "");
									MessageManager.msg(p, "&bȭ��Ʈ ����Ʈ ���");
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
									String Enabled = LeaderSection.getBoolean("World.Blacklist.Enabled") ? "&aȰ��ȭ" : "&c��Ȱ��ȭ";
									MessageManager.msg(p, "&a������Ʈ ����:&f " + Enabled);
									MessageManager.msg(p, "&b���� " + MPlayer.get(p).getFaction().getName() + " ��忡 �߰��� ������Ʈ��");
									if(BlacklistPlayer.size() == 0)
									{
										MessageManager.msg(p, "&c�� �� �����ϴ�!");
										return false;
									}
									else
									{
										MessageManager.msg(p, "&e�� " + BlacklistPlayer.size() + "&e���Դϴ�.");
									}
									MessageManager.msg(p, "");
									MessageManager.msg(p, "&b�� ����Ʈ ���");
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
									MessageManager.msg(p, "&a����:&f " + MPlayer.get(p).getFaction().getName());
									MessageManager.msg(p, "&a��� ����:&f " + LeaderPlayer.getName());
									MessageManager.msg(p, "&a��� ��ǥ ����:&f " + LeaderSection.getString("World.Faction-World"));
									MessageManager.msg(p, "&aȭ��Ʈ ����Ʈ ��:&f " + LeaderSection.getStringList("World.Whitelist.Players").size() + "��");
									MessageManager.msg(p, "&a�� ����Ʈ ��:&f " + LeaderSection.getStringList("World.Blacklist.Players").size() + "��");
									return true;							
								}
								else
								{
									MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
									MessageManager.msg(p, "&6��� ������ ���ڰ� : whitelist, blacklist, guild");
									return false;
								}
							}
						}
						else
						{
							MessageManager.msg(p, "&c����: &4���ڰ��� �� �� �����ϴ�.");
							MessageManager.msg(p, "&6��ɾ� Ȯ�� �ϱ�: /g");
							return false;
						}
					}
				}
				else
				{
					MessageManager.msg(p, "&c�ź�: &4��忡 �ҼӵǾ� �־�� ��ɾ ����� �� �ֽ��ϴ�.");
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
			MessageManager.msg(p, "&a/g example&f: &6���� ��� ����&f, &a/g example&f: &c����常 ��� ����");
			MessageManager.msg(p, "&4/g example&f: &4���Ǹ� ��� ����");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g <help | page> [page]");
			MessageManager.msg(p, "&f[Page]������ ���� ����");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g spawn [guild]");
			MessageManager.msg(p, "&f������� �����ϰ� �ִ� ��� ���� ���� ��ġ���� �̵�");
			MessageManager.msg(p, "&f���࿡ ������ ���� �� �ش� ��� ���� �������� �̵� (�㰡 ��)");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g info <blacklist | whitelist | guild>");
			MessageManager.msg(p, "&fȭ��Ʈ ����Ʈ ���� �Ǵ� �� ����Ʈ ���� �Ǵ� ��� ������ ��");
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
			MessageManager.msg(p, "&a/g example&f: &6���� ��� ����&f, &a/g example&f: &c����常 ��� ����");
			MessageManager.msg(p, "&4/g example&f: &4���Ǹ� ��� ����");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&a/g defaultspawn");
			MessageManager.msg(p, "&f������� ������ ��� ������ ����Ʈ ��ġ�� �̵�");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g setspawn");
			MessageManager.msg(p, "&f���� ���ִ� ���� ��� ���� ���� ��ġ�� ����");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g passworld <[on|off] | [true|false]>");
			MessageManager.msg(p, "&f���� ������ ���带 ������");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g add <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>�� ȭ��Ʈ����Ʈ �Ǵ� ������Ʈ�� �߰���");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g del <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>�� ȭ��Ʈ����Ʈ �Ǵ� ������Ʈ���� ������");
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
			MessageManager.msg(p, "&a/g example&f: &6���� ��� ����&f, &a/g example&f: &c����常 ��� ����");
			MessageManager.msg(p, "&4/g example&f: &4���Ǹ� ��� ����");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g add <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>�� ȭ��Ʈ����Ʈ �Ǵ� ������Ʈ�� �߰���");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&fȭ��Ʈ����Ʈ : &b��� ���忡 �����Ϸ��� �� ��");
			MessageManager.msg(p, "&b<player>���� ���� ������ ���� �ʰ� ����˴ϴ�.");
			MessageManager.msg(p, "&f[&e����&f] �����, ������ ���� �߰����� �ʾƵ� �˴ϴ�.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c������Ʈ : &c��� ���忡 �����Ϸ��� �� ��");
			MessageManager.msg(p, "&c<player>���� ���� �Ǵ� ȭ��Ʈ����Ʈ�� �߰��ص� ���ܵ˴ϴ�.");
			MessageManager.msg(p, "&f[&4����&f] &c������ ����Ǹ�, ������� ������� �ʽ��ϴ�.");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g del <whitelist | blacklist> <player>");
			MessageManager.msg(p, "&f<player>�� ȭ��Ʈ����Ʈ �Ǵ� ������Ʈ���� ������");
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
			MessageManager.msg(p, "&a/g example&f: &6���� ��� ����&f, &a/g example&f: &c����常 ��� ����");
			MessageManager.msg(p, "&4/g example&f: &4���Ǹ� ��� ����");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&c/g set <whitelist | blacklist> <[on|off] | [true|false]>");
			MessageManager.msg(p, "&fȭ��Ʈ����Ʈ �Ǵ� ������Ʈ ����� Ȱ��ȭ�ϰų� ��Ȱ��ȭ��");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&4/g add world <player> <worldname>");
			MessageManager.msg(p, "&f�ش� �÷��̾�� �ش� ������ ������ ��");
			MessageManager.msg(p, "&f���� �ش� �÷��̾ ������̶�� �ڵ����� ��� ����� ������");
			MessageManager.msg(p, "");
			MessageManager.msg(p, "&4/g del world <player>");
			MessageManager.msg(p, "&f�ش� �÷��̾ ���� ���� ������ ��Ż");
			return;
		}
		else if(Page == -255)
		{
			MessageManager.msg(p, "&c����: &4�߸��� ������ ���� �Է��ϼ̽��ϴ�. (������ ������ : 1 ~ 4)");
			return;
		}
		else
		{
			MessageManager.msg(p, "&c����: &4���Ǵ� ������ ���� �ʰ��Ͽ����ϴ�. (������ ������ : 1 ~ 4)");
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

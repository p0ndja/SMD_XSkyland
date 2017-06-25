package me.palapon2545.net.xskyland;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.palapon2545.net.xskyland.ActionBar;
import me.palapon2545.net.xskyland.pluginMain;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class pluginMain extends JavaPlugin implements Listener {

	public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	public final Logger logger = Logger.getLogger("Minecraft");
	public pluginMain plugin;
	LinkedList<String> badWord = new LinkedList<String>();

	String sv = ChatColor.BLUE + "Server> " + ChatColor.GRAY;
	String np = ChatColor.RED + "You don't have permission or op!";
	String tc = ChatColor.BLUE + "" + ChatColor.BOLD + "Teleport Charger: ";
	String ct = tc + ChatColor.RED + "Cancelled!";

	public void onDisable() {
		Bukkit.broadcastMessage(sv + "SMD-XSkyland: " + ChatColor.RED + ChatColor.BOLD + "Disable");
		for (Player player1 : Bukkit.getOnlinePlayers()) {
			player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_PLING, 10, 0);
		}
		saveConfig();
	}

	public void onEnable() {
		Bukkit.broadcastMessage(sv + "SMD-XSkyland: " + ChatColor.GREEN + ChatColor.BOLD + "Enable");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		for (Player player1 : Bukkit.getOnlinePlayers()) {
			player1.playSound(player1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		}
		BukkitScheduler s = getServer().getScheduler();
		s.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.setFoodLevel(40);
					p.setHealth(20);
				}
			}
		}, 0L, 20L);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args) {
		String message = "";
		Player player = (Player) sender;
		String playerName = player.getName();
		File userdata = new File(Bukkit.getServer().getPluginManager().getPlugin("SMDMain").getDataFolder(),
				File.separator + "PlayerDatabase");
		File f = new File(userdata, File.separator + playerName + ".yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		String rank = playerData.getString("rank");
		if (CommandLabel.equalsIgnoreCase("elytra")) {
			Inventory inv = player.getInventory();
			ItemStack elytra = new ItemStack(Material.ELYTRA, 1);
		    ItemMeta em = elytra.getItemMeta();
		    em.setDisplayName(ChatColor.GREEN + "Elytra");
		    elytra.setItemMeta(em);
		    if (inv.contains(Material.ELYTRA)) {
		    	inv.remove(Material.ELYTRA);	
		    } else {
		    	
		    }
	        player.getInventory().setItem(4, elytra);
	        player.sendMessage(sv + "Here is your elytra!");
		}
		if (CommandLabel.equalsIgnoreCase("platewarp")) {
			Location loc = player.getLocation();
			loc.setY(loc.getY());
			Location locs = player.getLocation();
			locs.setY(loc.getY() - 2);
			Block block = loc.getBlock();
			Block blocks = locs.getBlock();
			String w = getConfig().getString("WarpState." + playerName);
			if (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE) {
				if (blocks.getType() == Material.SIGN_POST || blocks.getType() == Material.WALL_SIGN) {
					Sign sign = (Sign) blocks.getState();
					if (sign.getLine(0).equalsIgnoreCase("[tp]")) {
						if (w.equalsIgnoreCase("1")) {
							ActionBar a = new ActionBar(tc + ChatColor.GOLD + "▃ " + ChatColor.GRAY + "▄ ▅ ▆ ▇");
							a.sendToPlayer(player);
							player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.3);
							player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
							player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
							player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
							getConfig().set("WarpState." + playerName, "2");
							saveConfig();
							getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
								@Override
								public void run() {
									player.performCommand("platewarp");
								}
							}, 10);
						} else if (w.equalsIgnoreCase("2")) {
							if (player.isSneaking() == false) {
								getConfig().set("WarpState." + playerName, "false");
								saveConfig();
								ActionBar a = new ActionBar(ct);
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 10, 0);
							} else {
								ActionBar a = new ActionBar(tc + ChatColor.GOLD + "▃ ▄ " + ChatColor.GRAY + "▅ ▆ ▇");
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.5);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								getConfig().set("WarpState." + playerName, "3");
								saveConfig();
								getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

									@Override
									public void run() {
										player.performCommand("platewarp");
									}
								}, 10);
							}
						} else if (w.equalsIgnoreCase("3")) {
							if (player.isSneaking() == false) {
								getConfig().set("WarpState." + playerName, "false");
								saveConfig();

								ActionBar a = new ActionBar(ct);
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 10, 0);
							} else {
								ActionBar a = new ActionBar(tc + ChatColor.GOLD + "▃ ▄ ▅ " + ChatColor.GRAY + "▆ ▇");
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.7);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);

								getConfig().set("WarpState." + playerName, "4");
								saveConfig();
								getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									@Override
									public void run() {
										player.performCommand("platewarp");
									}
								}, 10);
							}
						} else if (w.equalsIgnoreCase("4")) {
							if (player.isSneaking() == false) {
								getConfig().set("WarpState." + playerName, "false");
								saveConfig();

								ActionBar a = new ActionBar(ct);
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 10, 0);
							} else {
								ActionBar a = new ActionBar(tc + ChatColor.GOLD + "▃ ▄ ▅ ▆ " + ChatColor.GRAY + "▇");
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 0.9);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);

								getConfig().set("WarpState." + playerName, "5");
								saveConfig();
								getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									@Override
									public void run() {
										player.performCommand("platewarp");
									}
								}, 10);
							}
						} else if (w.equalsIgnoreCase("5")) {
							if (player.isSneaking() == false) {
								getConfig().set("WarpState." + playerName, "false");
								saveConfig();

								ActionBar a = new ActionBar(ct);
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 10, 0);
							} else {
								ActionBar a = new ActionBar(tc + ChatColor.GOLD + "▃ ▄ ▅ ▆ ▇");
								a.sendToPlayer(player);
								player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, (float) 1.2);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);
								player.playEffect(player.getLocation(), Effect.LAVA_POP, 10);

								getConfig().set("WarpState." + playerName, "6");
								saveConfig();
								getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									@Override
									public void run() {
										player.performCommand("platewarp");
									}
								}, 15);
							}
						} else if (w.equalsIgnoreCase("6")) {
							if (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE) {

								Location loc2 = player.getLocation();
								loc2.setY(loc.getY() - 2);
								Block block2 = loc2.getBlock();
								if ((block2.getType() == Material.SIGN_POST
										|| block2.getType() == Material.WALL_SIGN)) {
									Location loc3 = player.getLocation();
									loc3.setY(loc.getY() - 3);
									Block block3 = loc3.getBlock();
									Sign s1 = (Sign) block2.getState();
									Sign s2 = (Sign) block3.getState();
									if (s1.getLine(0).equalsIgnoreCase("[tp]")
											&& s2.getLine(0).equalsIgnoreCase("[world]")) {
										if (block3.getType() == Material.SIGN_POST
												|| block3.getType() == Material.WALL_SIGN) {
											World world = Bukkit
													.getWorld(s2.getLine(1) + s2.getLine(2) + s2.getLine(3));
											if (world != null) {
												Location pl = player.getLocation();
												double xh = Integer.parseInt(s1.getLine(1));
												double yh = Integer.parseInt(s1.getLine(2));
												double zh = Integer.parseInt(s1.getLine(3));
												double x = xh + 0.5;
												double y = yh;
												double z = zh + 0.5;
												double yaw = pl.getYaw();
												double pitch = pl.getPitch();
												Location loca = new Location(world, x, y, z);
												loca.setPitch((float) pitch);
												loca.setYaw((float) yaw);
												player.teleport(loca);
												ActionBar a = new ActionBar(
														ChatColor.GREEN + "" + ChatColor.BOLD + "Teleport!");
												a.sendToPlayer(player);
												player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10,
														2);
											} else {
												ActionBar a = new ActionBar(ChatColor.RED + "Target world "
														+ ChatColor.WHITE + "[" + s2.getLine(1) + s2.getLine(2)
														+ s2.getLine(3) + "]" + ChatColor.RED + " not found");
												a.sendToPlayer(player);
												player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 10, 0);
											}
										} else {
										}
									} else {
									}
								} else {
								}
							} else {
							}
							getConfig().set("WarpState." + playerName, "false");
							saveConfig();
						}
					}
				}
			} else {
				getConfig().set("WarpState." + playerName, "false");
				saveConfig();
				ActionBar a = new ActionBar(ct);
				a.sendToPlayer(player);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 10, 0);
			}
		}
		return true;
	}

	@EventHandler
	public void JoinServer(PlayerJoinEvent event) {
		String playerName = event.getPlayer().getName();
		getConfig().set("WarpState." + playerName, "false");
		saveConfig();
	}
	
	@EventHandler
	public void onHit(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            event.setCancelled(true);
        }
    }

	@EventHandler
	public void PlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.isGliding()) {
			event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(1f));
			player.playEffect(player.getLocation(), Effect.CLOUD, 5);
		} else {
			return;
		}
	}
	
    @EventHandler
    public void setFlyOnJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if(!player.isFlying() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            player.setFlying(false);
            Vector jump = player.getLocation().getDirection().multiply(0.2).setY(2);
            player.setVelocity(player.getVelocity().add(jump));
            event.setCancelled(true);
        }
    }

	@EventHandler
	public void QuitServer(PlayerQuitEvent event) {
		int n = Bukkit.getServer().getOnlinePlayers().size();
		if (n == 0 || n < 0) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		} else {
			return;
		}
	}

	@EventHandler
	public void PlayerChangeSign(SignChangeEvent event) {
		Player player = event.getPlayer();
		String line0 = event.getLine(0).toLowerCase();
		if (line0.endsWith("[tp]") || line0.endsWith("[sell]") || line0.endsWith("[buy]")
				|| line0.endsWith("[luckyclick")) {
			if (!player.isOp()) {
				event.setLine(0, "§4§lSorry§r, but");
				event.setLine(1, "You §lneed §rperm.");
				event.setLine(2, "or op to create sign with");
				event.setLine(3, "'" + line0 + "'" + " prefix!");
				player.sendMessage(sv + np);
				Bukkit.broadcastMessage(sv + "Player " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY
						+ " try to create sign " + ChatColor.RED + ChatColor.BOLD + line0);
				return;
			}
		}
	}

	@EventHandler
	public void PlayerStandOnPlate(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		Location loc = player.getLocation();
		loc.setY(loc.getY());
		String w = getConfig().getString("WarpState." + playerName);
		Block block = loc.getBlock();
		if (block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE) {
			Location loc2 = player.getLocation();
			loc2.setY(loc.getY() - 2);
			Block block2 = loc2.getBlock();
			if ((block2.getType() == Material.SIGN_POST) || (block2.getType() == Material.WALL_SIGN)) {
				Sign sign = (Sign) block2.getState();
				if (sign.getLine(0).equalsIgnoreCase("[tp]")) {
					if (!w.equalsIgnoreCase("false")) {
						// Mean player currently stand on plate, No sending
						// holding shift message
					} else {
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 10));
						ActionBar action = new ActionBar(
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Hold " + ChatColor.GREEN + ChatColor.BOLD
										+ ChatColor.UNDERLINE + "Shift" + ChatColor.AQUA + " to teleport.");
						action.sendToPlayer(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void PlayerUsePlate(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		String w = getConfig().getString("WarpState." + playerName);
		Location loc = player.getLocation();
		Location loc2 = player.getLocation();
		Location loc3 = player.getLocation();
		loc.setY(loc.getY());
		loc2.setY(loc.getY() - 2);
		loc3.setY(loc.getY() - 3);
		Block block = loc.getBlock();
		Block block2 = loc2.getBlock();
		Block block3 = loc3.getBlock();
		if (event.isSneaking() == true) {
			if ((block.getType() == Material.GOLD_PLATE || block.getType() == Material.IRON_PLATE)
					&& (block2.getType() == Material.SIGN_POST || block2.getType() == Material.WALL_SIGN)
					&& (block3.getType() == Material.SIGN_POST || block3.getType() == Material.WALL_SIGN)) {
				Sign s1 = (Sign) block2.getState();
				Sign s2 = (Sign) block3.getState();
				if (s1.getLine(0).equalsIgnoreCase("[tp]") && s2.getLine(0).equalsIgnoreCase("[world]")) {
					if (w.equalsIgnoreCase("false")) {
						getConfig().set("WarpState." + playerName, "1");
						saveConfig();
						player.performCommand("platewarp");
					} else {
						return;
					}
				} else {
					return;
				}
			} else {
				return;
			}
		} else

		{
			return;
		}
	}

	public void playCircularEffect(Location location, Effect effect, boolean v) {
		for (int i = 0; i <= 8; i += ((!v && (i == 3)) ? 2 : 1))
			location.getWorld().playEffect(location, effect, i);
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
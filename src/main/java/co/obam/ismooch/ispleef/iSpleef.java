package co.obam.ismooch.ispleef;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iSmooch on 3/21/2015.
 */
public class iSpleef extends JavaPlugin implements Listener {

    public static Location spleefSpawn;
    public static Location spleefCenter;
    public static Location spleefStaff;
    public static Location spleefThreshold;
    public static Location hubSpawn;
    public static String currentMode;
    public static Material currentBlock;
    public static ItemStack currentTool;
    public static List<Location> spleefFloor;
    public static List<Location> spleefDoor;
    public static List<String> spleefModes;
    public static List<String> spleefLocs;
    boolean spleefOn = false;

    @EventHandler
    public static void onPlayerDamage(EntityDamageEvent e) {

        if (e.getEntity() instanceof Player && e.getEntity().getWorld().getName().equalsIgnoreCase("spleef")) {

            e.setDamage(0);

        }

    }

    @EventHandler
    public static void onBlockBreak(BlockDamageEvent e) {

        if (e.getBlock().getType().equals(currentBlock)) {

            if (e.getItemInHand().equals(currentTool)) {


                e.getBlock().setType(Material.AIR);
                e.setCancelled(true);

            }

        }

    }

    private static List<Location> getFloorBlocks(Location l, int y, int radius) {
        World w = l.getWorld();
        int xCoord = (int) l.getX();
        int zCoord = (int) l.getZ();

        List<Location> tempList = new ArrayList<Location>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                tempList.add(new Location(w, xCoord + x, y, zCoord + z));

            }
        }
        return tempList;
    }

    private static List<Location> getDoorBlocks(Location l, int y, int radius) {
        World w = l.getWorld();
        int xCoord = (int) l.getX();
        int zCoord = (int) l.getZ();

        List<Location> tempList = new ArrayList<Location>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                Location temp = new Location(w, xCoord + x, y, zCoord + z);
                if (temp.getBlock().equals(Material.DIAMOND_BLOCK)) {

                    tempList.add(new Location(w, xCoord + x, y + 1, zCoord + z));
                    tempList.add(new Location(w, xCoord + x, y + 2, zCoord + z));
                }


            }
        }
        return tempList;
    }

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        spleefSpawn =
                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.spawn.x"), this.getConfig().getDouble("Locations.spawn.y"), this.getConfig().getDouble("Locations.spawn.z"));
        spleefCenter =
                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.center.x"), this.getConfig().getDouble("Locations.center.y"), this.getConfig().getDouble("Locations.center.z"));
        spleefStaff =
                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.staff.x"), this.getConfig().getDouble("Locations.staff.y"), this.getConfig().getDouble("Locations.staff.z"));
        spleefThreshold =
                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.threshold.x"), this.getConfig().getDouble("Locations.threshold.y"), this.getConfig().getDouble("Locations.threshold.z"));
        hubSpawn =
                new Location(Bukkit.getWorld("Hub"), Bukkit.getWorld("Hub").getSpawnLocation().getX(), Bukkit.getWorld("Hub").getSpawnLocation().getY(), Bukkit.getWorld("Hub").getSpawnLocation().getZ());
        spleefModes = this.getConfig().getStringList("Modes");
        spleefLocs = this.getConfig().getStringList("Locvalues");


        spleefFloor = getFloorBlocks(spleefCenter, (int) spleefCenter.getY(), 35);
        spleefDoor = getDoorBlocks(spleefCenter, (int) spleefCenter.getY(), 50);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {


                if (spleefOn) {
                    for (Player player : Bukkit.getWorld("spleef").getPlayers()) {

                        if (player.getLocation().getY() < spleefThreshold.getY()) {

                            player.teleport(spleefSpawn);
                            player.getInventory().clear();

                            for (Player person : Bukkit.getWorld("spleef").getPlayers()) {

                                person.sendRawMessage(
                                        ChatColor.YELLOW + player.getName() + ChatColor.RED + " has been eliminated!");

                            }
                        }

                    }
                }

            }
        }, 0L, 20L);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("spleef")) {

            if (args.length < 1) {

                Player player = (Player) sender;
                String worldName = player.getWorld().getName();

                if (!worldName.equalsIgnoreCase("spleef")) {

                    player.teleport(spleefSpawn);
                    player.sendMessage(
                            ChatColor.GREEN + "You are going to " + ChatColor.YELLOW + "Spleef" + ChatColor.GREEN +
                                    "!");
                    return true;

                } else {

                    player.teleport(hubSpawn);
                    player.sendMessage(
                            ChatColor.GREEN + "You are going to " + ChatColor.YELLOW + "Hub" + ChatColor.GREEN + "!");
                    return true;

                }

            } else {
                Player player = (Player) sender;
                if (player.hasPermission("obam.smod")) {

                    player.sendMessage(ChatColor.RED + "You do not have permission to do this!");
                    return true;

                } else {

                    if (args.length < 2 && args[0].equalsIgnoreCase("staff")) {

                        player.teleport(spleefStaff);
                        player.sendMessage(ChatColor.GREEN + "You are going to the staff room!");
                        return true;

                    } else if (args.length < 2 && args[0].equalsIgnoreCase("open")) {

                        for (Location block : spleefDoor) {

                            block.getBlock().setType(Material.AIR);

                        }

                        for (Player person : Bukkit.getWorld("spleef").getPlayers()) {

                            person.sendMessage(ChatColor.GREEN + "The doors have opened!");

                        }
                        spleefOn = false;
                        return true;

                    } else if (args.length < 2 && args[0].equalsIgnoreCase("close")) {

                        for (Location block : spleefDoor) {

                            block.getBlock().setType(Material.GOLD_BLOCK);

                        }

                        for (Player person : Bukkit.getWorld("spleef").getPlayers()) {

                            person.sendMessage(ChatColor.GREEN + "The doors have closed!");

                        }
                        return true;

                    } else if (args.length < 2 && args[0].equalsIgnoreCase("modelist")) {

                        player.sendMessage(ChatColor.GREEN + "The list of available modes:");

                        for (String mode : spleefModes) {

                            player.sendMessage(ChatColor.GREEN + mode);
                            // String block = mode +
                            player.sendRawMessage(ChatColor.GREEN + "Block: " + ChatColor.YELLOW +
                                    this.getConfig().getString(mode + ".block"));
                            player.sendRawMessage(ChatColor.GREEN + "Tool: " + ChatColor.YELLOW +
                                    this.getConfig().getString(mode + ".tool"));

                        }
                        return true;

                    } else if (args.length < 2 && args[0].equalsIgnoreCase("mode")) {

                        player.sendMessage(ChatColor.RED + "You need to specify a mode!");
                        return true;

                    } else if (args.length < 3 && args[0].equalsIgnoreCase("mode")) {

                        String comp = args[1];
                        if (!spleefModes.contains(comp)) {

                            player.sendRawMessage(
                                    ChatColor.YELLOW + comp + ChatColor.RED + " is not a registered mode!");
                            return true;

                        }

                        currentMode = comp;
                        ItemStack tool =
                                new ItemStack(Material.getMaterial(this.getConfig().getString(comp + ".tool")));
                        ItemMeta im = tool.getItemMeta();
                        im.addEnchant(Enchantment.getByName(this.getConfig().getString(
                                comp + ".enchant")), this.getConfig().getInt(comp + ".power"), true);
                        tool.setItemMeta(im);
                        currentTool = tool;
                        currentBlock = Material.getMaterial(this.getConfig().getString(comp + ".block"));

                        for (Location block : spleefFloor) {

                            block.getBlock().setType(currentBlock);

                        }

                        for (Player person : Bukkit.getWorld("spleef").getPlayers()) {

                            Inventory inv = person.getInventory();
                            inv.clear();
                            inv.addItem(tool);
                            person.sendMessage(
                                    ChatColor.GREEN + "The Floor mode has been set to " + ChatColor.YELLOW + comp +
                                            ChatColor.GREEN + "!");

                        }

                        return true;


                    } else if (args.length < 2 && args[0].equalsIgnoreCase("loc")) {

                        player.sendRawMessage(ChatColor.GREEN + "The list of accepting locations:");

                        for (String location : spleefLocs) {

                            player.sendRawMessage(ChatColor.GREEN + location);

                        }

                        player.sendRawMessage(
                                ChatColor.GREEN + "Use " +
                                        ChatColor.YELLOW + "/spleef loc <locaiton name>" + ChatColor.GREEN +
                                        " to set the location for that value.");


                    } else if (args.length < 3 && args[0].equalsIgnoreCase("loc")) {

                        String comp = args[1];

                        if (spleefLocs.contains(comp)) {

                            Location loc = player.getLocation();
                            Double y = loc.getY();
                            Double x = loc.getX();
                            Double z = loc.getZ();

                            this.getConfig().set("Locations." + comp + ".x", x);
                            this.getConfig().set("Locations." + comp + ".y", y);
                            this.getConfig().set("Locations." + comp + ".z", z);

                            player.sendRawMessage(
                                    ChatColor.YELLOW + comp + ChatColor.GREEN + " has successfully been set to " +
                                            ChatColor.YELLOW + String.valueOf(x) + ", " + String.valueOf(y) + ", " +
                                            String.valueOf(z) + ChatColor.GREEN + "!");
                            player.sendRawMessage(
                                    ChatColor.GREEN + "Please use " + ChatColor.YELLOW + "/spleef reload" +
                                            ChatColor.GREEN + " to update the values");
                            return true;

                        } else {

                            player.sendRawMessage(
                                    ChatColor.YELLOW + comp + ChatColor.RED + " is not a valid listed location!");
                            return true;

                        }

                    } else if (args.length < 2 && args[0].equalsIgnoreCase("reload")) {


                        spleefSpawn =
                                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.spawn.x"), this.getConfig().getDouble("Locations.spawn.y"), this.getConfig().getDouble("Locations.spawn.z"));
                        spleefCenter =
                                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.center.x"), this.getConfig().getDouble("Locations.center.y"), this.getConfig().getDouble("Locations.center.z"));
                        spleefStaff =
                                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.staff.x"), this.getConfig().getDouble("Locations.staff.y"), this.getConfig().getDouble("Locations.staff.z"));
                        spleefThreshold =
                                new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.threshold.x"), this.getConfig().getDouble("Locations.threshold.y"), this.getConfig().getDouble("Locations.threshold.z"));
                        hubSpawn =
                                new Location(Bukkit.getWorld("Hub"), Bukkit.getWorld("Hub").getSpawnLocation().getX(), Bukkit.getWorld("Hub").getSpawnLocation().getY(), Bukkit.getWorld("Hub").getSpawnLocation().getZ());
                        spleefModes = this.getConfig().getStringList("Modes");
                        spleefLocs = this.getConfig().getStringList("Locvalues");


                        spleefFloor = getFloorBlocks(spleefCenter, (int) spleefCenter.getY(), 35);
                        spleefDoor = getDoorBlocks(spleefCenter, (int) spleefCenter.getY(), 50);
                        player.sendRawMessage(ChatColor.GREEN + "Spleef has been reloaded!");

                    }

                }


            }
        }
        return false;
    }
}

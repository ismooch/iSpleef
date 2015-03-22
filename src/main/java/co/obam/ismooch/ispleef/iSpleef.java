package co.obam.ismooch.ispleef;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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

    Location spleefSpawn;
    Location spleefCenter;
    Location spleefStaff;
    Location spleefThreshold;
    Location hubSpawn;
    String currentMode;
    Material currentBlock;
    ItemStack currentTool;
    List<Location> spleefFloor;
    List<Location> spleefDoor;
    List<String> spleefModes;
    boolean spleefOn = false;


    public void onEnable(){

        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        spleefSpawn = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.spawn.x"), this.getConfig().getDouble("Locations.spawn.y"), this.getConfig().getDouble("Locations.spawn.z"));
        spleefCenter = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.center.x"), this.getConfig().getDouble("Locations.center.y"), this.getConfig().getDouble("Locations.center.z"));
        spleefStaff = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.staff.x"), this.getConfig().getDouble("Locations.staff.y"), this.getConfig().getDouble("Locations.staff.z"));
        spleefThreshold = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.threshold.x"), this.getConfig().getDouble("Locations.threshold.y"), this.getConfig().getDouble("Locations.threshold.z"));
        hubSpawn = new Location(Bukkit.getWorld("Hub"), Bukkit.getWorld("Hub").getSpawnLocation().getX(), Bukkit.getWorld("Hub").getSpawnLocation().getY(), Bukkit.getWorld("Hub").getSpawnLocation().getZ());
        spleefModes = this.getConfig().getStringList("Modes");



        spleefFloor = getFloorBlocks(spleefCenter, (int) spleefCenter.getY(), 35);
        spleefDoor = getDoorBlocks(spleefCenter, (int) spleefCenter.getY(), 50);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(cmd.getName().equalsIgnoreCase("spleef")){

            if(args.length < 1){

                Player player = (Player) sender;
                String worldName = player.getWorld().getName();

                if(!worldName.equalsIgnoreCase("spleef")){

                    player.teleport(spleefSpawn);
                    player.sendMessage(ChatColor.GREEN + "You are going to " + ChatColor.YELLOW + "Spleef" + ChatColor.GREEN + "!");
                    return true;

                }else{

                    player.teleport(hubSpawn);
                    player.sendMessage(ChatColor.GREEN + "You are going to " + ChatColor.YELLOW + "Hub" + ChatColor.GREEN + "!");
                    return true;

                }

            }else{
                Player player = (Player) sender;
                if(player.hasPermission("obam.smod")){

                    player.sendMessage(ChatColor.RED + "You do not have permission to do this!");
                    return true;

                }else{

                    if(args.length < 2 && args[0].equalsIgnoreCase("staff")){

                        player.teleport(spleefStaff);
                        player.sendMessage(ChatColor.GREEN + "You are going to the staff room!");
                        return true;

                    }else if(args.length < 2 && args[0].equalsIgnoreCase("open")){

                        for(Location block : spleefDoor){

                            block.getBlock().setType(Material.AIR);

                        }

                        for(Player person : Bukkit.getWorld("spleef").getPlayers()){

                            person.sendMessage(ChatColor.GREEN + "The doors have opened!");

                        }
                        spleefOn = false;
                        return true;

                    }else if(args.length < 2 && args[0].equalsIgnoreCase("close")){

                        for(Location block : spleefDoor){

                            block.getBlock().setType(Material.GOLD_BLOCK);

                        }

                        for(Player person : Bukkit.getWorld("spleef").getPlayers()){

                            person.sendMessage(ChatColor.GREEN + "The doors have closed!");

                        }
                        return true;

                    }else if(args.length < 2 && args[0].equalsIgnoreCase("modelist")){

                        player.sendMessage(ChatColor.GREEN + "The list of available modes:");

                        for(String mode : spleefModes){

                            player.sendMessage(ChatColor.GREEN + mode);
                           // String block = mode +
                            player.sendRawMessage(ChatColor.GREEN + "Block: " + ChatColor.YELLOW + this.getConfig().getString(mode + ".block"));
                            player.sendRawMessage(ChatColor.GREEN + "Tool: " + ChatColor.YELLOW + this.getConfig().getString(mode + ".tool"));

                        }
                        return true;

                    }else if(args.length < 2 && args[0].equalsIgnoreCase("mode")){

                        player.sendMessage(ChatColor.RED + "You need to specify a mode!");
                        return true;

                    }else if(args.length < 3 && args[0].equalsIgnoreCase("mode")){

                        String comp = args[1];
                        if(!spleefModes.contains(comp)){

                            player.sendRawMessage(ChatColor.YELLOW + comp + ChatColor.RED + " is not a registered mode!");
                            return true;

                        }

                        currentMode = comp;
                        ItemStack tool = new ItemStack(Material.getMaterial(this.getConfig().getString(comp + ".tool")));
                        ItemMeta im = tool.getItemMeta();
                        im.addEnchant(Enchantment.getByName(this.getConfig().getString(comp + ".enchant")), this.getConfig().getInt(comp + ".power"), true);
                        tool.setItemMeta(im);
                        currentTool = tool;
                        currentBlock = Material.getMaterial(this.getConfig().getString(comp + ".block"));

                        for(Location block : spleefFloor){

                            block.getBlock().setType(currentBlock);

                        }

                        for(Player person : Bukkit.getWorld("spleef").getPlayers()){

                            Inventory inv = person.getInventory();
                            inv.addItem(tool);
                            person.sendMessage(ChatColor.GREEN + "The Floor mode has been set to " + ChatColor.YELLOW + comp + ChatColor.GREEN + "!");

                        }

                        return true;


                    }

                }


            }

        }
        return false;
    }

    private static List<Location> getFloorBlocks(Location l, int y, int radius)
    {
        World w = l.getWorld();
        int xCoord = (int) l.getX();
        int zCoord = (int) l.getZ();

        List<Location> tempList = new ArrayList<Location>();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {

                tempList.add(new Location(w, xCoord + x, y, zCoord + z));

            }
        }
        return tempList;
    }

    private static List<Location> getDoorBlocks(Location l, int y, int radius)
    {
        World w = l.getWorld();
        int xCoord = (int) l.getX();
        int zCoord = (int) l.getZ();

        List<Location> tempList = new ArrayList<Location>();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {

                Location temp = new Location(w, xCoord + x, y, zCoord + z);
                if(temp.getBlock().equals(Material.DIAMOND_BLOCK)){

                    tempList.add(new Location(w, xCoord + x, y + 1, zCoord + z));
                    tempList.add(new Location(w, xCoord + x, y + 2, zCoord + z));
                }


            }
        }
        return tempList;
    }
}

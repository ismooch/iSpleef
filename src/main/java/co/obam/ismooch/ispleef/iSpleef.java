package co.obam.ismooch.ispleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
    List<Location> spleefFloor;
    List<Location> spleefDoor;


    public void onEnable(){

        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        spleefSpawn = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.spawn.x"), this.getConfig().getDouble("Locations.spawn.y"), this.getConfig().getDouble("Locations.spawn.z"));
        spleefCenter = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.center.x"), this.getConfig().getDouble("Locations.center.y"), this.getConfig().getDouble("Locations.center.z"));
        spleefStaff = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.staff.x"), this.getConfig().getDouble("Locations.staff.y"), this.getConfig().getDouble("Locations.staff.z"));
        spleefThreshold = new Location(Bukkit.getWorld("spleef"), this.getConfig().getDouble("Locations.threshold.x"), this.getConfig().getDouble("Locations.threshold.y"), this.getConfig().getDouble("Locations.threshold.z"));
        hubSpawn = new Location(Bukkit.getWorld("Hub"), Bukkit.getWorld("Hub").getSpawnLocation().getX(), Bukkit.getWorld("Hub").getSpawnLocation().getY(), Bukkit.getWorld("Hub").getSpawnLocation().getZ());


        spleefFloor = getFloorBlocks(spleefCenter, (int) spleefCenter.getY(), 35);

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
}

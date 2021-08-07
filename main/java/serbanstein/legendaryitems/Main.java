package serbanstein.legendaryitems;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public final class Main extends JavaPlugin {


    public static String worldName;
    public static List<BukkitTask> syncTasks = new ArrayList<>();

    public static void getWorldName() { //gets world name from server.properties
        Scanner propFile = null;
        try {
            propFile = new Scanner(new File("server.properties"));
        } catch (FileNotFoundException e) {/**/}
        String worldLine;
        do {
            assert propFile != null;
            worldLine = propFile.nextLine();
        } while (!Objects.requireNonNull(worldLine).contains("level-name"));
        worldName = worldLine.substring(worldLine.indexOf('=') + 1);
    }

    @Override
    public void onEnable() {          //Everything contained here runs on server boot
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this); //register event listener
        
        getWorldName();
        
        syncTasks.add(new BukkitRunnable(){     //repeating task which broadcasts the locations of dropped legendary items to everyone on the server
                          @Override
                          public void run() {
                              List<Entity> list1 = Bukkit.getWorld(worldName).getEntities();
                              List<Entity> list2 = Bukkit.getWorld(worldName+"_nether").getEntities();
                              List<Entity> list3 = Bukkit.getWorld(worldName+"_the_end").getEntities();
                              List<Item> itemList = new ArrayList<>();
                              for(Entity ent : list1){
                                  if(ent instanceof Item){
                                      if(((Item) ent).getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getPlugin(Main.class),"legendary-flag"),PersistentDataType.STRING)){
                                        itemList.add((Item)ent);
                                      }
                                  }
                              }
                              for(Entity ent : list2){
                                  if(ent instanceof Item){
                                      if(((Item) ent).getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getPlugin(Main.class),"legendary-flag"),PersistentDataType.STRING)){
                                          itemList.add((Item)ent);
                                      }
                                  }
                              }
                              for(Entity ent : list3){
                                  if(ent instanceof Item){
                                      if(((Item) ent).getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Main.getPlugin(Main.class),"legendary-flag"),PersistentDataType.STRING)){
                                          itemList.add((Item)ent);
                                      }
                                  }
                              }
                              if(!itemList.isEmpty()){
                                  Bukkit.broadcastMessage("The Mages of Spawn have sensed the following legendary artefacts:");
                                  for(Item item : itemList){
                                      String dimension;
                                      if(item.getLocation().getWorld().getName().equalsIgnoreCase(worldName+"_nether")) dimension = "Nether";
                                      else if(item.getLocation().getWorld().getName().equalsIgnoreCase(worldName+"_the_end")) dimension = "End";
                                      else dimension = "Overworld";
                                      Bukkit.broadcastMessage("At: "+(int)item.getLocation().getX()+","+(int)item.getLocation().getY()+","+(int)item.getLocation().getZ()+" in the "+dimension);
                                  }
                              }
                          }
                      }.runTaskTimer(Main.getPlugin(Main.class),0,200));//216000

        getWorldName();
    }

    @Override
    public void onDisable() {       //everything here runs when the server stops *cleanly*
        for(BukkitTask task : syncTasks) task.cancel();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //commands
        if(command.getName().equalsIgnoreCase("legendify") && sender.hasPermission("LegendaryItems.permission")){
            //adds NBT data to the item via Spigot's PersistendDataContainer
            if(args.length!=0){
                sender.sendMessage("/legendify");
                sender.sendMessage("The held item will be made legendary");
                return true;
            }
            if(!(sender instanceof Player)){
                sender.sendMessage("Only players can run this command");
                return true;
            }
            try{
                Player pl = (Player)sender;
                ItemStack item = pl.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = Objects.requireNonNull(meta).getPersistentDataContainer();
                if(data.has(new NamespacedKey(getPlugin(Main.class),"legendary-flag"),PersistentDataType.STRING)){
                    sender.sendMessage("already legendary");
                    return true;
                }
                data.set(new NamespacedKey(getPlugin(Main.class),"legendary-flag"), PersistentDataType.STRING,"ok");
                meta.setUnbreakable(true);
                item.setItemMeta(meta);
            }
            catch(Exception e){
                sender.sendMessage("Something's wrong with the item you're trying to legendify");
            }
        }
        return true;
    }
}

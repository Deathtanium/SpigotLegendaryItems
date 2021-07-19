package serbanstein.legendaryitems;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void itemDroppedEvent(ItemSpawnEvent event){
        if(event.getEntity().getItemStack().getItemMeta()
                .getPersistentDataContainer().has(new NamespacedKey(Main.getPlugin(Main.class),"legendary-flag"),PersistentDataType.STRING)){
            event.getEntity().setFireTicks(-1);
            event.getEntity().setInvulnerable(true);
            event.getEntity().setPersistent(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void entKillEvent(EntityDeathEvent event){
        Player killer = event.getEntity().getKiller();
        if(killer != null){
            ItemStack heldItem = killer.getInventory().getItemInMainHand();
            ItemMeta heldItemMeta = heldItem.getItemMeta();
            if(heldItemMeta != null)
                if(Objects.requireNonNull(heldItemMeta).getPersistentDataContainer().has(new NamespacedKey(Main.getPlugin(Main.class),"legendary-flag"),PersistentDataType.STRING)){
                    List<String> lore = heldItemMeta.getLore();
                    boolean killsFound = false;
                    String newkills = null;
                    if(lore == null){
                        lore = new ArrayList<String>();
                        lore.add("Kills: 1");
                        heldItemMeta.setLore(lore);
                        heldItem.setItemMeta(heldItemMeta);
                        return;
                    }
                    for(String line : lore) {
                        if(line.contains("Kills: ")){
                            killsFound=true;
                            newkills = line;
                            break;
                        }
                    }
                    if(!killsFound){
                        lore.add("Kills: 1");
                    }else{
                        int kills = Integer.parseInt(newkills.substring(7));
                        lore.remove(newkills);
                        lore.add(newkills.replace(kills+"",(kills+1)+""));
                    }
                    heldItemMeta.setLore(lore);
                    heldItem.setItemMeta(heldItemMeta);
                }

            heldItem = killer.getInventory().getItemInOffHand();
            heldItemMeta = heldItem.getItemMeta();
            if(heldItemMeta!=null)
                if(Objects.requireNonNull(heldItemMeta).getPersistentDataContainer().has(new NamespacedKey(Main.getPlugin(Main.class),"legendary-flag"),PersistentDataType.STRING)){
                    List<String> lore = heldItemMeta.getLore();
                    boolean killsFound = false;
                    String newkills = null;
                    if(lore == null){
                        lore = new ArrayList<String>();
                        lore.add("Kills: 1");
                        heldItemMeta.setLore(lore);
                        heldItem.setItemMeta(heldItemMeta);
                        return;
                    }
                    for(String line : lore) {
                        if(line.contains("Kills: ")){
                            killsFound=true;
                            newkills = line;
                            break;
                        }
                    }
                    if(!killsFound){
                        lore.add("Kills: 1");
                    }else{
                        int kills = Integer.parseInt(newkills.substring(7));
                        lore.remove(newkills);
                        lore.add(newkills.replace(kills+"",(kills+1)+""));
                    }
                    heldItemMeta.setLore(lore);
                    heldItem.setItemMeta(heldItemMeta);
                }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void playerDeathEvent(PlayerDeathEvent event){
        List<ItemStack> inv = event.getDrops();
        List<ItemStack> toMove = new ArrayList<ItemStack>();
        for(ItemStack item : inv)
            if(Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer()
                    .has(new NamespacedKey(Main.getPlugin(Main.class),"legendary-flag"), PersistentDataType.STRING))
                toMove.add(item);
        for(ItemStack item : toMove){
            PersistentDataContainer q = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
            Item droppedItemEntity = Bukkit.getWorld(Main.worldName).dropItem(event.getEntity().getLocation(),item);
            droppedItemEntity.setFireTicks(-1);
            droppedItemEntity.setInvulnerable(true);
            droppedItemEntity.setPersistent(true);
            inv.remove(item);
        }
    }

}

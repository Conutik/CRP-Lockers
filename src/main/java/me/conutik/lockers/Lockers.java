package me.conutik.lockers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.*;

public class Lockers extends JavaPlugin implements Listener {

    private final Database Database = new Database();

    @Override
    public void onEnable() {
        new Database().setMain(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Lockers is now working");
    }

    @EventHandler
    public void onRightClick(final PlayerInteractEvent e) {

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if(!e.getClickedBlock().getType().equals(Material.PUMPKIN)) return;

            if(e.getHand() == EquipmentSlot.OFF_HAND) return;

            Database.createData();

            Database.load();

            Object thing = Database.getConfig().get(String.valueOf("locker." + e.getClickedBlock().getLocation() + ".islocker"));

//            if(thing == null) return;

//            Boolean check =(Boolean) thing;

//            if(!check) return;

            Object key = Database.getConfig().get(String.valueOf("locker." + e.getClickedBlock().getLocation() + ".keydata"));

            if(key == null) {

                e.setCancelled(true);

                char[] available = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
//
                    StringBuilder let = new StringBuilder();
                    int i;
                    for (i = 0; i < 5; i++) {
                        int random = (int) Math.floor(Math.random() * (74 + 1) + 0);
                        let.append(available[random]);
                    }

                    Database.getConfig().set("locker." + e.getClickedBlock().getLocation() + ".keydata", let.toString());

                ItemStack items = new ItemStack(Material.TRIPWIRE_HOOK);

                ItemMeta meta = items.getItemMeta();

                meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Locker Key");

                List<String> lores = new ArrayList<String>();

                lores.add("");

                lores.add(ChatColor.GOLD + "This key is used for locker: " + let);

                meta.setLore(lores);

                items.setItemMeta(meta);





                    e.getPlayer().getInventory().addItem(items);

            } else {

                e.setCancelled(true);
                ItemStack keyItem = e.getPlayer().getInventory().getItemInMainHand();
                ItemMeta keyMeta = keyItem.getItemMeta();
                if(keyMeta == null) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You try opening the locker, but the key isn't working!");
                    return;
                }
                if(keyMeta.getLore() == null) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You try opening the locker, but the key isn't working!");
                    return;
                }
                    String keyLore = keyMeta.getLore().get(1);
                    if(keyLore == null) {
                        e.getPlayer().sendMessage(ChatColor.RED + "You try opening the locker, but the key isn't working!");
                        return;
                    }

                    String[] keyLoreSplit = keyLore.split(": ");

                    String keyCode = keyLoreSplit[1];

                if(!keyCode.equals(key)) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You try opening the locker, but the key isn't working!");
                    return;
                }
            }

            Inventory inv = Bukkit.createInventory(null, 18, "Locker");

            Object items = Database.getConfig().get("locker." + e.getClickedBlock().getLocation() + ".data");
            ArrayList item = (ArrayList) items;

            if (items == null) {
                e.getPlayer().openInventory(inv);
            } else {

                for (int i = 0; i < item.size(); i++) {

                    inv.setItem(i, (ItemStack) item.get(i));

                }
                e.getPlayer().openInventory(inv);
            }

            Database.getConfig().set("inlocker." + e.getPlayer().getUniqueId(), e.getClickedBlock().getLocation());

            Database.save();
        }

    }


    @EventHandler
    public void onInvClose(final InventoryCloseEvent e) {

        if(!e.getPlayer().getOpenInventory().getTitle().equals("Locker")) return;


        Database.createData();

        Database.load();

        Object thing = Database.getConfig().get("inlocker." + e.getPlayer().getUniqueId());


//        Thing: Temporary location of locker through player

//        Thing2: if locker or not

//        check2: boolean of locker or not

        if (thing == null) return;

        Location check = (Location) thing;

        thing = Database.getConfig().get(String.valueOf(check));

//        if(thing == null) return;

//        Boolean check2 = (Boolean) thing;

//        if(!check2) return;

        Database.getConfig().set("locker." + check + ".data", e.getInventory().getStorageContents());

        Database.save();


    }


}

package no.runsafe.headhunters;

import no.runsafe.framework.enchant.Enchant;
import no.runsafe.framework.server.enchantment.RunsafeEnchantment;
import no.runsafe.framework.server.enchantment.RunsafeEnchantmentType;
import no.runsafe.framework.server.inventory.RunsafeInventory;
import no.runsafe.framework.server.inventory.RunsafePlayerInventory;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 7-5-13
 * Time: 22:18
 */
public class EquipmentManager {

    RunsafeItemStack head, chest, legs, boots;
    ArrayList<RunsafeItemStack> inventory;


    public EquipmentManager(){

        chest = new RunsafeItemStack(Material.CHAINMAIL_CHESTPLATE.getId());
        legs = new RunsafeItemStack(Material.CHAINMAIL_LEGGINGS.getId());
        boots = new RunsafeItemStack(Material.IRON_BOOTS.getId());
        head = new RunsafeItemStack(Material.GOLD_HELMET.getId());

        inventory = new ArrayList<RunsafeItemStack>();


        RunsafeItemStack bow = new RunsafeItemStack(Material.BOW.getId());

        bow.addEnchantment(new RunsafeEnchantment(Enchant.InfiniteArrows.getId()), 1 );
        bow.addEnchantment(new RunsafeEnchantment(Enchant.ArrowDamage.getId()), 1);

        inventory.add(new RunsafeItemStack(Material.IRON_SWORD.getId()));
        inventory.add(bow);
        inventory.add(new RunsafeItemStack(Material.ARROW.getId()));
        inventory.add(new RunsafeItemStack(Material.COOKED_BEEF.getId()));

    }

    public void equip(RunsafePlayer player){
        RunsafePlayerInventory playerInventory = player.getInventory();
        playerInventory.setHelmet(head);
        playerInventory.setChestplate(chest);
        playerInventory.setLeggings(legs);
        playerInventory.setBoots(boots);

        for(RunsafeItemStack item : inventory) playerInventory.addItems(item);

    }

    public void unEquip(RunsafePlayer player){
        player.getInventory().clear();
    }

}
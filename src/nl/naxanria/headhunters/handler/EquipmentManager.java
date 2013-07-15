package nl.naxanria.headhunters.handler;

import no.runsafe.framework.minecraft.Enchant;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.inventory.RunsafeInventoryType;
import no.runsafe.framework.minecraft.inventory.RunsafePlayerInventory;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;

public class EquipmentManager
{
	public EquipmentManager()
	{
		RunsafePlayerInventory inventory = (RunsafePlayerInventory) RunsafeServer.Instance.createInventory(null, RunsafeInventoryType.PLAYER);

		inventory.setHelmet(Item.Combat.Chestplate.Chainmail.getItem());
		inventory.setLeggings(Item.Combat.Leggings.Chainmail.getItem());
		inventory.setBoots(Item.Combat.Boots.Iron.getItem());
		inventory.setChestplate(Item.Combat.Helmet.Gold.getItem());

		RunsafeMeta bow = Item.Combat.Bow.getItem();

		//infinity and power 1 bow
		Enchant.InfiniteArrows.power(1).applyTo(bow);
		Enchant.ArrowDamage.power(1).applyTo(bow);

		inventory.addItems(Item.Combat.Sword.Iron.getItem());
		inventory.addItems(bow);
		inventory.addItems(Item.Combat.Arrow.getItem());

		this.inventory = inventory.serialize();
	}

	public void equip(RunsafePlayer player)
	{
		player.getInventory().clear();
		player.getInventory().unserialize(inventory);
		player.updateInventory();
	}

	public void unEquip(RunsafePlayer player)
	{
		player.getInventory().clear();
	}

	public ArrayList<RunsafeMeta> drops(ArrayList<RunsafeMeta> itemDrop)
	{
		return null;
	}


	private final String inventory;
}
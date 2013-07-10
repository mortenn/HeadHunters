package no.runsafe.headhunters;

import no.runsafe.framework.minecraft.Enchant;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.inventory.RunsafePlayerInventory;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;

public class EquipmentManager
{
	public EquipmentManager()
	{

		chest = Item.Combat.Chestplate.Chainmail.getItem();
		legs = Item.Combat.Leggings.Chainmail.getItem();
		boots = Item.Combat.Boots.Iron.getItem();
		head = Item.Combat.Helmet.Gold.getItem();

		inventory = new ArrayList<RunsafeMeta>();


		RunsafeMeta bow = Item.Combat.Bow.getItem();

		//infinity and power 1 bow
		Enchant.InfiniteArrows.power(1).applyTo(bow);
		Enchant.ArrowDamage.power(1).applyTo(bow);

		inventory.add(Item.Combat.Sword.Iron.getItem());
		inventory.add(bow);
		inventory.add(Item.Combat.Arrow.getItem());

	}

	public void equip(RunsafePlayer player)
	{
		RunsafePlayerInventory playerInventory = player.getInventory();
		playerInventory.setHelmet(head);
		playerInventory.setChestplate(chest);
		playerInventory.setLeggings(legs);
		playerInventory.setBoots(boots);

		for (RunsafeMeta item : inventory) playerInventory.addItems(item);

	}

	public void unEquip(RunsafePlayer player)
	{
		player.getInventory().clear();
	}

	public ArrayList<RunsafeMeta> drops(ArrayList<RunsafeMeta> itemDrop)
	{


		return null;
	}

	private final RunsafeMeta head;
	private final RunsafeMeta chest;
	private final RunsafeMeta legs;
	private final RunsafeMeta boots;
	private final ArrayList<RunsafeMeta> inventory;
}
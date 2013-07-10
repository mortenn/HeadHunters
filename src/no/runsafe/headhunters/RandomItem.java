package no.runsafe.headhunters;

import no.runsafe.framework.minecraft.Enchant;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 7-5-13
 * Time: 3:12
 */
public class RandomItem
{
	final ArrayList<RunsafeMeta> items;
	final HashMap<RunsafeMeta, Integer> itemMap;

	public RandomItem()
	{

		itemMap = new HashMap<RunsafeMeta, Integer>();
		items = new ArrayList<RunsafeMeta>();

		RunsafeMeta apple = Item.Food.Golden.Apple.getItem();
		RunsafeMeta sugar = Item.Materials.Sugar.getItem();

		RunsafeMeta slimeBall = Item.Miscellaneous.Slimeball.getItem();
		slimeBall.setDisplayName("§6Slow Them Now");
		slimeBall.addLore("Right click on the ground");
		slimeBall.addLore("to slow enemies");
		slimeBall.addLore("in a 5 block radius.");

		RunsafeMeta magmaCream = Item.Brewing.MagmaCream.getItem();
		magmaCream.setDisplayName("§6Hot Topic");
		magmaCream.addLore("Right click on the ground");
		magmaCream.addLore("to smite enemies with lightning");
		magmaCream.addLore("in a 5 block radius.");

		RunsafeMeta star = Item.Materials.NetherStar.getItem();
		star.setDisplayName("§6Portal Deluxe 2000");
		star.addLore("Right Click to teleport a random Spot");
		star.addLore("§eUnstable device. Might blow up.");

		RunsafeMeta rod = Item.Materials.BlazeRod.getItem();
		rod.setAmount(3);
		rod.setDisplayName("§6BF-stick");
		rod.addLore("Right click to shoot a fireball!");

		RunsafeMeta sack = Item.Materials.InkSack.getItem();
		sack.setDisplayName("§6No Eyed Fool");
		sack.addLore("Right click to weaken you enemies in a 5 block radius.");

		RunsafeMeta tear = Item.Brewing.GhastTear.getItem();
		tear.setDisplayName("§6Feed On their Tears");
		tear.addLore("Right click to teleport everyone to you, and you away safely.");

		RunsafeMeta woodTumbler = Item.Combat.Sword.Wood.getItem();
		woodTumbler.setDisplayName("§6Wood Tumbler");

		woodTumbler.setDurability((byte) 57);
		Enchant.Knockback.power(10).applyTo(woodTumbler);

		itemMap.put(apple, 10);
		itemMap.put(sugar, 9);
		itemMap.put(slimeBall, 5);
		itemMap.put(magmaCream, 3);
		itemMap.put(star, 5);
		itemMap.put(rod, 5);
		itemMap.put(sack, 8);
		itemMap.put(tear, 1);
		//itemMap.put(woodTumbler, 3);

		for (RunsafeMeta item : itemMap.keySet())
			for (int i = 0; i < itemMap.get(item); i++)
				items.add(item);
	}

	public RunsafeMeta get()
	{
		return items.get(Util.getRandom(0, items.size()));
	}

	public ArrayList<RunsafeMeta> getCleanedDrops(List<RunsafeMeta> drops)
	{


		ArrayList<RunsafeMeta> outList = new ArrayList<RunsafeMeta>();

		for (RunsafeMeta item : drops)
			if (items.contains(item))
				outList.add(item);

		return outList;

	}

}

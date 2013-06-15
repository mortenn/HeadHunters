package no.runsafe.headhunters;

import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;


import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 7-5-13
 * Time: 3:12
 */
public class RandomItem  {

    ArrayList<RunsafeMeta> items;

    public RandomItem(){

        items = new ArrayList<RunsafeMeta>();

        RunsafeMeta apple = Item.Food.Golden.Apple.getItem();

        RunsafeMeta slimeBall = Item.Miscellaneous.Slimeball.getItem();
        slimeBall.setDisplayName("§6Slow Them Now");
        slimeBall.addLore("Right click on the ground");
        slimeBall.addLore("to slow enemies");
        slimeBall.addLore("in a 5 block radius.");


        RunsafeMeta sugar = Item.Materials.Sugar.getItem();


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
        sack.addLore("Right click to blind you enemies in a 5 block radius.");


        items.add(apple);
        items.add(apple);
        items.add(apple);
        items.add(apple);
        items.add(apple);
        items.add(apple);

        items.add(sugar);
        items.add(sugar);
        items.add(sugar);
        items.add(sugar);

        items.add(slimeBall);

        items.add(magmaCream);

        items.add(star);

        items.add(rod);
        items.add(rod);

        items.add(sack);

    }

    public RunsafeMeta get(){
        return items.get(Util.getRandom(0, items.size()));
    }

}

package no.runsafe.headhunters;

import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.item.meta.RunsafeItemMeta;
import org.bukkit.Material;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 7-5-13
 * Time: 3:12
 */
public class RandomItem  {

    ArrayList<RunsafeItemStack> items;

    public RandomItem(){

        items = new ArrayList<RunsafeItemStack>();

        RunsafeItemStack apple = new RunsafeItemStack(Material.GOLDEN_APPLE.getId());

        RunsafeItemStack slimeBall = new RunsafeItemStack(Material.SLIME_BALL.getId());
        RunsafeItemMeta slimeBallMeta = slimeBall.getItemMeta();
        slimeBallMeta.setDisplayName("§6Slow Them Now");
        slimeBallMeta.addLore("Right click on the ground");
        slimeBallMeta.addLore("to slow enemies");
        slimeBallMeta.addLore("in a 5 block radius.");
        slimeBall.setItemMeta(slimeBallMeta);

        RunsafeItemStack sugar = new RunsafeItemStack(Material.SUGAR.getId());

        RunsafeItemStack magmaCream = new RunsafeItemStack(Material.MAGMA_CREAM.getId());
        RunsafeItemMeta magmaMeta = magmaCream.getItemMeta();
        magmaMeta.setDisplayName("§6Hot Topic");
        magmaMeta.addLore("Right click on the ground");
        magmaMeta.addLore("to smite enemies with lightning");
        magmaMeta.addLore("in a 5 block radius.");
        magmaCream.setItemMeta(magmaMeta);

        RunsafeItemStack endBall = new RunsafeItemStack(Material.ENDER_PEARL.getId());
        RunsafeItemMeta endBallMeta = endBall.getItemMeta();
        endBallMeta.setDisplayName("§6Portal Deluxe 2000");
        endBallMeta.addLore("Shift Right Click to teleport a random player");
        endBall.setItemMeta(endBallMeta);



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

    }

    public RunsafeItemStack get(){
        return items.get(Util.getRandom(0, items.size()));
    }

}

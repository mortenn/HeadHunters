package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerDeathEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.RandomItem;
import no.runsafe.headhunters.Util;
import org.bukkit.Material;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 22:31
 */
public class PlayerDeath implements IPlayerDeathEvent {


    private RandomItem randomItem;
    private Core core;

    public PlayerDeath(Core core, RandomItem randomItem){
        this.core = core;
        this.randomItem = randomItem;
    }

    @Override
    public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event) {

        RunsafePlayer player = event.getEntity();

        if(core.isIngame(player)){

            event.setDroppedXP(0);
            int amount = core.amountHeads(event.getEntity());
            List<RunsafeItemStack> items = event.getDrops();
            items.clear();
            if(Util.actPercentage(95)) items.add(randomItem.get());
            event.setDrops(items);
            player.getWorld().dropItem(
                    player.getEyeLocation(),
                    new RunsafeItemStack(Material.SKULL_ITEM.getId(), amount + 1, (short)3)
            );
        }
    }
}

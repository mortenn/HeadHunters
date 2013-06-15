package no.runsafe.headhunters.event;


import no.runsafe.framework.api.event.player.IPlayerDeathEvent;
import no.runsafe.framework.minecraft.Item;

import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;
import no.runsafe.headhunters.RandomItem;
import no.runsafe.headhunters.Util;


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
    private PlayerHandler playerHandler;

    public PlayerDeath(Core core, RandomItem randomItem, PlayerHandler playerHandler){
        this.core = core;
        this.randomItem = randomItem;
        this.playerHandler = playerHandler;
    }

    @Override
    public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event) {

        RunsafePlayer player = event.getEntity();

        if(playerHandler.isIngame(player)){

            //todo: drop all usable items
            event.setDroppedXP(0);
            int amount = core.amountHeads(event.getEntity());
            List<RunsafeMeta> items = event.getDrops();
            items.clear();
            items.add(randomItem.get());
            event.setDrops(items);
            RunsafeMeta heads = Item.Decoration.Head.Human.getItem();
            heads.setAmount(amount + 1);
            player.getWorld().dropItem(
                    player.getEyeLocation(),
                    heads
            );
        }
    }
}

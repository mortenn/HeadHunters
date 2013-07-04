package no.runsafe.headhunters.event;

import net.minecraft.server.v1_6_R1.Packet205ClientCommand;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.player.IPlayerDeathEvent;
import no.runsafe.framework.minecraft.Item;

import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;
import no.runsafe.headhunters.RandomItem;
import org.bukkit.craftbukkit.v1_6_R1.entity.CraftPlayer;


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
    private IScheduler scheduler;

    public PlayerDeath(Core core, RandomItem randomItem, PlayerHandler playerHandler, IScheduler scheduler){
        this.core = core;
        this.randomItem = randomItem;
        this.playerHandler = playerHandler;
        this.scheduler = scheduler;
    }

    @Override
    public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event) {

        final RunsafePlayer player = event.getEntity();

        if(playerHandler.isIngame(player)){


            RunsafePlayer killer = player.getKiller();
            if(killer != null) killer.setHealth(Math.min(killer.getHealth() + 4, 20));
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
            //autorespawning
            scheduler.startSyncTask(new Runnable() {
                @Override
                public void run() {
                    Packet205ClientCommand packet205ClientCommand = new Packet205ClientCommand();
                    packet205ClientCommand.a = 1;
                    ((CraftPlayer)player.getRawPlayer()).getHandle().playerConnection.a(packet205ClientCommand);
                }
            }, 10L);

        }
    }
}
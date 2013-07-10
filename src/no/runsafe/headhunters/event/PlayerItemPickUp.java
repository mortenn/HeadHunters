package no.runsafe.headhunters.event;


import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.player.IPlayerPickupItemEvent;
import no.runsafe.framework.minecraft.Buff;

import no.runsafe.framework.minecraft.entity.RunsafeItem;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerPickupItemEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;
import org.bukkit.Material;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 12:45
 */
public class PlayerItemPickUp implements IPlayerPickupItemEvent {


    private final Core core;
    private final IOutput console;
    private final PlayerHandler playerHandler;

    public PlayerItemPickUp(Core core, IOutput console, PlayerHandler playerHandler){
        this.console = console;
        this.core = core;
        this.playerHandler = playerHandler;
    }

    @Override
    public void OnPlayerPickupItemEvent(RunsafePlayerPickupItemEvent event) {

        RunsafePlayer player = event.getPlayer();
        RunsafeItem item = event.getItem();
        int itemID = item.getItemStack().getItemId();
        if(playerHandler.isIngame(player)){
            console.fine(String.format("%s picked up %d", player.getName(), item.getItemStack().getItemId()));
            boolean used = false;
            if( itemID == Material.GOLDEN_APPLE.getId()){
                Buff.Healing.Regeneration.amplification(2).duration(6).applyTo(player);
                used = true;
            }else if(itemID == Material.SUGAR.getId()){
                Buff.Utility.Movement.IncreaseSpeed.duration(4).amplification(4).applyTo(player);
                used = true;
            }
            if(used){
                event.getItem().remove();
                event.setCancelled(true);
            }
        }


    }
}

package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerPickupItemEvent;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.entity.RunsafeItem;
import no.runsafe.framework.server.event.player.RunsafePlayerPickupItemEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.server.potion.RunsafePotionEffect;
import no.runsafe.headhunters.Core;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 12:45
 */
public class PlayerItemPickUp implements IPlayerPickupItemEvent {


    private final Core core;
    private final IOutput console;

    public PlayerItemPickUp(Core core, IOutput console){
        this.console = console;
        this.core = core;
    }

    @Override
    public void OnPlayerPickupItemEvent(RunsafePlayerPickupItemEvent event) {

        RunsafePlayer player = event.getPlayer();
        RunsafeItem item = event.getItem();
        int itemID = item.getItemStack().getItemId();
        if(core.isIngame(player)){
            console.fine(String.format("%s picked up %d", player.getName(), item.getItemStack().getItemId()));

            boolean used = false;
            if( itemID == Material.GOLDEN_APPLE.getId()){
                PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, 190, 1);
                player.addPotionEffect(new RunsafePotionEffect(regen));
                used = true;
            }else if(itemID == Material.SUGAR.getId()){
                PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 80, 3);
                player.addPotionEffect(new RunsafePotionEffect(speed));
                used = true;
            }
            if(used){
                event.getItem().remove();
                event.setCancelled(true);
            }
        }


    }
}

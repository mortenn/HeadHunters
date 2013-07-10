package no.runsafe.headhunters.event;

import no.runsafe.framework.api.event.player.IPlayerDamageEvent;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.PlayerHandler;
import org.bukkit.Effect;
import org.bukkit.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 2-7-13
 * Time: 12:55
 */
public class DamagePlayer implements IPlayerDamageEvent {

    private final AreaHandler areaHandler;
    private final PlayerHandler playerHandler;

    public DamagePlayer(AreaHandler areaHandler, PlayerHandler playerHandler){
        this.areaHandler = areaHandler;
        this.playerHandler = playerHandler;
    }


    @Override
    public void OnPlayerDamage(RunsafePlayer player, RunsafeEntityDamageEvent event) {
        if(player.getWorld().getName().equalsIgnoreCase(areaHandler.getWorld()) && playerHandler.isIngame(player)){

            player.getWorld().playEffect(player.getLocation(), Effect.getById(2001), 152);

        }
    }
}

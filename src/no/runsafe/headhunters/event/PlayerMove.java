package no.runsafe.headhunters.event;


import no.runsafe.framework.api.event.player.IPlayerMove;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import org.bukkit.GameMode;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 13:25
 */
public class PlayerMove implements IPlayerMove {

    private final Core core;

    public PlayerMove(Core core){
         this.core = core;
     }


    @Override
    public boolean OnPlayerMove(RunsafePlayer player, RunsafeLocation from, RunsafeLocation to) {

        if(player.getGameMode() != GameMode.CREATIVE) return true;

        String prefRegion = core.isInCombatRegion(from);
        String newRegion = core.isInCombatRegion(to);

        if(newRegion != null && prefRegion != null)
        {
            if(!newRegion.equalsIgnoreCase(prefRegion)){
                player.sendColouredMessage("Entered region: " + newRegion + ";Left region: " + prefRegion);
            }
        }else if(newRegion == null && prefRegion != null){
            player.sendColouredMessage("Left region: " + prefRegion);
        }else if(prefRegion == null && newRegion != null){
            player.sendColouredMessage("Entered region: " + newRegion);
        }

        return true;
    }
}

package no.runsafe.headhunters.event;


import no.runsafe.framework.api.event.block.IBlockBreakEvent;
import no.runsafe.framework.minecraft.event.block.RunsafeBlockBreakEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 13-5-13
 * Time: 13:42
 */
public class BlockBreak implements IBlockBreakEvent {


    private PlayerHandler playerHandler;
    private Core core;
    private AreaHandler areaHandler;

    public BlockBreak(PlayerHandler playerHandler, Core core, AreaHandler areaHandler){
        this.playerHandler = playerHandler;
        this.core = core;
    }


    @Override
    public void OnBlockBreakEvent(RunsafeBlockBreakEvent event) {
        RunsafePlayer eventPlayer = event.getPlayer();
        if(eventPlayer.getWorld().getName().equalsIgnoreCase(playerHandler.getWorldName())){
            if(areaHandler.isInCombatRegion(eventPlayer.getLocation())){

                if(core.getEnabled()){
                    event.setCancelled(true);
                }else{
                    if(!eventPlayer.hasPermission("headhunters.build")){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}

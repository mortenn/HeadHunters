package no.runsafe.headhunters.event;

import no.runsafe.framework.event.block.IBlockBreakEvent;
import no.runsafe.framework.server.event.block.RunsafeBlockBreakEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 13-5-13
 * Time: 13:42
 */
public class BlockBreak implements IBlockBreakEvent {

    private Core core;

    public BlockBreak(Core core){
        this.core = core;
    }


    @Override
    public void OnBlockBreakEvent(RunsafeBlockBreakEvent event) {
        RunsafePlayer eventPlayer = event.getPlayer();
        if(eventPlayer.getWorld().getName().equalsIgnoreCase(core.getWorldName())){
            if(core.isInCombatRegion(eventPlayer.getLocation()) != null){

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

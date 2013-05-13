package no.runsafe.headhunters.event;

import no.runsafe.framework.event.block.IBlockPlace;
import no.runsafe.framework.server.block.RunsafeBlock;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 13-5-13
 * Time: 13:56
 */
public class BlockPlace implements IBlockPlace {

    Core core;
    public BlockPlace(Core core){
        this.core = core;
    }


    @Override
    public boolean OnBlockPlace(RunsafePlayer eventPlayer, RunsafeBlock block) {



        if(eventPlayer.getWorld().getName().equalsIgnoreCase(core.getWorldName())){
            if(core.isInCombatRegion(eventPlayer)){

                if(core.getEnabled()){
                    return false;
                }else{
                    if(!eventPlayer.hasPermission("headhunters.build")){
                        return false;
                    }
                }
            }
        }
        return true;

    }
}

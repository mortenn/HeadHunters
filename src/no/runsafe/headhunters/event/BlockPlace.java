package no.runsafe.headhunters.event;


import no.runsafe.framework.api.event.block.IBlockPlace;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 13-5-13
 * Time: 13:56
 */
public class BlockPlace implements IBlockPlace {

    Core core;
    private PlayerHandler playerHandler;
    public BlockPlace(Core core, PlayerHandler playerHandler){
        this.core = core;
        this.playerHandler = playerHandler;
    }


    @Override
    public boolean OnBlockPlace(RunsafePlayer eventPlayer, RunsafeBlock block) {



        if(eventPlayer.getWorld().getName().equalsIgnoreCase(playerHandler.getWorldName())){
            if(core.isInCombatRegion(eventPlayer.getLocation()) != null){

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

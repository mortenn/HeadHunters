package no.runsafe.headhunters.event;


import no.runsafe.framework.api.event.player.IPlayerRespawn;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.EquipmentManager;
import no.runsafe.headhunters.PlayerHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 27-4-13
 * Time: 23:11
 */
public class PlayerRespawn implements IPlayerRespawn {

    private EquipmentManager manager;
    private Core core;
    private final PlayerHandler playerHandler;

    public PlayerRespawn(Core core, EquipmentManager manager, PlayerHandler playerHandler){
        this.core = core;
        this.manager = manager;
        this.playerHandler = playerHandler;
    }

    @Override
    public RunsafeLocation OnPlayerRespawn(RunsafePlayer player, RunsafeLocation location, boolean isBed) {
        if(playerHandler.isIngame(player)){
            manager.equip(player);
            playerHandler.setUpPlayer(player);
            player.teleport(core.safeLocation());
            return core.safeLocation();
        }
        return null;
    }
}

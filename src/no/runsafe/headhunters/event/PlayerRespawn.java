package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerRespawn;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.EquipmentManager;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 27-4-13
 * Time: 23:11
 */
public class PlayerRespawn implements IPlayerRespawn {

    private EquipmentManager manager;
    private Core core;

    public PlayerRespawn(Core core, EquipmentManager manager){
        this.core = core;
        this.manager = manager;
    }

    @Override
    public RunsafeLocation OnPlayerRespawn(RunsafePlayer player, RunsafeLocation location, boolean isBed) {
        if(core.isIngame(player)){
            manager.equip(player);
            core.teleportIntoGame(player, null);
            return core.safeLocation();
        }
        return  null;
    }
}

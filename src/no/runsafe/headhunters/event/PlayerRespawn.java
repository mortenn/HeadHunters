package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerRespawn;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 27-4-13
 * Time: 23:11
 */
public class PlayerRespawn implements IPlayerRespawn {

    Core core;

    public PlayerRespawn(Core core){
        this.core = core;
    }

    @Override
    public RunsafeLocation OnPlayerRespawn(RunsafePlayer player, RunsafeLocation location, boolean isBed) {
        return core.playerRespawn(player);
    }
}

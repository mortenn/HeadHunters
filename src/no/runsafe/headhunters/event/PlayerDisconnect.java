package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerQuitEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerQuitEvent;
import no.runsafe.headhunters.Core;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 0:05
 */
public class PlayerDisconnect implements IPlayerQuitEvent {

    private Core core;

    public PlayerDisconnect(Core core){
        this.core = core;
    }

    @Override
    public void OnPlayerQuit(RunsafePlayerQuitEvent event) {
        if(core.isIngame(event.getPlayer())) this.core.leave(event.getPlayer());
    }
}

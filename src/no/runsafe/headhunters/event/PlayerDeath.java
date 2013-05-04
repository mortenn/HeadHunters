package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerDeathEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerDeathEvent;
import no.runsafe.headhunters.Core;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 22:31
 */
public class PlayerDeath implements IPlayerDeathEvent {


    private Core core;

    public PlayerDeath(Core core){
        this.core = core;
    }

    @Override
    public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event) {

        core.playerDeath(event);
    }
}

package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerPickupItemEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerPickupItemEvent;
import no.runsafe.headhunters.Core;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 12:45
 */
public class PlayerItemPickUp implements IPlayerPickupItemEvent {


    private final Core core;

    public PlayerItemPickUp(Core core){
        this.core = core;
    }

    @Override
    public void OnPlayerPickupItemEvent(RunsafePlayerPickupItemEvent event) {

        core.pickUp(event);


    }
}

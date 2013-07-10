package no.runsafe.headhunters.event;

import no.runsafe.framework.api.event.player.IPlayerTeleportEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerTeleportEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.PlayerHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 8-7-13
 * Time: 12:52
 */
public class PlayerTeleport implements IPlayerTeleportEvent
{
	final PlayerHandler playerHandler;
	final AreaHandler areaHandler;

	public PlayerTeleport(PlayerHandler playerHandler, AreaHandler areaHandler)
	{
		this.playerHandler = playerHandler;
		this.areaHandler = areaHandler;
	}

	@Override
	public void OnPlayerTeleport(RunsafePlayerTeleportEvent event)
	{
		RunsafePlayer player = event.getPlayer();

		if (playerHandler.isIngame(player))
			if (!areaHandler.isInCurrentCombatRegion(event.getTo()))
			{
				System.out.println("Not in region! " + areaHandler.isInCurrentCombatRegion(player.getLocation()));

				playerHandler.remove(player);
			}

	}
}

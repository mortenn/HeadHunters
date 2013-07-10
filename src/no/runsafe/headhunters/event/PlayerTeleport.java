package no.runsafe.headhunters.event;

import no.runsafe.framework.api.event.player.IPlayerTeleportEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerTeleportEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.PlayerHandler;

public class PlayerTeleport implements IPlayerTeleportEvent
{
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

	private final PlayerHandler playerHandler;
	private final AreaHandler areaHandler;
}

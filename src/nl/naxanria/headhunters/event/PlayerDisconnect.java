package nl.naxanria.headhunters.event;

import no.runsafe.framework.api.event.player.IPlayerQuitEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerQuitEvent;
import nl.naxanria.headhunters.PlayerHandler;

public class PlayerDisconnect implements IPlayerQuitEvent
{
	public PlayerDisconnect(PlayerHandler playerHandler)
	{
		this.playerHandler = playerHandler;
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		if (playerHandler.isIngame(event.getPlayer())) playerHandler.remove(event.getPlayer());
	}

	private final PlayerHandler playerHandler;
}

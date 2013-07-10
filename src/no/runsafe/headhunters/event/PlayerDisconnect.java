package no.runsafe.headhunters.event;


import no.runsafe.framework.api.event.player.IPlayerQuitEvent;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerQuitEvent;
import no.runsafe.headhunters.PlayerHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 0:05
 */
public class PlayerDisconnect implements IPlayerQuitEvent
{
	private final PlayerHandler playerHandler;

	public PlayerDisconnect(PlayerHandler playerHandler)
	{
		this.playerHandler = playerHandler;
	}

	@Override
	public void OnPlayerQuit(RunsafePlayerQuitEvent event)
	{
		if (playerHandler.isIngame(event.getPlayer())) playerHandler.remove(event.getPlayer());
	}
}

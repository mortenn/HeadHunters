package no.runsafe.headhunters.event;

import no.runsafe.framework.api.event.player.IPlayerRespawn;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.PlayerHandler;

public class PlayerRespawn implements IPlayerRespawn
{
	public PlayerRespawn(PlayerHandler playerHandler, AreaHandler areaHandler)
	{

		this.playerHandler = playerHandler;
		this.areaHandler = areaHandler;
	}

	@Override
	public RunsafeLocation OnPlayerRespawn(RunsafePlayer player, RunsafeLocation location, boolean isBed)
	{
		if (playerHandler.isIngame(player))
		{
			playerHandler.setUpPlayer(player);
			player.teleport(areaHandler.getSafeLocation());
			player.removeBuffs();
			return areaHandler.getSafeLocation();
		}
		return null;
	}

	private final PlayerHandler playerHandler;
	private final AreaHandler areaHandler;
}

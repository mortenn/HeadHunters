package no.runsafe.headhunters.event;

import no.runsafe.framework.api.event.player.IPlayerDamageEvent;
import no.runsafe.framework.minecraft.event.entity.RunsafeEntityDamageEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.PlayerHandler;
import org.bukkit.Effect;

public class DamagePlayer implements IPlayerDamageEvent
{
	public DamagePlayer(AreaHandler areaHandler, PlayerHandler playerHandler)
	{
		this.areaHandler = areaHandler;
		this.playerHandler = playerHandler;
	}

	@Override
	public void OnPlayerDamage(RunsafePlayer player, RunsafeEntityDamageEvent event)
	{
		if (player.getWorld().getName().equalsIgnoreCase(areaHandler.getWorld()) && playerHandler.isIngame(player))
		{

			player.getWorld().playEffect(player.getLocation(), Effect.getById(2001), 152);

		}
	}

	private final AreaHandler areaHandler;
	private final PlayerHandler playerHandler;
}

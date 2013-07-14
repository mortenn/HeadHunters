package nl.naxanria.headhunters.event;

import nl.naxanria.headhunters.Core;
import no.runsafe.framework.api.event.block.IBlockBreakEvent;
import no.runsafe.framework.minecraft.event.block.RunsafeBlockBreakEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import nl.naxanria.headhunters.AreaHandler;
import nl.naxanria.headhunters.PlayerHandler;

public class BlockBreak implements IBlockBreakEvent
{
	public BlockBreak(PlayerHandler playerHandler, Core core, AreaHandler areaHandler)
	{
		this.playerHandler = playerHandler;
		this.core = core;
		this.areaHandler = areaHandler;
	}

	@Override
	public void OnBlockBreakEvent(RunsafeBlockBreakEvent event)
	{
		RunsafePlayer eventPlayer = event.getPlayer();
		if (eventPlayer.getWorld().getName().equalsIgnoreCase(playerHandler.getWorldName()))
		{
			if (areaHandler.isInCombatRegion(eventPlayer.getLocation()))
			{

				if (core.isEnabled())
				{
					event.setCancelled(true);
				}
				else
				{
					if (!eventPlayer.hasPermission("headhunters.build"))
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	private final PlayerHandler playerHandler;
	private final Core core;
	private final AreaHandler areaHandler;
}

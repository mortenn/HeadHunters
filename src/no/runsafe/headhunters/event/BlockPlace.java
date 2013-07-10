package no.runsafe.headhunters.event;

import no.runsafe.framework.api.event.block.IBlockPlace;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;

public class BlockPlace implements IBlockPlace
{
	private final Core core;
	private final PlayerHandler playerHandler;
	private final AreaHandler areaHandler;

	public BlockPlace(Core core, PlayerHandler playerHandler, AreaHandler areaHandler)
	{
		this.core = core;
		this.playerHandler = playerHandler;
		this.areaHandler = areaHandler;
	}


	@Override
	public boolean OnBlockPlace(RunsafePlayer eventPlayer, RunsafeBlock block)
	{


		if (eventPlayer.getWorld().getName().equalsIgnoreCase(playerHandler.getWorldName()))
		{
			if (areaHandler.isInCombatRegion(eventPlayer.getLocation()))
			{

				if (core.getEnabled())
				{
					return false;
				}
				else
				{
					if (!eventPlayer.hasPermission("headhunters.build"))
					{
						return false;
					}
				}
			}
		}
		return true;

	}
}

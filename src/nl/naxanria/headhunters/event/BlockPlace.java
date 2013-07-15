package nl.naxanria.headhunters.event;

import nl.naxanria.headhunters.handler.AreaHandler;
import nl.naxanria.headhunters.Core;
import nl.naxanria.headhunters.handler.PlayerHandler;
import no.runsafe.framework.api.event.block.IBlockPlace;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class BlockPlace implements IBlockPlace
{
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

				if (core.isEnabled())
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

	private final Core core;
	private final PlayerHandler playerHandler;
	private final AreaHandler areaHandler;
}

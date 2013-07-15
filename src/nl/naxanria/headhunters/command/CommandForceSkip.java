package nl.naxanria.headhunters.command;

import nl.naxanria.headhunters.Constants;
import nl.naxanria.headhunters.Core;
import nl.naxanria.headhunters.handler.VoteHandler;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import nl.naxanria.headhunters.handler.AreaHandler;

import java.util.HashMap;

public class CommandForceSkip extends PlayerCommand
{
	public CommandForceSkip(AreaHandler areaHandler, VoteHandler voteHandler, Core core)
	{
		super("forceskip", "skips the current map for another one", "headhunters.game-control.forceskip", "map");
		this.core = core;
		this.areaHandler = areaHandler;
		this.voteHandler = voteHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		if (core.isEnabled())
		{
			String nextMap = parameters.get("map");
			int nextMapIndex;
			if (nextMap.equalsIgnoreCase("random"))
				if (areaHandler.getAmountLoadedAreas() > 1) areaHandler.randomNextArea();
				else return Constants.ERROR_COLOR + "Define atleast 2 areas to be able to use random.";

			else if ((nextMapIndex = areaHandler.getAreaByName(nextMap)) != -1)
				areaHandler.setNextArea(nextMapIndex);
			else return Constants.ERROR_COLOR + "Please specify a correct map, or use &frandom" + Constants.ERROR_COLOR +
					" for a random map. Available:&f" + areaHandler.getAvailableRegions();
			voteHandler.resetVotes();
			return "&bNext region will be:&f" + areaHandler.getAreaName(areaHandler.getNextArea());
		}
		return Constants.ERROR_COLOR + "Only use this when headhunters is enabled!";
	}

	private final Core core;
	private final AreaHandler areaHandler;
	private final VoteHandler voteHandler;
}

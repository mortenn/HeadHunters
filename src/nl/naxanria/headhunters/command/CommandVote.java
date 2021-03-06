package nl.naxanria.headhunters.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import nl.naxanria.headhunters.handler.AreaHandler;
import nl.naxanria.headhunters.Constants;
import nl.naxanria.headhunters.Core;
import nl.naxanria.headhunters.handler.VoteHandler;

import java.util.HashMap;

public class CommandVote extends PlayerCommand
{
	public CommandVote(AreaHandler areaHandler, VoteHandler voteHandler, Core core)
	{
		super("vote", "Vote for a different next map", "headhunters.play");
		this.areaHandler = areaHandler;
		this.voteHandler = voteHandler;
		this.core = core;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		if (!core.isEnabled())
			return Constants.ERROR_COLOR + "Headhunters is disabled";
		if (!areaHandler.isInWaitRoom(executor))
			return Constants.MSG_NEED_IN_WAITROOM;

		return voteHandler.vote(executor);
	}

	private final AreaHandler areaHandler;
	private final VoteHandler voteHandler;
	private final Core core;
}

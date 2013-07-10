package no.runsafe.headhunters.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.VoteHandler;

import java.util.HashMap;

public class CommandVote extends PlayerCommand
{
	public CommandVote(Core core, VoteHandler voteHandler)
	{
		super("vote", "Vote for a different next map", "headhunters.play");
		this.core = core;
		this.voteHandler = voteHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{

		if (!core.isInWaitroom(executor)) return Constants.MSG_NEED_IN_WAITROOM;

		return voteHandler.vote(executor);

	}

	private final Core core;
	private final VoteHandler voteHandler;
}

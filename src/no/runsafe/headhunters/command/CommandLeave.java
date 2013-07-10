package no.runsafe.headhunters.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.PlayerHandler;

import java.util.HashMap;

public class CommandLeave extends PlayerCommand
{
	private final PlayerHandler playerHandler;

	public CommandLeave(PlayerHandler playerHandler)
	{
		super("leave", "Leaves the current game", "headhunters.play");

		this.playerHandler = playerHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		playerHandler.remove(executor);
		return null;
	}
}

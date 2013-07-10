package no.runsafe.headhunters.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandShowIngame extends PlayerCommand
{
	final Core core;
	private final PlayerHandler playerHandler;

	public CommandShowIngame(Core core, PlayerHandler playerHandler)
	{
		super("show", "shows ingame players", "headhunters.play");
		this.core = core;

		this.playerHandler = playerHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		List<String> players = new ArrayList<String>();
		for (RunsafePlayer player : playerHandler.getIngamePlayers())
			players.add(player.getPrettyName());
		return Constants.MSG_COLOR + Strings.join(players, ", ");
	}
}

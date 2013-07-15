package nl.naxanria.headhunters.command;

import nl.naxanria.headhunters.AreaHandler;
import nl.naxanria.headhunters.Constants;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class CommandSetSpawn extends PlayerCommand
{
	public CommandSetSpawn(AreaHandler areaHandler)
	{
		super("spawn", "Sets the spawn point for in the wait room", "headhunters.regions.modify.spawn");
		this.areaHandler = areaHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer player, HashMap<String, String> parameters)
	{
		if (!areaHandler.isInGameWorld(player))
			return Constants.ERROR_COLOR + "Please move to the correct world";

		areaHandler.setWaitRoomSpawn(player.getLocation());
		return "Successfully set spawn";
	}

	private final AreaHandler areaHandler;
}

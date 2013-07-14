package no.runsafe.headhunters.command;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.Constants;

import java.util.HashMap;

public class CommandSetSpawn extends PlayerCommand
{
	public CommandSetSpawn(IConfiguration config, AreaHandler areaHandler)
	{
		super("spawn", "Sets the spawn point for in the wait room", "headhunters.regions.modify.spawn");
		this.config = config;
		this.areaHandler = areaHandler;
	}

	@Override
	public String OnExecute(RunsafePlayer player, HashMap<String, String> parameters)
	{

		if (player.getWorld().getName().equalsIgnoreCase(areaHandler.getWorld()))
		{
			config.setConfigValue("waitingroomspawn.x", player.getLocation().getBlockX());
			config.setConfigValue("waitingroomspawn.y", player.getLocation().getBlockY());
			config.setConfigValue("waitingroomspawn.z", player.getLocation().getBlockZ());

			config.save();

			areaHandler.setWaitRoomSpawn(player.getLocation());

			return "Successfully set spawn";
		}


		return Constants.ERROR_COLOR + "Please move to the correct world";
	}

	private final IConfiguration config;
	private final AreaHandler areaHandler;
}
